import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class BoardClient {

	public static void main(String[] args) throws Exception {
		frame = new JFrame("Backgammon");
		JMenuBar menuBar = new JMenuBar();
		JMenu gameOptions = new JMenu("Game Options");

		JMenuItem reset = new JMenuItem("GIVE UP");
		JMenuItem quit = new JMenuItem("QUIT");
		reset.addActionListener(mL);
		quit.addActionListener(mL);
		gameOptions.add(reset);
		gameOptions.add(quit);
		menuBar.add(gameOptions);

		frame.setLayout(new BorderLayout());
		frame.add(bH, BorderLayout.NORTH);
		frame.add(new BoardViewer(), BorderLayout.CENTER);
		frame.add(bS, BorderLayout.SOUTH);
		frame.add(new BoardHouse(), BorderLayout.EAST);
		frame.setJMenuBar(menuBar);
		frame.setSize(925, 721);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		BoardClient client = new BoardClient();
		client.play();

	}

	private static JFrame frame;
	static Socket s;
	static BoardHeader bH = new BoardHeader();
	static BufferedReader in;
	static PrintWriter out;
	static int[] rollN = new int[4];
	static ArrayList<Integer> posMov = new ArrayList<Integer>();
	static String tempPosMov = "";
	static int to, from;
	static BoardStatus bS = new BoardStatus();
	static ActionListener mL = new MenuListener();
	Border border = new LineBorder(Color.BLUE, 6);

	/**
	 * @return the frame
	 */
	public static JFrame getFrame() {
		return frame;
	}

	public static void closeFrame() {
		frame.dispose();
	}

	public BoardClient() throws Exception {
		s = new Socket("localhost", 8901);
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
	}

	public void play() throws Exception {
		String response;
		try {
			response = in.readLine();
			if (response.startsWith("WELCOME")) {
				char mark = response.charAt(8);
				// icon = new ImageIcon(mark == 'X' ? "x.gif" : "o.gif");
				// opponentIcon = new ImageIcon(mark == 'X' ? "o.gif" :
				// "x.gif");
				frame.setTitle("Backgammon - Player " + mark);

				if (mark == 'W') {
					Object[] possibilities = { "1", "2", "3", "4" };
					String s = (String) JOptionPane.showInputDialog(frame, "",
							"Points to win the game",
							JOptionPane.PLAIN_MESSAGE, new ImageIcon(
									"src/no_moves.png"), possibilities, "1");

					// If a string was returned, say so.
					if ((s != null) && (s.length() > 0)) {
						BoardHouse.mP = Integer.parseInt(s);
						System.out.println("Maximum: " + s);
					} else {
						s = "1";
						BoardHouse.mP = Integer.parseInt(s);
						System.out.println("Maximum: " + s);

					}

				}

			}
			while (true) {

				response = in.readLine();
				if (response.equals(null)) {
					System.out.println("RESPONSE NULL");
				}

				System.out.println(response);
				if (response.startsWith("VALID_ROLL")) {
					String temp = response.substring(11);
					for (int i = 0; i < temp.length(); i++) {
						rollN[i] = Integer.parseInt(Character.toString(temp
								.charAt(i)));
					}

					BoardHouse.house[1].setIcon(BoardHouse.diceImg[rollN[0]]);
					BoardHouse.house[2].setIcon(BoardHouse.diceImg[rollN[1]]);
					BoardHouse.house[4].setIcon(BoardHouse.diceImg[rollN[2]]);
					BoardHouse.house[5].setIcon(BoardHouse.diceImg[rollN[3]]);

				} else if (response.startsWith("OPPONENT_ROLL")) {
					String temp = response.substring(14);
					for (int i = 0; i < temp.length(); i++) {
						rollN[i] = Integer.parseInt(Character.toString(temp
								.charAt(i)));
					}

					BoardHouse.house[1].setIcon(BoardHouse.diceImg[rollN[0]]);
					BoardHouse.house[2].setIcon(BoardHouse.diceImg[rollN[1]]);
					BoardHouse.house[4].setIcon(BoardHouse.diceImg[rollN[2]]);
					BoardHouse.house[5].setIcon(BoardHouse.diceImg[rollN[3]]);
				} else if (response.startsWith("MESSAGE")) {
					BoardStatus.updateStatus(response.substring(8));
				} else if (response.startsWith("POSSIBLE_MOVES")) {
					posMov.clear();
					String posMov = response.substring(15);
					for (int i = 0; i < posMov.length(); i++) {
						if (posMov.charAt(i) != ',') {
							tempPosMov = tempPosMov.concat(Character
									.toString(posMov.charAt(i)));
						} else {
							BoardClient.posMov
							.add(Integer.parseInt(tempPosMov));
							tempPosMov = "";
						}
					}

					for (int i = 0; i < BoardClient.posMov.size(); i++) {
						if (!(BoardClient.posMov.get(i) == 26 || BoardClient.posMov
								.get(i) == 27)) {
							BoardViewer.pieces[BoardClient.posMov.get(i)]
									.setBorderPainted(true);
							BoardViewer.pieces[BoardClient.posMov.get(i)]
									.setBorder(border);
						} else {
							if (BoardClient.posMov.get(i) == 26) {
								BoardHouse.house[6].setBorderPainted(true);
								BoardHouse.house[6].setBorder(border);
							} else if (BoardClient.posMov.get(i) == 27) {
								BoardHouse.house[0].setBorderPainted(true);
								BoardHouse.house[0].setBorder(border);
							}
						}
					}
				} else if (response.startsWith("VALID_MOVE")) {
					if (!(BoardViewer.pieces[from].getColor()
							.equals(BoardViewer.pieces[to].getColor()))
							&& !(BoardViewer.pieces[from].getColor().equals(""))
							&& !(BoardViewer.pieces[to].getColor().equals(""))) {

						if (BoardViewer.pieces[to].getColor().equals("black")) {
							BoardViewer.pieces[19]
									.setStoneNum(BoardViewer.pieces[19]
											.getStoneNum() + 1);
						} else {
							BoardViewer.pieces[6]
									.setStoneNum(BoardViewer.pieces[6]
											.getStoneNum() + 1);
						}
						BoardViewer.pieces[to].setStoneNum(0);

					}

					BoardViewer.pieces[from]
							.setStoneNum(BoardViewer.pieces[from].getStoneNum() - 1);
					BoardViewer.pieces[to].setStoneNum(BoardViewer.pieces[to]
							.getStoneNum() + 1);
					BoardViewer.pieces[to].setColor(BoardViewer.pieces[from]
							.getColor());
					if (BoardViewer.pieces[from].getStoneNum() == 0) {
						if (from != 6 && from != 19)
							BoardViewer.pieces[from].setColor("");
						BoardViewer.pieces[from].setIcon(null);
					}
					BoardViewer.repaintButtons();

				} else if (response.startsWith("VALID_DISCHARGE")) {
					int disLoc = Integer.parseInt(response.substring(16));

					BoardViewer.pieces[disLoc]
							.setStoneNum(BoardViewer.pieces[disLoc]
									.getStoneNum() - 1);

					BoardViewer.repaintButtons();

				} else if (response.startsWith("OPPONENT_VALID_DISCHARGE")) {
					int disLoc = Integer.parseInt(response.substring(25));

					BoardViewer.pieces[disLoc].setStoneNum(BoardViewer.pieces[disLoc].getStoneNum() - 1);

					BoardViewer.repaintButtons();
				} else if (response.startsWith("OPPONENT_MOVED")) {

					String opMoves = response.substring(15);
					String tempPosMov = "";
					ArrayList<Integer> opMovesInt = new ArrayList<Integer>();

					for (int i = 0; i < opMoves.length(); i++) {
						if (opMoves.charAt(i) != ',') {
							tempPosMov = tempPosMov.concat(Character
									.toString(opMoves.charAt(i)));
						} else {

							opMovesInt.add(Integer.parseInt(tempPosMov));
							tempPosMov = "";
						}
					}

					if (!(BoardViewer.pieces[opMovesInt.get(0)].getColor()
							.equals(BoardViewer.pieces[opMovesInt.get(1)]
									.getColor()))
									&& !(BoardViewer.pieces[opMovesInt.get(0)]
											.getColor().equals(""))
											&& !(BoardViewer.pieces[opMovesInt.get(1)]
													.getColor().equals(""))) {

						if (BoardViewer.pieces[opMovesInt.get(1)].getColor()
								.equals("black")) {
							BoardViewer.pieces[19]
									.setStoneNum(BoardViewer.pieces[19]
											.getStoneNum() + 1);
						} else {
							BoardViewer.pieces[6]
									.setStoneNum(BoardViewer.pieces[6]
											.getStoneNum() + 1);
						}
						BoardViewer.pieces[opMovesInt.get(1)].setStoneNum(0);

					}

					BoardViewer.pieces[opMovesInt.get(0)]
							.setStoneNum(BoardViewer.pieces[opMovesInt.get(0)]
									.getStoneNum() - 1);
					BoardViewer.pieces[opMovesInt.get(1)]
							.setStoneNum(BoardViewer.pieces[opMovesInt.get(1)]
									.getStoneNum() + 1);
					if (!BoardViewer.pieces[opMovesInt.get(0)].getColor()
							.equals("")) {
						BoardViewer.pieces[opMovesInt.get(1)]
								.setColor(BoardViewer.pieces[opMovesInt.get(0)]
										.getColor());
					}

					if (BoardViewer.pieces[opMovesInt.get(0)].getStoneNum() == 0) {
						if (opMovesInt.get(0) != 6 && opMovesInt.get(0) != 19) {
							BoardViewer.pieces[opMovesInt.get(0)].setColor("");
						}
						BoardViewer.pieces[opMovesInt.get(0)].setIcon(null);
					}

					BoardViewer.repaintButtons();

				} else if (response.startsWith("NO_MOVES")) {
					JOptionPane.showMessageDialog(frame, "No moves available",
							"Sorry", JOptionPane.INFORMATION_MESSAGE,
							new ImageIcon("src/no_moves.png"));

					BoardViewer.removeBorderButtons();
					BoardViewer.resetDices();
				} else if (response.startsWith("OPPONENT_NO_MOVES")) {
					BoardViewer.removeBorderButtons();
					BoardViewer.resetDices();
				} else if (response.startsWith("VALID_DISCHARGE")) {
					BoardViewer.pieces[from]
							.setStoneNum(BoardViewer.pieces[from].getStoneNum() - 1);
					BoardViewer.repaintButtons();

				} else if (response.startsWith("YOU_WIN")) {
					bH.updateScore(BoardHeader.score1++, 0); 
					bH.setText("Player 1:" + BoardHeader.score1 + " pts" 
							+ "  Player 2:" + BoardHeader.score2 + " pts");
					BoardViewer.removeBorderButtons();
					BoardViewer.resetDices();
					BoardViewer.resetButtons();
					
					
					if (BoardHeader.score1 == BoardHouse.mP) { 
						BoardClient.out.println("WON_GAME"); 
					}

				} else if (response.startsWith("OPPONENT_WIN")) {
					bH.updateScore(0, BoardHeader.score2++);
					bH.setText("Player 1:" + BoardHeader.score1 + " pts"
							+ "  Player 2:" + BoardHeader.score2 + " pts");
					BoardViewer.removeBorderButtons();
					BoardViewer.resetDices();
					BoardViewer.resetButtons();

				} else if (response.startsWith("WON_GAME")) { 
					JOptionPane.showMessageDialog(
							frame,
							"Congratulations! You Won!", 
							"!!!!!!!!", JOptionPane.INFORMATION_MESSAGE,
							new ImageIcon("src/no_moves.png"));
					frame.dispose();
					s.close();

				} else if (response.startsWith("OPPONENT_WON_GAME")) {
					JOptionPane.showMessageDialog(frame, "You Lose!", ":(",
							JOptionPane.INFORMATION_MESSAGE, new ImageIcon(
									"src/no_moves.png"));
					frame.dispose();
					s.close();

				}

			}
		} finally {
			s.close();
		}
	}
}