package com.vlab.backend.service;

import com.vlab.backend.domain.*; // for static metamodels
import com.vlab.backend.domain.Abastecimento;
import com.vlab.backend.repository.AbastecimentoRepository;
import com.vlab.backend.service.criteria.AbastecimentoCriteria;
import com.vlab.backend.service.dto.AbastecimentoDTO;
import com.vlab.backend.service.mapper.AbastecimentoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Abastecimento} entities in the database.
 * The main input is a {@link AbastecimentoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AbastecimentoDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AbastecimentoQueryService extends QueryService<Abastecimento> {

    private static final Logger LOG = LoggerFactory.getLogger(AbastecimentoQueryService.class);

    private final AbastecimentoRepository abastecimentoRepository;

    private final AbastecimentoMapper abastecimentoMapper;

    public AbastecimentoQueryService(AbastecimentoRepository abastecimentoRepository, AbastecimentoMapper abastecimentoMapper) {
        this.abastecimentoRepository = abastecimentoRepository;
        this.abastecimentoMapper = abastecimentoMapper;
    }

    /**
     * Return a {@link Page} of {@link AbastecimentoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AbastecimentoDTO> findByCriteria(AbastecimentoCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Abastecimento> specification = createSpecification(criteria);
        return abastecimentoRepository.findAll(specification, page).map(abastecimentoMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AbastecimentoCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Abastecimento> specification = createSpecification(criteria);
        return abastecimentoRepository.count(specification);
    }

    /**
     * Function to convert {@link AbastecimentoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Abastecimento> createSpecification(AbastecimentoCriteria criteria) {
        Specification<Abastecimento> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Abastecimento_.id),
                buildRangeSpecification(criteria.getIdPosto(), Abastecimento_.idPosto),
                buildRangeSpecification(criteria.getDataHora(), Abastecimento_.dataHora),
                buildSpecification(criteria.getTipoCombustivel(), Abastecimento_.tipoCombustivel),
                buildRangeSpecification(criteria.getPrecoPorLitro(), Abastecimento_.precoPorLitro),
                buildRangeSpecification(criteria.getVolumeAbastecido(), Abastecimento_.volumeAbastecido),
                buildStringSpecification(criteria.getCpfMotorista(), Abastecimento_.cpfMotorista),
                buildSpecification(criteria.getImproperData(), Abastecimento_.improperData)
            );
        }
        return specification;
    }
}
