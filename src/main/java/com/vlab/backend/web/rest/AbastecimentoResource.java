package com.vlab.backend.web.rest;

import com.vlab.backend.repository.AbastecimentoRepository;
import com.vlab.backend.service.AbastecimentoQueryService;
import com.vlab.backend.service.AbastecimentoService;
import com.vlab.backend.service.criteria.AbastecimentoCriteria;
import com.vlab.backend.service.dto.AbastecimentoDTO;
import com.vlab.backend.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.vlab.backend.domain.Abastecimento}.
 */
@RestController
@RequestMapping("/api/abastecimentos")
public class AbastecimentoResource {

    private static final Logger LOG = LoggerFactory.getLogger(AbastecimentoResource.class);

    private static final String ENTITY_NAME = "abastecimento";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AbastecimentoService abastecimentoService;

    private final AbastecimentoRepository abastecimentoRepository;

    private final AbastecimentoQueryService abastecimentoQueryService;

    public AbastecimentoResource(
        AbastecimentoService abastecimentoService,
        AbastecimentoRepository abastecimentoRepository,
        AbastecimentoQueryService abastecimentoQueryService
    ) {
        this.abastecimentoService = abastecimentoService;
        this.abastecimentoRepository = abastecimentoRepository;
        this.abastecimentoQueryService = abastecimentoQueryService;
    }

    /**
     * {@code POST  /abastecimentos} : Create a new abastecimento.
     *
     * @param abastecimentoDTO the abastecimentoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new abastecimentoDTO, or with status {@code 400 (Bad Request)} if the abastecimento has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AbastecimentoDTO> createAbastecimento(@Valid @RequestBody AbastecimentoDTO abastecimentoDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Abastecimento : {}", abastecimentoDTO);
        if (abastecimentoDTO.getId() != null) {
            throw new BadRequestAlertException("A new abastecimento cannot already have an ID", ENTITY_NAME, "idexists");
        }
        abastecimentoDTO = abastecimentoService.save(abastecimentoDTO);
        return ResponseEntity.created(new URI("/api/abastecimentos/" + abastecimentoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, abastecimentoDTO.getId().toString()))
            .body(abastecimentoDTO);
    }

    /**
     * {@code PUT  /abastecimentos/:id} : Updates an existing abastecimento.
     *
     * @param id the id of the abastecimentoDTO to save.
     * @param abastecimentoDTO the abastecimentoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated abastecimentoDTO,
     * or with status {@code 400 (Bad Request)} if the abastecimentoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the abastecimentoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AbastecimentoDTO> updateAbastecimento(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AbastecimentoDTO abastecimentoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Abastecimento : {}, {}", id, abastecimentoDTO);
        if (abastecimentoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, abastecimentoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!abastecimentoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        abastecimentoDTO = abastecimentoService.update(abastecimentoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, abastecimentoDTO.getId().toString()))
            .body(abastecimentoDTO);
    }

    /**
     * {@code PATCH  /abastecimentos/:id} : Partial updates given fields of an existing abastecimento, field will ignore if it is null
     *
     * @param id the id of the abastecimentoDTO to save.
     * @param abastecimentoDTO the abastecimentoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated abastecimentoDTO,
     * or with status {@code 400 (Bad Request)} if the abastecimentoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the abastecimentoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the abastecimentoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AbastecimentoDTO> partialUpdateAbastecimento(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AbastecimentoDTO abastecimentoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Abastecimento partially : {}, {}", id, abastecimentoDTO);
        if (abastecimentoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, abastecimentoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!abastecimentoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AbastecimentoDTO> result = abastecimentoService.partialUpdate(abastecimentoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, abastecimentoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /abastecimentos} : get all the abastecimentos.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of abastecimentos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AbastecimentoDTO>> getAllAbastecimentos(
        AbastecimentoCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Abastecimentos by criteria: {}", criteria);

        Page<AbastecimentoDTO> page = abastecimentoQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /abastecimentos/count} : count all the abastecimentos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAbastecimentos(AbastecimentoCriteria criteria) {
        LOG.debug("REST request to count Abastecimentos by criteria: {}", criteria);
        return ResponseEntity.ok().body(abastecimentoQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /abastecimentos/:id} : get the "id" abastecimento.
     *
     * @param id the id of the abastecimentoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the abastecimentoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AbastecimentoDTO> getAbastecimento(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Abastecimento : {}", id);
        Optional<AbastecimentoDTO> abastecimentoDTO = abastecimentoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(abastecimentoDTO);
    }

    /**
     * {@code DELETE  /abastecimentos/:id} : delete the "id" abastecimento.
     *
     * @param id the id of the abastecimentoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbastecimento(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Abastecimento : {}", id);
        abastecimentoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
