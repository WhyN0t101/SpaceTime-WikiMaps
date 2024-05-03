package projeto.projetoinformatico.model.layers;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.users.User;

import java.util.List;

@Repository
public interface LayersRepository extends JpaRepository<Layer, Long> {

    List<Layer> findByUsername(String username);

    @Query("SELECT l FROM Layer l WHERE lower(l.layerName) LIKE %:query% OR lower(l.description) LIKE %:query%")
    List<Layer> findByKeywords(@Param("query") String query);

}
