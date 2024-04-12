package projeto.projetoinformatico.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "Layers")
@Data
public class Layer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String username;

    @Getter
    @Setter
    @Column(nullable = false)
    private String description;

    @Getter
    @Setter
    @Column(nullable = false)
    private Date timestamp;

    @Getter
    @Setter
    @Column(nullable = false, columnDefinition = "TEXT")
    private String searchQuery;

    @Getter
    @Setter
    @Column(nullable = false, columnDefinition = "TEXT") // Store the JSON as a string
    private String searchResult;




}
