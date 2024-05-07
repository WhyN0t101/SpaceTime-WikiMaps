package projeto.projetoinformatico.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projeto.projetoinformatico.exceptions.Exception.InvalidParamsRequestException;
import projeto.projetoinformatico.exceptions.Exception.NotFoundException;
import projeto.projetoinformatico.requests.*;
import projeto.projetoinformatico.responses.AuthenticationResponse;
import projeto.projetoinformatico.responses.JwtAuthenticationResponse;
import projeto.projetoinformatico.responses.UserResponse;
import projeto.projetoinformatico.service.JWT.JWTServiceImpl;
import projeto.projetoinformatico.model.users.Role;
import projeto.projetoinformatico.model.users.User;
import projeto.projetoinformatico.model.users.UserRepository;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    //private final JWTServiceImpl jwtService;



    public UserResponse signup(SignUpRequest signUpRequest){
        // Check if the username is already taken
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new InvalidParamsRequestException("Username already exists");
        }

        // Check if the email is already taken
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new InvalidParamsRequestException("Email already registered");
        }

        // Create a new user
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setUsername(signUpRequest.getUsername());
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);

        // Create a UserResponse object and populate it with user data
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setRole(user.getRole());
        userResponse.setEmail(user.getEmail());
        userResponse.setEnabled(user.isEnabled());
        // Set other fields as needed

        return userResponse;
    }


    public AuthenticationResponse signin(SignInRequest signInRequest){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                signInRequest.getUsername(), signInRequest.getPassword()));

        var user = userRepository.findByUsername(signInRequest.getUsername());
        if (user == null){
            throw new NotFoundException("User not found");
        }

        var jwt = JWTServiceImpl.generateToken(user);
        var refreshToken = JWTServiceImpl.generateRefreshToken(new HashMap<>(), user);

        // Create an instance of UserResponse
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setRole(user.getRole());
        userResponse.setEmail(user.getEmail());
        userResponse.setEnabled(user.isEnabled());
        // Set other fields as needed

        // Create an instance of AuthenticationResponse and set the UserResponse
        AuthenticationResponse response = new AuthenticationResponse();
        response.setAccessToken(jwt);
        response.setRefreshToken(refreshToken);
        response.setUser(userResponse);

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

            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
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

}
