package projeto.projetoinformatico.model.users;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



import java.util.List;

@Repository
public interface UserRepository  extends JpaRepository<User,Long> {
    User findByUsername(String userName);

    @NotNull
    List<User> findAll();

    List<User> findAllByRole(Role role);

    Page<User> findAll(Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByUsernameStartingWithIgnoreCase(String name);

    List<User> findByUsernameStartingWithIgnoreCaseAndRole(String name, Role role);

    User findUserById(Long id);

    Page<User> findByUsernameStartingWithIgnoreCaseAndRole(String username, Role role, Pageable pageable);

    Page<User> findByUsernameStartingWithIgnoreCase(String username, Pageable pageable);

    Page<User> findAllByRole(Role role, Pageable pageable);
}
