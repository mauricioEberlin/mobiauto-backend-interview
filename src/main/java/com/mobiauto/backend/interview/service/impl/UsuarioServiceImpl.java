package com.mobiauto.backend.interview.service.impl;

import com.mobiauto.backend.interview.model.Usuario;
import com.mobiauto.backend.interview.repository.UsuarioRepository;
import com.mobiauto.backend.interview.service.UsuarioService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private UsuarioRepository repository;

    public Usuario findById(Long id) {
        Optional<Usuario> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado. Id: " + id + ", Tipo: " + Usuario.class.getName(), obj));
    }

    public List<Usuario> findAll() {
        return repository.findAll();
    }

    public Usuario save(Usuario obj) {

        Usuario usuarioExistente = repository.findByEmail(obj.getEmail());

        if (usuarioExistente != null){
            throw new Error("Usuário já existe.") ;
        }

        obj.setSenha(passwordEncoder().encode(obj.getSenha()));

        return repository.save(obj);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

}