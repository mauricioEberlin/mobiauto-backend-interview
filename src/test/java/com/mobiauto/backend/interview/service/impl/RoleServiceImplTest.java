package com.mobiauto.backend.interview.service.impl;

import com.mobiauto.backend.interview.model.Role;
import com.mobiauto.backend.interview.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    @InjectMocks
    RoleServiceImpl service;

    @Mock
    RoleRepository repository;

    Role role;

    List<Role> roles = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        role = Role.builder()
                .id(0L)
                .name("ROLE_ADMINISTRADOR")
                .build();

        roles.add(role);
    }

    @Test
    void findById() {
        when(repository.findById(role.getId())).thenReturn(Optional.of(role));

        Role roleRetornado = service.findById(role.getId());

        assertEquals(Optional.of(role).get(), roleRetornado);
        verify(repository).findById(role.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findByIdNull() {
        assertNull(service.findById(0L));

        verify(repository).findById(0L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAll() {
        when(repository.findAll()).thenReturn(roles);

        List<Role> listRoles = service.findAll();

        assertEquals(roles, listRoles);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void save() {
        when(repository.save(role)).thenReturn(role);

        Role roleRetornado = service.save(role);
        assertEquals(role, roleRetornado);

        verify(repository).save(role);
        verifyNoMoreInteractions(repository);
    }

}