package com.mobiauto.backend.interview.controller;

import com.mobiauto.backend.interview.config.NivelAcessoConfig;
import com.mobiauto.backend.interview.model.Usuario;
import com.mobiauto.backend.interview.security.UserPrincipal;
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
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios")
public class UsuarioController {

    private final UsuarioService service;

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @GetMapping
    public ResponseEntity<Object> buscarUsuarios() {
        return ResponseEntity.ok(service.findAll());
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarUsuarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @PostMapping("/cadastrar")
    public ResponseEntity<Object> cadastrarUsuario(@RequestBody @Validated Usuario usuario) {

        if(usuario.getId() != null){
            return ResponseEntity.badRequest().body("O parâmetro 'id' não pode ser informado em cadastro.");
        }

        if(service.findByEmail(usuario.getEmail()) != null){
            return ResponseEntity.badRequest().body("O email informado já possuí cadastro.");
        }

        return ResponseEntity.ok(service.save(usuario));
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_GERENTE + "')")
    @PostMapping("/cadastrar/revenda")
    public ResponseEntity<Object> cadastrarUsuarioEmRevenda(@RequestBody @Validated Usuario usuario, Authentication auth) {

        Usuario usuarioAutenticado = getUsuarioAutenticado(auth);

        if(usuarioAutenticado.getLojaAssociada() == null) {
            return ResponseEntity.badRequest().body("O usuário precisa ter uma loja associada para realizar o cadastro.");
        }

        //Associando a loja do usuário autenticado ao novo usuário.
        usuario.setLojaAssociada(usuarioAutenticado.getLojaAssociada());

        return ResponseEntity.ok(service.save(usuario));
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @PutMapping("/editar/{id}")
    public ResponseEntity<Object> editarUsuario(@PathVariable Long id, @RequestBody @Validated Usuario usuario) {
        return ResponseEntity.ok(service.update(id, usuario));
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_PROPRIETARIO + "')")
    @PutMapping("/editar/revenda/{id}")
    public ResponseEntity<Object> editarUsuarioEmRevenda(@PathVariable Long id, @RequestBody @Validated Usuario usuario, Authentication auth) {

        Usuario usuarioAutenticado = getUsuarioAutenticado(auth);
        Long idRevendaUsuarioAutenticado = usuarioAutenticado.getLojaAssociada().getId();
        Long idRevendaUsuarioNovo = service.findById(id).getLojaAssociada().getId();

        if(!Objects.equals(idRevendaUsuarioAutenticado, idRevendaUsuarioNovo)) {
            return ResponseEntity.badRequest().body("O usuário precisa ter uma loja associada correspondente a loja do usuário à editar.");
        }

        return ResponseEntity.ok(service.update(id, usuario));
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Object> deletarUsuarioPorId(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().body("Usuário deletado com sucesso.");
    }

    private Usuario getUsuarioAutenticado(Authentication auth) {

        String emailAutenticado = ((UserPrincipal) auth.getPrincipal()).getUsername();
        return service.findByEmail(emailAutenticado);
    }

}