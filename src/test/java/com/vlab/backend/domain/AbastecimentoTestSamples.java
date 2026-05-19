package com.vlab.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AbastecimentoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Abastecimento getAbastecimentoSample1() {
        return new Abastecimento().id(1L).idPosto(1L).cpfMotorista("cpfMotorista1");
    }

    public static Abastecimento getAbastecimentoSample2() {
        return new Abastecimento().id(2L).idPosto(2L).cpfMotorista("cpfMotorista2");
    }

    public static Abastecimento getAbastecimentoRandomSampleGenerator() {
        return new Abastecimento()
            .id(longCount.incrementAndGet())
            .idPosto(longCount.incrementAndGet())
            .cpfMotorista(UUID.randomUUID().toString());
    }
}
