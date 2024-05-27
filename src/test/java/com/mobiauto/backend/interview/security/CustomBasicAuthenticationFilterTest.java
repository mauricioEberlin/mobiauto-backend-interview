package com.mobiauto.backend.interview.security;

import com.mobiauto.backend.interview.model.Cargo;
import com.mobiauto.backend.interview.model.Usuario;
import com.mobiauto.backend.interview.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomBasicAuthenticationFilterTest {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC_AUTENTICADO = "Basic dGVzdGVAZW1haWwuY29tOjEyMw";

    @InjectMocks
    CustomBasicAuthenticationFilter cbaf;

    @Mock
    HttpServletRequest request;

    @Mock
    UsuarioRepository repository;

    @Mock
    HttpServletResponse response;

    @Mock
    PrintWriter printWriter;

    @Mock
    FilterChain filterChain;

    Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("teste")
                .email("teste@email.com")
                .senha("123")
                .cargo(Cargo.ADMINISTRADOR)
                .build();
    }

    @Test
    void doFilterInternal() throws ServletException, IOException {
        when(request.getHeader(AUTHORIZATION)).thenReturn(BASIC_AUTENTICADO);
        when(repository.findByEmailFetchRoles(usuario.getEmail())).thenReturn(usuario);
        when(response.getWriter()).thenReturn(printWriter);
        cbaf.doFilterInternal(request, response, filterChain);

        when(request.getHeader(AUTHORIZATION)).thenReturn("Basic ");
        cbaf.doFilterInternal(request, response, filterChain);

        when(request.getHeader(AUTHORIZATION)).thenReturn(BASIC_AUTENTICADO);
        when(repository.findByEmailFetchRoles(usuario.getEmail())).thenReturn(null);
        cbaf.doFilterInternal(request, response, filterChain);
    }

}
