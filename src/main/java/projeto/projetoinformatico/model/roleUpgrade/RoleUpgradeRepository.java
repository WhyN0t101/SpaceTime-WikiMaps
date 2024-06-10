package projeto.projetoinformatico.model.roleUpgrade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projeto.projetoinformatico.model.roleUpgrade.RoleStatus;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;
import projeto.projetoinformatico.model.users.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleUpgradeRepository extends JpaRepository<RoleUpgrade, Long> {


    // Corrected method name
    RoleUpgrade findByUserId(Long id);

    // Corrected method name
    Optional<RoleUpgrade> findFirstByUserOrderByTimestampDesc(User user);



    Optional<RoleUpgrade> findFirstByUserIdAndStatusInOrderByTimestampDesc(Long id, List<RoleStatus> pending);

    List<RoleUpgrade> findALlByUserId(Long userId);

    Page<RoleUpgrade> findByUserUsernameContainingIgnoreCase(String username, Pageable pageable);

    Page<RoleUpgrade> findByUserUsernameContainingIgnoreCaseAndStatus(String username, RoleStatus roleEnum, Pageable pageable);
    Page<RoleUpgrade> findByStatus(RoleStatus statusEnum, Pageable pageable);
    Page<RoleUpgrade> findAll(Pageable pageable);

    Optional<RoleUpgrade> findFirstByUserIdOrderByTimestampDesc(Long id);
}
