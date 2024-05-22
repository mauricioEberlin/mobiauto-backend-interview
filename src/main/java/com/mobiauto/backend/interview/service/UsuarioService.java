package com.mobiauto.backend.interview.service;

import com.mobiauto.backend.interview.model.Usuario;

import java.util.List;

public interface UsuarioService {

    Usuario findById(Long id);

    Usuario findByEmail(String email);

    List<Usuario> findAll();

    Usuario save(Usuario obj);

    Usuario update(Long id, Usuario obj);

    void delete(Long id);
}
