package projeto.projetoinformatico.model.roleUpgrade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projeto.projetoinformatico.model.roleUpgrade.RoleStatus;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;
import projeto.projetoinformatico.model.users.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleUpgradeRepository extends JpaRepository<RoleUpgrade, Long> {

    List<RoleUpgrade> findByStatus(RoleStatus statusEnum);

    // Corrected method name
    RoleUpgrade findByUserId(Long id);

    // Corrected method name
    Optional<RoleUpgrade> findFirstByUserOrderByTimestampDesc(User user);


    List<RoleUpgrade> findByUserUsernameContainingIgnoreCase(String username);

    List<RoleUpgrade> findByUserUsernameContainingIgnoreCaseAndStatus(String username, RoleStatus roleEnum);

    Optional<RoleUpgrade> findFirstByUserIdAndStatusInOrderByTimestampDesc(Long id, List<RoleStatus> pending);

    List<RoleUpgrade> findALlByUserId(Long userId);
}
