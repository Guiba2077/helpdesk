# Helpdesk Back-End — API REST (Spring Boot 3 + PostgreSQL 16+)

> **Projeto:** Sistema de Controle de Chamados de TI (Helpdesk — Tema G3)  
> **Instituição:** SENAI FATESG — Análise e Desenvolvimento de Sistemas (ADS 4)  
> **Disciplina:** Gerenciamento de Banco de Dados / Desenvolvimento de Aplicações Fullstack  
> **Porta Padrão:** `8080` | **URL Base:** `http://localhost:8080/api`  
> **Documentação Interativa (Swagger/OpenAPI):** `http://localhost:8080/swagger-ui.html`

---

## 1. Proposta do Sistema

O **Helpdesk Back-End** é uma API REST desenvolvida em **Java 21 com Spring Boot 3**, projetada para operar como a camada de aplicação de um ecossistema corporativo de controle de chamados e incidentes de TI.

Diferente de aplicações convencionais que tratam o banco de dados como mero depósito de dados, este projeto foi desenhado sob o conceito de **Banco de Dados Ativo e Seguro**, transferindo a inteligência transacional, o compliance e o isolamento de privilégios para o **PostgreSQL 16+**:

- **Autenticação com Criptografia Nativa (`pgcrypto`)**: O hash e a conferência de senhas são processados nativamente via Blowfish (`bf`) no SQL do PostgreSQL.
- **Isolamento em Dois Schemas (`app` e `audit`)**: Divisão arquitetural entre o domínio operacional de chamados (`app`) e a trilha de auditoria imutável (`audit`).
- **Segurança RBAC (Role-Based Access Control)**: A aplicação conecta através de credenciais restritas com privilégios mínimos (`rl_escritor` e `rl_leitor`).
- **Dois Pools de Conexão Dedicados (HikariCP)**:
  - **Pool Primário (Escrita)**: Conecta com `usr_helpdesk_escritor` para operações transacionais e chamada da Stored Procedure.
  - **Pool Secundário (Leitura)**: Conecta com `usr_helpdesk_leitor` em modo estrito de leitura para relatórios e auditoria.
- **Objetos Programáveis no Banco**: O Back-End consome diretamente **2 Views**, **1 Stored Procedure**, **1 Stored Function** e é monitorado por **2 Triggers** de auditoria e linha do tempo de status.
- **Tratamento de Server Tuning (`statement_timeout = 3s`)**: Tratamento de exceção com retorno `HTTP 504 Gateway Timeout` caso uma consulta ultrapasse o tempo limite do PostgreSQL.

---

## 2. Stack Tecnológica

| Tecnologia / Ferramenta | Versão | Finalidade no Projeto |
| :--- | :---: | :--- |
| **Java** | `21 (LTS)` | Linguagem base da aplicação com suporte a Virtual Threads e Records. |
| **Spring Boot** | `3.4.x` | Framework principal para criação da API REST e injeção de dependências. |
| **Spring Data JPA / Hibernate** | `6.x` | Mapeamento objeto-relacional com schemas explícitos (`app` e `audit`). |
| **HikariCP** | `5.x` | Pool de conexões de alta performance (2 datasources: leitura e escrita). |
| **PostgreSQL JDBC Driver** | `42.7.x` | Conector nativo de banco de dados com suporte a Prepared Statements. |
| **SpringDoc OpenAPI (Swagger)** | `2.8.x` | Documentação viva interativa dos endpoints em `/swagger-ui.html`. |
| **Lombok** | `1.18.x` | Redução de boilerplate (`@Data`, `@Builder`, `@NoArgsConstructor`). |
| **Maven Wrapper** | `3.9.x` | Gerenciador de dependências e build automatizado sem necessidade de instalação global do Maven. |
| **Docker & Docker Compose** | — | Conteinerização do banco de dados PostgreSQL e PgBouncer. |

---

## 3. Arquitetura do Back-End

O código segue o padrão **monólito modular em camadas** inspirado na arquitetura de referência corporativa, separando responsabilidades de forma estrita:

