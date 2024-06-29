package projeto.projetoinformatico.model.layers;

import jakarta.persistence.*;
import lombok.Data;
import projeto.projetoinformatico.model.users.User;

import java.util.Date;


@Table(name = "layers")
@Data
@Entity
public class Layer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    @Column(nullable = false)
    private String layerName;

    @Column(nullable = false)
    private String description;


    @Column(nullable = false)
    private Date timestamp;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String query;

    @PrePersist
    protected void onCreate() {
        timestamp = new Date();
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String searchQuery) {
        this.query = searchQuery;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }
}
