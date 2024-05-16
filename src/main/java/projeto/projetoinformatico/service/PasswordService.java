package projeto.projetoinformatico.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.dtos.LayerDTO;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.model.layers.Layer;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.utils.ModelMapperUtils;

@Service
public class PasswordService {

    private final ModelMapperUtils mapperUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordService(ModelMapperUtils mapperUtils, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.mapperUtils = mapperUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public UserDTO updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        UserDTO convUser = convertUserToDTO(user);
        userRepository.save(user);
        return convUser;
    }

    public boolean validatePassword(User user, String oldPassword){
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }
    private UserDTO convertUserToDTO(User user) {
        return mapperUtils.userToDTO(user, UserDTO.class);
    }

}
