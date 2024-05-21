package com.mobiauto.backend.interview.security;

import com.mobiauto.backend.interview.model.Usuario;
import com.mobiauto.backend.interview.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class CustomBasicAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC = "Basic ";
    private final UsuarioRepository repository;

    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(isBasicAuthentication(request)){

            response.setHeader("Content-Type", "text/xml; charset=UTF-8");

            String rawCredentials = decodeBase64(getHeader(request).replace(BASIC, ""));

            if (rawCredentials.length() < 3) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Usuário deve fornecer e-mail e senha para autenticação.");
                return;
            }

            String[] credentials = rawCredentials.split(":");

            String username = credentials[0];
            String password = credentials[1];

            Usuario usuario = repository.findByEmailFetchRoles(username);

            if (usuario == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("O usuário não existe.");
                return;
            }

            boolean valid = checkPassword(usuario.getSenha(), password);

            if (!valid) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("A senha está incorreta.");
                return;
            }

            setAuthentication(usuario);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(Usuario usuario) {
        Authentication authentication = createAuthenticationToken(usuario);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Authentication createAuthenticationToken(Usuario usuario) {
        UserPrincipal userPrincipal = UserPrincipal.create(usuario);
        return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
    }

    private boolean checkPassword(String userPassword, String loginPassword) {
        return passwordEncoder().matches(loginPassword, userPassword);
    }

    private String decodeBase64(String base64) {
        byte[] decodeBytes = Base64.getDecoder().decode(base64);
        return new String(decodeBytes);
    }

    private boolean isBasicAuthentication(HttpServletRequest request) {
        String header = getHeader(request);
        return header != null && header.startsWith(BASIC);
    }

    private String getHeader(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }

}