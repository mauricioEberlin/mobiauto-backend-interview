package com.mobiauto.backend.interview.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private Cargo cargo;

    @ManyToOne(fetch = FetchType.EAGER)
    private Revenda lojaAssociada;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Oportunidade> oportunidades;

    @ManyToMany
    private List<Role> roles;
}