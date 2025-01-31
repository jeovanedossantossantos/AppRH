package com.AppRH.AppRH.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.CrudRepository;
import com.AppRH.AppRH.models.Vaga;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
    Page<Vaga> findPageBy(Pageable pageable);
    // CrudRepository<Vaga, Long> {

    Vaga findByCodigo(long condigo);

    List<Vaga> findByNome(String nome);

    @Query(value = "select u from Vaga u where u.nome like %?1%")
    List<Vaga> findByNomesVaga(String nome);
}
