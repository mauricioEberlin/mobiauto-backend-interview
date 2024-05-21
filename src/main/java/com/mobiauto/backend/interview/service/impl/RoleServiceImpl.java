package com.mobiauto.backend.interview.service.impl;

import com.mobiauto.backend.interview.model.Role;
import com.mobiauto.backend.interview.repository.RoleRepository;
import com.mobiauto.backend.interview.service.RoleService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository repository;

    public Role findById(Long id) {
        Optional<Role> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado. Id: " + id + ", Tipo: " + Role.class.getName(), obj));
    }

    public List<Role> findAll() {
        return repository.findAll();
    }

    public Role save(Role obj) {
        return repository.save(obj);
    }

}