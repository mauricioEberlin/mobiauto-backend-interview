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
@Table(name = "revendas")
public class Revenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Column(name = "nome_social", nullable = false)
    private String nomeSocial;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Oportunidade> oportunidades;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Usuario> funcionarios;

}