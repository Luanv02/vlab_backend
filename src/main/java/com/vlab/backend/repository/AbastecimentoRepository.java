package com.vlab.backend.repository;

import com.vlab.backend.domain.Abastecimento;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Abastecimento entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AbastecimentoRepository extends JpaRepository<Abastecimento, Long>, JpaSpecificationExecutor<Abastecimento> {}
