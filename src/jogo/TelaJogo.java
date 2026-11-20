package jogo;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class TelaJogo extends JFrame {
	
	private PainelJogo painel;
	
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
	
	// Chamado pelo botão JOGAR do menu: troca o menu pelo corredor
	public void iniciarJogo() {
		
		getContentPane().removeAll();
		
		painel = new PainelJogo();
		add(painel);
		
		revalidate();
		repaint();
		SwingUtilities.invokeLater( () -> painel.requestFocusInWindow());
	}

}
