package com.mobiauto.backend.interview.controller;

import com.mobiauto.backend.interview.model.*;
import com.mobiauto.backend.interview.security.UserPrincipal;
import com.mobiauto.backend.interview.service.OportunidadeService;
import com.mobiauto.backend.interview.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OportunidadeControllerTest {

    @InjectMocks
    OportunidadeController controller;

    @Mock
    OportunidadeService service;

    @Mock
    UsuarioService usuarioService;

    Oportunidade oportunidade, oportunidade2;

    Oportunidade oportunidadeComId;

    Usuario usuario, usuario2;

    Revenda revenda, revenda2;

    UserPrincipal userPrincipal;

    List<Oportunidade> oportunidades = new ArrayList<>();

    List<Usuario> usuarios = new ArrayList<>();

    List<Role> roles = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        roles.add(Role.builder()
                .id(1L)
                .name("NIVEL_ADMINISTRADOR")
                .build());

        revenda = Revenda.builder()
                .id(1L)
                .cnpj("432432556")
                .nomeSocial("Revendedora Teste")
                .build();

        revenda2 = Revenda.builder()
                .id(2L)
                .cnpj("432432557")
                .nomeSocial("Revendedora Teste")
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .nome("teste")
                .email("teste@email.com")
                .senha("123")
                .cargo(Cargo.ASSISTENTE)
                .lojaAssociada(revenda)
                .roles(roles)
                .horarioUltimaOportunidade(Instant.MIN)
                .build();

        usuario2 = Usuario.builder()
                .id(2L)
                .nome("teste2")
                .email("teste2@email.com")
                .senha("123")
                .cargo(Cargo.ASSISTENTE)
                .lojaAssociada(revenda2)
                .roles(roles)
                .horarioUltimaOportunidade(Instant.MIN)
                .build();

        oportunidade = Oportunidade.builder()
                .status(Status.NOVO)
                .nomeCliente("Cliente")
                .emailCliente("cliente@email.com")
                .telefoneCliente("946474844")
                .marcaVeiculo("Nissan")
                .modeloVeiculo("Sentra")
                .versaoVeiculo("1.0")
                .anoVeiculo(2018)
                .lojaAssociada(revenda)
                .usuarioAssociado(usuario)
                .build();

        oportunidade2 = Oportunidade.builder()
                .status(Status.NOVO)
                .nomeCliente("Cliente")
                .emailCliente("cliente@email.com")
                .telefoneCliente("946474844")
                .marcaVeiculo("Nissan")
                .modeloVeiculo("Sentra")
                .versaoVeiculo("1.0")
                .anoVeiculo(2018)
                .lojaAssociada(revenda)
                .usuarioAssociado(usuario2)
                .build();

        oportunidadeComId = Oportunidade.builder()
                .id(1L)
                .status(Status.NOVO)
                .nomeCliente("Cliente")
                .emailCliente("cliente@email.com")
                .telefoneCliente("946474844")
                .marcaVeiculo("Nissan")
                .modeloVeiculo("Sentra")
                .versaoVeiculo("1.0")
                .anoVeiculo(2018)
                .lojaAssociada(revenda)
                .usuarioAssociado(usuario)
                .build();

        userPrincipal = UserPrincipal.create(usuario);
        oportunidades.add(oportunidadeComId);
        usuarios.add(usuario);
    }

    @Test
    void buscarOportunidades() {
        when(service.findAll()).thenReturn(oportunidades);

        ResponseEntity<Object> responseOportunidades = controller.buscarOportunidades();

        assertEquals(ResponseEntity.ok(oportunidades), responseOportunidades);
        verify(service).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    void buscarOportunidadesDaRevenda() {
        when(usuarioService.findByEmail(userPrincipal.getUsername())).thenReturn(usuario);
        when(service.findAllInRevenda(usuario.getLojaAssociada().getId())).thenReturn(oportunidades);

        ResponseEntity<Object> responseOportunidades = controller.buscarOportunidadesDaRevenda(userPrincipal);

        assertEquals(ResponseEntity.ok(oportunidades), responseOportunidades);
        verify(service).findAllInRevenda(usuario.getLojaAssociada().getId());
        verifyNoMoreInteractions(service);
        verify(usuarioService).findByEmail(userPrincipal.getUsername());
        verifyNoMoreInteractions(usuarioService);

        usuario.setLojaAssociada(null);
        responseOportunidades = controller.buscarOportunidadesDaRevenda(userPrincipal);

        assertEquals(ResponseEntity.badRequest().body("O usuário precisa ter uma loja associada para realizar a busca."), responseOportunidades);
        verify(service, never()).findAllInRevenda(null);
    }

    @Test
    void buscarOportunidadePorId() {
        when(service.findById(oportunidade.getId())).thenReturn(oportunidade);

        ResponseEntity<Object> responseOportunidade = controller.buscarOportunidadePorId(oportunidade.getId());

        assertEquals(ResponseEntity.ok(oportunidade), responseOportunidade);

        verify(service).findById(oportunidade.getId());
        verifyNoMoreInteractions(service);

        when(service.findById(oportunidade.getId())).thenReturn(null);
        responseOportunidade = controller.buscarOportunidadePorId(oportunidade.getId());

        assertEquals(ResponseEntity.notFound().build(), responseOportunidade);
    }

    @Test
    void cadastrarOportunidade() {
        when(service.save(oportunidade)).thenReturn(oportunidade);

        ResponseEntity<Object> responseOportunidade = controller.cadastrarOportunidade(oportunidade);
        assertEquals(ResponseEntity.ok(oportunidade), responseOportunidade);
        verify(service).save(oportunidade);
        verifyNoMoreInteractions(service);

        responseOportunidade = controller.cadastrarOportunidade(oportunidadeComId);
        assertEquals(ResponseEntity.badRequest().body("O parâmetro 'id' não pode ser informado em cadastro."), responseOportunidade);
        verify(service, never()).save(oportunidadeComId);
    }

    @Test
    void atenderOportunidade() {
        when(usuarioService.findByEmail(userPrincipal.getUsername())).thenReturn(usuario);
        when(usuarioService.findAllInRevenda(usuario.getId())).thenReturn(usuarios);
        when(service.save(oportunidade)).thenReturn(oportunidade);

        ResponseEntity<Object> responseOportunidade = controller.atenderOportunidade(oportunidade, userPrincipal);
        assertEquals(ResponseEntity.ok(oportunidade), responseOportunidade);
        verify(service).save(oportunidade);
        verifyNoMoreInteractions(service);

        usuario.setLojaAssociada(null);
        when(usuarioService.findByEmail(userPrincipal.getUsername())).thenReturn(usuario);
        responseOportunidade = controller.atenderOportunidade(oportunidade, userPrincipal);
        assertEquals(ResponseEntity.badRequest().body("O usuário deve ter uma loja associada para atender uma oportunidade."), responseOportunidade);

    }
    @Test
    void editarOportunidade() {
        when(service.findById(oportunidade.getId())).thenReturn(oportunidade);
        when(service.update(oportunidade.getId(), oportunidade)).thenReturn(oportunidade);

        ResponseEntity<Object> responseOportunidade = controller.editarOportunidade(oportunidade.getId(), oportunidade);

        assertEquals(ResponseEntity.ok(oportunidade), responseOportunidade);
        verify(service).findById(oportunidade.getId());
        verify(service).update(oportunidade.getId(), oportunidade);
        verifyNoMoreInteractions(service);

        when(service.findById(oportunidade.getId())).thenReturn(null);
        responseOportunidade = controller.editarOportunidade(oportunidade.getId(), oportunidade);
        assertEquals(ResponseEntity.notFound().build(), responseOportunidade);

        oportunidade.setStatus(Status.CONCLUIDO);
        responseOportunidade = controller.editarOportunidade(oportunidade.getId(), oportunidade);
        assertEquals(ResponseEntity.badRequest().body("O motivo de conclusão deve ser informado ao concluir a oportunidade."), responseOportunidade);
    }

    @Test
    void editarOportunidadeEmRevenda() {
        when(service.findById(oportunidadeComId.getId())).thenReturn(oportunidade);
        when(usuarioService.findByEmail(usuario.getEmail())).thenReturn(usuario);
        when(service.findById(oportunidadeComId.getId())).thenReturn(oportunidade);
        when(service.update(oportunidadeComId.getId(), oportunidade)).thenReturn(oportunidadeComId);

        ResponseEntity<Object> responseOportunidade = controller.editarOportunidadeEmRevenda(oportunidadeComId.getId(), oportunidade, userPrincipal);

        assertEquals(ResponseEntity.ok(oportunidadeComId), responseOportunidade);

        verify(service, times(2)).findById(oportunidadeComId.getId());
        verify(service).update(oportunidadeComId.getId(), oportunidade);
        verifyNoMoreInteractions(service);

        verify(usuarioService).findByEmail(usuario.getEmail());
        verifyNoMoreInteractions(usuarioService);

        when(usuarioService.findByEmail(usuario.getEmail())).thenReturn(usuario2);
        when(service.findById(oportunidade.getId())).thenReturn(oportunidade).thenReturn(oportunidade);
        responseOportunidade = controller.editarOportunidadeEmRevenda(oportunidade.getId(), oportunidade, userPrincipal);
        assertEquals(ResponseEntity.badRequest().body("O usuário deve ter uma loja associada correspondente o da loja da oportunidade à editar."), responseOportunidade);
    }

    @Test
    void editarOportunidadeAssociada() {
        when(service.findById(oportunidadeComId.getId())).thenReturn(oportunidade);
        when(usuarioService.findByEmail(usuario.getEmail())).thenReturn(usuario);
        when(service.update(oportunidadeComId.getId(), oportunidade)).thenReturn(oportunidadeComId);

        ResponseEntity<Object> responseOportunidade = controller.editarOportunidadeAssociada(oportunidadeComId.getId(), oportunidade, userPrincipal);

        assertEquals(ResponseEntity.ok(oportunidadeComId), responseOportunidade);
        verify(service, times(2)).findById(oportunidadeComId.getId());
        verify(service).update(oportunidadeComId.getId(), oportunidade);
        verifyNoMoreInteractions(service);

        when(service.findById(oportunidadeComId.getId())).thenReturn(oportunidade);
        when(usuarioService.findByEmail(usuario.getEmail())).thenReturn(usuario2);
        responseOportunidade = controller.editarOportunidadeAssociada(oportunidadeComId.getId(), oportunidade, userPrincipal);
        assertEquals(ResponseEntity.badRequest().body("O assistente pode editar somente as oportunidades associadas à ele mesmo."), responseOportunidade);

        when(service.findById(oportunidadeComId.getId())).thenReturn(oportunidade2);
        responseOportunidade = controller.editarOportunidadeAssociada(oportunidadeComId.getId(), oportunidade, userPrincipal);
        assertEquals(ResponseEntity.badRequest().body("O assistente não pode transferir a oportunidade para outro usuário."), responseOportunidade);

    }

    @Test
    void deletarOportunidadePorId() {
        when(service.findById(oportunidade.getId())).thenReturn(oportunidade);

        ResponseEntity<Object> responseOportunidade = controller.deletarOportunidadePorId(oportunidade.getId());

        assertEquals(ResponseEntity.ok().body("Oportunidade deletada com sucesso."), responseOportunidade);
        verify(service).findById(oportunidade.getId());
        verify(service).delete(oportunidade.getId());
        verifyNoMoreInteractions(service);

        when(service.findById(oportunidade.getId())).thenReturn(null);
        responseOportunidade = controller.deletarOportunidadePorId(oportunidade.getId());
        assertEquals(ResponseEntity.notFound().build(), responseOportunidade);


    }

}
