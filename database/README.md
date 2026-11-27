 <div align="center">

<img src="../resources/images/star-divider-1.svg" width="100%" alt="" />

<img src="https://img.shields.io/badge/🗄️_database-8B5CF6?style=for-the-badge&logoColor=white" alt="database" />

<sub>✦ projeto-livre • Carroll Studios ✦</sub>

</div>

<br/>

<div align="center">

# Banco de Dados

**Professor Rogério: Fora de Órbita**

Responsável pelo armazenamento e gerenciamento das informações das partidas, fases, puzzles e do progresso dos jogadores.

<br/>

<img src="https://img.shields.io/badge/MySQL-8.0.46-4479A1?style=flat-square&logo=mysql&logoColor=white" alt="MySQL" />
<img src="https://img.shields.io/badge/Java-POO-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java" />
<img src="https://img.shields.io/badge/JDBC-Connector%2FJ-8B5CF6?style=flat-square" alt="JDBC" />

<br/><br/>

<img src="https://img.shields.io/badge/📈_DER-8B5CF6?style=flat-square" alt="DER" />
<img src="https://img.shields.io/badge/🧩_DL-6D28D9?style=flat-square" alt="DL" />
<img src="https://img.shields.io/badge/💾_scripts-EC4899?style=flat-square" alt="Scripts" />

</div>

<br/>

## 🪐 Sobre o projeto

A parte Database tem como objetivo estruturar e gerenciar os dados necessários para acompanhar o progresso dos jogadores em **Professor Rogério: Fora de Órbita**.

O banco utiliza MySQL, enquanto a comunicação com a aplicação é realizada por meio de Java e JDBC.

## 🛠️ Tecnologias utilizadas

| Tecnologia | Finalidade |
|---|---|
| MySQL 8.0.46 | Gerenciamento do banco de dados. |
| Java | Implementação da persistência. |
| JDBC | Comunicação entre Java e MySQL. |
| MySQL Connector/J | Driver de conexão. |
| SQL | Criação e manipulação dos dados. |
| Git e GitHub | Versionamento do projeto. |

## 🗂️ Organização

| Diretório | Conteúdo |
|---|---|
| `database/DER/` | Diagramas Entidade-Relacionamento. |
| `database/DL/` | Diagramas Lógicos. |
| `database/scripts/tabelas/` | Scripts de criação e atualização das tabelas. |
| `database/scripts/dados/` | Scripts de cadastro dos dados iniciais. |
| `database/scripts/consultas/` | Consultas e testes SQL. |
| `src/banco/` | Classes de conexão e modelos. |
| `src/banco/dao/` | Classes responsáveis pelas operações de persistência. |
| `lib/` | Bibliotecas utilizadas pelo projeto. |

## 🗄️ Estrutura do banco de dados

**Nome do banco:** `professor_rogerio`

O banco é composto por sete tabelas:

| Tabela | Finalidade |
|---|---|
| `fases` | Cadastro das fases do jogo. |
| `pecas_nave` | Peças da nave associadas às fases. |
| `tipos_puzzle` | Cadastro dos tipos de puzzles. |
| `partida` | Registro das partidas dos jogadores. |
| `tentativa_fase` | Histórico das tentativas realizadas em cada fase. |
| `tentativa_puzzle` | Registro dos puzzles de cada tentativa de fase. |
| `evento_puzzle` | Registro dos eventos ocorridos durante os puzzles. |

### 🔗 Relacionamentos

As tabelas se relacionam por meio de chaves estrangeiras
