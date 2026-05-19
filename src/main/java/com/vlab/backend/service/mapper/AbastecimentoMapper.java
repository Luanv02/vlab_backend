package com.vlab.backend.service.mapper;

import com.vlab.backend.domain.Abastecimento;
import com.vlab.backend.service.dto.AbastecimentoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Abastecimento} and its DTO {@link AbastecimentoDTO}.
 */
@Mapper(componentModel = "spring")
public interface AbastecimentoMapper extends EntityMapper<AbastecimentoDTO, Abastecimento> {}
