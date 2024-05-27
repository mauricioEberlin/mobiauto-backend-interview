package com.mobiauto.backend.interview.service.impl;

import com.mobiauto.backend.interview.model.*;
import com.mobiauto.backend.interview.repository.OportunidadeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class OportunidadeServiceImplTest {
    @InjectMocks
    OportunidadeServiceImpl service;

    @Mock
    OportunidadeRepository repository;

    Oportunidade oportunidade;

    Oportunidade oportunidadeComId;

    List<Oportunidade> oportunidades = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        oportunidade = Oportunidade.builder()
                .status(Status.NOVO)
                .nomeCliente("Cliente")
                .emailCliente("cliente@email.com")
                .telefoneCliente("946474844")
                .marcaVeiculo("Nissan")
                .modeloVeiculo("Sentra")
                .versaoVeiculo("1.0")
                .anoVeiculo(2018)
                .lojaAssociada(Revenda.builder()
                        .id(1L)
                        .cnpj("432432556")
                        .nomeSocial("Revendedora Teste")
                        .build())
                .usuarioAssociado(Usuario.builder()
                        .id(1L)
                        .email("funcionario@email.com")
                        .cargo(Cargo.ASSISTENTE)
                        .build())
                .build();

        oportunidadeComId = Oportunidade.builder()
                .id(1L)
                .status(Status.CONCLUIDO)
                .nomeCliente("Cliente")
                .emailCliente("cliente@email.com")
                .telefoneCliente("946474844")
                .marcaVeiculo("Nissan")
                .modeloVeiculo("Sentra")
                .versaoVeiculo("1.0")
                .anoVeiculo(2018)
                .dataAtribuicao(LocalDate.now())
                .usuarioAssociado(Usuario.builder()
                        .id(1L)
                        .email("funcionario@email.com")
                        .cargo(Cargo.ASSISTENTE)
                        .build())
                .build();

        oportunidades.add(oportunidade);
    }

    @Test
    void findById() {
        when(repository.findById(oportunidade.getId())).thenReturn(Optional.of(oportunidade));

        Oportunidade oportunidadeRetornado = service.findById(oportunidade.getId());

        assertEquals(Optional.of(oportunidade).get(), oportunidadeRetornado);
        verify(repository).findById(oportunidade.getId());
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
        when(repository.findAll()).thenReturn(oportunidades);

        List<Oportunidade> listOportunidades = service.findAll();

        assertEquals(oportunidades, listOportunidades);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAllInRevenda() {
        when(repository.findAll().stream().filter(u -> Objects.equals((u.getLojaAssociada() != null) ? u.getLojaAssociada().getId() : null, oportunidade.getLojaAssociada().getId())).toList()).thenReturn(oportunidades);

        List<Oportunidade> listOportunidades = service.findAllInRevenda(oportunidade.getLojaAssociada().getId());

        assertEquals(oportunidades, listOportunidades);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void save() {
        when(repository.save(oportunidade)).thenReturn(oportunidade);

        Oportunidade oportunidadeRetornado = service.save(oportunidade);
        assertEquals(oportunidade, oportunidadeRetornado);

        verify(repository).save(oportunidade);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void saveErroIdInformado() {
        Error error = assertThrows(Error.class, () -> service.save(oportunidadeComId));
        assertEquals("Tentativa de passar ID em cadastro.", error.getMessage());

        verify(repository, never()).save(oportunidadeComId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void update1() {
        when(repository.findById(oportunidade.getId())).thenReturn(Optional.of(oportunidade));
        when(repository.save(oportunidade)).thenReturn(oportunidade);

        Oportunidade oportunidadeRetornado = service.update(oportunidade.getId(), oportunidade);
        assertEquals(Optional.of(oportunidade).get(), oportunidadeRetornado);

        verify(repository).save(oportunidade);
        verify(repository).findById(oportunidade.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void update2() {
        when(repository.findById(oportunidadeComId.getId())).thenReturn(Optional.of(oportunidadeComId));
        when(repository.save(oportunidadeComId)).thenReturn(oportunidadeComId);

        Oportunidade oportunidadeRetornado = service.update(oportunidadeComId.getId(), oportunidadeComId);
        assertEquals(Optional.of(oportunidadeComId).get(), oportunidadeRetornado);
    }

    @Test
    void delete() {
        service.delete(oportunidade.getId());

        verify(repository).deleteById(oportunidade.getId());
        verifyNoMoreInteractions(repository);
    }
}