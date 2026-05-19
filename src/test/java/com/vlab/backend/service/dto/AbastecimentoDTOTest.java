package com.vlab.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.vlab.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AbastecimentoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AbastecimentoDTO.class);
        AbastecimentoDTO abastecimentoDTO1 = new AbastecimentoDTO();
        abastecimentoDTO1.setId(1L);
        AbastecimentoDTO abastecimentoDTO2 = new AbastecimentoDTO();
        assertThat(abastecimentoDTO1).isNotEqualTo(abastecimentoDTO2);
        abastecimentoDTO2.setId(abastecimentoDTO1.getId());
        assertThat(abastecimentoDTO1).isEqualTo(abastecimentoDTO2);
        abastecimentoDTO2.setId(2L);
        assertThat(abastecimentoDTO1).isNotEqualTo(abastecimentoDTO2);
        abastecimentoDTO1.setId(null);
        assertThat(abastecimentoDTO1).isNotEqualTo(abastecimentoDTO2);
    }
}
