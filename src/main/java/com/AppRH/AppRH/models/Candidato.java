package com.AppRH.AppRH.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Candidato implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String rg;

    @NotEmpty
    private String nome;

    @NotEmpty
    private String email;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(name = "candidatos_vagas", joinColumns = @JoinColumn(name = "candidato_id"), inverseJoinColumns = @JoinColumn(name = "vaga_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
            "candidato_id", "vaga_id" }))
    private List<Vaga> vagas;

    public long getId() {
        return id;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Vaga> getVagas() {
        return vagas;
    }

    public void setVagas(List<Vaga> vagas) {
        this.vagas = vagas;
    }

}
