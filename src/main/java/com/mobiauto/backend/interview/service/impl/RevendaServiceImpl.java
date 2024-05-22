package com.mobiauto.backend.interview.service.impl;

import com.mobiauto.backend.interview.model.Revenda;
import com.mobiauto.backend.interview.repository.RevendaRepository;
import com.mobiauto.backend.interview.service.RevendaService;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RevendaServiceImpl implements RevendaService {

    private final RevendaRepository repository;

    public Revenda findById(Long id) {
        Optional<Revenda> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado. Id: " + id + ", Tipo: " + Revenda.class.getName(), obj));
    }

    public List<Revenda> findAll() {
        return repository.findAll();
    }

    public Revenda save(Revenda obj) {
        return repository.save(obj);
    }

    public Revenda update(Long id, Revenda obj) {
        Revenda objBanco = findById(id);

        objBanco.setCnpj(obj.getCnpj());
        objBanco.setFuncionarios(obj.getFuncionarios());
        objBanco.setOportunidades(obj.getOportunidades());
        objBanco.setNomeSocial(obj.getNomeSocial());

        return repository.save(objBanco);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

}