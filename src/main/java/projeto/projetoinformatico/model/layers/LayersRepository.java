package projeto.projetoinformatico.model.layers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LayersRepository extends JpaRepository<Layer, Long> {


    @Query("SELECT l FROM Layer l WHERE lower(l.layerName) LIKE %:query% OR lower(l.description) LIKE %:query%")
    List<Layer> findByKeywords(@Param("query") String query);

    boolean existsByLayerName(String layerName);

    List<Layer> findLayersByUserId(Long id);
    Page<Layer> findAll(Pageable pageable);

    @Query("SELECT l FROM Layer l WHERE lower(l.layerName) LIKE %:query% OR lower(l.description) LIKE %:query%")
    Page<Layer> findByKeywordsPage(@Param("query") String query, Pageable pageable);

    Page<Layer> findLayersByUserId(Long id, Pageable pageable);
}
