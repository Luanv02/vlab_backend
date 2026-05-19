# V-Lab Gateway Backend

## Desafio Técnico: API Gateway para Gestão de Abastecimentos

Este projeto é a resolução do desafio técnico para desenvolvimento de uma API Gateway voltada para a gestão de abastecimentos e motoristas. O sistema foi construído utilizando Java 21, Spring Boot 3 e JHipster 8, com foco em automatização de infraestrutura, segurança customizada e regras de negócio de detecção de anomalias.

---

### Tecnologias e Pré-requisitos

Para executar este projeto localmente, certifique-se de ter as seguintes ferramentas devidamente instaladas em sua máquina:

- Java Development Kit (JDK) 21: Necessário para compilar o código-fonte.
- Docker & Docker Compose: Essencial para orquestrar e rodar os containers do banco de dados e da aplicação. O Docker Desktop deve estar ativo em background.
- Node.js (LTS) & npm: Utilizados pelo JHipster (sob o capô) para gerenciar ferramentas de build e formatação (como o Prettier).
- JHipster CLI: Para geração, manutenção e atualização do projeto. Instale globalmente com `npm install -g generator-jhipster`.
- Git: Para clonar e versionar o repositório.
- (Opcional) Postman / Insomnia: Para testes de chamadas de API, embora o projeto já conte com o Swagger UI embutido.

---

### Como rodar

A infraestrutura foi simplificada e unificada para rodar com o menor esforço possível, garantindo que as dependências subam na ordem correta.

Importante: O arquivo `docker-compose.yml` na raiz do projeto serve apenas como um ponto de entrada e contém um `include` que referencia o arquivo `src/main/docker/app.yml` gerado pelo JHipster. Toda a configuração de dependências, como a ordem de subida dos containers, healthchecks e o controle para que a aplicação principal (vlabgateway-app) só inicie após o banco de dados (PostgreSQL) estar pronto, está definida dentro desse arquivo incluído (`app.yml`).

#### Passo a Passo

1. Clone o repositório:

   ```bash
   git clone https://github.com/seu-usuario/vlab-gateway.git
   cd vlab-gateway
   ```

2. Gere a imagem Docker da aplicação:

   Antes de subir os containers, precisamos empacotar nosso código Java em uma imagem Docker. Rode o comando abaixo (ele utiliza o plugin Jib do Google):

   ```bash
   ./mvnw package -Pprod verify jib:dockerBuild -DskipTests
   ```

   (Aguarde a mensagem de BUILD SUCCESS)

3. Suba a infraestrutura:

   Com a imagem gerada, orquestre a subida do banco e do app com um único comando:

   ```bash
   docker compose up
   ```

4. Acesse a Documentação Viva:

   Assim que o terminal indicar que a aplicação Spring Boot iniciou (na porta 8080), acesse o Swagger UI pelo seu navegador:

   http://localhost:8080/swagger-ui/index.html

---

### Endpoints Implementados

O projeto expõe a API REST no prefixo `/api/v1/`.

- GET /api/v1/abastecimentos: (Público) Lista todo o histórico de abastecimentos registrados.
- GET /api/v1/motoristas: (Público) Lista os motoristas cadastrados.
- POST /api/v1/abastecimentos: (Protegido) Endpoint de ingestão de dados.
  - Segurança: Requer a passagem de uma API Key através do header `X-API-Key` (Chave configurada: `senha-pesadona-123`).
  - Regra de Negócio (Anomalia): Se o `precoPorLitro` do abastecimento exceder 25% da média mockada no sistema para aquele tipo de combustível, a API intercepta a requisição e salva o registro automaticamente com a flag `"improperData": true`.

Swagger UI & OpenAPI: (Públicos) Toda a interface gráfica e o JSON da documentação (`/v3/api-docs/`) foram abertos no Security Filter Chain para não exigir autenticação, facilitando os testes técnicos.

---

### Decisões: O que eu escrevi vs. O que o JHipster gerou

Este projeto utilizou o JHipster como scaffold inicial, o que acelerou a entrega da base do projeto, mas exigiu refatorações profundas para atender aos requisitos específicos da V-Lab.

O que o JHipster gerou:

- A estrutura inicial do Spring Boot e configurações base.
- Entidades baseadas no arquivo modelo.jdl e a estrutura de classes (Controller, Service, Repository, DTO).
- Configuração padrão de segurança baseada em JWT.
- Scripts de versionamento de banco via Liquibase.
- Arquivos de configuração e propriedades específicas do JHipster (ver comentários em `application.yml`, `application-dev.yml`, `application-prod.yml`).
- Integração com ferramentas de monitoramento e logging (ex: Prometheus, Logback, etc).

O que eu escrevi (Customizações):

- Filtro Customizado de Segurança (ApiKeyFilter): O JHipster travou toda a aplicação com JWT. Eu reescrevi a SecurityConfiguration liberando as rotas da API (`/api/v1/` e `/swagger-ui/`) e criei um `OncePerRequestFilter` do zero para interceptar e validar especificamente o POST de abastecimentos utilizando o header estático `X-API-Key`.
- Regra de Anomalia no Service: Inserção da lógica de detecção de preços abusivos (regra dos 25%) diretamente na camada de serviço (AbastecimentoService).
- Validações Automáticas: Adição de `@Valid` e anotações como `@CPF` e `@Positive` diretamente nos DTOs, forçando o Controller a retornar `400 Bad Request` antes mesmo da requisição chegar ao banco.
- Unificação do Docker: Criação de um `docker-compose.yml` na raiz usando a funcionalidade de include para reaproveitar os arquivos gerados pelo JHipster, abstraindo a complexidade de rodar caminhos longos de pastas para o usuário final.

---

### Pendências e Trade-offs

Apesar de cobrir os requisitos essenciais, algumas decisões foram tomadas visando o escopo de tempo de um desafio técnico:

- API Key Mockada no `application.yml`: A chave `X-API-Key` está sendo lida do arquivo de configuração (`application.yml`). Em um cenário produtivo real, essa informação estaria armazenada em um cofre de chaves (como o AWS Secrets Manager ou HashiCorp Vault) ou lida via variáveis de ambiente injetadas por uma pipeline CI/CD.
- Média de Preços Mockada: A regra de anomalia dos 25% utiliza uma média de preços "chumbada" no código (`MOCK_MEDIA_GASOLINA = 6.00`). Idealmente, isso deveria ser uma chamada ao banco de dados agrupando a média móvel dos abastecimentos dos últimos 30 dias para aquele tipo específico de combustível.
- Cobertura de Testes Unitários: Para a entrega dos requisitos de núcleo, o foco foi na integração real (Docker/PostgreSQL) e regras de segurança. Há espaço para ampliar os testes unitários (JUnit) em cima da classe `ApiKeyFilter` e do comportamento de flag do `AbastecimentoService`.
- Carga de Dados Base (Datafaker): Como próximo passo, seria interessante implementar um `CommandLineRunner` utilizando bibliotecas como Datafaker ou gerar scripts prévios no Liquibase para que o banco já nascesse com histórico de motoristas e abastecimentos no primeiro `docker compose up`.
