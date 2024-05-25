package com.mobiauto.backend.interview.service.impl;

import com.mobiauto.backend.interview.model.Role;
import com.mobiauto.backend.interview.model.Usuario;
import com.mobiauto.backend.interview.repository.RoleRepository;
import com.mobiauto.backend.interview.repository.UsuarioRepository;
import com.mobiauto.backend.interview.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;

    private final RoleRepository roleRepository;

    public Usuario findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Usuario findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<Usuario> findAll() {
        return repository.findAll();
    }

    public List<Usuario> findAllInRevenda(Long idRevenda) {
        return repository.findAll().stream().filter(u -> Objects.equals((u.getLojaAssociada() != null) ? u.getLojaAssociada().getId() : null, idRevenda)).collect(Collectors.toList());
    }

    public Usuario save(Usuario obj) {

        List<Role> roles = roleRepository.findAll();

        if (findByEmail(obj.getEmail()) != null){
            throw new Error("Esse e-mail já possuí cadastro.");
        }

        if(obj.getId() != null){
            throw new Error("Tentativa de passar ID em cadastro");
        }

        obj.setSenha(passwordEncoder().encode(obj.getSenha()));
        obj.setHorarioUltimaOportunidade(Instant.MIN);
        obj.setRoles(roles.subList(obj.getCargo().ordinal(), roles.size()));

        return repository.save(obj);
    }

    public Usuario update(Long id, Usuario obj) {
        Usuario objBanco = findById(id);

        if(obj.getId() != null){
            throw new Error("Tentativa de passar ID ao editar.");
        }

        obj.setSenha(passwordEncoder().encode(obj.getSenha()));
        obj.setRoles(roleRepository.findAll().subList(obj.getCargo().ordinal(), 4));

        objBanco.setEmail((obj.getEmail()));
        objBanco.setNome(obj.getNome());
        objBanco.setLojaAssociada(obj.getLojaAssociada());
        objBanco.setCargo(obj.getCargo());

        return repository.save(objBanco);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    private PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}