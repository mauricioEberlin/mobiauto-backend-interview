package com.mobiauto.backend.interview.controller;

import com.mobiauto.backend.interview.config.NivelAcessoConfig;
import com.mobiauto.backend.interview.model.Revenda;
import com.mobiauto.backend.interview.service.RevendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
@RequestMapping(value = "/api/revendas", produces = {"application/json"})
@RequiredArgsConstructor
@Tag(name = "Revendas")
@ApiResponses( value = {
        @ApiResponse(responseCode = "200", description = "Operação executada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Algum parâmetro foi inválido."),
        @ApiResponse(responseCode = "401", description = "O campo 'usuário' ou 'senha' na autenticação está incorreta."),
        @ApiResponse(responseCode = "403", description = "O usuário não tem permissão para usar essa rota."),
        @ApiResponse(responseCode = "404", description = "A revenda não existe.")
})
public class RevendaController {

    private final RevendaService service;

    @Operation(summary = "Realiza a busca de todas as revendedoras cadastradas.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @GetMapping
    public ResponseEntity<Object> buscarRevendas() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Realiza a busca de uma revendedora pelo seu ID.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarRevendaPorId(@PathVariable Long id) {
        Revenda revendaBuscada = service.findById(id);

        return (revendaBuscada != null) ? ResponseEntity.ok(revendaBuscada) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Realiza o cadastro de uma nova revendedora.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PostMapping("/cadastrar")
    public ResponseEntity<Object> cadastrarRevenda(@RequestBody @Validated Revenda revenda) {
        ResponseEntity<Object> erro = validarRevenda(true, revenda, null);

        return (erro == null) ? ResponseEntity.ok(service.save(revenda)) : erro;
    }

    @Operation(summary = "Realiza a edição da revendedora.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PutMapping("/editar/{id}")
    public ResponseEntity<Object> editarRevenda(@PathVariable Long id, @RequestBody @Validated Revenda revenda) {
        ResponseEntity<Object> erro = validarRevenda(false, revenda, id);

        return (erro == null) ? ResponseEntity.ok(service.update(id, revenda)) : erro;
    }

    @Operation(summary = "Deleta a revendedora pelo seu ID.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Object> deletarRevendaPorId(@PathVariable Long id) {
        boolean existe = service.findById(id) != null;
        service.delete(id);

        return (existe) ? ResponseEntity.ok().body("Revenda deletada com sucesso.") : ResponseEntity.notFound().build();
    }

    private ResponseEntity<Object> validarRevenda(boolean isCadastro, Revenda revenda, Long id) {
        ResponseEntity<Object> erro = null;

        Revenda revendaPorCnpj = service.findByCnpj(revenda.getCnpj());

        // Validando se o usuário está tentando passar o 'id' no corpo da requisição.
        if(revenda.getId() != null) {
            erro = ResponseEntity.badRequest().body("O parâmetro 'id' não pode ser informado no cadastro.");
        }

        if(isCadastro){
            // Validando se o CNPJ já existe no banco de dados ao cadastrar.
            if(revendaPorCnpj != null) {
                erro = ResponseEntity.badRequest().body("O CNPJ informado já possuí cadastro.");
            }
        }else{
            // Validando se o CNPJ já existe no banco de dados e desconsiderando o próprio.
            if(revendaPorCnpj != null && !Objects.equals(id, revendaPorCnpj.getId())){
                erro = ResponseEntity.badRequest().body("O CNPJ informado já possuí cadastro.");
            }
            // Validando se o usuário está tentando alterar uma revenda que não existe.
            if(service.findById(id) == null){
                erro = ResponseEntity.notFound().build();
            }
        }

        return erro;
    }

}