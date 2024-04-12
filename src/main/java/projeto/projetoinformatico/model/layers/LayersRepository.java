package projeto.projetoinformatico.model.layers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projeto.projetoinformatico.model.layers.Layer;

@Repository
public interface LayersRepository extends JpaRepository<Layer, Long> {

}
