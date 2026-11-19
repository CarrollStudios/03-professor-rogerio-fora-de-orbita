USE sistema_login;

CREATE TABLE IF NOT EXISTS naves (
    id_nave INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE,
    cor VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS progresso_jogo (
    id_usuario INT NOT NULL PRIMARY KEY,
    id_nave INT DEFAULT NULL,
    estado_jogo VARCHAR(30) NOT NULL DEFAULT 'INTRODUCAO',
    fase_atual INT DEFAULT 1,
    ponto_salvo VARCHAR(100) DEFAULT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_nave) REFERENCES naves(id_nave)
);

CREATE TABLE IF NOT EXISTS pecas_nave (
    id_peca INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS pecas_conquistadas (
    id_usuario INT NOT NULL,
    id_peca INT NOT NULL,
    data_conquista TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario, id_peca),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_peca) REFERENCES pecas_nave(id_peca)
);

INSERT INTO naves (nome, cor)
VALUES
    ('Estrela', 'Rosa'),
    ('Cometa', 'Azul'),
    ('Aurora', 'Amarela')
ON DUPLICATE KEY UPDATE cor = VALUES(cor);

INSERT INTO pecas_nave (nome, descricao)
VALUES
    ('Casco de Proteção', 'Protege a nave contra os perigos do espaço.'),
    ('Motores e Propulsores', 'Permitem movimentar a nave e mudar sua direção.')
ON DUPLICATE KEY UPDATE descricao = VALUES(descricao);
