package com.mobiauto.backend.interview.service;

import com.mobiauto.backend.interview.model.Role;

import java.util.List;

public interface RoleService {

    Role findById(Long id);

    List<Role> findAll();

    Role save(Role obj);

}