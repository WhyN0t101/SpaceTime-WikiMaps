package projeto.projetoinformatico.config.jwt;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import projeto.projetoinformatico.exceptions.Exception.JwtExpiredException;
import projeto.projetoinformatico.service.JWT.JWTServiceImpl;
import projeto.projetoinformatico.service.UserService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;




    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if(StringUtils.isEmpty(authHeader) || !org.apache.commons.lang3.StringUtils.startsWith(authHeader, "Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            if (JWTServiceImpl.isTokenExpired(jwt)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("JWT Token is expired.");
                response.getWriter().flush();
            }
            username = JWTServiceImpl.extractUsername(jwt);
            if(StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = userService.userDetailsService().loadUserByUsername(username);

                if(JWTServiceImpl.isTokenValid(jwt, userDetails)){
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    securityContext.setAuthentication(token);
                    SecurityContextHolder.setContext(securityContext);
                }
                else{
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("JWT Token is invalid.");
                    response.getWriter().flush();
                    return;
                }
            }
            filterChain.doFilter(request, response);
        } catch (JwtExpiredException e) {
            /*response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("JWT Token is expired.");
            response.getWriter().flush();

             */
            throw new JwtExpiredException("JWT Token is expired");
        }
    }

}