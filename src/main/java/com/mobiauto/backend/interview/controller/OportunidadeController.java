package com.mobiauto.backend.interview.controller;

import com.mobiauto.backend.interview.config.NivelAcessoConfig;
import com.mobiauto.backend.interview.model.*;
import com.mobiauto.backend.interview.security.UserPrincipal;
import com.mobiauto.backend.interview.service.OportunidadeService;
import com.mobiauto.backend.interview.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/oportunidades", produces = {"application/json"})
@RequiredArgsConstructor
@Tag(name = "Oportunidades")
@ApiResponses( value = {
        @ApiResponse(responseCode = "200", description = "Operação executada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Algum parâmetro foi inválido."),
        @ApiResponse(responseCode = "401", description = "O campo 'usuário' ou 'senha' na autenticação está incorreta."),
        @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para usar essa rota."),
        @ApiResponse(responseCode = "404", description = "A oportunidade não existe.")
})
public class OportunidadeController {

    private final OportunidadeService service;

    private final UsuarioService usuarioService;

    @Operation(summary = "Realiza a busca de todas as oportunidades cadastradas.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @GetMapping
    public ResponseEntity<Object> buscarOportunidades() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Realiza a busca de todas as oportunidades associadas à mesma revendedora do usuário autenticado.", description = NivelAcessoConfig.NIVEL_ASSISTENTE)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ASSISTENTE + "')")
    @GetMapping("/revenda")
    public ResponseEntity<Object> buscarOportunidadesDaRevenda(Authentication auth) {

        Revenda revendaUsuarioAutenticado = getUsuarioAutenticado(auth).getLojaAssociada();

        return (revendaUsuarioAutenticado != null) ?
                ResponseEntity.ok(service.findAllInRevenda(revendaUsuarioAutenticado.getId())) :
                ResponseEntity.badRequest().body("O usuário precisa ter uma loja associada para realizar a busca.");
    }

    @Operation(summary = "Realiza a busca de uma oportunidade a partir de seu ID.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarOportunidadePorId(@PathVariable Long id) {

        Oportunidade oportunidadeBuscada = service.findById(id);

        return (oportunidadeBuscada != null) ? ResponseEntity.ok(oportunidadeBuscada) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Realiza o cadastro de uma oportunidade.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @PostMapping("/cadastrar")
    public ResponseEntity<Object> cadastrarOportunidade(@RequestBody @Validated Oportunidade oportunidade) {

        ResponseEntity<Object> erro = validarOportunidade(true, oportunidade, null);
        Usuario usuarioAssociado = oportunidade.getUsuarioAssociado();

        if(usuarioAssociado != null){
            usuarioAssociado.setHorarioUltimaOportunidade(Instant.now());
            oportunidade.setDataAtribuicao(LocalDate.now(ZoneId.of("BET")));
        }

        return (erro == null) ? ResponseEntity.ok(service.save(oportunidade)) : erro;
    }

    @Operation(summary = "Realiza o cadastro de uma oportunidade na mesma revendedora do usuário autenticado. Com validação e distribuição da oportunidade ao usuário de cargo ASSISTENTE com mais ociosidade", description = NivelAcessoConfig.NIVEL_ASSISTENTE)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ASSISTENTE + "')")
    @PostMapping("/atender")
    public ResponseEntity<Object> atenderOportunidade(@RequestBody @Validated Oportunidade oportunidade, Authentication auth) {

        ResponseEntity<Object> erro = validarOportunidade(true, oportunidade, null);
        Revenda revendaUsuarioAutenticado = getUsuarioAutenticado(auth).getLojaAssociada();

        if (revendaUsuarioAutenticado == null){
            erro = ResponseEntity.badRequest().body("O usuário deve ter uma loja associada para atender uma oportunidade.");
        }else{
            oportunidade.setLojaAssociada(revendaUsuarioAutenticado);

            // Buscando o usuário da revenda que ficou mais tempo sem oportunidade nova.
            Usuario usuarioOcioso = usuarioService.findAllInRevenda(revendaUsuarioAutenticado.getId()).stream()
                    .filter(u -> u.getCargo() == Cargo.ASSISTENTE)
                    .min(Comparator.comparing(Usuario::getHorarioUltimaOportunidade))
                    .orElse(null);

            // Atribuindo uma oportunidade ao usuário, se existente.
            if (usuarioOcioso != null) {
                usuarioOcioso.setHorarioUltimaOportunidade(Instant.now());
                oportunidade.setUsuarioAssociado(usuarioOcioso);
            }
        }

        return (erro == null) ? ResponseEntity.ok(service.save(oportunidade)) : erro;
    }

    @Operation(summary = "Realiza a edição de uma oportunidade a partir de seu ID.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @PutMapping("/editar/{id}")
    public ResponseEntity<Object> editarOportunidade(@PathVariable Long id, @RequestBody @Validated Oportunidade oportunidade) {

        ResponseEntity<Object> erro = validarOportunidade(false, oportunidade, id);

        return (erro == null) ? ResponseEntity.ok(service.update(id, oportunidade)) : erro;
    }

    @Operation(summary = "Realiza a edição de uma oportunidade a partir de seu ID. Com validação se o usuário autenticado está na mesma revendedora da oportunidade.", description = NivelAcessoConfig.NIVEL_GERENTE)
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

    @Operation(summary = "Realiza a edição de uma oportunidade a partir de seu ID. Com validação se o usuário autenticado está relacionado a oportunidade.", description = NivelAcessoConfig.NIVEL_ASSISTENTE)
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

    @Operation(summary = "Deleta a oportunidade a partir do seu ID.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
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

            //Validando se houve a tentativa de concluír uma oportunidade sem motivo de conclusão.
            if(oportunidade.getStatus() == Status.CONCLUIDO && oportunidade.getMotivoConclusao() == null) {
                erro = ResponseEntity.badRequest().body("O motivo de conclusão deve ser informado ao concluir a oportunidade.");
            }
        }
        return erro;
    }

}