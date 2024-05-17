package projeto.projetoinformatico.config.security;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import projeto.projetoinformatico.config.jwt.JwtAuthenticationFilter;
import projeto.projetoinformatico.exceptions.JWTAuthenticationEntryPoint;
import projeto.projetoinformatico.service.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private UserService userService;



    @Value("${spring.security.debug:false}")
    boolean securityDebug;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(request -> request
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/layers").permitAll()
                    .requestMatchers("/api/layers/id/{id}").permitAll()
                    .requestMatchers("/api/layers/{id}").permitAll()
                    .requestMatchers("/api/layers/search").permitAll()
                    .requestMatchers("/api/sparql").permitAll()
                    .requestMatchers("/api/users/{id}/layers").permitAll()
                    .requestMatchers("/api/users/id/{id}").permitAll()
                    .anyRequest().authenticated()
            )
                    /*
                    .requestMatchers("/api/search").permitAll()
                    .requestMatchers("/api/search/time").permitAll()
                    .requestMatchers("/api/sparql").permitAll()
                    //.requestMatchers("/api/admin").hasAnyAuthority(Role.ADMIN.name())
                    //.requestMatchers("/api").hasAnyAuthority(Role.USER.name())
                    .requestMatchers("/api/items/{itemId}").permitAll()
                    .requestMatchers("/api/properties/{propertyId}").permitAll()
                    .requestMatchers("/api/data/geolocation/{itemId}").permitAll()
                    .requestMatchers("/api/data/property-values/{item_id}/{property_id}").permitAll()
                    .requestMatchers("/api/layers").permitAll()
                    .requestMatchers("/api/layers/{id}").permitAll()
                    .requestMatchers("/api/layers/id/{id}").permitAll()
                    .requestMatchers("/api/layers/create").authenticated()
                    .requestMatchers("/api/users/{id}/layers").permitAll()
                    .requestMatchers("/api/user").permitAll()
                    /*.requestMatchers("/api/users/{username}").permitAll()
                    .requestMatchers("/api/users").permitAll()
                    .requestMatchers("/api/users/role/{role}").permitAll()
                    .requestMatchers("/api/users/id/{id}").permitAll()

            )*/
            //.httpBasic(Customizer.withDefaults())
            .exceptionHandling(customizer -> customizer.authenticationEntryPoint(authenticationEntryPoint()))
            //.authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class
            );
        return http.build();
    }



    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.debug(securityDebug)
                .ignoring()
                .requestMatchers("/css/**", "/js/**", "/img/**", "/lib/**", "/favicon.ico");
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService.userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
        throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new JWTAuthenticationEntryPoint();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }


}