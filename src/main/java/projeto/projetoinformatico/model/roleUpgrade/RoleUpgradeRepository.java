package projeto.projetoinformatico.model.roleUpgrade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleUpgradeRepository extends JpaRepository<RoleUpgrade, Long> {
    Optional<RoleUpgrade> findFirstByUsernameAndStatusInOrderByTimestampDesc(String username, List<RoleStatus> pending);

    Optional<RoleUpgrade> findFirstByUsernameOrderByTimestampDesc(String username);

    List<RoleUpgrade> findByStatus(RoleStatus statusEnum);

    RoleUpgrade findByUsername(String username);
}
