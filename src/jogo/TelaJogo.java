package jogo;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class TelaJogo extends JFrame {
	
	private PainelJogo painel;
	
	public TelaJogo() {
		
		painel = new PainelJogo();
		add(painel);
		
		setTitle("Professor Rogério: Fora de Órbita");
		setSize(800, 600);
		 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		SwingUtilities.invokeLater( () -> painel.requestFocusInWindow());
	}

}
