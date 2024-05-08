package projeto.projetoinformatico.model.roleUpgrade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleUpgradeRepository extends JpaRepository<RoleUpgrade, Long> {
}
