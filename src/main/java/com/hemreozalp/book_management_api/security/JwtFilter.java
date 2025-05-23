package com.hemreozalp.book_management_api.security;

import com.hemreozalp.book_management_api.model.User;
import com.hemreozalp.book_management_api.repository.UserRepository;
import com.hemreozalp.book_management_api.util.JwtServcice;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtFilter implements Filter {

    private final JwtServcice jwtServcice;
    private final UserRepository userRepository;

    public JwtFilter(JwtServcice jwtServcice,
                     UserRepository userRepository) {
        this.jwtServcice = jwtServcice;
        this.userRepository = userRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException{

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);

            try {
                String username = jwtServcice.extractUsername(token);
                Optional<User> optUser = userRepository.findByUsername(username);

                if (optUser.isPresent() && jwtServcice.validateToken(token, optUser.get())){
                    User user = optUser.get();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, null);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
