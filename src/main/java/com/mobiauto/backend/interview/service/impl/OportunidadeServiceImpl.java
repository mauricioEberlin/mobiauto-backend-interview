package com.mobiauto.backend.interview.service.impl;

import com.mobiauto.backend.interview.model.Oportunidade;
import com.mobiauto.backend.interview.repository.OportunidadeRepository;
import com.mobiauto.backend.interview.service.OportunidadeService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OportunidadeServiceImpl implements OportunidadeService {

    @Autowired
    private OportunidadeRepository repository;

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

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

}