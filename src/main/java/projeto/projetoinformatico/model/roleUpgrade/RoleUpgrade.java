package projeto.projetoinformatico.model.roleUpgrade;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import projeto.projetoinformatico.model.users.User;

import java.util.Date;

@Entity
@Table(name = "upgrade")
@Data
public class RoleUpgrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

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
    private RoleStatus status;

    @Column
    @Getter
    @Setter
    private String message;

    @PrePersist
    protected void onCreate() {
        timestamp = new Date();
        status = RoleStatus.PENDING;
    }
}
