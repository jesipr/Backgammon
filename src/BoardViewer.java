import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class BoardViewer extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JChecker[] pieces = new JChecker[26];
	public static ImageIcon[] uPB = new ImageIcon[6];
	public static ImageIcon[] uPW = new ImageIcon[6];
	public static ImageIcon[] lPB = new ImageIcon[6];
	public static ImageIcon[] lPW = new ImageIcon[6];

	private static final String B = "black";
	private static final String W = "white";

	public MouseListener cL = new CheckerListener();

	public BoardViewer() {
		drawGUI();

	}

	private void drawGUI() {

		this.setLayout(new GridLayout(2, 13));
		initImages();

		initButtons();
		addButtons();

	}

	private void initImages() {
		for (int i = 0; i < 4; i++) {
			for (int j = 1; j < 6; j++) {
				if (i == 0) {
					uPB[j] = new ImageIcon("src/pieces/upb" + (j) + ".png");
				} else if (i == 1) {
					uPW[j] = new ImageIcon("src/pieces/upw" + (j) + ".png");
				} else if (i == 2) {
					lPB[j] = new ImageIcon("src/pieces/lpb" + (j) + ".png");

				} else if (i == 3) {
					lPW[j] = new ImageIcon("src/pieces/lpw" + (j) + ".png");

				}
			}
		}
	}

	public static void resetDices() {
		BoardHouse.house[1].setIcon(BoardHouse.diceImg[0]);
		BoardHouse.house[2].setIcon(BoardHouse.diceImg[0]);
		BoardHouse.house[4].setIcon(BoardHouse.diceImg[0]);
		BoardHouse.house[5].setIcon(BoardHouse.diceImg[0]);
	}

	private void initButtons() {
		for (int i = 0; i < 26; i++) {
			pieces[i] = new JChecker(i, 0);
			pieces[i].setBorderPainted(false);
			pieces[i].addMouseListener(cL);
			pieces[i].setToolTipText(Integer.toString(i));
			pieces[i].setFont(new Font(Font.SERIF, Font.BOLD, 0));
			if (i == 0 || i == 12 || i == 17 || i == 20) {
				pieces[i].setColor(W);
				if (i == 0) {
					pieces[i].setIcon(uPW[5]);
					pieces[i].setStoneNum(5);
				} else if (i == 12) {
					pieces[i].setIcon(uPW[2]);
					pieces[i].setStoneNum(2);
				} else if (i == 17) {
					pieces[i].setIcon(lPW[3]);
					pieces[i].setStoneNum(3);
				} else if (i == 20) {
					pieces[i].setIcon(lPW[5]);
					pieces[i].setStoneNum(5);
				}
			} else if (i == 4 || i == 7 || i == 13 || i == 25) {
				pieces[i].setColor(B);
				if (i == 4) {
					pieces[i].setIcon(uPB[3]);
					pieces[i].setStoneNum(3);
				} else if (i == 7) {
					pieces[i].setIcon(uPB[5]);
					pieces[i].setStoneNum(5);
				} else if (i == 13) {
					pieces[i].setIcon(lPB[5]);
					pieces[i].setStoneNum(5);
				} else if (i == 25) {
					pieces[i].setIcon(lPB[2]);
					pieces[i].setStoneNum(2);
				}
			} else {
				if (i == 6) {
					pieces[i].setIcon(null);
					pieces[i].setColor(W);
					pieces[i].setStoneNum(0);
				} else if (i == 19) {
					pieces[i].setIcon(null);
					pieces[i].setColor(B);
					pieces[i].setStoneNum(0);
				} else
					pieces[i].setColor("");
			}
		}

	}

	private void addButtons() {
		for (int i = 0; i < 26; i++) {
			this.add(pieces[i]);

		}
	}

	static void resetButtons() {
		for (int i = 0; i < 26; i++) {

			if (i == 0 || i == 12 || i == 17 || i == 20) {
				pieces[i].setColor(W);
				if (i == 0) {
					pieces[i].setIcon(uPW[5]);
					pieces[i].setStoneNum(5);
				} else if (i == 12) {
					pieces[i].setIcon(uPW[2]);
					pieces[i].setStoneNum(2);
				} else if (i == 17) {
					pieces[i].setIcon(lPW[3]);
					pieces[i].setStoneNum(3);
				} else if (i == 20) {
					pieces[i].setIcon(lPW[5]);
					pieces[i].setStoneNum(5);
				}
			} else if (i == 4 || i == 7 || i == 13 || i == 25) {
				pieces[i].setColor(B);
				if (i == 4) {
					pieces[i].setIcon(uPB[3]);
					pieces[i].setStoneNum(3);
				} else if (i == 7) {
					pieces[i].setIcon(uPB[5]);
					pieces[i].setStoneNum(5);
				} else if (i == 13) {
					pieces[i].setIcon(lPB[5]);
					pieces[i].setStoneNum(5);
				} else if (i == 25) {
					pieces[i].setIcon(lPB[2]);
					pieces[i].setStoneNum(2);
				}
			} else {
				if (i == 6) {
					pieces[i].setIcon(null);
					pieces[i].setColor(W);
					pieces[i].setStoneNum(0);
				} else if (i == 19) {
					pieces[i].setIcon(null);
					pieces[i].setColor(B);
					pieces[i].setStoneNum(0);
				} else
					pieces[i].setColor("");
			}
		}
	}

	static void removeBorderButtons() {
		for (int i = 0; i < 26; i++) {
			pieces[i].setBorderPainted(false);
			pieces[i].setBorder(null);
		}
		BoardHouse.house[0].setBorderPainted(false);
		BoardHouse.house[6].setBorderPainted(false);
		BoardHouse.house[0].setBorder(null);
		BoardHouse.house[6].setBorder(null);
	}

	static void repaintButtons() {
		for (int i = 0; i < 26; i++) {
			if (i >= 0 && i <= 12) {
				if (pieces[i].getColor().equals("black")) {
					pieces[i].setIcon(uPB[pieces[i].getStoneNum()]);
				} else if (pieces[i].getColor().equals("white"))
					pieces[i].setIcon(uPW[pieces[i].getStoneNum()]);
			} else {
				if (pieces[i].getColor().equals("black")) {
					pieces[i].setIcon(lPB[pieces[i].getStoneNum()]);
				} else if (pieces[i].getColor().equals("white"))
					pieces[i].setIcon(lPW[pieces[i].getStoneNum()]);
			}

		}

	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		BufferedImage background = null;
		try {
			background = ImageIO.read(new File("src/background.png"));
		} catch (IOException e) {

			e.printStackTrace();
		}

		g2.drawImage(background, 0, 0, null);

	}

}
