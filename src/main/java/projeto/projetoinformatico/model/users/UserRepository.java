package projeto.projetoinformatico.model.users;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository  extends JpaRepository<User,Long> {
    User findByUsername(String userName);

    User findByRole(Role role);
    @NotNull
    List<User> findAll();

    List<User> findAllByRole(Role role);


    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByUsernameContainingIgnoreCaseAndRole(String name, Role role);

    List<User> findByUsernameContainingIgnoreCase(String name);

    List<User> findByUsernameStartingWithIgnoreCase(String name);

    List<User> findByUsernameStartingWithIgnoreCaseAndRole(String name, Role role);
}
