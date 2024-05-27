package com.mobiauto.backend.interview.controller;

import com.mobiauto.backend.interview.model.Revenda;
import com.mobiauto.backend.interview.service.RevendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RevendaControllerTest {

    @InjectMocks
    RevendaController controller;

    @Mock
    RevendaService service;

    Revenda revenda;

    Revenda revendaComId;

    List<Revenda> revendas = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        revenda = Revenda.builder()
                .cnpj("414214214122")
                .nomeSocial("Revendedora Teste")
                .build();

        revendaComId = Revenda.builder()
                .id(1L)
                .cnpj("414214214122")
                .nomeSocial("Revendedora Teste")
                .build();

        revendas.add(revenda);
    }

    @Test
    void buscarRevendas() {
        when(service.findAll()).thenReturn(revendas);

        ResponseEntity<Object> responseRevendas = controller.buscarRevendas();

        assertEquals(ResponseEntity.ok(revendas), responseRevendas);
        verify(service).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    void buscarRevendaPorId() {
        when(service.findById(revenda.getId())).thenReturn(revenda);

        ResponseEntity<Object> responseRevenda = controller.buscarRevendaPorId(revenda.getId());

        assertEquals(ResponseEntity.ok(revenda), responseRevenda);

        verify(service).findById(revenda.getId());
        verifyNoMoreInteractions(service);

        when(service.findById(revenda.getId())).thenReturn(null);
        responseRevenda = controller.buscarRevendaPorId(revenda.getId());

        assertEquals(ResponseEntity.notFound().build(), responseRevenda);
    }

    @Test
    void cadastrarRevenda() {
        when(service.save(revenda)).thenReturn(revenda);

        ResponseEntity<Object> responseRevenda = controller.cadastrarRevenda(revenda);
        assertEquals(ResponseEntity.ok(revenda), responseRevenda);
        verify(service).findByCnpj(revenda.getCnpj());
        verify(service).save(revenda);
        verifyNoMoreInteractions(service);

        responseRevenda = controller.cadastrarRevenda(revendaComId);
        assertEquals(ResponseEntity.badRequest().body("O parâmetro 'id' não pode ser informado no cadastro."), responseRevenda);
        verify(service, never()).save(revendaComId);

        when(service.findByCnpj(revenda.getCnpj())).thenReturn(revenda);
        responseRevenda = controller.cadastrarRevenda(revenda);
        assertEquals(ResponseEntity.badRequest().body("O CNPJ informado já possuí cadastro."), responseRevenda);
        verify(service, never()).save(revendaComId);
    }

    @Test
    void editarRevenda() {
        when(service.findById(revenda.getId())).thenReturn(revenda);
        when(service.update(revenda.getId(), revenda)).thenReturn(revenda);

        ResponseEntity<Object> responseRevenda = controller.editarRevenda(revenda.getId(), revenda);

        assertEquals(ResponseEntity.ok(revenda), responseRevenda);
        verify(service).findByCnpj(revenda.getCnpj());
        verify(service).findById(revenda.getId());
        verify(service).update(revenda.getId(), revenda);
        verifyNoMoreInteractions(service);

        when(service.findById(revenda.getId())).thenReturn(revenda);
        when(service.findByCnpj(revenda.getCnpj())).thenReturn(revendaComId);
        responseRevenda = controller.editarRevenda(revenda.getId(), revenda);
        assertEquals(ResponseEntity.badRequest().body("O CNPJ informado já possuí cadastro."), responseRevenda);

        when(service.findById(revenda.getId())).thenReturn(null);
        responseRevenda = controller.editarRevenda(revenda.getId(), revenda);
        assertEquals(ResponseEntity.notFound().build(), responseRevenda);

    }

    @Test
    void deletarRevendaPorId() {
        when(service.findById(revenda.getId())).thenReturn(revenda);

        ResponseEntity<Object> responseRevenda = controller.deletarRevendaPorId(revenda.getId());

        assertEquals(ResponseEntity.ok().body("Revenda deletada com sucesso."), responseRevenda);
        verify(service).findById(revenda.getId());
        verify(service).delete(revenda.getId());
        verifyNoMoreInteractions(service);

        when(service.findById(revenda.getId())).thenReturn(null);
        responseRevenda = controller.deletarRevendaPorId(revenda.getId());
        assertEquals(ResponseEntity.notFound().build(), responseRevenda);

    }

}