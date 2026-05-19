package com.vlab.backend.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AbastecimentoCriteriaTest {

    @Test
    void newAbastecimentoCriteriaHasAllFiltersNullTest() {
        var abastecimentoCriteria = new AbastecimentoCriteria();
        assertThat(abastecimentoCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void abastecimentoCriteriaFluentMethodsCreatesFiltersTest() {
        var abastecimentoCriteria = new AbastecimentoCriteria();

        setAllFilters(abastecimentoCriteria);

        assertThat(abastecimentoCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void abastecimentoCriteriaCopyCreatesNullFilterTest() {
        var abastecimentoCriteria = new AbastecimentoCriteria();
        var copy = abastecimentoCriteria.copy();

        assertThat(abastecimentoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(abastecimentoCriteria)
        );
    }

    @Test
    void abastecimentoCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var abastecimentoCriteria = new AbastecimentoCriteria();
        setAllFilters(abastecimentoCriteria);

        var copy = abastecimentoCriteria.copy();

        assertThat(abastecimentoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(abastecimentoCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var abastecimentoCriteria = new AbastecimentoCriteria();

        assertThat(abastecimentoCriteria).hasToString("AbastecimentoCriteria{}");
    }

    private static void setAllFilters(AbastecimentoCriteria abastecimentoCriteria) {
        abastecimentoCriteria.id();
        abastecimentoCriteria.idPosto();
        abastecimentoCriteria.dataHora();
        abastecimentoCriteria.tipoCombustivel();
        abastecimentoCriteria.precoPorLitro();
        abastecimentoCriteria.volumeAbastecido();
        abastecimentoCriteria.cpfMotorista();
        abastecimentoCriteria.improperData();
        abastecimentoCriteria.distinct();
    }

    private static Condition<AbastecimentoCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getIdPosto()) &&
                condition.apply(criteria.getDataHora()) &&
                condition.apply(criteria.getTipoCombustivel()) &&
                condition.apply(criteria.getPrecoPorLitro()) &&
                condition.apply(criteria.getVolumeAbastecido()) &&
                condition.apply(criteria.getCpfMotorista()) &&
                condition.apply(criteria.getImproperData()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AbastecimentoCriteria> copyFiltersAre(
        AbastecimentoCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getIdPosto(), copy.getIdPosto()) &&
                condition.apply(criteria.getDataHora(), copy.getDataHora()) &&
                condition.apply(criteria.getTipoCombustivel(), copy.getTipoCombustivel()) &&
                condition.apply(criteria.getPrecoPorLitro(), copy.getPrecoPorLitro()) &&
                condition.apply(criteria.getVolumeAbastecido(), copy.getVolumeAbastecido()) &&
                condition.apply(criteria.getCpfMotorista(), copy.getCpfMotorista()) &&
                condition.apply(criteria.getImproperData(), copy.getImproperData()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
