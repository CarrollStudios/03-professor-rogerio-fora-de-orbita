package jogo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * A janela do jogo (a "televisão").
 * Ela mostra uma cena por vez e sabe trocar de uma cena para a próxima.
 * Ordem: menu -> corredor -> sala -> decolagem -> ...
 */
public class TelaJogo extends JFrame {
	
	public TelaJogo() {
		
		setTitle("Professor Rogério: Fora de Órbita");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		// Primeiro aparece o menu (TelaMenu)
		add(new TelaMenu(this));
		
		pack(); // ajusta a janela ao tamanho do menu (1280 x 720)
		setLocationRelativeTo(null);
		setVisible(true);
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
	public void iniciarJogo() {
		trocarCena(new CenaCorredor(this));
	}
	
	// Fim do corredor: começa a cena da sala
	public void mostrarSala() {
		trocarCena(new CenaSala(this));
	}
	
	// Fim da sala: cena da decolagem (ainda em construção)
	public void mostrarDecolagem() {
		
		JPanel emConstrucao = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(new Color(0xE9E4FF));
				g.setFont(new Font("SansSerif", Font.BOLD, 28));
				g.drawString("Cena da decolagem: em construção", 400, 360);
			}
		};
		emConstrucao.setBackground(new Color(0x0D0B1E));
		trocarCena(emConstrucao);
	}

}
