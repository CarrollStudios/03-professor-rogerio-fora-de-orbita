CREATE TABLE tentativa_fase (
    id_tentativa_fase INT AUTO_INCREMENT PRIMARY KEY,
    id_partida INT NOT NULL,
    id_fase INT NOT NULL,
    numero_tentativa INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'em_andamento',
    oxigenio_inicial INT NOT NULL DEFAULT 4,
    iniciada_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    finalizada_em TIMESTAMP NULL DEFAULT NULL,
    UNIQUE (id_partida, id_fase, numero_tentativa),
    FOREIGN KEY (id_partida) REFERENCES partida(id_partida),
    FOREIGN KEY (id_fase) REFERENCES fases(id_fase)
);

CREATE TABLE tentativa_puzzle (
    id_tentativa_puzzle INT AUTO_INCREMENT PRIMARY KEY,
    id_tentativa_fase INT NOT NULL,
    id_tipo_puzzle INT NOT NULL,
    id_peca INT NOT NULL,
    ordem INT NOT NULL,
    semente INT,
    versao_conteudo INT,
    status VARCHAR(30) NOT NULL DEFAULT 'em_andamento',
    conteudo JSON NULL,
    UNIQUE (id_tentativa_fase, ordem),
    FOREIGN KEY (id_tentativa_fase) REFERENCES tentativa_fase(id_tentativa_fase),
    FOREIGN KEY (id_tipo_puzzle) REFERENCES tipos_puzzle(id_tipo_puzzle),
    FOREIGN KEY (id_peca) REFERENCES pecas_nave(id_peca)
);

CREATE TABLE evento_puzzle (
    id_evento INT AUTO_INCREMENT PRIMARY KEY,
    id_tentativa_puzzle INT NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL,
    valor TEXT,
    correto BOOLEAN,
    custo_oxigenio INT NOT NULL DEFAULT 0,
    oxigenio_depois INT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_tentativa_puzzle) REFERENCES tentativa_puzzle(id_tentativa_puzzle)
);
