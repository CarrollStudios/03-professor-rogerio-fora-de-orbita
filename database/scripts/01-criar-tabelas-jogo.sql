CREATE DATABASE IF NOT EXISTS professor_rogerio;

USE professor_rogerio;

CREATE TABLE IF NOT EXISTS jogadores (
    id_jogador INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS progresso_jogo (
    id_jogador INT NOT NULL PRIMARY KEY,
    estado_jogo VARCHAR(30) NOT NULL DEFAULT 'INTRODUCAO',
    fase_atual INT DEFAULT 1,
    ponto_salvo VARCHAR(100) DEFAULT NULL,
    FOREIGN KEY (id_jogador) REFERENCES jogadores(id_jogador)
);

CREATE TABLE IF NOT EXISTS pecas_nave (
    id_peca INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS pecas_conquistadas (
    id_jogador INT NOT NULL,
    id_peca INT NOT NULL,
    data_conquista TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_jogador, id_peca),
    FOREIGN KEY (id_jogador) REFERENCES jogadores(id_jogador),
    FOREIGN KEY (id_peca) REFERENCES pecas_nave(id_peca)
);
