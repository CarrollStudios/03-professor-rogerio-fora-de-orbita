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

| 🪐 Pasta | Conteúdo |
|---|---|
| 📈 `DER/` | Diagramas Entidade-Relacionamento. |
| 🧩 `DL/` | Diagramas Lógicos do banco de dados. |
| 📋 `scripts/tabelas/` | Scripts SQL para criação das tabelas. |
| 📦 `scripts/dados/` | Scripts SQL para inserção dos dados iniciais. |
| 🧩 `scripts/puzzles/` | Scripts relacionados aos dados dos puzzles, quando necessários. |
| 🔎 `scripts/consultas/` | Consultas SQL utilizadas no projeto. |

## 🗄️ Banco de dados

O projeto utiliza o **MySQL** para armazenar as informações relacionadas às partidas, fases, puzzles e ao progresso dos jogadores.

**Nome do banco:** `professor_rogerio`

### 📚 Tabelas

| 🗃️ Tabela | Finalidade |
|---|---|
| `fases` | Armazena as três fases do jogo. |
| `pecas_nave` | Cadastra as seis peças necessárias para a montagem da nave. |
| `tipos_puzzle` | Identifica os seis tipos de puzzles disponíveis. |
| `partida` | Registra as partidas dos jogadores. |
| `tentativa_fase` | Registra as tentativas realizadas em cada fase, incluindo o oxigênio inicial e o status. |
| `tentativa_puzzle` | Registra os puzzles de cada tentativa, suas sementes, conteúdos e peças relacionadas. |
| `evento_puzzle` | Registra os acontecimentos dos puzzles, como respostas, dicas utilizadas e consumo de oxigênio. |

## ⚙️ Funcionamento e persistência

O banco de dados considera as regras de funcionamento do jogo:

- 🚀 Cada partida possui três fases.
- 🧩 Cada fase apresenta dois puzzles.
- 🔧 Cada puzzle está relacionado à recuperação de uma peça da nave.
- 💨 O jogador inicia cada fase com quatro unidades de oxigênio.
- 🔄 Ao ocorrer Game Over, uma nova tentativa é iniciada na mesma fase.
- 📚 As tentativas anteriores são preservadas para manter o histórico.
- ⭐ O progresso das fases já concluídas é mantido.

As informações de perguntas, sementes, dicas e progresso são consideradas na estrutura de persistência, conforme o funcionamento implementado no jogo.

## 🔗 Integração

O banco de dados do jogo é independente do armazenamento utilizado pelo sistema de login.

A identificação do jogador será recebida por meio da integração com o sistema responsável pela autenticação, permitindo relacionar cada partida ao respectivo usuário.

<br/>

<img src="../resources/images/star-divider-2.svg" width="100%" alt="" />
