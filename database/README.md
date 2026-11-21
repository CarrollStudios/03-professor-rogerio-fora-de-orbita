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
| 📈 `DER/` | Diagrama Entidade-Relacionamento. |
| 🧩 `DL/` | Diagrama Lógico do banco de dados. |
| 💾 `scripts/` | Scripts SQL para criação das tabelas e cadastro das peças da nave. |

## 🗄️ Banco de dados

O projeto utiliza o MySQL para armazenar os dados relacionados ao funcionamento do jogo.

**Nome do banco:** `professor_rogerio`

### Tabelas

| Tabela | Finalidade |
|---|---|
| `jogadores` | Armazena a identificação e o nome dos jogadores. |
| `progresso_jogo` | Registra o estado atual, a fase e o ponto salvo de cada jogador. |
| `pecas_nave` | Armazena as peças disponíveis para a nave. |
| `pecas_conquistadas` | Registra as peças conquistadas por cada jogador. |

## ⚙️ Scripts SQL

Os scripts estão organizados na pasta `scripts/`:

1. `01-criar-tabelas-jogo.sql` — cria o banco de dados e suas tabelas.
2. `02-cadastrar-pecas.sql` — cadastra as peças da nave.

### Ordem de execução

Os scripts devem ser executados na ordem numérica apresentada, começando pela criação do banco e das tabelas e, em seguida, pelo cadastro das peças.

## 🔗 Integração

O banco de dados do jogo é independente do banco utilizado anteriormente pelo sistema de login. Dessa forma, as informações de autenticação e os dados de progresso do jogo permanecem separados.

<img src="../resources/images/star-divider-2.svg" width="100%" alt="" />o

