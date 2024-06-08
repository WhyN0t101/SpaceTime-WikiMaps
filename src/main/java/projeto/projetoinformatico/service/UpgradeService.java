package projeto.projetoinformatico.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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

        Optional<RoleUpgrade> existingRequest = roleUpgradeRepository.findFirstByUserIdAndStatusInOrderByTimestampDesc(user.getId(),
                List.of(RoleStatus.PENDING, RoleStatus.ACCEPTED));

        if (existingRequest.isPresent()) {
            throw new InvalidRequestException("A request is still pending or has already been accepted.");
        }

        // Check if the user made a request in the last 7 days
        Optional<RoleUpgrade> lastRequest = roleUpgradeRepository.findFirstByUserOrderByTimestampDesc(user);
        if (lastRequest.isPresent()) {
            RoleUpgrade lastRoleUpgrade = lastRequest.get();
           Date currentDate = new Date();
            Date lastRequestDate = lastRoleUpgrade.getTimestamp();
            long differenceInMillis = currentDate.getTime() - lastRequestDate.getTime();
            long differenceInDays = differenceInMillis / (1000 * 60 * 60 * 24);
            if (differenceInDays < 7 && lastRoleUpgrade.getStatus() == RoleStatus.DECLINED) {
                throw new InvalidRequestException("Request has been denied. Please try again after 7 days.");
            }
            // Delete the last request if it's not pending or accepted
            roleUpgradeRepository.delete(lastRoleUpgrade);
        }
       // roleUpgradeRepository.delete(lastRequest);
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
        dto.setUser(upgrade.getUser());
        return dto;
    }
}
