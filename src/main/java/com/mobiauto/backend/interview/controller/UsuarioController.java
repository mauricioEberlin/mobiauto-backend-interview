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

        Usuario usuarioBuscado = service.findById(id);

        return (usuarioBuscado != null) ? ResponseEntity.ok(usuarioBuscado) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @PostMapping("/cadastrar")
    public ResponseEntity<Object> cadastrarUsuario(@RequestBody @Validated Usuario usuario) {

        ResponseEntity<Object> erro = validarUsuario(true, usuario, null);
        return (erro == null) ? ResponseEntity.ok(service.save(usuario)) : erro;
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_GERENTE + "')")
    @PostMapping("/cadastrar/revenda")
    public ResponseEntity<Object> cadastrarUsuarioEmRevenda(@RequestBody @Validated Usuario usuario, Authentication auth) {

        ResponseEntity<Object> erro = validarUsuario(true, usuario, null);
        Usuario usuarioAutenticado = getUsuarioAutenticado(auth);

        if(usuarioAutenticado.getLojaAssociada() == null) {
            erro = ResponseEntity.badRequest().body("O usuário precisa ter uma loja associada para realizar o cadastro.");
        }

        //Associando a loja do usuário autenticado ao novo usuário.
        usuario.setLojaAssociada(usuarioAutenticado.getLojaAssociada());

        return (erro == null) ? ResponseEntity.ok(service.save(usuario)) : erro;
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @PutMapping("/editar/{id}")
    public ResponseEntity<Object> editarUsuario(@PathVariable Long id, @RequestBody @Validated Usuario usuario) {

        ResponseEntity<Object> erro = validarUsuario(false, usuario, id);
        return (erro == null) ? ResponseEntity.ok(service.update(id, usuario)) : erro;
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_PROPRIETARIO + "')")
    @PutMapping("/editar/revenda/{id}")
    public ResponseEntity<Object> editarUsuarioEmRevenda(@PathVariable Long id, @RequestBody @Validated Usuario usuario, Authentication auth) {

        ResponseEntity<Object> erro = validarUsuario(false, usuario, id);
        Usuario usuarioAutenticado = getUsuarioAutenticado(auth);
        Long idRevendaUsuarioAutenticado = usuarioAutenticado.getLojaAssociada().getId();
        Long idRevendaUsuarioNovo = service.findById(id).getLojaAssociada().getId();

        if(!Objects.equals(idRevendaUsuarioAutenticado, idRevendaUsuarioNovo)) {
            erro = ResponseEntity.badRequest().body("O usuário precisa ter uma loja associada correspondente a loja do usuário à editar.");
        }

        return (erro == null) ? ResponseEntity.ok(service.update(id, usuario)) : erro;
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Object> deletarUsuarioPorId(@PathVariable Long id) {
        boolean existe = service.findById(id) != null;
        service.delete(id);

        return (existe) ? ResponseEntity.ok().body("Usuário deletado com sucesso.") : ResponseEntity.notFound().build();
    }

    private Usuario getUsuarioAutenticado(Authentication auth) {

        String emailAutenticado = ((UserPrincipal) auth.getPrincipal()).getUsername();
        return service.findByEmail(emailAutenticado);
    }

    private ResponseEntity<Object> validarUsuario(boolean isCadastro, Usuario usuario, Long id) {
        ResponseEntity<Object> erro = null;
        Usuario usuarioPorEmail = service.findByEmail(usuario.getEmail());

        // Validando se o usuário está tentando passar o 'id' no corpo da requisição.
        if (usuario.getId() != null) {
            erro = ResponseEntity.badRequest().body("O parâmetro 'id' não pode ser informado em cadastro.");
        }
        if(isCadastro) {
            // Validando se o email já existe no banco de dados ao cadastrar.
            if (usuarioPorEmail != null) {
                erro = ResponseEntity.badRequest().body("O email informado já possuí cadastro.");
            }
        }else{
            // Validando se o email já existe no banco de dados e desconsiderando o próprio.
            if(usuarioPorEmail != null && !Objects.equals(id, usuarioPorEmail.getId())){
                erro = ResponseEntity.badRequest().body("O email informado já possuí cadastro.");
            }
            // Validando se o usuário está tentando alterar outro que não existe.
            if(service.findById(id) == null){
                erro = ResponseEntity.notFound().build();
            }
        }
        return erro;
    }

}