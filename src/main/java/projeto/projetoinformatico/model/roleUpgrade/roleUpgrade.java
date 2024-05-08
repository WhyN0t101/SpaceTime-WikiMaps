package projeto.projetoinformatico.model.roleUpgrade;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "upgrade")
@Data
public class roleUpgrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Getter
    @Setter
    private String username;

    @Column(nullable = false)
    @Getter
    @Setter
    private String reason;

    @Column(nullable = false)
    @Getter
    @Setter
    private Date timestamp;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private roleStatus status;

    @Column
    @Getter
    @Setter
    private String message;

    @PrePersist
    protected void onCreate() {
        timestamp = new Date();
    }
}
