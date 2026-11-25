 <div align="center">

<img src="../resources/images/star-divider-1.svg" width="100%" alt="" />

<img src="https://img.shields.io/badge/🗄️_database-8B5CF6?style=for-the-badge&logoColor=white" alt="database" />

<sub>✦ projeto-livre • Carroll Studios ✦</sub>

</div>

<br/>

<div align="center">

Banco de dados responsável pelo armazenamento das informações e do progresso dos jogadores no jogo **Professor Rogério: Fora de Órbita**.

<br/>

<img src="https://img.shields.io/badge/📈_DER-8B5CF6?style=flat-square&logoColor=white" alt="DER" />
<img src="https://img.shields.io/badge/🧩_DL-6D28D9?style=flat-square&logoColor=white" alt="DL" />
<img src="https://img.shields.io/badge/💾_scripts-EC4899?style=flat-square&logoColor=white" alt="scripts" />

</div>

<br/>

## 🗂️ Organização

O projeto mantém separados os arquivos responsáveis pela estrutura do banco de dados e a implementação Java responsável pela comunicação com o MySQL.

| 🪐 Diretório | Conteúdo |
|---|---|
| `database/DER/` | Diagramas Entidade-Relacionamento. |
| `database/DL/` | Diagramas Lógicos do banco de dados. |
| `database/scripts/tabelas/` | Scripts SQL para criação das tabelas. |
| `database/scripts/dados/` | Scripts SQL para inserção dos dados iniciais. |
| `database/scripts/puzzles/` | Scripts relacionados aos dados dos puzzles, quando necessários. |
| `database/scripts/consultas/` | Consultas SQL utilizadas no projeto e testes dos relacionamentos. |
| `src/banco/` | Classes Java de conexão e entidades relacionadas ao banco de dados. |
| `src/banco/dao/` | Classes DAO responsáveis pelas operações de persistência e recuperação dos dados. |

## 🗄️ Banco de dados

O projeto utiliza o **MySQL** para armazenar as informações relacionadas às partidas, fases, puzzles e ao progresso dos jogadores.

**Nome do banco:** `professor_rogerio`

### 📚 Tabelas

| 🗃️ Tabela | Finalidade |
|---|---|
| `fases` | Armazena as três fases do jogo. |
| `pecas_nave` | Cadastra
