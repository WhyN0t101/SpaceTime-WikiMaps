package projeto.projetoinformatico.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LayersRepository extends JpaRepository<Layer, Long> {

}
