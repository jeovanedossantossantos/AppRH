package com.AppRH.AppRH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.AppRH.AppRH.models.Candidato;
import com.AppRH.AppRH.models.Vaga;

public interface CandidatoRepository extends CrudRepository<Candidato, Long> {

    Iterable<Candidato> findByVagas(Vaga vagas);

    Candidato findByRg(String rg);

    Candidato findById(long id);

    // Query para a busca
    @Query(value = "select u from Candidato u where u.nome like %?1%")
    List<Candidato> findByNomesCandidatos(String nome);
}
