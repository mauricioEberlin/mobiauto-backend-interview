package com.mobiauto.backend.interview.service.impl;

import com.mobiauto.backend.interview.model.Cargo;
import com.mobiauto.backend.interview.model.Revenda;
import com.mobiauto.backend.interview.model.Usuario;
import com.mobiauto.backend.interview.repository.RoleRepository;
import com.mobiauto.backend.interview.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceImplTest {

    @InjectMocks
    UsuarioServiceImpl service;

    @Mock
    UsuarioRepository repository;

    @Mock
    RoleRepository roleRepository;
    Usuario usuario;

    Usuario usuarioComId;

    List<Usuario> usuarios = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        usuario = Usuario.builder()
                .nome("teste")
                .email("teste@email.com")
                .senha("123")
                .cargo(Cargo.ADMINISTRADOR)
                .lojaAssociada(Revenda.builder()
                        .id(1L)
                        .cnpj("432432556")
                        .nomeSocial("Revendedora Teste")
                        .build())
                .build();

        usuarioComId = Usuario.builder()
                .id(1L)
                .nome("teste")
                .email("teste@email.com")
                .senha("123")
                .cargo(Cargo.ADMINISTRADOR)
                .lojaAssociada(Revenda.builder()
                        .id(1L)
                        .cnpj("432432556")
                        .nomeSocial("Revendedora Teste")
                        .build())
                .build();

        usuarios.add(usuarioComId);
    }

    @Test
    void findById() {
        when(repository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        Usuario usuarioRetornado = service.findById(usuario.getId());

        assertEquals(Optional.of(usuario).get(), usuarioRetornado);
        verify(repository).findById(usuario.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findByIdNull() {
        assertNull(service.findById(0L));

        verify(repository).findById(0L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findByIdEmail() {
        when(repository.findByEmail(usuario.getEmail())).thenReturn(usuario);

        Usuario usuarioRetornado = service.findByEmail(usuario.getEmail());

        assertEquals(Optional.of(usuario).get(), usuarioRetornado);
        verify(repository).findByEmail(usuario.getEmail());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAll() {
        when(repository.findAll()).thenReturn(usuarios);

        List<Usuario> listUsuarios = service.findAll();

        assertEquals(usuarios, listUsuarios);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAllInRevenda() {
        when(repository.findAll().stream().filter(u -> Objects.equals((u.getLojaAssociada() != null) ? u.getLojaAssociada().getId() : null, usuario.getLojaAssociada().getId())).toList()).thenReturn(usuarios);

        List<Usuario> listUsuarios = service.findAllInRevenda(usuario.getLojaAssociada().getId());

        assertEquals(usuarios, listUsuarios);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void save() {
        when(repository.save(usuario)).thenReturn(usuario);

        Usuario usuarioRetornado = service.save(usuario);
        assertEquals(usuario, usuarioRetornado);

        verify(repository).save(usuario);
        verify(roleRepository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void saveErroIdInformado() {
        Error error = assertThrows(Error.class, () -> service.save(usuarioComId));
        assertEquals("Tentativa de passar ID em cadastro.", error.getMessage());

        verify(repository, never()).save(usuarioComId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void update() {
        when(repository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(repository.save(usuario)).thenReturn(usuario);

        Usuario usuarioRetornado = service.update(usuario.getId(), usuario);
        assertEquals(Optional.of(usuario).get(), usuarioRetornado);

        verify(repository).save(usuario);
        verify(repository).findById(usuario.getId());
        verify(roleRepository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void updateErroIdInformado() {
        Error error = assertThrows(Error.class, () -> service.update(usuarioComId.getId(), usuarioComId));
        assertEquals("Tentativa de passar ID ao editar.", error.getMessage());

        verify(repository, never()).save(usuarioComId);
        verify(repository, never()).findById(usuarioComId.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void delete() {
        service.delete(usuario.getId());

        verify(repository).deleteById(usuario.getId());
        verifyNoMoreInteractions(repository);
    }

}