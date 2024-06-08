package projeto.projetoinformatico.password;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.service.PasswordService;
import projeto.projetoinformatico.utils.ModelMapperUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PasswordServiceTest {
    private PasswordService passwordService;
    private ModelMapperUtils mapperUtils;
    private  UserRepository userRepository;
    private  PasswordEncoder passwordEncoder;



    @BeforeEach
    void setUp() {
        mapperUtils = mock(ModelMapperUtils.class);
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        passwordService = new PasswordService(mapperUtils,userRepository,passwordEncoder);

    }

    @Test
    public void testUpdatePassword() {
        // Mocking user
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);

        // Mocking password encoder
        String newPassword = "newPassword";
        String encodedPassword = "encodedNewPassword";
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        // Mocking user repository save
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO convertedUserDTO = new UserDTO();
        when(mapperUtils.userToDTO(user, UserDTO.class)).thenReturn(convertedUserDTO);

        // Call the method under test
        UserDTO updatedUserDTO = passwordService.updatePassword(user, newPassword);

        // Verify that the password is encoded
        assertEquals(encodedPassword, user.getPassword());

        // Verify that the user object is saved
        verify(userRepository, times(1)).save(user);

        // Verify that the returned UserDTO is the same as the mocked conversion result
        assertSame(convertedUserDTO, updatedUserDTO);

    }


}
