package jogo;

import java.awt.Image;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * A janela do jogo (a "televisão").
 * Ela mostra uma cena por vez e sabe trocar de uma cena para a próxima.
 * Ordem: menu -> corredor -> sala -> decolagem -> meteoro -> mapa -> fases 1, 2 e 3 -> final
 */
public class TelaJogo extends JFrame {
	
	public TelaJogo() {
		
		setTitle("Professor Rogério: Fora de Órbita");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		// Ícone da janela: o símbolo da Carroll Studios (no lugar da xícara do Java)
		List<Image> icones = new ArrayList<>();
		for (int tamanho : new int[] { 16, 32, 64, 128 }) {
			Image icone = Imagens.carregar("/icons/icone-carroll-" + tamanho + ".png");
			if (icone != null) {
				icones.add(icone);
			}
		}
		setIconImages(icones);
		
		criarBotaoSom();
		
		// Primeiro aparece o menu (TelaMenu)
		add(new TelaMenu(this));
		
		pack(); // ajusta a janela ao tamanho do menu (1280 x 720)
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	// Botão de som: fica numa camada por cima de todas as cenas,
	// por isso aparece em todas as telas sem precisar repetir o código
	private JButton botaoSom;
	private ImageIcon somLigado, somLigadoHover, somDesligado, somDesligadoHover;
	
	private void criarBotaoSom() {
		
		somLigado = new ImageIcon(Imagens.carregar("/images/botoes/botao-som-ligado.png"));
		somLigadoHover = new ImageIcon(Imagens.carregar("/images/botoes/botao-som-ligado-hover.png"));
		somDesligado = new ImageIcon(Imagens.carregar("/images/botoes/botao-som-desligado.png"));
		somDesligadoHover = new ImageIcon(Imagens.carregar("/images/botoes/botao-som-desligado-hover.png"));
		
		botaoSom = Botoes.criar("/images/botoes/botao-som-ligado.png", "/images/botoes/botao-som-ligado-hover.png");
		botaoSom.setLocation(24, 24);   // canto de cima, à esquerda
		botaoSom.addActionListener(e -> {
			Sons.alternarMudo();
			// troca o desenho: alto-falante com ondas (ligado) ou com X (desligado)
			botaoSom.setIcon(Sons.estaMudo() ? somDesligado : somLigado);
			botaoSom.setRolloverIcon(Sons.estaMudo() ? somDesligadoHover : somLigadoHover);
		});
		getLayeredPane().add(botaoSom, JLayeredPane.PALETTE_LAYER);
	}
	
	// Tira a cena atual da janela e coloca a próxima
	private void trocarCena(JPanel novaCena) {
		
		getContentPane().removeAll();
		add(novaCena);
		revalidate();
		repaint();
		SwingUtilities.invokeLater( () -> novaCena.requestFocusInWindow());
	}
	
	// Botão JOGAR do menu: começa a cutscene do corredor
	// (quem já recuperou peças, e fechou o jogo, continua direto pelo mapa)
	public void iniciarJogo() {
		if (Progresso.getPecas() > 0) {
			mostrarMapa();
		} else {
			trocarCena(new CenaCorredor(this));
		}
	}
	
	// Fim do corredor: começa a cena da sala
	// (também é chamado pelo botão "Ver de novo" do meteoro)
	public void mostrarSala() {
		trocarCena(new CenaSala(this));
	}
	
	// Fim da sala: a nave busca a aluna e decola
	public void mostrarDecolagem() {
		trocarCena(new CenaDecolagem(this));
	}
	
	// Fim da decolagem: o meteoro quebra a nave
	public void mostrarMeteoro() {
		trocarCena(new CenaMeteoro(this));
	}
	
	// Botão "Continuar" da cutscene do meteoro: tela de entrada das fases (mapa da missão)
	public void mostrarMapa() {
		trocarCena(new CenaMapa(this));
	}
	
	// Uma fase no espaço (0 = fase 1, 1 = fase 2, 2 = fase 3)
	public void mostrarFase(int fase) {
		trocarCena(new CenaFase(this, fase));
	}
	
	// Depois de recuperar as 6 peças: a nave é reconstruída e volta para a Terra
	public void mostrarFinal() {
		trocarCena(new CenaFinal(this));
	}
	
	// Volta para o menu inicial
	public void mostrarMenu() {
		trocarCena(new TelaMenu(this));
	}

}
