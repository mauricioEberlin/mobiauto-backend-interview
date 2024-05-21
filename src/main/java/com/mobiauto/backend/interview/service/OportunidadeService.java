package com.mobiauto.backend.interview.service;

import com.mobiauto.backend.interview.model.Oportunidade;

import java.util.List;

public interface OportunidadeService {

    Oportunidade findById(Long id);

    List<Oportunidade> findAll();

    Oportunidade save(Oportunidade obj);

    void delete(Long id);
}