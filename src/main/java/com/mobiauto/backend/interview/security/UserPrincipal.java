package com.mobiauto.backend.interview.security;

import com.mobiauto.backend.interview.model.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class UserPrincipal {
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    private UserPrincipal(Usuario usuario){
        this.username = usuario.getEmail();
        this.password = usuario.getSenha();

        this.authorities = usuario.getRoles().stream().map(role -> {
            return new SimpleGrantedAuthority("ROLE_".concat(role.getName()));
        }).collect(Collectors.toList());
    }

    public static UserPrincipal create(Usuario usuario){
        return new UserPrincipal(usuario);
    }

}