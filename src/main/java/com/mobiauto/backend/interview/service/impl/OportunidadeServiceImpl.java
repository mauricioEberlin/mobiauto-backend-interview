package com.mobiauto.backend.interview.service.impl;

import com.mobiauto.backend.interview.model.Oportunidade;
import com.mobiauto.backend.interview.repository.OportunidadeRepository;
import com.mobiauto.backend.interview.service.OportunidadeService;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OportunidadeServiceImpl implements OportunidadeService {

    private final OportunidadeRepository repository;

    public Oportunidade findById(Long id) {
        Optional<Oportunidade> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado. Id: " + id + ", Tipo: " + Oportunidade.class.getName(), obj));
    }

    public List<Oportunidade> findAll() {
        return repository.findAll();
    }

    public Oportunidade save(Oportunidade obj) {
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
        objBanco.setDataAtribuicao(obj.getDataAtribuicao());
        objBanco.setDataConclusao(obj.getDataConclusao());
        objBanco.setMotivoConclusao(obj.getMotivoConclusao());
        objBanco.setLojaAssociada(obj.getLojaAssociada());
        objBanco.setUsuarioAssociado(obj.getUsuarioAssociado());

        return repository.save(objBanco);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

}