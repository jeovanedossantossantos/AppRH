package com.AppRH.AppRH.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.AppRH.AppRH.models.Candidato;
import com.AppRH.AppRH.models.Vaga;
import com.AppRH.AppRH.repository.CandidatoRepository;
import com.AppRH.AppRH.repository.VagaRepository;
import com.AppRH.AppRH.services.RedisService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CandidatoController {
    @Autowired
    private VagaRepository vr;

    @Autowired
    private CandidatoRepository cr;

    @Autowired
    private RedisService redis;

    @PostMapping("/subscribe/{id_vaga}")
    public ResponseEntity<?> subscribeVaga(@Valid @RequestBody Candidato candidato,
            BindingResult result, @PathVariable("id_vaga") long id_vaga) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Verifique os campos.");
        }
        Candidato cand = cr.findByRg(candidato.getRg());

        if (cand == null) {
            cand = cr.save(candidato);
        }
        Vaga vaga = vr.findByCodigo(id_vaga);

        if (vaga == null) {
            return ResponseEntity.badRequest().body("Vaga não encontrada!");
        }

        if (cand.getVagas() != null && cand.getVagas().contains(vaga)) {
            return ResponseEntity.badRequest().body("Candidato já está inscrito nesta vaga!");
        }

        if (cand.getVagas() == null) {
            List<Vaga> vagas = new ArrayList<Vaga>();
            vagas.add(vaga);

            cand.setVagas(vagas);
        } else {
            cand.getVagas().add(vaga);
        }

        Candidato candUpdate = cr.save(cand);

        redis.deleteAllVagasPages("candidato:vagas:rg=" + cand.getRg());
        redis.deleteAllVagasPages("vagas:" + id_vaga);
        return ResponseEntity.ok(candUpdate);
    }

    @GetMapping("/candidato/{rg}/vagas")
    public ResponseEntity<?> candidatoVagas(@PathVariable("rg") String rg,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {

        String key = "candidato:vagas:rg=" + rg + "&page=" + page + "&size=" + size;
        Pageable pageable = PageRequest.of(page, size);

        @SuppressWarnings("unchecked")
        Page<Object> cache = (Page<Object>) redis.getFromRedis(key, pageable);
        if (!cache.isEmpty()) {

            return ResponseEntity.ok(cache);
        }
        Candidato cand = cr.findByRg(rg);

        if (cand == null) {
            return ResponseEntity.badRequest().body("Candidato não encontrado!");
        }

        var pageResponse = cr.findPageBy(cand, pageable);
        redis.saveToRedisId(key, pageResponse);

        return ResponseEntity.ok(pageResponse);
    }

}
