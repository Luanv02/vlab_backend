package com.vlab.backend.web.rest;

import com.vlab.backend.service.AbastecimentoService;
import com.vlab.backend.service.dto.AbastecimentoDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/motoristas")
public class MotoristaResource {

    private static final Logger LOG = LoggerFactory.getLogger(MotoristaResource.class);

    private final AbastecimentoService abastecimentoService;

    public MotoristaResource(AbastecimentoService abastecimentoService) {
        this.abastecimentoService = abastecimentoService;
    }

    @GetMapping("/{cpf}/historico")
    public ResponseEntity<List<AbastecimentoDTO>> getHistoricoByCpf(@PathVariable("cpf") String cpf) {
        LOG.debug("REST request to get Historico de Abastecimento for CPF : {}", cpf);
        List<AbastecimentoDTO> historico = abastecimentoService.findHistoricoByCpf(cpf);
        return ResponseEntity.ok().body(historico);
    }
}
