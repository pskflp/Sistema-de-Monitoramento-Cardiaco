# Sistema de Monitoramento Cardíaco

API REST para acompanhamento de saúde cardíaca. Permite que usuários criem conta, façam
login, registrem indicadores de saúde (pressão arterial, frequência cardíaca, oxigenação,
peso e sintomas) e obtenham um relatório com estatísticas, histórico e alertas
de risco.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 4 (Spring Framework 7)
- Spring Web MVC (camada REST)
- Spring Data JPA / Hibernate (persistência)
- Bean Validation (validação de entrada)
- H2 (banco de dados em memória, usado em execução e nos testes)
- Lombok (redução de código repetitivo)
- springdoc-openapi (documentação Swagger UI)
- JUnit 5 e Mockito (testes)
- Maven (build e gerenciamento de dependências)

## Arquitetura

O projeto adota uma arquitetura em camadas. Cada camada possui uma responsabilidade única e se
comunica apenas com a camada imediatamente adjacente, o que reduz o acoplamento e facilita
manutenção e testes.

O fluxo de uma requisição percorre as camadas da seguinte forma:

```
Cliente HTTP 
        |
        v
  Controller  ......  recebe a requisição HTTP, valida o corpo (@Valid) e devolve a resposta
        |
        v
   Service    ......  contém as regras de negócio e orquestra as operações
        |
        v
  Repository  ......  abstrai o acesso ao banco de dados (Spring Data JPA)
        |
        v
   Banco de dados (H2 em memória)
```

Objetos de transferência (DTOs) trafegam entre o cliente e o controller, isolando o modelo
de persistência (entidades JPA) da interface pública da API. Isso evita expor diretamente
as entidades e permite controlar exatamente quais dados entram e saem da aplicação.

### Responsabilidade de cada camada

- **Controller**: expõe os endpoints REST, recebe e valida os dados de entrada, delega o
  processamento ao service e monta a resposta HTTP (status e corpo). Não contém regra de
  negócio.
- **Service**: concentra a lógica de negócio (por exemplo, verificar e-mail duplicado,
  conferir senha, calcular estatísticas e alertas do relatório). É a camada testada de
  forma isolada pelos testes unitários.
- **Repository**: interface que estende `JpaRepository`. O Spring Data JPA gera a
  implementação automaticamente, fornecendo operações de persistência e consultas derivadas
  do nome dos métodos.
- **Model (entidades)**: classes anotadas com `@Entity` que representam as tabelas do banco.
- **DTO**: objetos de entrada (Create/Request) e saída (Response) que definem o contrato da
  API, desacoplados das entidades.

## Modularização

O código é organizado em pacotes por responsabilidade técnica, dentro do pacote base
`ES2.MonitoramentoCardiaco`. Cada pacote corresponde a uma das camadas descritas acima:

- **`controller`**: pontos de entrada da API (endpoints REST).
  - `UsuarioController` — cadastro e login de usuários.
  - `MonitoramentoController` — registro e listagem de monitoramentos.
  - `RelatorioController` — geração do relatório de saúde cardíaca.
- **`service`**: regras de negócio.
  - `UsuarioService` — criação de conta (com validação de senha e e-mail único) e login.
  - `MonitoramentoService` — registro e listagem de monitoramentos por usuário.
  - `RelatorioService` — cálculo de estatísticas, contagem de sintomas e geração de alertas.
- **`repository`**: acesso a dados.
  - `UsuarioRepository`, `MonitoramentoRepository`.
- **`model`**: entidades de persistência.
  - `Usuario`, `MonitoramentoCardiaco`.
- **`dto`**: contratos de entrada e saída.
  - `UsuarioCreateDTO`, `LoginRequestDTO`, `UsuarioResponseDTO`,
    `MonitoramentoCreateDTO`, `MonitoramentoResponseDTO`,
    `RelatorioResponseDTO`, `EstatisticaDTO`.
- **`MonitoramentoCardiacoApplication`**: classe principal que inicializa a aplicação
  Spring Boot.

