# Serviço Auditor — DLQ Consumer

Serviço Spring Boot responsável por consumir mensagens da fila Dead Letter Queue (DLQ) da AWS SQS, aplicar triagem de severidade e persistir os registros no banco de dados para análise posterior.

---

## Justificativa Arquitetural — Por que Hexagonal?

### O problema que a arquitetura resolve

Este serviço tem uma responsabilidade clara e delimitada: **receber mensagens que falharam, classificá-las por severidade e salvá-las no banco**. Parece simples, mas existem três fontes de complexidade que precisam ser gerenciadas:

1. **A origem da mensagem pode mudar** — hoje é SQS, amanhã pode ser Kafka, RabbitMQ ou uma chamada HTTP.
2. **O banco de dados pode mudar** — hoje é H2 (desenvolvimento), mas pode migrar para PostgreSQL ou outro.
3. **A regra de negócio (triagem 50/100) nunca deveria depender de nenhum dos dois acima.**

A Arquitetura Hexagonal (também chamada de *Ports and Adapters*) resolve exatamente isso: **isola o núcleo do sistema das suas dependências externas**.

### O princípio central

> "Dependências apontam para dentro. O domínio não conhece ninguém."

O `domain` não sabe que existe Spring, JPA, SQS ou Jackson. Ele é código Java puro. Isso significa que a regra de negócio pode ser testada sem subir nenhum servidor, sem conexão com banco e sem fila.

### Por que não MVC tradicional?

Em uma arquitetura MVC tradicional, o `Service` conhece o `Repository` diretamente. Isso cria um acoplamento: se o banco mudar, o service muda. Se a fila mudar, o controller muda. Em um serviço de auditoria — onde a confiabilidade e a manutenibilidade são críticas — esse acoplamento é inaceitável.

Com Hexagonal, o `ErroEventoService` nunca viu o JPA na vida. Ele só conhece a interface `ErroEventoRepositoryPort`. Quem implementa essa interface (JPA, MongoDB, arquivo CSV) é detalhe de infraestrutura.

### Por que usar BO (Business Object) separado do Service?

O `ErroEventoBO` concentra exclusivamente a regra dos 50/100. O `ErroEventoService` orquestra o fluxo. Essa separação garante que a regra de triagem possa ser alterada, testada e explicada de forma isolada — sem tocar no fluxo de orquestração.

---

## Estrutura de Pastas

```
SERVICO-AUDITOR/
├── .vscode/
└── demo/
    └── demo/
        ├── .mvn/
        │   └── wrapper/
        ├── src/
        │   ├── main/
        │   │   ├── java/
        │   │   │   └── com/example/demo/
        │   │   │       │
        │   │   │       ├── application/              → orquestra casos de uso
        │   │   │       │   ├── bo/
        │   │   │       │   │   └── ErroEventoBO.java         → regra de triagem (50/100)
        │   │   │       │   ├── dto/
        │   │   │       │   │   ├── OrderEventDTO.java        → estrutura do JSON da DLQ
        │   │   │       │   │   └── OrderItemDTO.java
        │   │   │       │   ├── ports/
        │   │   │       │   │   ├── in/
        │   │   │       │   │   │   └── ErroEventoServicePort.java    → contrato de entrada
        │   │   │       │   │   └── out/
        │   │   │       │   │       └── ErroEventoRepositoryPort.java → contrato de saída
        │   │   │       │   └── service/
        │   │   │       │       └── ErroEventoService.java    → orquestra o fluxo
        │   │   │       │
        │   │   │       ├── domain/                   → núcleo puro, zero dependências externas
        │   │   │       │   ├── enums/
        │   │   │       │   │   ├── Severity.java     → LOW | MEDIUM | HIGH
        │   │   │       │   │   └── Status.java       → PENDING_ANALYSIS
        │   │   │       │   └── model/
        │   │   │       │       └── ErroEvento.java   → entidade de domínio (POJO puro)
        │   │   │       │
        │   │   │       └── infrastructure/           → detalhes técnicos e frameworks
        │   │   │           ├── adapters/
        │   │   │           │   ├── in/
        │   │   │           │   │   └── sqs/
        │   │   │           │   │       └── SqsDlqAdapter.java        → @SqsListener DLQ
        │   │   │           │   └── out/
        │   │   │           │       └── persistence/
        │   │   │           │           ├── entity/
        │   │   │           │           │   └── ErroEventoEntity.java → @Entity JPA
        │   │   │           │           ├── mapper/
        │   │   │           │           │   └── ErroEventoMapper.java → domain → entity
        │   │   │           │           └── repository/
        │   │   │           │               ├── ErroEventoJpaRepository.java     → JpaRepository
        │   │   │           │               └── ErroEventoRepositoryAdapter.java → implements port
        │   │   │           └── config/
        │   │   │               ├── BeanConfig.java           → wiring dos ports
        │   │   │               └── DemoApplication.java      → @SpringBootApplication
        │   │   └── resources/
        │   │       └── application.properties
        │   └── test/
        │       └── java/com/example/demo/
        └── target/
```

---

## Fluxo de Processamento

```
DLQ (AWS SQS)
     ↓
SqsDlqAdapter              → recebe o payload bruto como String
     ↓
ErroEventoService          → deserializa o JSON
     ↓
ErroEventoBO               → soma os amounts → aplica regra 50/100 → define Severity
     ↓
ErroEvento                 → monta o objeto com UUID + PENDING_ANALYSIS + timestamp
     ↓
ErroEventoRepositoryAdapter → converte para Entity → salva no banco
     ↓
Banco de dados             → registro persistido com todos os campos do contrato
```

---

## Regra de Triagem de Severidade

| Total de itens (amount) | Severidade |
|------------------------|------------|
| Menor que 50           | LOW        |
| Entre 50 e 100         | MEDIUM     |
| Maior que 100          | HIGH       |

---

## Contrato da Tabela no Banco

| Campo      | Tipo      | Descrição                           |
|------------|-----------|-------------------------------------|
| error_id   | UUID      | Gerado automaticamente pelo serviço |
| queue_name | VARCHAR   | Nome da fila DLQ de origem          |
| payload    | TEXT      | JSON bruto da mensagem que falhou   |
| timestamp  | TIMESTAMP | Momento do processamento            |
| status     | VARCHAR   | Sempre PENDING_ANALYSIS             |
| severity   | VARCHAR   | LOW, MEDIUM ou HIGH                 |

---

## Tecnologias

- Java 17
- Spring Boot 3.2.5
- Spring Cloud AWS SQS 3.0.2
- Spring Data JPA
- H2 Database (desenvolvimento)
- AWS SQS (Dead Letter Queue)