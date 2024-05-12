package projeto.projetoinformatico.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.dtos.RoleUpgradeDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidRequestException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.roleUpgrade.RoleStatus;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgradeRepository;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.requests.StatusRequest;
import projeto.projetoinformatico.utils.ModelMapperUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UpgradeService {

    private final RoleUpgradeRepository roleUpgradeRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ModelMapperUtils mapperUtils;

    @Autowired
    public UpgradeService(RoleUpgradeRepository roleUpgradeRepository, UserRepository userRepository, ModelMapper modelMapper, ModelMapperUtils mapperUtils) {
        this.roleUpgradeRepository = roleUpgradeRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.mapperUtils = mapperUtils;
    }

    @CacheEvict(value = {"userCache", "requestCache"}, allEntries = true)
    public RoleUpgradeDTO requestUpgrade(String username, String reason) {
        // Check if the user has a pending or accepted request
        Optional<RoleUpgrade> existingRequest = roleUpgradeRepository.findFirstByUsernameAndStatusInOrderByTimestampDesc(username,
                List.of(RoleStatus.PENDING, RoleStatus.ACCEPTED));

        if (existingRequest.isPresent()) {
            // If a request exists and it's pending or accepted, return null indicating the user cannot make another request
            throw new InvalidRequestException("A request is still pending or already been accepted");
        }

        // Check if the user made a request in the last 7 days
        Optional<RoleUpgrade> lastRequest = roleUpgradeRepository.findFirstByUsernameOrderByTimestampDesc(username);
        if (lastRequest.isPresent()) {
            Date currentDate = new Date();
            Date lastRequestDate = lastRequest.get().getTimestamp();
            long differenceInMillis = currentDate.getTime() - lastRequestDate.getTime();
            long differenceInDays = differenceInMillis / (1000 * 60 * 60 * 24);
            if (differenceInDays < 7 && lastRequest.get().getStatus() == RoleStatus.DECLINED) {
                // If the last request was made less than 7 days ago and was declined, return null indicating the user cannot make another request
                throw new InvalidRequestException("Request has been denied. Do it again after 7 days.");
            }
        }
        // Create a new upgrade request
        RoleUpgrade request = new RoleUpgrade();
        request.setReason(reason);
        request.setUsername(username);
        return convertUpgradeToDTO(saveRequest(request));
    }

    private RoleUpgrade saveRequest(RoleUpgrade request) {
        return roleUpgradeRepository.save(request);
    }


    @CacheEvict(value = {"userCache", "requestCache"}, allEntries = true)
    public RoleUpgradeDTO  handleRequest(StatusRequest request, Long id) {
        // Retrieve the requested upgrade by ID
        Optional<RoleUpgrade> optionalRequest = roleUpgradeRepository.findById(id);

        // Check if the requested upgrade exists
        if (optionalRequest.isEmpty()) {
            throw new NotFoundException("Upgrade request not found");
        }

        RoleUpgrade roleUpgrade = optionalRequest.get();

        // Update the status and message of the upgrade request
        RoleStatus statusEnum = RoleStatus.valueOf(request.getStatus().toUpperCase());

        String message = request.getMessage();
        roleUpgrade.setStatus(statusEnum);
        roleUpgrade.setMessage(message);

        // If the request is accepted, update the user's role
        if (statusEnum == RoleStatus.ACCEPTED) {
            String username = roleUpgrade.getUsername();
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new NotFoundException("User not found");
            }
            user.setRole(Role.EDITOR);
            // Save the updated user back to the database
            userRepository.save(user);
        }

        // Save the updated upgrade request
        return convertUpgradeToDTO(roleUpgradeRepository.save(roleUpgrade));
    }

    @Cacheable(value = "requestCache", key = "#status")
    public List<RoleUpgradeDTO> getByStatus(String status) {
        try {
            RoleStatus statusEnum = RoleStatus.valueOf(status.toUpperCase());
            List<RoleUpgrade> requests = roleUpgradeRepository.findByStatus(statusEnum);
            if (requests.isEmpty()) {
                throw new NotFoundException("No requests found with status " + status );
            }
            return requests.stream()
                    .map(this::convertUpgradeToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Status not found: " + status);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Status cannot be null");
        }
    }


    @Cacheable(value = "requestCache")
    public List<RoleUpgradeDTO> getAllRequests() {
        return roleUpgradeRepository.findAll().stream()
                .map(this::convertUpgradeToDTO)
                .collect(Collectors.toList());
    }
    private RoleUpgradeDTO convertUpgradeToDTO(RoleUpgrade upgrade) {
        return mapperUtils.roleUpgradeToDTO(upgrade, RoleUpgradeDTO.class);
    }


    public List<RoleUpgradeDTO> getRequestsByNameAndStatus(String username, String status) {
        try {
            RoleStatus roleEnum = RoleStatus.valueOf(status.toUpperCase());

            List<RoleUpgrade> users = roleUpgradeRepository.findByUsernameStartingWithIgnoreCaseAndStatus(username, roleEnum);
            if (users.isEmpty()) {
                throw new NotFoundException("No requests found with name starting with: " + username + " and status: " + status);
            }
            return users.stream()
                    .map(this::convertUpgradeToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Status not found: " + status);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Status cannot be null");
        }
    }

    public List<RoleUpgradeDTO> getRequestsContainingUsername(String username) {
        List<RoleUpgrade> requests = roleUpgradeRepository.findByUsernameStartingWithIgnoreCase(username);
        if (requests.isEmpty()) {
            throw new NotFoundException("No users found with name starting with: " + username);
        }
        return requests.stream()
                .map(this::convertUpgradeToDTO)
                .collect(Collectors.toList());
    }
}