```text
helpdesk-back/
├── src/main/java/br/com/edu/senai/fatesg/ads4/helpdesk_back/
│   ├── business/                     # Módulos de Domínio de Negócio
│   │   ├── usuarios/                 # Contas de operadores, técnicos e admins + Login pgcrypto
│   │   ├── tecnicos/                 # Gestão de técnicos, especialidades e disponibilidade
│   │   ├── chamados/                 # AGREGADO CENTRAL: abertura, alocação, histórico e apontamentos
│   │   ├── auditoria/                # Trilha imutável do schema audit (View 2)
│   │   └── relatorios/               # Function de métricas SLA e simulação de timeout
│   │
│   ├── core/                         # Núcleo Reutilizável da Aplicação
│   │   ├── controllers/              # GenericController e envelope ApiResponse<T>
│   │   ├── domains/                  # BaseModel (id, ativo)
│   │   ├── dtos/                     # BaseDTO
│   │   ├── exceptions/               # GlobalExceptionHandler, TimeoutBancoException (HTTP 504), NegocioException
│   │   ├── patterns/                 # SessionContextManager (Injeção de usuário na sessão do Postgres)
│   │   └── services/                 # GenericService e regras de negócio base
│   │
│   └── config/                       # Infraestrutura e Configurações
│       ├── DatabaseConfig.java       # Configuração dos 2 Pools Hikari (Escrita e Leitura)
│       ├── CorsConfig.java           # Liberação de CORS para o Front-End (Vite/React na porta 5173)
│       └── OpenApiConfig.java        # Metadados do Swagger
│
├── src/main/resources/
│   └── application.properties        # Credenciais das roles, URLs e parâmetros JPA
└── pom.xml                           # Dependências e plugins de build
```

---

## 4. Pré-Requisitos para Executar

Antes de iniciar o Back-End, certifique-se de possuir:

1. **Java JDK 21+** instalado e configurado na variável de ambiente `JAVA_HOME`.
2. **PostgreSQL 16+** ativo na porta `5432` com o banco `helpdesk` criado e o script `bd/backup.sql` executado.

> [!TIP]
> Para criar e carregar o banco de dados localmente com 1 clique, execute na raiz do projeto:
> ```powershell
> .\bd\setup-banco.ps1
> ```

---

## 5. Como Iniciar o Back-End

### Opção A: Execução Local com Maven Wrapper (Recomendado para Desenvolvimento)

1. Abra o terminal (PowerShell ou Bash) na pasta `helpdesk-back`:
   ```bash
   cd helpdesk-back
   ```

2. *(Opcional no Windows caso a variável JAVA_HOME não esteja setada globalmente)*:
   ```powershell
   $env:JAVA_HOME = "C:\Users\<SEU_USUARIO>\.jdks\ms-21.0.12.1"
   ```

