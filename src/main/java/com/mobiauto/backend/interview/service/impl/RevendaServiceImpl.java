package com.mobiauto.backend.interview.service.impl;

import com.mobiauto.backend.interview.model.Revenda;
import com.mobiauto.backend.interview.repository.RevendaRepository;
import com.mobiauto.backend.interview.service.RevendaService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RevendaServiceImpl implements RevendaService {

    @Autowired
    private RevendaRepository repository;

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

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

}