Essa separação por pacotes torna a estrutura modular: cada funcionalidade (usuário,
monitoramento, relatório) é representada de forma consistente em todas as camadas, e novas
funcionalidades podem ser adicionadas seguindo o mesmo padrão, sem impactar as existentes.

## Estrutura de diretórios

```
src
├── main
│   ├── java/ES2/MonitoramentoCardiaco
│   │   ├── MonitoramentoCardiacoApplication.java
│   │   ├── controller
│   │   │   ├── UsuarioController.java
│   │   │   ├── MonitoramentoController.java
│   │   │   └── RelatorioController.java
│   │   ├── service
│   │   │   ├── UsuarioService.java
│   │   │   ├── MonitoramentoService.java
│   │   │   └── RelatorioService.java
│   │   ├── repository
│   │   │   ├── UsuarioRepository.java
│   │   │   └── MonitoramentoRepository.java
│   │   ├── model
│   │   │   ├── Usuario.java
│   │   │   └── MonitoramentoCardiaco.java
│   │   └── dto
│   │       ├── UsuarioCreateDTO.java
│   │       ├── LoginRequestDTO.java
│   │       ├── UsuarioResponseDTO.java
│   │       ├── MonitoramentoCreateDTO.java
│   │       ├── MonitoramentoResponseDTO.java
│   │       ├── RelatorioResponseDTO.java
│   │       └── EstatisticaDTO.java
│   └── resources
│       └── application.properties
└── test
    ├── java/ES2/MonitoramentoCardiacoTeste
    │   ├── MonitoramentoCardiacoApplicationTests.java
    │   ├── UsuarioServiceTest.java
    │   ├── MonitoramentoServiceTest.java
    │   ├── RelatorioServiceTest.java
    │   └── integration
    │       ├── UsuarioControllerIT.java
    │       ├── MonitoramentoControllerIT.java
    │       └── RelatorioControllerIT.java
    └── resources
        └── application.properties
```

## Endpoints

| Método | Caminho                              | Descrição                              |
|--------|--------------------------------------|----------------------------------------|
| POST   | `/usuarios/cadastro`                 | Cria uma nova conta de usuário         |
| POST   | `/usuarios/login`                    | Autentica o usuário                    |
| POST   | `/monitoramentos`                    | Registra um monitoramento cardíaco     |
| GET    | `/monitoramentos/usuario/{usuarioId}`| Lista os monitoramentos de um usuário  |
| GET    | `/relatorios/usuario/{usuarioId}`    | Gera o relatório de saúde cardíaca     |

## Como executar

### Pré-requisitos

- Java 17 instalado.

Não é necessário instalar nenhum banco de dados. A aplicação usa o H2 em memória, criado
automaticamente na inicialização, o que permite executar o projeto em qualquer máquina sem
configuração adicional.

### Subir a aplicação

```
./mvnw spring-boot:run
```

A aplicação fica disponível em `http://localhost:8080`.

Observação sobre os dados: por ser um banco em memória, todos os registros são reiniciados
sempre que a aplicação é parada. O console web do H2 (para inspecionar as tabelas durante a
execução) fica disponível em `http://localhost:8080/h2-console` (JDBC URL
`jdbc:h2:mem:monitoramento`, usuário `sa`, senha em branco).

## Documentação da API (Swagger)

Com a aplicação em execução, a documentação interativa fica disponível em:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Especificação OpenAPI (JSON): `http://localhost:8080/v3/api-docs`

## Testes

O projeto possui testes unitários e testes de integração.

- **Testes unitários** (sufixo `Test`): validam as classes de serviço de forma isolada,
  utilizando Mockito para simular as dependências. São executados pelo plugin Surefire.
- **Testes de integração** (sufixo `IT`): sobem o contexto completo do Spring e exercitam
  os endpoints de ponta a ponta (controller, service, repository e banco H2 em memória),
  usando MockMvc. São executados pelo plugin Failsafe.

Executar apenas os testes unitários:

```
./mvnw test
```

Executar os testes unitários e de integração:

```
./mvnw verify
```
