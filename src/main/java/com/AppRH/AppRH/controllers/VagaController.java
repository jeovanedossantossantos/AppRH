package com.AppRH.AppRH.controllers;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.AppRH.AppRH.dto.CandidatoDTO;
import com.AppRH.AppRH.dto.VagaDTO;
import com.AppRH.AppRH.models.Vaga;
import com.AppRH.AppRH.repository.*;
import com.AppRH.AppRH.services.RedisService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api")
public class VagaController {

    @Autowired
    private VagaRepository vr;
    @Autowired
    private RedisService redis;

    @GetMapping
    public ResponseEntity<?> listening() {
        Map<String, String> response = new HashMap<>();
        response.put("menssage", "Ok");
        return ResponseEntity.ok(response);
    };

    @PostMapping("/vagas")
    public ResponseEntity<?> registerVaga(
            @Valid @RequestBody Vaga vaga,
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Verifique os campos.");
        }

        Vaga vagaSalva = vr.save(vaga);
        redis.deleteAllVagasPages("vagas:page=*");

        return ResponseEntity.ok(vagaSalva);
    }

    @GetMapping("/vagas/list")
    public ResponseEntity<Object> listVagas(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {

        String key = "vagas:page=" + page + "&size=" + size;
        Pageable pageable = PageRequest.of(page, size);

        @SuppressWarnings("unchecked")
        Page<Object> cache = (Page<Object>) redis.getFromRedis(key, pageable);
        if (!cache.isEmpty()) {

            return ResponseEntity.ok(cache);
        }
        var pageResponse = vr.findPageBy(pageable);

        redis.saveToRedis(key, pageResponse);

        return ResponseEntity.ok(pageResponse);

    }

    @GetMapping("/vagas/{id}")
    public ResponseEntity<?> indexVagar(@PathVariable("id") long id) {

        Vaga vaga = vr.findByCodigo(id);
        VagaDTO vagaDTO = new VagaDTO();
        var key = "vagas:" + id;
        if (vaga == null) {
            Map<String, String> response = new HashMap<>();
            response.put("menssage", "Vaga não encontrada!");
            return ResponseEntity.badRequest().body(response);

        }

        var cache = redis.getToRedisId(key, VagaDTO.class);
        if (cache != null) {

            return ResponseEntity.ok(cache);
        }

        vagaDTO.setCodigo(vaga.getCodigo());
        vagaDTO.setNome(vaga.getNome());
        vagaDTO.setDescricao(vaga.getDescricao());
        vagaDTO.setDate(vaga.getDate());
        vagaDTO.setSalario(vaga.getSalario());

        List<CandidatoDTO> candidatos = vaga.getCandidatos().stream().map(c -> {
            CandidatoDTO candidatoDTO = new CandidatoDTO();
            candidatoDTO.setId(c.getId());
            candidatoDTO.setRg(c.getRg());
            candidatoDTO.setNome(c.getNome());
            candidatoDTO.setEmail(c.getEmail());
            return candidatoDTO;
        }).toList();

        vagaDTO.setCandidatos(candidatos);
        redis.saveToRedisId(key, vagaDTO);

        return ResponseEntity.ok(vagaDTO);
    }

    @DeleteMapping("/vagas/{id}")
    public ResponseEntity<?> deleteVagar(@PathVariable("id") long id) {
        Map<String, String> response = new HashMap<>();
        Vaga vaga = vr.findByCodigo(id);
        if (vaga == null) {
            response.put("menssage", "Vaga não encontrada!");
            return ResponseEntity.badRequest().body(response);

        }

        vr.delete(vaga);
        redis.deleteAllVagasPages("vagas:page=*");
        redis.deleteAllVagasPages("vagas:" + id);

        response.put("menssage", "Vaga deletada com sucesso!");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/vagas/{id}")
    public ResponseEntity<?> updateVaga(@Valid @RequestBody Vaga vaga, BindingResult result,
            @PathVariable("id") long id) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Verifique os campos.");
        }

        Vaga vagaSalva = vr.findByCodigo(id);

        if (vagaSalva == null) {
            return ResponseEntity.badRequest().body("Registro não encontrado!");
        }
        if (vaga.getNome() != null && !vaga.getNome().equals(vagaSalva.getNome())) {
            vagaSalva.setNome(vaga.getNome());
        }
        if (vaga.getDescricao() != null && !vaga.getDescricao().equals(vagaSalva.getDescricao())) {
            vagaSalva.setDescricao(vaga.getDescricao());
        }
        if (vaga.getSalario() != null && !vaga.getSalario().equals(vagaSalva.getSalario())) {
            vagaSalva.setSalario(vaga.getSalario());
        }

        Vaga vagaUpdate = vr.save(vagaSalva);
        redis.deleteAllVagasPages("vagas:page=*");
        redis.deleteAllVagasPages("vagas:" + id);

        return ResponseEntity.ok(vagaUpdate);

    }
}
