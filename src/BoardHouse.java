import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class BoardHouse extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int hB, hW;
	public static JButton[] house = new JButton[7];
	private final String[] COLOR = { "WHITE", "BLACK" };
	public static final ImageIcon[] diceImg = new ImageIcon[7];
	public static ImageIcon blackHouse;
	public static ImageIcon whiteHouse;
	public MouseListener rHL = new RHListener();

	public BoardHouse() {
		hB = 0;
		hW = 0;
		initImages();
		initButtons();

		setLayout(new GridLayout(7, 1));
		addButtons();
	}

	private void initImages() {
		for (int i = 0; i < 7; i++) {
			if (i == 0)
				diceImg[i] = null;
			else
				diceImg[i] = new ImageIcon("src/dices/dice_" + (i) + ".png");
		}
		blackHouse = new ImageIcon("src/blackHouse.png");
		whiteHouse = new ImageIcon("src/whiteHouse.png");
	}

	private void addButtons() {
		for (int i = 0; i < 7; i++) {
			add(house[i]);
		}
	}

	private void initButtons() {
		for (int i = 0; i < 7; i++) {

			house[i] = new JButton();
			house[i].setContentAreaFilled(false);
			house[i].setBorderPainted(false);
			if (i != 1 || i != 2 || i != 4 || i != 5) {
				house[i].addMouseListener(rHL);
			}

		}

		house[0].setToolTipText("DISCHARGE " + COLOR[1]);
		house[0].setIcon(blackHouse);

		house[3].setToolTipText("ROLL");
		house[3].setIcon(new ImageIcon("src/roll.png"));

		house[6].setToolTipText("DISCHARGE " + COLOR[0]);
		house[6].setIcon(whiteHouse);
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		BufferedImage background = null;
		try {
			background = ImageIO.read(new File("src/house.png"));
		} catch (IOException e) {

			e.printStackTrace();
		}

		g2.drawImage(background, 0, 0, null);

	}

}