3. Inicie o servidor Spring Boot através do Maven Wrapper:
   - **No Windows:**
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```
   - **No Linux/Mac:**
     ```bash
     ./mvnw spring-boot:run
     ```

4. A aplicação inicializará na porta `8080`.  
   Assim que você visualizar a mensagem:
   ```text
   Tomcat started on port 8080 (http) with context path '/'
   Started HelpdeskBackApplication in 3.5 seconds
   ```
   O Back-End estará pronto para receber requisições!

---

### Opção B: Execução via Docker Compose

Caso deseje rodar a infraestrutura completa (Banco de Dados PostgreSQL + Back-End) via containers:

1. Na raiz do projeto onde está o `docker-compose.yml`, execute:
   ```bash
   docker compose up -d
   ```

2. Para acompanhar os logs da aplicação:
   ```bash
   docker compose logs -f helpdesk-back
   ```

3. Para encerrar os containers:
   ```bash
   docker compose down
   ```

---

## 6. Endpoints Principais da API REST

A API possui **22 endpoints** estruturados. Todos utilizam o envelope padrão `ApiResponse<T>`:

| Método | Endpoint | Descrição / Objeto do Banco Envolvido | Perfil Mínimo |
| :---: | :--- | :--- | :---: |
| `POST` | `/api/auth/login` | Login com validação `pgcrypto` (`crypt(?, senha_hash) = senha_hash`). | Público |
| `GET` | `/api/usuarios` | Lista todos os usuários cadastrados no schema `app`. | Todos |
| `POST` | `/api/usuarios` | Cadastra novo operador, técnico ou admin com senha segura. | `ADMIN` |
| `GET` | `/api/tecnicos/especialidades`| Lista especialidades técnicas da tabela `app.especialidade`. | Todos |
| `POST` | `/api/tecnicos/especialidades`| Cadastra nova especialidade no banco. | `ADMIN` |
| `GET` | `/api/tecnicos` | Lista técnicos de TI para preenchimento de dropdowns. | Todos |
| `POST` | `/api/tecnicos` | Vincula um usuário ao papel de técnico (matrícula, nível N1/N2/N3). | `ADMIN` |
| `PATCH`| `/api/tecnicos/{id}/disponibilidade`| Alterna status de férias/pausa (`disponivel = true/false`). | `ADMIN` ou Próprio |
| `GET` | `/api/chamados` | Fila geral de chamados lida da **View 1** (`app.vw_chamados_detalhados`).| Todos |
| `POST` | `/api/chamados` | Abre novo chamado (status `ABERTO` + dispara **Trigger 1** de histórico).| `OPERADOR` ou `ADMIN` |
| `POST` | `/api/chamados/{id}/alocar` | Invoca a **Stored Procedure** (`app.sp_alocar_chamado`). Status $\rightarrow$ `ATENDIMENTO`. | `ADMIN` |
| `PATCH`| `/api/chamados/{id}/prioridade`| Altera prioridade e aciona a **Trigger 2** no schema `audit`. | `ADMIN` |
| `POST` | `/api/chamados/{id}/apontamentos`| Registra nota técnica de diagnóstico no chamado. | `TECNICO` ou `ADMIN` |
| `PATCH`| `/api/chamados/{id}/fechar` | Conclui o incidente com descrição da solução (status $\rightarrow$ `FECHADO`). | `TECNICO` ou `ADMIN` |
| `GET` | `/api/auditoria/prioridades`| Consulta o histórico de compliance lendo da **View 2** (`audit.vw_auditoria_prioridade`).| Todos |
| `GET` | `/api/relatorios/sla-tecnicos`| Retorna relatório analítico consumindo a **Stored Function** (`app.fn_metricas_sla_tecnico`).| Todos |
| `GET` | `/api/relatorios/simular-query-pesada`| Executa query de 4s para demonstrar o `statement_timeout = 3s` $\rightarrow$ `HTTP 504`.| Todos |

---

## 7. Como o Front-End se Comunica (Header `X-Usuario-Id`)

Para facilitar demonstrações ao vivo para a banca e evitar problemas de expiração de token JWT, o Back-End valida as ações a partir do ID do usuário logado enviado via cabeçalho HTTP:

```http
X-Usuario-Id: 1
```

- Quando um usuário faz login em `POST /api/auth/login`, o Back-End retorna seus dados (`id`, `nome`, `email`, `perfil`).
- O Front-End armazena esses dados no `localStorage`.
- A cada requisição (`POST`, `PUT`, `PATCH`), o Front injeta o `X-Usuario-Id` do usuário ativo.
- O Back-End utiliza o `SessionContextManager` para propagar esse ID para dentro da transação do PostgreSQL via `set_config('app.current_user_id', ...)`, garantindo que as **Triggers de Auditoria e Histórico gravem exatamente quem foi o autor da ação**.

---

## 8. Testes e Compilação

Para compilar e verificar a integridade de todas as classes, entidades e repositórios sem subir o servidor:

```powershell
.\mvnw.cmd test-compile
```

Para rodar os testes unitários automatizados:
```powershell
.\mvnw.cmd test
```

Para gerar o pacote executável `.jar`:
```powershell
.\mvnw.cmd clean package -DskipTests
# O arquivo gerado estará em: target/helpdesk-back-0.0.1-SNAPSHOT.jar
```

