package com.vlab.backend.service.dto;

import com.vlab.backend.domain.enumeration.TipoCombustivel;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.vlab.backend.domain.Abastecimento} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AbastecimentoDTO implements Serializable {

    private Long id;

    @NotNull
    private Long idPosto;

    @NotNull
    private Instant dataHora;

    @NotNull
    private TipoCombustivel tipoCombustivel;

    @NotNull
    private BigDecimal precoPorLitro;

    @NotNull
    private BigDecimal volumeAbastecido;

    @NotNull
    private String cpfMotorista;

    private Boolean improperData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPosto() {
        return idPosto;
    }

    public void setIdPosto(Long idPosto) {
        this.idPosto = idPosto;
    }

    public Instant getDataHora() {
        return dataHora;
    }

    public void setDataHora(Instant dataHora) {
        this.dataHora = dataHora;
    }

    public TipoCombustivel getTipoCombustivel() {
        return tipoCombustivel;
    }

    public void setTipoCombustivel(TipoCombustivel tipoCombustivel) {
        this.tipoCombustivel = tipoCombustivel;
    }

    public BigDecimal getPrecoPorLitro() {
        return precoPorLitro;
    }

    public void setPrecoPorLitro(BigDecimal precoPorLitro) {
        this.precoPorLitro = precoPorLitro;
    }

    public BigDecimal getVolumeAbastecido() {
        return volumeAbastecido;
    }

    public void setVolumeAbastecido(BigDecimal volumeAbastecido) {
        this.volumeAbastecido = volumeAbastecido;
    }

    public String getCpfMotorista() {
        return cpfMotorista;
    }

    public void setCpfMotorista(String cpfMotorista) {
        this.cpfMotorista = cpfMotorista;
    }

    public Boolean getImproperData() {
        return improperData;
    }

    public void setImproperData(Boolean improperData) {
        this.improperData = improperData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbastecimentoDTO)) {
            return false;
        }

        AbastecimentoDTO abastecimentoDTO = (AbastecimentoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, abastecimentoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AbastecimentoDTO{" +
            "id=" + getId() +
            ", idPosto=" + getIdPosto() +
            ", dataHora='" + getDataHora() + "'" +
            ", tipoCombustivel='" + getTipoCombustivel() + "'" +
            ", precoPorLitro=" + getPrecoPorLitro() +
            ", volumeAbastecido=" + getVolumeAbastecido() +
            ", cpfMotorista='" + getCpfMotorista() + "'" +
            ", improperData='" + getImproperData() + "'" +
            "}";
    }
}
