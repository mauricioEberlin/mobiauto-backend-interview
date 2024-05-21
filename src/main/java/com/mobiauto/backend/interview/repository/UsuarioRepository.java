package com.mobiauto.backend.interview.repository;

import com.mobiauto.backend.interview.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.roles WHERE u.email = :email")
    Usuario findByEmailFetchRoles(@Param("email") String email);
}