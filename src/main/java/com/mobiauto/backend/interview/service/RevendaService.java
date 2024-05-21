package com.mobiauto.backend.interview.service;

import com.mobiauto.backend.interview.model.Revenda;

import java.util.List;

public interface RevendaService {

    Revenda findById(Long id);

    List<Revenda> findAll();

    Revenda save(Revenda obj);

    void delete(Long id);
}
