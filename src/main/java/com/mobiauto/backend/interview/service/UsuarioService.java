package com.mobiauto.backend.interview.service;

import com.mobiauto.backend.interview.model.Usuario;

import java.util.List;

public interface UsuarioService {

    Usuario findById(Long id);

    List<Usuario> findAll();

    Usuario save(Usuario obj);

    void delete(Long id);
}
