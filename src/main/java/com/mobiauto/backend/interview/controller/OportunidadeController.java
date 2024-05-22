package com.mobiauto.backend.interview.controller;

import com.mobiauto.backend.interview.config.NivelAcessoConfig;
import com.mobiauto.backend.interview.model.Oportunidade;
import com.mobiauto.backend.interview.model.Usuario;
import com.mobiauto.backend.interview.security.UserPrincipal;
import com.mobiauto.backend.interview.service.OportunidadeService;
import com.mobiauto.backend.interview.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/oportunidades")
@RequiredArgsConstructor
@Tag(name = "Oportunidade")
public class OportunidadeController {

    private final OportunidadeService service;

    private final UsuarioService usuarioService;

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @GetMapping
    public ResponseEntity<Object> buscarOportunidades() {
        return ResponseEntity.ok(service.findAll());
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarOportunidadePorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @PostMapping("/cadastrar")
    public ResponseEntity<Object> cadastrarOportunidade(@RequestBody @Validated Oportunidade oportunidade) {
        return ResponseEntity.ok(service.save(oportunidade));
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @PutMapping("/editar/{id}")
    public ResponseEntity<Object> editarOportunidade(@PathVariable Long id, @RequestBody @Validated Oportunidade oportunidade) {
        return ResponseEntity.ok(service.update(id, oportunidade));
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_GERENTE + "')")
    @PutMapping("/editar/revenda/{id}")
    public ResponseEntity<Object> editarOportunidadeEmRevenda(@PathVariable Long id, @RequestBody @Validated Oportunidade oportunidade, Authentication auth) {

        Usuario usuarioAutenticado = getUsuarioAutenticado(auth);
        Long idRevendaUsuarioAutenticado = usuarioAutenticado.getLojaAssociada().getId();
        Long idRevendaUsuarioNovo = service.findById(id).getLojaAssociada().getId();

        if(!Objects.equals(idRevendaUsuarioNovo, idRevendaUsuarioAutenticado)){
            return ResponseEntity.badRequest().body("O usuário deve ter uma loja associada correspondente a loja da oportunidade à editar");
        }

        return ResponseEntity.ok(service.update(id, oportunidade));
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ASSISTENTE + "')")
    @PutMapping("/editar/associado/{id}")
    public ResponseEntity<Object> editarOportunidadeAssociada(@PathVariable Long id, @RequestBody @Validated Oportunidade oportunidade, Authentication auth) {

        Usuario usuarioAutenticado = getUsuarioAutenticado(auth);
        Long idUsuarioAutenticado = usuarioAutenticado.getId();
        Long idUsuarioAssociadoOportunidade = service.findById(id).getUsuarioAssociado().getId();
        Long idUsuarioAssociadoOportunidadeNovo = oportunidade.getUsuarioAssociado().getId();

        if(!Objects.equals(idUsuarioAutenticado, idUsuarioAssociadoOportunidade)){
            return ResponseEntity.badRequest().body("O usuário pode editar somente as oportunidades associadas à ele mesmo.");
        }

        if(!Objects.equals(idUsuarioAssociadoOportunidade, idUsuarioAssociadoOportunidadeNovo)){
            return ResponseEntity.badRequest().body("O usuário não pode transferir a oportunidade para outro usuário");
        }

        return ResponseEntity.ok(service.update(id, oportunidade));
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarOportunidadePorId(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Usuario getUsuarioAutenticado(Authentication auth) {

        String emailAutenticado = ((UserPrincipal) auth.getPrincipal()).getUsername();
        return usuarioService.findByEmail(emailAutenticado);
    }

}