package com.mobiauto.backend.interview.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "oportunidades")
public class Oportunidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "motivo_conclusao")
    private String motivoConclusao;

    @Column(name = "nome_cliente", nullable = false)
    private String nomeCliente;

    @Column(name = "email_cliente", nullable = false)
    private String emailCliente;

    @Column(name = "telefone_cliente", nullable = false)
    private String telefoneCliente;

    @Column(name = "marca_veiculo", nullable = false)
    private String marcaVeiculo;

    @Column(name = "modelo_veiculo", nullable = false)
    private String modeloVeiculo;

    @Column(name = "versao_veiculo", nullable = false)
    private String versaoVeiculo;

    @Column(name = "ano_veiculo", nullable = false)
    private Integer anoVeiculo;

    @Column(name = "data_atribuicao")
    private LocalDate dataAtribuicao;

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

    @ManyToOne(fetch = FetchType.EAGER)
    private Revenda lojaAssociada;

    @ManyToOne(fetch = FetchType.EAGER)
    private Usuario usuarioAssociado;

}