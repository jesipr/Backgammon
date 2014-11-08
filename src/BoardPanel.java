import java.awt.BorderLayout;

import javax.swing.JFrame;


public class BoardPanel {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Backgammon");
		// JMenuBar menuBar = new JMenuBar();
		// JMenu gameOptions = new JMenu("Game Options");
		// JMenu quit = new JMenu("Quit");
		// quit.setMnemonic(KeyEvent.VK_Q);
		// gameOptions.add(quit);
		// menuBar.add(gameOptions);

		frame.setLayout(new BorderLayout());
		frame.add(new BoardHeader(), BorderLayout.NORTH);
		frame.add(new BoardViewer(), BorderLayout.CENTER);
		frame.add(new BoardStatus(), BorderLayout.SOUTH);
		frame.add(new BoardHouse(), BorderLayout.EAST);
		frame.setSize(925, 721);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}

}
