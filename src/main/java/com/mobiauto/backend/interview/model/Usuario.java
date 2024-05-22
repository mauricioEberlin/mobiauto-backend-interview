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

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senha;

    @ManyToOne(fetch = FetchType.EAGER)
    private Revenda lojaAssociada;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Oportunidade> oportunidades;

    @ManyToMany
    private List<Role> roles;
}