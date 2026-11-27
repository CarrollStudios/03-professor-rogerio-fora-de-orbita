CREATE TABLE fases (
    id_fase INT AUTO_INCREMENT PRIMARY KEY,
    numero_fase INT NOT NULL UNIQUE,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT
);

CREATE TABLE pecas_nave (
    id_peca INT AUTO_INCREMENT PRIMARY KEY,
    id_fase INT NOT NULL,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    FOREIGN KEY (id_fase) REFERENCES fases(id_fase)
);

CREATE TABLE tipos_puzzle (
    id_tipo_puzzle INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT
);

CREATE TABLE partida (
    id_partida INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'em_andamento',
    criada_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizada_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
