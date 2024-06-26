package projeto.projetoinformatico.service;


import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.dtos.UserDTO;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.exceptions.Exception.InvalidPasswordException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgrade;
import projeto.projetoinformatico.model.roleUpgrade.RoleUpgradeRepository;
import projeto.projetoinformatico.requests.*;
import projeto.projetoinformatico.responses.AuthenticationResponse;
import projeto.projetoinformatico.responses.JwtAuthenticationResponse;
import projeto.projetoinformatico.config.jwt.JWTServiceImpl;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;
import projeto.projetoinformatico.utils.ModelMapperUtils;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final RoleUpgradeRepository roleUpgradeRepository;
    private final ModelMapperUtils mapperUtils;



    @CacheEvict(value = "userCache")
    public UserDTO signup(SignUpRequest signUpRequest){
        // Check if the username is already taken
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new InvalidParamsRequestException("Username already exists");
        }

        // Check if the email is already taken
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new InvalidParamsRequestException("Email already registered");
        }
        //Create the user with the data
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setUsername(signUpRequest.getUsername());
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);


        return convertUserToDTO(user);
    }


    public AuthenticationResponse signin(SignInRequest signInRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    signInRequest.getUsername(), signInRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new InvalidPasswordException("Invalid Password");
        }
        // Retrieve the user by username
        var user = userRepository.findByUsername(signInRequest.getUsername());
        if (user == null) {
            throw new NotFoundException("User not found");
        }


        // Validate the provided password against the stored hashed password
        boolean valid = passwordEncoder.matches(signInRequest.getPassword(), user.getPassword());
        // Throw exception if the password is invalid
        if (!valid) {
            throw new InvalidPasswordException("Invalid Password");
        }

        // Generate JWT tokens
        var jwt = JWTServiceImpl.generateToken(user);
        var refreshToken = JWTServiceImpl.generateRefreshToken(new HashMap<>(), user);

        // Create an instance of AuthenticationResponse and set the UserResponse
        AuthenticationResponse response = new AuthenticationResponse();
        response.setAccessToken(jwt);
        response.setRefreshToken(refreshToken);
        UserDTO userDTO = convertUserToDTO(user);
        RoleUpgrade roleUpgrade = roleUpgradeRepository.findByUserId(user.getId());
        if (roleUpgrade != null) {
            userDTO.setRoleUpgrade(roleUpgrade);
        }
        response.setUser(userDTO);

        return response;
    }



    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest){
        String userUsername = JWTServiceImpl.extractUsername(refreshTokenRequest.getToken());

        User user = userRepository.findByUsername(userUsername);
        if (user == null){
            throw new NotFoundException("User not found");
        }
        if(JWTServiceImpl.isTokenValid(refreshTokenRequest.getToken(), user)){
            var jwt = JWTServiceImpl.generateToken(user);
            var refreshToken = JWTServiceImpl.generateRefreshToken(new HashMap<>(), user);


            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshToken);
            return jwtAuthenticationResponse;
        }
        return null;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    private UserDTO convertUserToDTO(User user) {
        return mapperUtils.userToDTO(user, UserDTO.class);
    }

}
