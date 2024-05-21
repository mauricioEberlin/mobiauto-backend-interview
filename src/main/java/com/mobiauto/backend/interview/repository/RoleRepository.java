package com.mobiauto.backend.interview.repository;

import com.mobiauto.backend.interview.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {}