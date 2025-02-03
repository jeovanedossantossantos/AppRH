package com.AppRH.AppRH.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.AppRH.AppRH.models.Candidato;
import com.AppRH.AppRH.models.Vaga;

public interface CandidatoRepository extends JpaRepository<Candidato, Long> {

    @Query("SELECT v FROM Vaga v WHERE :candidato MEMBER OF v.candidatos")
    Page<Vaga> findPageBy(@Param("candidato") Candidato candidato, Pageable pageable);

    Iterable<Candidato> findByVagas(Vaga vagas);

    Candidato findByRg(String rg);

    Candidato findById(long id);

    // Query para a busca
    @Query(value = "select u from Candidato u where u.nome like %?1%")
    List<Candidato> findByNomesCandidatos(String nome);
}
