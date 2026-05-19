package com.vlab.backend.service;

import com.vlab.backend.domain.Abastecimento;
import com.vlab.backend.repository.AbastecimentoRepository;
import com.vlab.backend.service.dto.AbastecimentoDTO;
import com.vlab.backend.service.mapper.AbastecimentoMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.vlab.backend.domain.Abastecimento}.
 */
@Service
@Transactional
public class AbastecimentoService {

    private static final BigDecimal MEDIA_GASOLINA = new BigDecimal("6.00");
    private static final BigDecimal MEDIA_ETANOL = new BigDecimal("4.20");
    private static final BigDecimal MEDIA_DIESEL = new BigDecimal("6.50");
    private static final BigDecimal FATOR_ANOMALIA = new BigDecimal("1.25");

    private static final Logger LOG = LoggerFactory.getLogger(AbastecimentoService.class);

    private final AbastecimentoRepository abastecimentoRepository;

    private final AbastecimentoMapper abastecimentoMapper;

    public AbastecimentoService(AbastecimentoRepository abastecimentoRepository, AbastecimentoMapper abastecimentoMapper) {
        this.abastecimentoRepository = abastecimentoRepository;
        this.abastecimentoMapper = abastecimentoMapper;
    }

    /**
     * Save a abastecimento.
     *
     * @param abastecimentoDTO the entity to save.
     * @return the persisted entity.
     */
    public AbastecimentoDTO save(AbastecimentoDTO abastecimentoDTO) {
        LOG.debug("Request to save Abastecimento : {}", abastecimentoDTO);
        validarAnomaliaPreco(abastecimentoDTO);
        Abastecimento abastecimento = abastecimentoMapper.toEntity(abastecimentoDTO);
        abastecimento = abastecimentoRepository.save(abastecimento);
        return abastecimentoMapper.toDto(abastecimento);
    }

    /**
     * Update a abastecimento.
     *
     * @param abastecimentoDTO the entity to save.
     * @return the persisted entity.
     */
    public AbastecimentoDTO update(AbastecimentoDTO abastecimentoDTO) {
        LOG.debug("Request to update Abastecimento : {}", abastecimentoDTO);
        Abastecimento abastecimento = abastecimentoMapper.toEntity(abastecimentoDTO);
        abastecimento = abastecimentoRepository.save(abastecimento);
        return abastecimentoMapper.toDto(abastecimento);
    }

    /**
     * Partially update a abastecimento.
     *
     * @param abastecimentoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AbastecimentoDTO> partialUpdate(AbastecimentoDTO abastecimentoDTO) {
        LOG.debug("Request to partially update Abastecimento : {}", abastecimentoDTO);

        return abastecimentoRepository
            .findById(abastecimentoDTO.getId())
            .map(existingAbastecimento -> {
                abastecimentoMapper.partialUpdate(existingAbastecimento, abastecimentoDTO);

                return existingAbastecimento;
            })
            .map(abastecimentoRepository::save)
            .map(abastecimentoMapper::toDto);
    }

    /**
     * Get one abastecimento by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AbastecimentoDTO> findOne(Long id) {
        LOG.debug("Request to get Abastecimento : {}", id);
        return abastecimentoRepository.findById(id).map(abastecimentoMapper::toDto);
    }

    /**
     * Delete the abastecimento by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Abastecimento : {}", id);
        abastecimentoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AbastecimentoDTO> findHistoricoByCpf(String cpf) {
        LOG.debug("Request to get Historico de Abastecimentos for CPF : {}", cpf);
        return abastecimentoRepository.findAllByCpfMotorista(cpf).stream().map(abastecimentoMapper::toDto).collect(Collectors.toList());
    }

    private void validarAnomaliaPreco(AbastecimentoDTO dto) {
        if (dto.getTipoCombustivel() == null || dto.getPrecoPorLitro() == null) {
            return;
        }

        BigDecimal mediaReferencia =
            switch (dto.getTipoCombustivel()) {
                case GASOLINA -> MEDIA_GASOLINA;
                case ETANOL -> MEDIA_ETANOL;
                case DIESEL -> MEDIA_DIESEL;
            };

        BigDecimal limiteMaximo = mediaReferencia.multiply(FATOR_ANOMALIA);

        if (dto.getPrecoPorLitro().compareTo(limiteMaximo) > 0) {
            dto.setImproperData(true);
        } else {
            dto.setImproperData(false);
        }
    }
}
