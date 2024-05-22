package com.mobiauto.backend.interview.service.impl;

import com.mobiauto.backend.interview.model.Cargo;
import com.mobiauto.backend.interview.model.Usuario;
import com.mobiauto.backend.interview.repository.RoleRepository;
import com.mobiauto.backend.interview.repository.UsuarioRepository;
import com.mobiauto.backend.interview.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    private final UsuarioRepository repository;

    private final RoleRepository roleRepository;

    public Usuario findById(Long id) {
        Optional<Usuario> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado. Id: " + id + ", Tipo: " + Usuario.class.getName(), obj));
    }

    public Usuario findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<Usuario> findAll() {
        return repository.findAll();
    }

    public Usuario save(Usuario obj) {

        if (findByEmail(obj.getEmail()) != null){
            throw new Error("Esse e-mail já possuí cadastro.");
        }

        if(obj.getId() != null){
            throw new Error("Tentativa de passar ID em cadastro");
        }

        obj.setSenha(passwordEncoder().encode(obj.getSenha()));

        Map<Cargo, Integer> map = new HashMap<>();
        map.put(Cargo.ADMINISTRADOR, 0);
        map.put(Cargo.PROPRIETARIO, 1);
        map.put(Cargo.GERENTE, 2);
        map.put(Cargo.ASSISTENTE, 3);

        obj.setRoles(roleRepository.findAll().subList(map.get(obj.getCargo()), 4));

        return repository.save(obj);
    }

    public Usuario update(Long id, Usuario obj) {
        Usuario objBanco = findById(id);

        if(findByEmail(obj.getEmail()) == null){
            objBanco.setEmail((obj.getEmail()));
        }

        objBanco.setNome(obj.getNome());
        objBanco.setSenha(obj.getSenha());
        objBanco.setLojaAssociada(obj.getLojaAssociada());
        objBanco.setOportunidades(obj.getOportunidades());
        objBanco.setCargo(obj.getCargo());
        objBanco.setRoles(obj.getRoles());

        return repository.save(objBanco);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

}