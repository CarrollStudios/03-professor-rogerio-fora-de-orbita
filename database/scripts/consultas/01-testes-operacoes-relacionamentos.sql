SELECT 'fases' AS tabela, COUNT(*) AS total FROM fases
UNION ALL
SELECT 'pecas_nave', COUNT(*) FROM pecas_nave
UNION ALL
SELECT 'tipos_puzzle', COUNT(*) FROM tipos_puzzle
UNION ALL
SELECT 'partida', COUNT(*) FROM partida
UNION ALL
SELECT 'tentativa_fase', COUNT(*) FROM tentativa_fase
UNION ALL
SELECT 'tentativa_puzzle', COUNT(*) FROM tentativa_puzzle
UNION ALL
SELECT 'evento_puzzle', COUNT(*) FROM evento_puzzle;

SELECT
    partida.id_partida,
    partida.id_usuario,
    tentativa_fase.id_fase,
    tentativa_puzzle.id_tipo_puzzle,
    tentativa_puzzle.id_peca,
    evento_puzzle.tipo_evento,
    evento_puzzle.valor,
    evento_puzzle.custo_oxigenio,
    evento_puzzle.oxigenio_depois
FROM partida
INNER JOIN tentativa_fase
    ON partida.id_partida = tentativa_fase.id_partida
INNER JOIN tentativa_puzzle
    ON tentativa_fase.id_tentativa_fase = tentativa_puzzle.id_tentativa_fase
INNER JOIN evento_puzzle
    ON tentativa_puzzle.id_tentativa_puzzle = evento_puzzle.id_tentativa_puzzle;
