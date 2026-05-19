package com.vlab.backend.web.rest;

import static com.vlab.backend.domain.AbastecimentoAsserts.*;
import static com.vlab.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static com.vlab.backend.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlab.backend.IntegrationTest;
import com.vlab.backend.domain.Abastecimento;
import com.vlab.backend.domain.enumeration.TipoCombustivel;
import com.vlab.backend.repository.AbastecimentoRepository;
import com.vlab.backend.service.dto.AbastecimentoDTO;
import com.vlab.backend.service.mapper.AbastecimentoMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AbastecimentoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AbastecimentoResourceIT {

    private static final Long DEFAULT_ID_POSTO = 1L;
    private static final Long UPDATED_ID_POSTO = 2L;
    private static final Long SMALLER_ID_POSTO = 1L - 1L;

    private static final Instant DEFAULT_DATA_HORA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATA_HORA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final TipoCombustivel DEFAULT_TIPO_COMBUSTIVEL = TipoCombustivel.GASOLINA;
    private static final TipoCombustivel UPDATED_TIPO_COMBUSTIVEL = TipoCombustivel.ETANOL;

    private static final BigDecimal DEFAULT_PRECO_POR_LITRO = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRECO_POR_LITRO = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRECO_POR_LITRO = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_VOLUME_ABASTECIDO = new BigDecimal(1);
    private static final BigDecimal UPDATED_VOLUME_ABASTECIDO = new BigDecimal(2);
    private static final BigDecimal SMALLER_VOLUME_ABASTECIDO = new BigDecimal(1 - 1);

    private static final String DEFAULT_CPF_MOTORISTA = "AAAAAAAAAA";
    private static final String UPDATED_CPF_MOTORISTA = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IMPROPER_DATA = false;
    private static final Boolean UPDATED_IMPROPER_DATA = true;

    private static final String ENTITY_API_URL = "/api/abastecimentos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AbastecimentoRepository abastecimentoRepository;

    @Autowired
    private AbastecimentoMapper abastecimentoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAbastecimentoMockMvc;

    private Abastecimento abastecimento;

    private Abastecimento insertedAbastecimento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Abastecimento createEntity() {
        return new Abastecimento()
            .idPosto(DEFAULT_ID_POSTO)
            .dataHora(DEFAULT_DATA_HORA)
            .tipoCombustivel(DEFAULT_TIPO_COMBUSTIVEL)
            .precoPorLitro(DEFAULT_PRECO_POR_LITRO)
            .volumeAbastecido(DEFAULT_VOLUME_ABASTECIDO)
            .cpfMotorista(DEFAULT_CPF_MOTORISTA)
            .improperData(DEFAULT_IMPROPER_DATA);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Abastecimento createUpdatedEntity() {
        return new Abastecimento()
            .idPosto(UPDATED_ID_POSTO)
            .dataHora(UPDATED_DATA_HORA)
            .tipoCombustivel(UPDATED_TIPO_COMBUSTIVEL)
            .precoPorLitro(UPDATED_PRECO_POR_LITRO)
            .volumeAbastecido(UPDATED_VOLUME_ABASTECIDO)
            .cpfMotorista(UPDATED_CPF_MOTORISTA)
            .improperData(UPDATED_IMPROPER_DATA);
    }

    @BeforeEach
    void initTest() {
        abastecimento = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAbastecimento != null) {
            abastecimentoRepository.delete(insertedAbastecimento);
            insertedAbastecimento = null;
        }
    }

    @Test
    @Transactional
    void createAbastecimento() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Abastecimento
        AbastecimentoDTO abastecimentoDTO = abastecimentoMapper.toDto(abastecimento);
        var returnedAbastecimentoDTO = om.readValue(
            restAbastecimentoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(abastecimentoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AbastecimentoDTO.class
        );

        // Validate the Abastecimento in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAbastecimento = abastecimentoMapper.toEntity(returnedAbastecimentoDTO);
        assertAbastecimentoUpdatableFieldsEquals(returnedAbastecimento, getPersistedAbastecimento(returnedAbastecimento));

        insertedAbastecimento = returnedAbastecimento;
    }

    @Test
    @Transactional
    void createAbastecimentoWithExistingId() throws Exception {
        // Create the Abastecimento with an existing ID
        abastecimento.setId(1L);
        AbastecimentoDTO abastecimentoDTO = abastecimentoMapper.toDto(abastecimento);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAbastecimentoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(abastecimentoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Abastecimento in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIdPostoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        abastecimento.setIdPosto(null);

        // Create the Abastecimento, which fails.
        AbastecimentoDTO abastecimentoDTO = abastecimentoMapper.toDto(abastecimento);

        restAbastecimentoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(abastecimentoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDataHoraIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        abastecimento.setDataHora(null);

        // Create the Abastecimento, which fails.
        AbastecimentoDTO abastecimentoDTO = abastecimentoMapper.toDto(abastecimento);

        restAbastecimentoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(abastecimentoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTipoCombustivelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        abastecimento.setTipoCombustivel(null);

        // Create the Abastecimento, which fails.
        AbastecimentoDTO abastecimentoDTO = abastecimentoMapper.toDto(abastecimento);

        restAbastecimentoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(abastecimentoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrecoPorLitroIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        abastecimento.setPrecoPorLitro(null);

        // Create the Abastecimento, which fails.
        AbastecimentoDTO abastecimentoDTO = abastecimentoMapper.toDto(abastecimento);

        restAbastecimentoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(abastecimentoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVolumeAbastecidoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        abastecimento.setVolumeAbastecido(null);

        // Create the Abastecimento, which fails.
        AbastecimentoDTO abastecimentoDTO = abastecimentoMapper.toDto(abastecimento);

        restAbastecimentoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(abastecimentoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCpfMotoristaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        abastecimento.setCpfMotorista(null);

        // Create the Abastecimento, which fails.
        AbastecimentoDTO abastecimentoDTO = abastecimentoMapper.toDto(abastecimento);

        restAbastecimentoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(abastecimentoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAbastecimentos() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList
        restAbastecimentoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(abastecimento.getId().intValue())))
            .andExpect(jsonPath("$.[*].idPosto").value(hasItem(DEFAULT_ID_POSTO.intValue())))
            .andExpect(jsonPath("$.[*].dataHora").value(hasItem(DEFAULT_DATA_HORA.toString())))
            .andExpect(jsonPath("$.[*].tipoCombustivel").value(hasItem(DEFAULT_TIPO_COMBUSTIVEL.toString())))
            .andExpect(jsonPath("$.[*].precoPorLitro").value(hasItem(sameNumber(DEFAULT_PRECO_POR_LITRO))))
            .andExpect(jsonPath("$.[*].volumeAbastecido").value(hasItem(sameNumber(DEFAULT_VOLUME_ABASTECIDO))))
            .andExpect(jsonPath("$.[*].cpfMotorista").value(hasItem(DEFAULT_CPF_MOTORISTA)))
            .andExpect(jsonPath("$.[*].improperData").value(hasItem(DEFAULT_IMPROPER_DATA)));
    }

    @Test
    @Transactional
    void getAbastecimento() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get the abastecimento
        restAbastecimentoMockMvc
            .perform(get(ENTITY_API_URL_ID, abastecimento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(abastecimento.getId().intValue()))
            .andExpect(jsonPath("$.idPosto").value(DEFAULT_ID_POSTO.intValue()))
            .andExpect(jsonPath("$.dataHora").value(DEFAULT_DATA_HORA.toString()))
            .andExpect(jsonPath("$.tipoCombustivel").value(DEFAULT_TIPO_COMBUSTIVEL.toString()))
            .andExpect(jsonPath("$.precoPorLitro").value(sameNumber(DEFAULT_PRECO_POR_LITRO)))
            .andExpect(jsonPath("$.volumeAbastecido").value(sameNumber(DEFAULT_VOLUME_ABASTECIDO)))
            .andExpect(jsonPath("$.cpfMotorista").value(DEFAULT_CPF_MOTORISTA))
            .andExpect(jsonPath("$.improperData").value(DEFAULT_IMPROPER_DATA));
    }

    @Test
    @Transactional
    void getAbastecimentosByIdFiltering() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        Long id = abastecimento.getId();

        defaultAbastecimentoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAbastecimentoFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAbastecimentoFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAbastecimentosByIdPostoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where idPosto equals to
        defaultAbastecimentoFiltering("idPosto.equals=" + DEFAULT_ID_POSTO, "idPosto.equals=" + UPDATED_ID_POSTO);
    }

    @Test
    @Transactional
    void getAllAbastecimentosByIdPostoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where idPosto in
        defaultAbastecimentoFiltering("idPosto.in=" + DEFAULT_ID_POSTO + "," + UPDATED_ID_POSTO, "idPosto.in=" + UPDATED_ID_POSTO);
    }

    @Test
    @Transactional
    void getAllAbastecimentosByIdPostoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where idPosto is not null
        defaultAbastecimentoFiltering("idPosto.specified=true", "idPosto.specified=false");
    }

    @Test
    @Transactional
    void getAllAbastecimentosByIdPostoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where idPosto is greater than or equal to
        defaultAbastecimentoFiltering("idPosto.greaterThanOrEqual=" + DEFAULT_ID_POSTO, "idPosto.greaterThanOrEqual=" + UPDATED_ID_POSTO);
    }

    @Test
    @Transactional
    void getAllAbastecimentosByIdPostoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where idPosto is less than or equal to
        defaultAbastecimentoFiltering("idPosto.lessThanOrEqual=" + DEFAULT_ID_POSTO, "idPosto.lessThanOrEqual=" + SMALLER_ID_POSTO);
    }

    @Test
    @Transactional
    void getAllAbastecimentosByIdPostoIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where idPosto is less than
        defaultAbastecimentoFiltering("idPosto.lessThan=" + UPDATED_ID_POSTO, "idPosto.lessThan=" + DEFAULT_ID_POSTO);
    }

    @Test
    @Transactional
    void getAllAbastecimentosByIdPostoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where idPosto is greater than
        defaultAbastecimentoFiltering("idPosto.greaterThan=" + SMALLER_ID_POSTO, "idPosto.greaterThan=" + DEFAULT_ID_POSTO);
    }

    @Test
    @Transactional
    void getAllAbastecimentosByDataHoraIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where dataHora equals to
        defaultAbastecimentoFiltering("dataHora.equals=" + DEFAULT_DATA_HORA, "dataHora.equals=" + UPDATED_DATA_HORA);
    }

    @Test
    @Transactional
    void getAllAbastecimentosByDataHoraIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where dataHora in
        defaultAbastecimentoFiltering("dataHora.in=" + DEFAULT_DATA_HORA + "," + UPDATED_DATA_HORA, "dataHora.in=" + UPDATED_DATA_HORA);
    }

    @Test
    @Transactional
    void getAllAbastecimentosByDataHoraIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where dataHora is not null
        defaultAbastecimentoFiltering("dataHora.specified=true", "dataHora.specified=false");
    }

    @Test
    @Transactional
    void getAllAbastecimentosByTipoCombustivelIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where tipoCombustivel equals to
        defaultAbastecimentoFiltering(
            "tipoCombustivel.equals=" + DEFAULT_TIPO_COMBUSTIVEL,
            "tipoCombustivel.equals=" + UPDATED_TIPO_COMBUSTIVEL
        );
    }

    @Test
    @Transactional
    void getAllAbastecimentosByTipoCombustivelIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where tipoCombustivel in
        defaultAbastecimentoFiltering(
            "tipoCombustivel.in=" + DEFAULT_TIPO_COMBUSTIVEL + "," + UPDATED_TIPO_COMBUSTIVEL,
            "tipoCombustivel.in=" + UPDATED_TIPO_COMBUSTIVEL
        );
    }

    @Test
    @Transactional
    void getAllAbastecimentosByTipoCombustivelIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where tipoCombustivel is not null
        defaultAbastecimentoFiltering("tipoCombustivel.specified=true", "tipoCombustivel.specified=false");
    }

    @Test
    @Transactional
    void getAllAbastecimentosByPrecoPorLitroIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where precoPorLitro equals to
        defaultAbastecimentoFiltering("precoPorLitro.equals=" + DEFAULT_PRECO_POR_LITRO, "precoPorLitro.equals=" + UPDATED_PRECO_POR_LITRO);
    }

    @Test
    @Transactional
    void getAllAbastecimentosByPrecoPorLitroIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where precoPorLitro in
        defaultAbastecimentoFiltering(
            "precoPorLitro.in=" + DEFAULT_PRECO_POR_LITRO + "," + UPDATED_PRECO_POR_LITRO,
            "precoPorLitro.in=" + UPDATED_PRECO_POR_LITRO
        );
    }

    @Test
    @Transactional
    void getAllAbastecimentosByPrecoPorLitroIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where precoPorLitro is not null
        defaultAbastecimentoFiltering("precoPorLitro.specified=true", "precoPorLitro.specified=false");
    }

    @Test
    @Transactional
    void getAllAbastecimentosByPrecoPorLitroIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where precoPorLitro is greater than or equal to
        defaultAbastecimentoFiltering(
            "precoPorLitro.greaterThanOrEqual=" + DEFAULT_PRECO_POR_LITRO,
            "precoPorLitro.greaterThanOrEqual=" + UPDATED_PRECO_POR_LITRO
        );
    }

    @Test
    @Transactional
    void getAllAbastecimentosByPrecoPorLitroIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where precoPorLitro is less than or equal to
        defaultAbastecimentoFiltering(
            "precoPorLitro.lessThanOrEqual=" + DEFAULT_PRECO_POR_LITRO,
            "precoPorLitro.lessThanOrEqual=" + SMALLER_PRECO_POR_LITRO
        );
    }

    @Test
    @Transactional
    void getAllAbastecimentosByPrecoPorLitroIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where precoPorLitro is less than
        defaultAbastecimentoFiltering(
            "precoPorLitro.lessThan=" + UPDATED_PRECO_POR_LITRO,
            "precoPorLitro.lessThan=" + DEFAULT_PRECO_POR_LITRO
        );
    }

    @Test
    @Transactional
    void getAllAbastecimentosByPrecoPorLitroIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where precoPorLitro is greater than
        defaultAbastecimentoFiltering(
            "precoPorLitro.greaterThan=" + SMALLER_PRECO_POR_LITRO,
            "precoPorLitro.greaterThan=" + DEFAULT_PRECO_POR_LITRO
        );
    }

    @Test
    @Transactional
    void getAllAbastecimentosByVolumeAbastecidoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where volumeAbastecido equals to
        defaultAbastecimentoFiltering(
            "volumeAbastecido.equals=" + DEFAULT_VOLUME_ABASTECIDO,
            "volumeAbastecido.equals=" + UPDATED_VOLUME_ABASTECIDO
        );
    }

    @Test
    @Transactional
    void getAllAbastecimentosByVolumeAbastecidoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where volumeAbastecido in
        defaultAbastecimentoFiltering(
            "volumeAbastecido.in=" + DEFAULT_VOLUME_ABASTECIDO + "," + UPDATED_VOLUME_ABASTECIDO,
            "volumeAbastecido.in=" + UPDATED_VOLUME_ABASTECIDO
        );
    }

    @Test
    @Transactional
    void getAllAbastecimentosByVolumeAbastecidoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where volumeAbastecido is not null
        defaultAbastecimentoFiltering("volumeAbastecido.specified=true", "volumeAbastecido.specified=false");
    }

    @Test
    @Transactional
    void getAllAbastecimentosByVolumeAbastecidoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where volumeAbastecido is greater than or equal to
        defaultAbastecimentoFiltering(
            "volumeAbastecido.greaterThanOrEqual=" + DEFAULT_VOLUME_ABASTECIDO,
            "volumeAbastecido.greaterThanOrEqual=" + UPDATED_VOLUME_ABASTECIDO
        );
    }

    @Test
    @Transactional
    void getAllAbastecimentosByVolumeAbastecidoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where volumeAbastecido is less than or equal to
        defaultAbastecimentoFiltering(
            "volumeAbastecido.lessThanOrEqual=" + DEFAULT_VOLUME_ABASTECIDO,
            "volumeAbastecido.lessThanOrEqual=" + SMALLER_VOLUME_ABASTECIDO
        );
    }

    @Test
    @Transactional
    void getAllAbastecimentosByVolumeAbastecidoIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where volumeAbastecido is less than
        defaultAbastecimentoFiltering(
            "volumeAbastecido.lessThan=" + UPDATED_VOLUME_ABASTECIDO,
            "volumeAbastecido.lessThan=" + DEFAULT_VOLUME_ABASTECIDO
        );
    }

    @Test
    @Transactional
    void getAllAbastecimentosByVolumeAbastecidoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where volumeAbastecido is greater than
        defaultAbastecimentoFiltering(
            "volumeAbastecido.greaterThan=" + SMALLER_VOLUME_ABASTECIDO,
            "volumeAbastecido.greaterThan=" + DEFAULT_VOLUME_ABASTECIDO
        );
    }

    @Test
    @Transactional
    void getAllAbastecimentosByCpfMotoristaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where cpfMotorista equals to
        defaultAbastecimentoFiltering("cpfMotorista.equals=" + DEFAULT_CPF_MOTORISTA, "cpfMotorista.equals=" + UPDATED_CPF_MOTORISTA);
    }

    @Test
    @Transactional
    void getAllAbastecimentosByCpfMotoristaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where cpfMotorista in
        defaultAbastecimentoFiltering(
            "cpfMotorista.in=" + DEFAULT_CPF_MOTORISTA + "," + UPDATED_CPF_MOTORISTA,
            "cpfMotorista.in=" + UPDATED_CPF_MOTORISTA
        );
    }

    @Test
    @Transactional
    void getAllAbastecimentosByCpfMotoristaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where cpfMotorista is not null
        defaultAbastecimentoFiltering("cpfMotorista.specified=true", "cpfMotorista.specified=false");
    }

    @Test
    @Transactional
    void getAllAbastecimentosByCpfMotoristaContainsSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where cpfMotorista contains
        defaultAbastecimentoFiltering("cpfMotorista.contains=" + DEFAULT_CPF_MOTORISTA, "cpfMotorista.contains=" + UPDATED_CPF_MOTORISTA);
    }

    @Test
    @Transactional
    void getAllAbastecimentosByCpfMotoristaNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where cpfMotorista does not contain
        defaultAbastecimentoFiltering(
            "cpfMotorista.doesNotContain=" + UPDATED_CPF_MOTORISTA,
            "cpfMotorista.doesNotContain=" + DEFAULT_CPF_MOTORISTA
        );
    }

    @Test
    @Transactional
    void getAllAbastecimentosByImproperDataIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where improperData equals to
        defaultAbastecimentoFiltering("improperData.equals=" + DEFAULT_IMPROPER_DATA, "improperData.equals=" + UPDATED_IMPROPER_DATA);
    }

    @Test
    @Transactional
    void getAllAbastecimentosByImproperDataIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where improperData in
        defaultAbastecimentoFiltering(
            "improperData.in=" + DEFAULT_IMPROPER_DATA + "," + UPDATED_IMPROPER_DATA,
            "improperData.in=" + UPDATED_IMPROPER_DATA
        );
    }

    @Test
    @Transactional
    void getAllAbastecimentosByImproperDataIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        // Get all the abastecimentoList where improperData is not null
        defaultAbastecimentoFiltering("improperData.specified=true", "improperData.specified=false");
    }

    private void defaultAbastecimentoFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAbastecimentoShouldBeFound(shouldBeFound);
        defaultAbastecimentoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAbastecimentoShouldBeFound(String filter) throws Exception {
        restAbastecimentoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(abastecimento.getId().intValue())))
            .andExpect(jsonPath("$.[*].idPosto").value(hasItem(DEFAULT_ID_POSTO.intValue())))
            .andExpect(jsonPath("$.[*].dataHora").value(hasItem(DEFAULT_DATA_HORA.toString())))
            .andExpect(jsonPath("$.[*].tipoCombustivel").value(hasItem(DEFAULT_TIPO_COMBUSTIVEL.toString())))
            .andExpect(jsonPath("$.[*].precoPorLitro").value(hasItem(sameNumber(DEFAULT_PRECO_POR_LITRO))))
            .andExpect(jsonPath("$.[*].volumeAbastecido").value(hasItem(sameNumber(DEFAULT_VOLUME_ABASTECIDO))))
            .andExpect(jsonPath("$.[*].cpfMotorista").value(hasItem(DEFAULT_CPF_MOTORISTA)))
            .andExpect(jsonPath("$.[*].improperData").value(hasItem(DEFAULT_IMPROPER_DATA)));

        // Check, that the count call also returns 1
        restAbastecimentoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAbastecimentoShouldNotBeFound(String filter) throws Exception {
        restAbastecimentoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAbastecimentoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAbastecimento() throws Exception {
        // Get the abastecimento
        restAbastecimentoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAbastecimento() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the abastecimento
        Abastecimento updatedAbastecimento = abastecimentoRepository.findById(abastecimento.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAbastecimento are not directly saved in db
        em.detach(updatedAbastecimento);
        updatedAbastecimento
            .idPosto(UPDATED_ID_POSTO)
            .dataHora(UPDATED_DATA_HORA)
            .tipoCombustivel(UPDATED_TIPO_COMBUSTIVEL)
            .precoPorLitro(UPDATED_PRECO_POR_LITRO)
            .volumeAbastecido(UPDATED_VOLUME_ABASTECIDO)
            .cpfMotorista(UPDATED_CPF_MOTORISTA)
            .improperData(UPDATED_IMPROPER_DATA);
        AbastecimentoDTO abastecimentoDTO = abastecimentoMapper.toDto(updatedAbastecimento);

        restAbastecimentoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, abastecimentoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(abastecimentoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Abastecimento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAbastecimentoToMatchAllProperties(updatedAbastecimento);
    }

    @Test
    @Transactional
    void putNonExistingAbastecimento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        abastecimento.setId(longCount.incrementAndGet());

        // Create the Abastecimento
        AbastecimentoDTO abastecimentoDTO = abastecimentoMapper.toDto(abastecimento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAbastecimentoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, abastecimentoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(abastecimentoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Abastecimento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAbastecimento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        abastecimento.setId(longCount.incrementAndGet());

        // Create the Abastecimento
        AbastecimentoDTO abastecimentoDTO = abastecimentoMapper.toDto(abastecimento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAbastecimentoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(abastecimentoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Abastecimento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAbastecimento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        abastecimento.setId(longCount.incrementAndGet());

        // Create the Abastecimento
        AbastecimentoDTO abastecimentoDTO = abastecimentoMapper.toDto(abastecimento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAbastecimentoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(abastecimentoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Abastecimento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAbastecimentoWithPatch() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the abastecimento using partial update
        Abastecimento partialUpdatedAbastecimento = new Abastecimento();
        partialUpdatedAbastecimento.setId(abastecimento.getId());

        partialUpdatedAbastecimento.volumeAbastecido(UPDATED_VOLUME_ABASTECIDO).improperData(UPDATED_IMPROPER_DATA);

        restAbastecimentoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAbastecimento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAbastecimento))
            )
            .andExpect(status().isOk());

        // Validate the Abastecimento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAbastecimentoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAbastecimento, abastecimento),
            getPersistedAbastecimento(abastecimento)
        );
    }

    @Test
    @Transactional
    void fullUpdateAbastecimentoWithPatch() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the abastecimento using partial update
        Abastecimento partialUpdatedAbastecimento = new Abastecimento();
        partialUpdatedAbastecimento.setId(abastecimento.getId());

        partialUpdatedAbastecimento
            .idPosto(UPDATED_ID_POSTO)
            .dataHora(UPDATED_DATA_HORA)
            .tipoCombustivel(UPDATED_TIPO_COMBUSTIVEL)
            .precoPorLitro(UPDATED_PRECO_POR_LITRO)
            .volumeAbastecido(UPDATED_VOLUME_ABASTECIDO)
            .cpfMotorista(UPDATED_CPF_MOTORISTA)
            .improperData(UPDATED_IMPROPER_DATA);

        restAbastecimentoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAbastecimento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAbastecimento))
            )
            .andExpect(status().isOk());

        // Validate the Abastecimento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAbastecimentoUpdatableFieldsEquals(partialUpdatedAbastecimento, getPersistedAbastecimento(partialUpdatedAbastecimento));
    }

    @Test
    @Transactional
    void patchNonExistingAbastecimento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        abastecimento.setId(longCount.incrementAndGet());

        // Create the Abastecimento
        AbastecimentoDTO abastecimentoDTO = abastecimentoMapper.toDto(abastecimento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAbastecimentoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, abastecimentoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(abastecimentoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Abastecimento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAbastecimento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        abastecimento.setId(longCount.incrementAndGet());

        // Create the Abastecimento
        AbastecimentoDTO abastecimentoDTO = abastecimentoMapper.toDto(abastecimento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAbastecimentoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(abastecimentoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Abastecimento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAbastecimento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        abastecimento.setId(longCount.incrementAndGet());

        // Create the Abastecimento
        AbastecimentoDTO abastecimentoDTO = abastecimentoMapper.toDto(abastecimento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAbastecimentoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(abastecimentoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Abastecimento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAbastecimento() throws Exception {
        // Initialize the database
        insertedAbastecimento = abastecimentoRepository.saveAndFlush(abastecimento);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the abastecimento
        restAbastecimentoMockMvc
            .perform(delete(ENTITY_API_URL_ID, abastecimento.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return abastecimentoRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Abastecimento getPersistedAbastecimento(Abastecimento abastecimento) {
        return abastecimentoRepository.findById(abastecimento.getId()).orElseThrow();
    }

    protected void assertPersistedAbastecimentoToMatchAllProperties(Abastecimento expectedAbastecimento) {
        assertAbastecimentoAllPropertiesEquals(expectedAbastecimento, getPersistedAbastecimento(expectedAbastecimento));
    }

    protected void assertPersistedAbastecimentoToMatchUpdatableProperties(Abastecimento expectedAbastecimento) {
        assertAbastecimentoAllUpdatablePropertiesEquals(expectedAbastecimento, getPersistedAbastecimento(expectedAbastecimento));
    }
}
