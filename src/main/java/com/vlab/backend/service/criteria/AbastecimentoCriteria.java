package com.vlab.backend.service.criteria;

import com.vlab.backend.domain.enumeration.TipoCombustivel;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.vlab.backend.domain.Abastecimento} entity. This class is used
 * in {@link com.vlab.backend.web.rest.AbastecimentoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /abastecimentos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AbastecimentoCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TipoCombustivel
     */
    public static class TipoCombustivelFilter extends Filter<TipoCombustivel> {

        public TipoCombustivelFilter() {}

        public TipoCombustivelFilter(TipoCombustivelFilter filter) {
            super(filter);
        }

        @Override
        public TipoCombustivelFilter copy() {
            return new TipoCombustivelFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter idPosto;

    private InstantFilter dataHora;

    private TipoCombustivelFilter tipoCombustivel;

    private BigDecimalFilter precoPorLitro;

    private BigDecimalFilter volumeAbastecido;

    private StringFilter cpfMotorista;

    private BooleanFilter improperData;

    private Boolean distinct;

    public AbastecimentoCriteria() {}

    public AbastecimentoCriteria(AbastecimentoCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.idPosto = other.optionalIdPosto().map(LongFilter::copy).orElse(null);
        this.dataHora = other.optionalDataHora().map(InstantFilter::copy).orElse(null);
        this.tipoCombustivel = other.optionalTipoCombustivel().map(TipoCombustivelFilter::copy).orElse(null);
        this.precoPorLitro = other.optionalPrecoPorLitro().map(BigDecimalFilter::copy).orElse(null);
        this.volumeAbastecido = other.optionalVolumeAbastecido().map(BigDecimalFilter::copy).orElse(null);
        this.cpfMotorista = other.optionalCpfMotorista().map(StringFilter::copy).orElse(null);
        this.improperData = other.optionalImproperData().map(BooleanFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AbastecimentoCriteria copy() {
        return new AbastecimentoCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getIdPosto() {
        return idPosto;
    }

    public Optional<LongFilter> optionalIdPosto() {
        return Optional.ofNullable(idPosto);
    }

    public LongFilter idPosto() {
        if (idPosto == null) {
            setIdPosto(new LongFilter());
        }
        return idPosto;
    }

    public void setIdPosto(LongFilter idPosto) {
        this.idPosto = idPosto;
    }

    public InstantFilter getDataHora() {
        return dataHora;
    }

    public Optional<InstantFilter> optionalDataHora() {
        return Optional.ofNullable(dataHora);
    }

    public InstantFilter dataHora() {
        if (dataHora == null) {
            setDataHora(new InstantFilter());
        }
        return dataHora;
    }

    public void setDataHora(InstantFilter dataHora) {
        this.dataHora = dataHora;
    }

    public TipoCombustivelFilter getTipoCombustivel() {
        return tipoCombustivel;
    }

    public Optional<TipoCombustivelFilter> optionalTipoCombustivel() {
        return Optional.ofNullable(tipoCombustivel);
    }

    public TipoCombustivelFilter tipoCombustivel() {
        if (tipoCombustivel == null) {
            setTipoCombustivel(new TipoCombustivelFilter());
        }
        return tipoCombustivel;
    }

    public void setTipoCombustivel(TipoCombustivelFilter tipoCombustivel) {
        this.tipoCombustivel = tipoCombustivel;
    }

    public BigDecimalFilter getPrecoPorLitro() {
        return precoPorLitro;
    }

    public Optional<BigDecimalFilter> optionalPrecoPorLitro() {
        return Optional.ofNullable(precoPorLitro);
    }

    public BigDecimalFilter precoPorLitro() {
        if (precoPorLitro == null) {
            setPrecoPorLitro(new BigDecimalFilter());
        }
        return precoPorLitro;
    }

    public void setPrecoPorLitro(BigDecimalFilter precoPorLitro) {
        this.precoPorLitro = precoPorLitro;
    }

    public BigDecimalFilter getVolumeAbastecido() {
        return volumeAbastecido;
    }

    public Optional<BigDecimalFilter> optionalVolumeAbastecido() {
        return Optional.ofNullable(volumeAbastecido);
    }

    public BigDecimalFilter volumeAbastecido() {
        if (volumeAbastecido == null) {
            setVolumeAbastecido(new BigDecimalFilter());
        }
        return volumeAbastecido;
    }

    public void setVolumeAbastecido(BigDecimalFilter volumeAbastecido) {
        this.volumeAbastecido = volumeAbastecido;
    }

    public StringFilter getCpfMotorista() {
        return cpfMotorista;
    }

    public Optional<StringFilter> optionalCpfMotorista() {
        return Optional.ofNullable(cpfMotorista);
    }

    public StringFilter cpfMotorista() {
        if (cpfMotorista == null) {
            setCpfMotorista(new StringFilter());
        }
        return cpfMotorista;
    }

    public void setCpfMotorista(StringFilter cpfMotorista) {
        this.cpfMotorista = cpfMotorista;
    }

    public BooleanFilter getImproperData() {
        return improperData;
    }

    public Optional<BooleanFilter> optionalImproperData() {
        return Optional.ofNullable(improperData);
    }

    public BooleanFilter improperData() {
        if (improperData == null) {
            setImproperData(new BooleanFilter());
        }
        return improperData;
    }

    public void setImproperData(BooleanFilter improperData) {
        this.improperData = improperData;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AbastecimentoCriteria that = (AbastecimentoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(idPosto, that.idPosto) &&
            Objects.equals(dataHora, that.dataHora) &&
            Objects.equals(tipoCombustivel, that.tipoCombustivel) &&
            Objects.equals(precoPorLitro, that.precoPorLitro) &&
            Objects.equals(volumeAbastecido, that.volumeAbastecido) &&
            Objects.equals(cpfMotorista, that.cpfMotorista) &&
            Objects.equals(improperData, that.improperData) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idPosto, dataHora, tipoCombustivel, precoPorLitro, volumeAbastecido, cpfMotorista, improperData, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AbastecimentoCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalIdPosto().map(f -> "idPosto=" + f + ", ").orElse("") +
            optionalDataHora().map(f -> "dataHora=" + f + ", ").orElse("") +
            optionalTipoCombustivel().map(f -> "tipoCombustivel=" + f + ", ").orElse("") +
            optionalPrecoPorLitro().map(f -> "precoPorLitro=" + f + ", ").orElse("") +
            optionalVolumeAbastecido().map(f -> "volumeAbastecido=" + f + ", ").orElse("") +
            optionalCpfMotorista().map(f -> "cpfMotorista=" + f + ", ").orElse("") +
            optionalImproperData().map(f -> "improperData=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
