package com.mobiauto.backend.interview.service.impl;

import com.mobiauto.backend.interview.model.Role;
import com.mobiauto.backend.interview.repository.RoleRepository;
import com.mobiauto.backend.interview.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repository;

    public Role findById(Long id) {
        Optional<Role> obj = repository.findById(id);
        return obj.orElse(null);
    }

    public List<Role> findAll() {
        return repository.findAll();
    }

    public Role save(Role obj) {
        return repository.save(obj);
    }

}