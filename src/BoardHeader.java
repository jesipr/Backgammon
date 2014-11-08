import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

class BoardHeader extends JLabel {

	private static int score1 = 0, score2 = 0;

	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

	public BoardHeader() {
		setOpaque(true);
		setIcon(new ImageIcon("src/header.png"));
		setForeground(Color.WHITE);
		setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 28));
		setText("Player 1:" + score1 + " pts" + "  Player 2:" + score2 + " pts");
		setIconTextGap(-400);

	}

	/**
	 * Update the corresponding scores in the header
	 * 
	 * @param s1
	 * @param s2
	 */
	public void updateScore(int s1, int s2) {
		score1 = s1;
		score2 = s2;
		setText("Player 1:" + score1 + " pts" + "  Player 2:" + score2 + " pts");
	}
	
}