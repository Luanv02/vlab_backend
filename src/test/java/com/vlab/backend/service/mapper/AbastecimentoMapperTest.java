package com.vlab.backend.service.mapper;

import static com.vlab.backend.domain.AbastecimentoAsserts.*;
import static com.vlab.backend.domain.AbastecimentoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbastecimentoMapperTest {

    private AbastecimentoMapper abastecimentoMapper;

    @BeforeEach
    void setUp() {
        abastecimentoMapper = new AbastecimentoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAbastecimentoSample1();
        var actual = abastecimentoMapper.toEntity(abastecimentoMapper.toDto(expected));
        assertAbastecimentoAllPropertiesEquals(expected, actual);
    }
}
