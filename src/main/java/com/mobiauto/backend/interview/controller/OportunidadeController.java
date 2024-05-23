package com.mobiauto.backend.interview.controller;

import com.mobiauto.backend.interview.config.NivelAcessoConfig;
import com.mobiauto.backend.interview.model.Cargo;
import com.mobiauto.backend.interview.model.Oportunidade;
import com.mobiauto.backend.interview.model.Revenda;
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

import java.time.Instant;
import java.util.Comparator;
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

        Oportunidade oportunidadeBuscada = service.findById(id);

        return (oportunidadeBuscada != null) ? ResponseEntity.ok(oportunidadeBuscada) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @PostMapping("/cadastrar")
    public ResponseEntity<Object> cadastrarOportunidade(@RequestBody @Validated Oportunidade oportunidade) {

        ResponseEntity<Object> erro = validarOportunidade(true, oportunidade, null);

        Usuario usuarioAssociado = oportunidade.getUsuarioAssociado();

        if(usuarioAssociado != null){
            usuarioAssociado.setHorarioUltimaOportunidade(Instant.now());
        }

        return (erro == null) ? ResponseEntity.ok(service.save(oportunidade)) : erro;
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ASSISTENTE + "')")
    @PostMapping("/atender")
    public ResponseEntity<Object> atenderOportunidade(@RequestBody @Validated Oportunidade oportunidade, Authentication auth) {

        ResponseEntity<Object> erro = validarOportunidade(true, oportunidade, null);
        Revenda revendaUsuarioAutenticado = getUsuarioAutenticado(auth).getLojaAssociada();


        if (revendaUsuarioAutenticado == null){
            erro = ResponseEntity.badRequest().body("O usuário deve ter uma loja associada para atender uma oportunidade.");
        }else{
            oportunidade.setLojaAssociada(revendaUsuarioAutenticado);

            Usuario usuarioOcioso = usuarioService.findAllInRevenda(revendaUsuarioAutenticado.getId()).stream()
                    .filter(u -> u.getCargo() == Cargo.ASSISTENTE)
                    .min(Comparator.comparing(Usuario::getHorarioUltimaOportunidade))
                    .orElse(null);

            if (usuarioOcioso != null) {
                usuarioOcioso.setHorarioUltimaOportunidade(Instant.now());
                oportunidade.setUsuarioAssociado(usuarioOcioso);
            }
        }

        return (erro == null) ? ResponseEntity.ok(service.save(oportunidade)) : erro;
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @PutMapping("/editar/{id}")
    public ResponseEntity<Object> editarOportunidade(@PathVariable Long id, @RequestBody @Validated Oportunidade oportunidade) {

        ResponseEntity<Object> erro = validarOportunidade(false, oportunidade, id);
        return (erro == null) ? ResponseEntity.ok(service.update(id, oportunidade)) : erro;
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_GERENTE + "')")
    @PutMapping("/editar/revenda/{id}")
    public ResponseEntity<Object> editarOportunidadeEmRevenda(@PathVariable Long id, @RequestBody @Validated Oportunidade oportunidade, Authentication auth) {

        ResponseEntity<Object> erro = validarOportunidade(false, oportunidade, id);
        Usuario usuarioAutenticado = getUsuarioAutenticado(auth);
        Long idRevendaUsuarioAutenticado = (usuarioAutenticado.getLojaAssociada() != null) ? usuarioAutenticado.getLojaAssociada().getId() : null;

        if(!Objects.equals(id, idRevendaUsuarioAutenticado)){
            erro = ResponseEntity.badRequest().body("O usuário deve ter uma loja associada correspondente o da loja da oportunidade à editar");
        }

        return (erro == null) ? ResponseEntity.ok(service.update(id, oportunidade)) : erro;
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ASSISTENTE + "')")
    @PutMapping("/editar/associado/{id}")
    public ResponseEntity<Object> editarOportunidadeAssociada(@PathVariable Long id, @RequestBody @Validated Oportunidade oportunidade, Authentication auth) {

        ResponseEntity<Object> erro = validarOportunidade(false, oportunidade, id);
        if(erro == null) {
            Long idUsuarioAutenticado = getUsuarioAutenticado(auth).getId();
            Long idUsuarioOportunidade = (service.findById(id).getUsuarioAssociado() != null) ? service.findById(id).getUsuarioAssociado().getId() : null;
            Long idUsuarioOportunidadeNovo = (oportunidade.getUsuarioAssociado() != null) ? oportunidade.getUsuarioAssociado().getId() : null;

            if(!Objects.equals(idUsuarioAutenticado, idUsuarioOportunidade)){
                erro = ResponseEntity.badRequest().body("O assistente pode editar somente as oportunidades associadas à ele mesmo.");
            }

            if(!Objects.equals(idUsuarioOportunidade, idUsuarioOportunidadeNovo)){
                erro =  ResponseEntity.badRequest().body("O assistente não pode transferir a oportunidade para outro usuário");
            }
        }

        return (erro == null) ? ResponseEntity.ok(service.update(id, oportunidade)) : erro;
    }

    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Object> deletarOportunidadePorId(@PathVariable Long id) {
        boolean existe = service.findById(id) != null;
        service.delete(id);

        return (existe) ? ResponseEntity.ok().body("Oportunidade deletada com sucesso.") : ResponseEntity.notFound().build();
    }

    private Usuario getUsuarioAutenticado(Authentication auth) {

        String emailAutenticado = ((UserPrincipal) auth.getPrincipal()).getUsername();
        return usuarioService.findByEmail(emailAutenticado);
    }

    private ResponseEntity<Object> validarOportunidade(boolean isCadastro, Oportunidade oportunidade, Long id) {
        ResponseEntity<Object> erro = null;

        // Validando se o usuário está tentando passar o 'id' no corpo da requisição.
        if (oportunidade.getId() != null) {
            erro = ResponseEntity.badRequest().body("O parâmetro 'id' não pode ser informado em cadastro.");
        }
        if(!isCadastro){
            // Validando se o usuário está tentando alterar uma oportunidade que não existe.
            if(service.findById(id) == null){
                erro = ResponseEntity.notFound().build();
            }
        }
        return erro;
    }

}