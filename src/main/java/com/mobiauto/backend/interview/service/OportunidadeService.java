package com.mobiauto.backend.interview.service;

import com.mobiauto.backend.interview.model.Oportunidade;

import java.util.List;

public interface OportunidadeService {

    Oportunidade findById(Long id);

    List<Oportunidade> findAll();

    List<Oportunidade> findAllInRevenda(Long idRevenda);
    Oportunidade save(Oportunidade obj);

    Oportunidade update(Long id, Oportunidade obj);

    void delete(Long id);
}