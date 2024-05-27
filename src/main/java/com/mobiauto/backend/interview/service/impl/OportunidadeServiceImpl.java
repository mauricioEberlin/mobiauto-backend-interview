package com.mobiauto.backend.interview.service.impl;

import com.mobiauto.backend.interview.model.Oportunidade;
import com.mobiauto.backend.interview.model.Status;
import com.mobiauto.backend.interview.repository.OportunidadeRepository;
import com.mobiauto.backend.interview.service.OportunidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OportunidadeServiceImpl implements OportunidadeService {

    private final OportunidadeRepository repository;

    public Oportunidade findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Oportunidade> findAll() {
        return repository.findAll();
    }

    public List<Oportunidade> findAllInRevenda(Long idRevenda) {
        return repository.findAll().stream().filter(o -> Objects.equals((o.getLojaAssociada() != null) ? o.getLojaAssociada().getId() : null, idRevenda)).collect(Collectors.toList());
    }

    public Oportunidade save(Oportunidade obj) {

        if(obj.getId() != null){
            throw new Error("Tentativa de passar ID em cadastro.");
        }

        if(obj.getUsuarioAssociado() != null && obj.getDataAtribuicao() == null){
            obj.setDataAtribuicao(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
        }

        obj.setStatus(Status.NOVO);

        return repository.save(obj);
    }

    @Override
    public Oportunidade update(Long id, Oportunidade obj) {
        Oportunidade objBanco = findById(id);

        objBanco.setNomeCliente(obj.getNomeCliente());
        objBanco.setEmailCliente(obj.getEmailCliente());
        objBanco.setTelefoneCliente(obj.getTelefoneCliente());
        objBanco.setMarcaVeiculo(obj.getMarcaVeiculo());
        objBanco.setModeloVeiculo(obj.getModeloVeiculo());
        objBanco.setVersaoVeiculo(obj.getVersaoVeiculo());
        objBanco.setAnoVeiculo(obj.getAnoVeiculo());
        objBanco.setStatus(obj.getStatus());
        objBanco.setLojaAssociada(obj.getLojaAssociada());
        objBanco.setUsuarioAssociado(obj.getUsuarioAssociado());

        if(obj.getUsuarioAssociado() != null && obj.getDataAtribuicao() == null){
            objBanco.setDataAtribuicao(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
        }else{
            objBanco.setDataAtribuicao(obj.getDataAtribuicao());
        }

        if(obj.getStatus() == Status.CONCLUIDO && obj.getDataConclusao() == null && obj.getUsuarioAssociado() != null) {
            objBanco.setDataConclusao(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
            objBanco.setMotivoConclusao("Concluído por " + objBanco.getUsuarioAssociado().getNome() + ". Motivo: " + obj.getMotivoConclusao());
        }else{
            objBanco.setMotivoConclusao(obj.getMotivoConclusao());
        }

        return repository.save(objBanco);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}