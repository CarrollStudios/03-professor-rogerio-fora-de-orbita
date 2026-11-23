package jogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

/**
 * Perguntas de múltipla escolha (4 alternativas). Clique ou aperte as teclas 1 a 4.
 * As perguntas vêm de um banco (classe Perguntas) e são sorteadas a cada vez.
 * Dica do ET (fase 3): a 1ª mostra uma pista, a 2ª risca duas alternativas erradas.
 */
public class PuzzleQuiz extends Puzzle {

    private final String instrucao;
    private final Perguntas.Pergunta[] perguntas;
    private final boolean[][] riscada;     // alternativas riscadas pelo ET

    private int atual = 0;
    private int piscando = -1;
    private double tempoPisca = 0;
    private boolean piscaCerto;
    private double fim = -1;

    public PuzzleQuiz(SistemaNave sistema, String instrucao, Perguntas.Pergunta[] perguntas) {
        super(sistema);
        this.instrucao = instrucao;
        this.perguntas = perguntas;
        this.riscada = new boolean[perguntas.length][4];
    }

    @Override
    protected String instrucao() {
        return instrucao + "  (pergunta " + Math.min(atual + 1, perguntas.length) + " de " + perguntas.length
                + " | clique ou aperte 1-4)";
    }

    @Override
    protected String[] dicas() {
        int p = Math.min(atual, perguntas.length - 1);
        return new String[] { perguntas[p].dica, "Está bem, vou riscar duas alternativas erradas para você!" };
    }

    @Override
    protected void aplicarDica(int n) {
        if (n == 1 && atual < perguntas.length) {
            int riscadas = 0;
            for (int i = 0; i < 4 && riscadas < 2; i++) {
                if (i != perguntas[atual].certa && !riscada[atual][i]) {
                    riscada[atual][i] = true;
                    riscadas++;
                }
            }
        }
    }

    private Rectangle caixa(int i) {
        return new Rectangle(CX + (i % 2) * 430, CY + 90 + (i / 2) * 85, 410, 70);
    }

    @Override
    protected void andar(double dt) {
        if (tempoPisca > 0) {
            tempoPisca -= dt;
        }
        if (fim >= 0) {
            fim += dt;
            if (fim > 1.1) {
                resolvido = true;
            }
        }
    }

    @Override
    protected void desenharConteudo(Graphics2D g) {
        int p = Math.min(atual, perguntas.length - 1);
        g.setFont(F_MEDIA);
        Desenho.paragrafo(g, perguntas[p].texto, CX, CY + 24, CW, 32, Color.WHITE);

        for (int i = 0; i < 4; i++) {
            Rectangle r = caixa(i);
            boolean cortada = riscada[p][i];
            boolean sobre = fim < 0 && !cortada && r.contains(mouseX, mouseY);
            Color fundo = cortada ? new Color(24, 26, 44) : sobre ? new Color(55, 75, 140) : new Color(30, 40, 85);
            if (tempoPisca > 0 && i == piscando) {
                fundo = piscaCerto ? new Color(40, 155, 85) : new Color(175, 55, 65);
            }
            Desenho.caixa(g, r.x, r.y, r.width, r.height, fundo, cortada ? new Color(70, 80, 110) : new Color(120, 200, 255));
            g.setFont(F_MEDIA);
            Desenho.texto(g, (i + 1) + ")", r.x + 16, r.y + 43, cortada ? new Color(110, 110, 130) : Desenho.AMARELO);
            g.setFont(F_TEXTO);
            Desenho.texto(g, perguntas[p].opcoes[i], r.x + 56, r.y + 43, cortada ? new Color(110, 110, 130) : Color.WHITE);
            if (cortada) {
                g.setColor(new Color(255, 120, 120, 200));
                g.drawLine(r.x + 50, r.y + 36, r.x + r.width - 20, r.y + 36);
            }
        }
    }

    @Override
    public void tecla(int codigo) {
        if (codigo >= KeyEvent.VK_1 && codigo <= KeyEvent.VK_4) {
            escolher(codigo - KeyEvent.VK_1);
        }
    }

    @Override
    public void clique(int x, int y) {
        for (int i = 0; i < 4; i++) {
            if (caixa(i).contains(x, y)) {
                escolher(i);
            }
        }
    }

    private void escolher(int i) {
        if (fim >= 0 || atual >= perguntas.length || riscada[atual][i]) {
            return;
        }
        piscando = i;
        tempoPisca = 0.5;
        som();
        if (i == perguntas[atual].certa) {
            piscaCerto = true;
            atual++;
            if (atual >= perguntas.length) {
                fim = 0;
                dizer("Peça encaixada!", VERDE);
            } else {
                dizer("Correto! Próxima pergunta...", VERDE);
            }
        } else {
            piscaCerto = false;
            errou();
            dizer("Resposta errada. Tente de novo!", VERMELHO);
        }
    }
}
