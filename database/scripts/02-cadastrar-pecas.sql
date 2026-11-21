USE professor_rogerio;

INSERT INTO pecas_nave (nome, descricao)
VALUES
    ('Casco de Proteção', 'Protege a nave contra os perigos do espaço.'),
    ('Motores e Propulsores', 'Permitem movimentar a nave e mudar sua direção.'),
    ('Sistema de Energia', 'Fornece energia para todos os equipamentos da nave.'),
    ('Controle de Temperatura', 'Mantém a nave na temperatura adequada.'),
    ('Sistema de Orientação', 'Permite controlar para onde a nave está apontando.'),
    ('Sistema de Navegação', 'Ajuda a identificar a posição da nave e definir seu caminho.')
ON DUPLICATE KEY UPDATE
    descricao = VALUES(descricao);
