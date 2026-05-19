package com.vlab.backend.domain;

import com.vlab.backend.domain.enumeration.TipoCombustivel;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Abastecimento.
 */
@Entity
@Table(name = "abastecimento")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Abastecimento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "id_posto", nullable = false)
    private Long idPosto;

    @NotNull
    @Column(name = "data_hora", nullable = false)
    private Instant dataHora;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_combustivel", nullable = false)
    private TipoCombustivel tipoCombustivel;

    @NotNull
    @Column(name = "preco_por_litro", precision = 21, scale = 2, nullable = false)
    private BigDecimal precoPorLitro;

    @NotNull
    @Column(name = "volume_abastecido", precision = 21, scale = 2, nullable = false)
    private BigDecimal volumeAbastecido;

    @NotNull
    @Column(name = "cpf_motorista", nullable = false)
    private String cpfMotorista;

    @Column(name = "improper_data")
    private Boolean improperData;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Abastecimento id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPosto() {
        return this.idPosto;
    }

    public Abastecimento idPosto(Long idPosto) {
        this.setIdPosto(idPosto);
        return this;
    }

    public void setIdPosto(Long idPosto) {
        this.idPosto = idPosto;
    }

    public Instant getDataHora() {
        return this.dataHora;
    }

    public Abastecimento dataHora(Instant dataHora) {
        this.setDataHora(dataHora);
        return this;
    }

    public void setDataHora(Instant dataHora) {
        this.dataHora = dataHora;
    }

    public TipoCombustivel getTipoCombustivel() {
        return this.tipoCombustivel;
    }

    public Abastecimento tipoCombustivel(TipoCombustivel tipoCombustivel) {
        this.setTipoCombustivel(tipoCombustivel);
        return this;
    }

    public void setTipoCombustivel(TipoCombustivel tipoCombustivel) {
        this.tipoCombustivel = tipoCombustivel;
    }

    public BigDecimal getPrecoPorLitro() {
        return this.precoPorLitro;
    }

    public Abastecimento precoPorLitro(BigDecimal precoPorLitro) {
        this.setPrecoPorLitro(precoPorLitro);
        return this;
    }

    public void setPrecoPorLitro(BigDecimal precoPorLitro) {
        this.precoPorLitro = precoPorLitro;
    }

    public BigDecimal getVolumeAbastecido() {
        return this.volumeAbastecido;
    }

    public Abastecimento volumeAbastecido(BigDecimal volumeAbastecido) {
        this.setVolumeAbastecido(volumeAbastecido);
        return this;
    }

    public void setVolumeAbastecido(BigDecimal volumeAbastecido) {
        this.volumeAbastecido = volumeAbastecido;
    }

    public String getCpfMotorista() {
        return this.cpfMotorista;
    }

    public Abastecimento cpfMotorista(String cpfMotorista) {
        this.setCpfMotorista(cpfMotorista);
        return this;
    }

    public void setCpfMotorista(String cpfMotorista) {
        this.cpfMotorista = cpfMotorista;
    }

    public Boolean getImproperData() {
        return this.improperData;
    }

    public Abastecimento improperData(Boolean improperData) {
        this.setImproperData(improperData);
        return this;
    }

    public void setImproperData(Boolean improperData) {
        this.improperData = improperData;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Abastecimento)) {
            return false;
        }
        return getId() != null && getId().equals(((Abastecimento) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Abastecimento{" +
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
