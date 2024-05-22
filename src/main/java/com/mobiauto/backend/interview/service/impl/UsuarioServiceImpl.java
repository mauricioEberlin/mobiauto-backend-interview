package com.mobiauto.backend.interview.service.impl;

import com.mobiauto.backend.interview.model.Usuario;
import com.mobiauto.backend.interview.repository.UsuarioRepository;
import com.mobiauto.backend.interview.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    private final UsuarioRepository repository;

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

        Usuario usuarioExistente = findByEmail(obj.getEmail());

        if (usuarioExistente != null){
            throw new Error("Esse e-mail já possuí cadastro.") ;
        }

        obj.setSenha(passwordEncoder().encode(obj.getSenha()));

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
        objBanco.setRoles(obj.getRoles());

        return repository.save(objBanco);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

}