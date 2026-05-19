package com.vlab.backend.domain;

import static com.vlab.backend.domain.AbastecimentoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.vlab.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AbastecimentoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Abastecimento.class);
        Abastecimento abastecimento1 = getAbastecimentoSample1();
        Abastecimento abastecimento2 = new Abastecimento();
        assertThat(abastecimento1).isNotEqualTo(abastecimento2);

        abastecimento2.setId(abastecimento1.getId());
        assertThat(abastecimento1).isEqualTo(abastecimento2);

        abastecimento2 = getAbastecimentoSample2();
        assertThat(abastecimento1).isNotEqualTo(abastecimento2);
    }
}
