package projeto.projetoinformatico.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.dtos.RoleUpgradeDTO;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UpgradeService {

    private final RoleUpgradeRepository roleUpgradeRepository;
    private final UserRepository userRepository;
    private final ModelMapperUtils mapperUtils;

    @Autowired
    public UpgradeService(RoleUpgradeRepository roleUpgradeRepository, UserRepository userRepository, ModelMapperUtils mapperUtils) {
        this.roleUpgradeRepository = roleUpgradeRepository;
        this.userRepository = userRepository;
        this.mapperUtils = mapperUtils;
    }

    @CacheEvict(value = {"userCache", "requestCache"}, allEntries = true)
    public RoleUpgradeDTO requestUpgrade(String username, String reason) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        // Check if the user has a pending or accepted request
        Optional<RoleUpgrade> existingRequest = roleUpgradeRepository.findFirstByUserIdAndStatusInOrderByTimestampDesc(user.getId(),
                List.of(RoleStatus.PENDING, RoleStatus.ACCEPTED));

        if (existingRequest.isPresent()) {
            throw new InvalidRequestException("A request is still pending or has already been accepted.");
        }

        // Check if the user made a request in the last 7 days
        Optional<RoleUpgrade> lastRequest = roleUpgradeRepository.findFirstByUserOrderByTimestampDesc(user);
        if (lastRequest.isPresent()) {
            Date currentDate = new Date();
            Date lastRequestDate = lastRequest.get().getTimestamp();
            long differenceInMillis = currentDate.getTime() - lastRequestDate.getTime();
            long differenceInDays = differenceInMillis / (1000 * 60 * 60 * 24);
            if (differenceInDays < 7 && lastRequest.get().getStatus() == RoleStatus.DECLINED) {
                throw new InvalidRequestException("Request has been denied. Please try again after 7 days.");
            }
        }

        // Create a new upgrade request
        RoleUpgrade request = new RoleUpgrade();
        request.setUser(user);
        request.setReason(reason);
        saveRequest(request);
        return convertUpgradeToDTO(request);
    }

    private void saveRequest(RoleUpgrade request) {
        roleUpgradeRepository.save(request);
    }

    @CacheEvict(value = {"userCache", "requestCache"}, allEntries = true)
    public RoleUpgradeDTO handleRequest(StatusRequest request, Long id) {
        RoleUpgrade roleUpgrade = roleUpgradeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Upgrade request not found"));

        RoleStatus statusEnum = RoleStatus.valueOf(request.getStatus().toUpperCase());
        roleUpgrade.setStatus(statusEnum);
        roleUpgrade.setMessage(request.getMessage());

        if (statusEnum == RoleStatus.ACCEPTED) {
            User user = roleUpgrade.getUser();
            user.setRole(Role.EDITOR);
            userRepository.save(user);
        }

        roleUpgradeRepository.save(roleUpgrade);
        return convertUpgradeToDTO(roleUpgrade);
    }

    @Cacheable(value = "requestCache", key = "#status")
    public List<RoleUpgradeDTO> getByStatus(String status) {
        RoleStatus statusEnum;
        try {
            statusEnum = RoleStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Status not found: " + status);
        }

        List<RoleUpgrade> requests = roleUpgradeRepository.findByStatus(statusEnum);
        if (requests.isEmpty()) {
            throw new NotFoundException("No requests found with status: " + status);
        }

        return requests.stream()
                .map(this::convertUpgradeToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "requestCache")
    public List<RoleUpgradeDTO> getAllRequests() {
        List<RoleUpgrade> requests = roleUpgradeRepository.findAll();
        return requests.stream()
                .map(this::convertUpgradeToDTO)
                .collect(Collectors.toList());
    }

    public List<RoleUpgradeDTO> getRequestsByNameAndStatus(String username, String status) {
        RoleStatus statusEnum;
        try {
            statusEnum = RoleStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Status not found: " + status);
        }

        List<RoleUpgrade> requests = roleUpgradeRepository.findByUserUsernameContainingIgnoreCaseAndStatus(username, statusEnum);
        if (requests.isEmpty()) {
            throw new NotFoundException("No requests found with name containing: " + username + " and status: " + status);
        }

        return requests.stream()
                .map(this::convertUpgradeToDTO)
                .collect(Collectors.toList());
    }

    public List<RoleUpgradeDTO> getRequestsContainingUsername(String username) {
        List<RoleUpgrade> requests = roleUpgradeRepository.findByUserUsernameContainingIgnoreCase(username);
        if (requests.isEmpty()) {
            throw new NotFoundException("No requests found with name containing: " + username);
        }

        return requests.stream()
                .map(this::convertUpgradeToDTO)
                .collect(Collectors.toList());
    }
/// PAGINATION

    public Page<RoleUpgradeDTO> getByStatusPaged(String status, Pageable pageable) {
        RoleStatus statusEnum = RoleStatus.valueOf(status.toUpperCase());
        Page<RoleUpgrade> requests = roleUpgradeRepository.findByStatus(statusEnum, pageable);
        return requests.map(this::convertUpgradeToDTO);
    }

    public Page<RoleUpgradeDTO> getAllRequestsPaged(Pageable pageable) {
        Page<RoleUpgrade> requests = roleUpgradeRepository.findAll(pageable);
        return requests.map(this::convertUpgradeToDTO);
    }

    public Page<RoleUpgradeDTO> getRequestsByNameAndStatusPaged(String username, String status, Pageable pageable) {
        RoleStatus statusEnum = RoleStatus.valueOf(status.toUpperCase());
        Page<RoleUpgrade> requests = roleUpgradeRepository.findByUserUsernameContainingIgnoreCaseAndStatus(username, statusEnum, pageable);
        return requests.map(this::convertUpgradeToDTO);
    }

    public Page<RoleUpgradeDTO> getRequestsContainingUsernamePaged(String username, Pageable pageable) {
        Page<RoleUpgrade> requests = roleUpgradeRepository.findByUserUsernameContainingIgnoreCase(username, pageable);
        return requests.map(this::convertUpgradeToDTO);
    }


    private RoleUpgradeDTO convertUpgradeToDTO(RoleUpgrade upgrade) {
        RoleUpgradeDTO dto = mapperUtils.roleUpgradeToDTO(upgrade, RoleUpgradeDTO.class);
        dto.setUsername(upgrade.getUser().getUsername());
        return dto;
    }
}
