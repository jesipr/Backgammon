import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 * A server for a network multi player Backgammon game.
 * It allows an unlimited number of pairs of players to play.
 */
public class BackgammonServer {
	/**
	 * Runs the application. Pairs up clients that connect.
	 */
	public static void main(String[] args) throws Exception {
		ServerSocket listener = new ServerSocket(8901);
		System.out.println("Backgammon Server is Running");
		try {
			while (true) {
				Game game = new Game();
				Game.Player playerWhite = game.new Player(listener.accept(),'W');
				Game.Player playerBlack = game.new Player(listener.accept(),'B');
				playerWhite.setOpponent(playerBlack);
				playerBlack.setOpponent(playerWhite);
				game.currentPlayer = playerWhite;
				game.initGameBoard();
				playerWhite.start();
				playerBlack.start();
			}
		} finally {
			listener.close();
		}
	}
}

/**
 * A two-player game.
 */
class Game {
	/**
	 * A board has 26 squares. Each square is either unowned or it is owned by a
	 * player. So we use a simple array of player references. If null, the
	 * corresponding square is unowned, otherwise the array cell stores a
	 * reference to the player that owns it.
	 */
	private Player[] board = { null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null };

	private int[] numberOfPieces = new int[26];

	/**
	 * Used to get a random number for the dice roll.
	 */
	private Random rand = new Random();

	/**
	 * Initialize a new game layout putting the player pieces in the way they
	 * belong
	 */
	public void initGameBoard() {
		for (int i = 0; i < board.length; i++) {
			if (i == 0 || i == 12 || i == 17 || i == 20 || i == 6) {
				board[i] = currentPlayer;
			} else if (i == 4 || i == 7 || i == 13 || i == 25 || i == 19) {
				board[i] = currentPlayer.opponent;
			}
		}
		for (int i = 0; i < numberOfPieces.length; i++) {
			if (i == 0 || i == 7 || i == 13 || i == 20)
				numberOfPieces[i] = 5;
			else if (i == 4 || i == 17)
				numberOfPieces[i] = 3;
			else if (i == 12 || i == 25)
				numberOfPieces[i] = 2;
			else
				numberOfPieces[i] = 0;
		}
	}

	/**
	 * Gets called whenever a player makes a move in the client board. This
	 * change the current status of the server board to reflect the change.
	 * 
	 * @param from
	 * @param to
	 * @param player
	 */
	public void updateBoard(int from, int to, Player player) {
		numberOfPieces[from]--;
		if (numberOfPieces[from] == 0) {
			if(from!=6&&from!=19) board[from] = null;
		}

		if (board[to] == player.opponent) {
			if (player.opponent.mark == 'W') {
				board[to] = player;
				numberOfPieces[6]++;
			} else {
				board[to] = player;
				numberOfPieces[19]++;
			}
		} else {
			numberOfPieces[to]++;
			board[to] = player;
		}

	}

	/**
	 * The current player state.
	 */
	Player currentPlayer;
	/**
	 * Indicates the position of the selected piece.
	 */
	int pieceSelectedPos;

	/**
	 * Indicates if the player have already chosen a piece. This variable is to
	 * control the difference between the "CHOOSE" and "MOVE" commands
	 */
	Boolean alreadySelect = false;
	/**
	 * Holds the current dice movements available for the player.
	 */
	ArrayList<Integer> diceOptions = new ArrayList<Integer>();
	/**
	 * Defines the trail a black piece can make. We will be using the index to
	 * sum the number shown in dice to make movement computations.
	 */
	public final int blackTrail[] = { 25, 24, 23, 22, 21, 20, 18, 17, 16, 15,
			14, 13, 0, 1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 12 };
	/**
	 * Defines the trail a white piece can make. We will be using the index to
	 * sum the number shown in dice to make movement computations.
	 */
	public final int whiteTrail[] = { 12, 11, 10, 9, 8, 7, 5, 4, 3, 2, 1, 0,
			13, 14, 15, 16, 17, 18, 20, 21, 22, 23, 24, 25 };


	/**
	 * Called by the player threads when a player choose a piece. This checks if
	 * the player have chosen a piece that corresponds to him, that is, the
	 * piece chosen belongs to the current player.
	 * 
	 * @param location the location of the piece selected by the player
	 * @param player the player that made the move, either White or Black player
	 * @return true if the selection is legal or not
	 */
	public synchronized boolean legalPieceSelection(int location, Player player) {
		if (player == currentPlayer && board[location] == currentPlayer) {
			pieceSelectedPos = location;
			return true;
		}
		return false;
	}

	public synchronized boolean incorrectPiece(int location , Player player){
		if(player == currentPlayer && board[location]!=player) return true;
		else return false;
	}

	/**
	 * Determines if the move made by the player is legal or not. This is
	 * computed with the dice options available, whether the player is white or
	 * black, if there is no pieces of the current player in the bar, if the
	 * position of the selected movement is within the dice options available.
	 * This method also takes care of updating the server board.
	 * 
	 * @param location
	 * @param player
	 * @return
	 */
	public synchronized boolean legalPieceMove(int location, Player player) {
		if (player == currentPlayer) {
			for (int i = 0; i < possibleMoves.size(); i++) {
				if (location == possibleMoves.get(i)) {
					player.updateRoll(location);
					System.out.println("Roll updated");
					return true;
				}
			}
		}
		return false;
	}

	ArrayList<Integer> possibleMoves = new ArrayList<Integer>();

	/**
	 * Called by the player threads when a player tries to roll a dice. This
	 * method check if the player requesting to move the dice is the current
	 * player.
	 * 
	 * @param player
	 * @return If the player have the turn to roll dice.
	 */
	public synchronized boolean legalDiceTurn(Player player) {
		if (player == currentPlayer) {
			return true;
		}
		return false;
	}

	public void noMovesCommand(Player player){

	}

	/**
	 * This method gets called to check if the current player has any piece that
	 * can move with the current dices options. It returns true if there is at
	 * least one possible move available.
	 * 
	 * @param player
	 * @return
	 */
	public boolean anyMoveLeft(Player player) {
		if (player.mark == 'W') {
			for (int i = 0; i < diceOptions.size(); i++) {
				for (int j = 0; j < whiteTrail.length - diceOptions.get(i); j++) { 
					if (board[whiteTrail[j]] == player) {
						if (board[whiteTrail[j + diceOptions.get(i)]] == player&& numberOfPieces[whiteTrail[j+ diceOptions.get(i)]] < 5) {
							return true;
						} else if (board[whiteTrail[j + diceOptions.get(i)]] == null) {
							return true;
						} else if (board[whiteTrail[j + diceOptions.get(i)]] == player.opponent&& numberOfPieces[whiteTrail[j+ diceOptions.get(i)]] == 1) {
							return true;
						}
					}
				}
			}
		} else {
			for (int i = 0; i < diceOptions.size(); i++) {
				for (int j = 0; j < blackTrail.length - diceOptions.get(i); j++) { 
					if (board[blackTrail[j]] == player) {
						if (board[blackTrail[j + diceOptions.get(i)]] == player&& numberOfPieces[blackTrail[j+ diceOptions.get(i)]] < 5) {
							return true;
						} else if (board[blackTrail[j + diceOptions.get(i)]] == null) {
							return true;
						} else if (board[blackTrail[j + diceOptions.get(i)]] == player.opponent&& numberOfPieces[blackTrail[j+ diceOptions.get(i)]] == 1) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks if there are any pieces in the bar.
	 * 
	 * @param player
	 * @return
	 */
	public boolean anyPieceInBar(Player player){
		if(player.mark=='W' && player == currentPlayer){
			if(numberOfPieces[6]>0) return true;
		}else if(player.mark=='B' && player == currentPlayer)  {
			if(numberOfPieces[19]>0) return true;

		}
		return false;
	}

	/**
	 * This check if there is a house condition
	 * that is there is no other piece in the 
	 * board other than on home.
	 * 
	 * @param player
	 * @return
	 */
	public boolean houseCondition(Player player){
		if(player.mark=='W'){
			for(int i = 0; i<18;i++){
				if(board[whiteTrail[i]]==player || numberOfPieces[6] > 1) return false;
			}
		}else{
			for(int i = 0; i<18;i++){
				if(board[blackTrail[i]]==player || numberOfPieces[19] > 1) return false;
			}
		}
		return true;
	}

	/**
	 * Called after checking for house condition. 
	 * This return the possible moves to the player
	 * that has the turn.
	 * 
	 * @param player
	 * @param location
	 */
	public void houseConditionMoves(Player player, int location){
		if(player.mark=='W'){
			for(int i=0;i<diceOptions.size();i++){
				for(int j = 18;j<whiteTrail.length;j++){
					if (j + diceOptions.get(i) < 24){
						if(whiteTrail[j]==location){

							if(board[whiteTrail[j+diceOptions.get(i)]]==null) possibleMoves.add(whiteTrail[j+diceOptions.get(i)]);
							else if(board[whiteTrail[j+diceOptions.get(i)]]==player && numberOfPieces[whiteTrail[j+diceOptions.get(i)]]<5) possibleMoves.add(whiteTrail[j+diceOptions.get(i)]);
							else if(board[whiteTrail[j+diceOptions.get(i)]]==player.opponent && numberOfPieces[whiteTrail[j+diceOptions.get(i)]]==1) possibleMoves.add(whiteTrail[j+diceOptions.get(i)]);

						}
					}
				}
				if(whiteTrail[24-diceOptions.get(i)]==location){
					possibleMoves.add(26);
				}

			}

			String temp = "POSSIBLE_MOVES ";
			for (int i = 0; i < possibleMoves.size(); i++) {
				temp = temp.concat(Integer
						.toString(possibleMoves.get(i))
						+ ",");
			}
			player.output.println(temp);
		}else{
			for(int i=0;i<diceOptions.size();i++){
				for(int j = 18;j<blackTrail.length;j++){
					if (j + diceOptions.get(i) < 24){
						if(blackTrail[j]==location){
							if(board[blackTrail[j+diceOptions.get(i)]]==null) possibleMoves.add(blackTrail[j+diceOptions.get(i)]);
							else if(board[blackTrail[j+diceOptions.get(i)]]==player && numberOfPieces[blackTrail[j+diceOptions.get(i)]]<5) possibleMoves.add(blackTrail[j+diceOptions.get(i)]);
							else if(board[blackTrail[j+diceOptions.get(i)]]==player.opponent && numberOfPieces[blackTrail[j+diceOptions.get(i)]]==1) possibleMoves.add(blackTrail[j+diceOptions.get(i)]);
						}
					}
				}
				if(blackTrail[24-diceOptions.get(i)]==location){
					possibleMoves.add(27);
				}

			}

			String temp = "POSSIBLE_MOVES ";
			for (int i = 0; i < possibleMoves.size(); i++) {
				temp = temp.concat(Integer
						.toString(possibleMoves.get(i))
						+ ",");
			}
			player.output.println(temp);
		}
	}

	/**
	 * Checks if there are any moves available in the bar. This
	 * method is called after the anyPiecesInBar method.
	 * 
	 * @param player
	 * @return
	 */
	public boolean anyMoveInBar(Player player){ //VERIFICAR CAMBIE LOS diceOptions.get(i)+1 por diceOptions.get(i)-1
		if(player.mark=='W'){
			for(int i = 0; i<diceOptions.size(); i++){
				for(int j = 0; j<6;j++){
					if(board[whiteTrail[diceOptions.get(i)-1]]==player && numberOfPieces[whiteTrail[diceOptions.get(i)-1]]<5){
						return true;
					}
					else if(board[whiteTrail[diceOptions.get(i)-1]]==player.opponent && numberOfPieces[whiteTrail[diceOptions.get(i)-1]]==1){
						return true;
					}
					else if(board[whiteTrail[diceOptions.get(i)-1]]==null){
						return true;
					}
				}
			}
		}
		else{
			for(int i = 0; i<diceOptions.size(); i++){
				for(int j = 0; j<6;j++){
					if(board[blackTrail[diceOptions.get(i)-1]]==player && numberOfPieces[blackTrail[diceOptions.get(i)-1]]<5){
						return true;
					}
					else if(board[blackTrail[diceOptions.get(i)-1]]==player.opponent && numberOfPieces[blackTrail[diceOptions.get(i)-1]]==1){
						return true;
					}
					else if(board[blackTrail[diceOptions.get(i)-1]]==null){
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Gets called when there are no more moves available. When there are no
	 * more dice rolls to move or can't make any move with the remaining dice
	 * rolls.
	 * 
	 * @param player
	 */
	public synchronized void changeTurn(Player player) {
		diceOptions.clear();
		currentPlayer.output.println("MESSAGE Opponent turn, please wait");
		currentPlayer = player.opponent;
		currentPlayer.output.println("MESSAGE Your turn, roll dices");
	}

	/**
	 * The class for the helper threads in this multithreaded server
	 * application. A Player is identified by a character mark which is either
	 * 'W' or 'B'. For communication with the client the player has a socket
	 * with its input and output streams. Since only text is being communicated
	 * we use a reader and a writer.
	 */
	class Player extends Thread {
		char mark;
		Player opponent;
		Socket socket;
		BufferedReader input;
		PrintWriter output;
		int house = 0; // Para editar luego

		/**
		 * Constructs a handler thread for a given socket and mark initializes
		 * the stream fields, displays the first two welcoming messages.
		 */
		public Player(Socket socket, char mark) {
			this.socket = socket;
			this.mark = mark;
			try {
				input = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream(), true);
				output.println("WELCOME " + mark);
				output.println("MESSAGE Waiting for opponent to connect");
			} catch (IOException e) {
				System.out.println("Player died: " + e);
			}
		}

		/**
		 * Accepts notification of who the opponent is.
		 */
		public void setOpponent(Player opponent) {
			this.opponent = opponent;
		}


		/**
		 * Gets called when player get to roll dice.
		 */
		public void rollDice() {
			int ans1 = rand.nextInt(6) + 1;
			int ans2 = rand.nextInt(6) + 1;
			diceOptions.clear(); // Don't know if I should call this here.
			if (ans1 == ans2) {
				output.println("VALID_ROLL " + ans1 + "" + ans1 + "" + ans1+ "" + ans1);
				this.opponent.output.println("OPPONENT_ROLL " + ans1 + ""+ ans1 + "" + ans1 + "" + ans1);
				diceOptions.add(ans1);
				diceOptions.add(ans1);
				diceOptions.add(ans1);
				diceOptions.add(ans1);
			} else {
				output.println("VALID_ROLL " + ans1 + "" + ans2 + "00");
				this.opponent.output.println("OPPONENT_ROLL " + ans1 + ""
						+ ans2 + "00");
				diceOptions.add(ans1);
				diceOptions.add(ans2);
			}
		}

		/**
		 * When a user make a valid moves this method get called. This method
		 * takes care of eliminating the dice that the user just used to make
		 * the move. Then sends the VALID_ROLL command to the client with the
		 * available moves.
		 *
		 * @param location the location of the selected piece.
		 */
		public void updateRoll(int location) {
			String temp = "";
			int roll = 0;
			if (this.mark == 'W') {
				if(pieceSelectedPos==6){
					for(int i = 0;i<6;i++){
						if(location==whiteTrail[i]){
							roll = i+1;
							break;
						}
					}
				}
				else{
					for (int i = 0; i < whiteTrail.length; i++) {
						if (whiteTrail[i] == pieceSelectedPos) {
							for (int j = i; j < whiteTrail.length; j++) {
								if (whiteTrail[j] == location)
									break;
								roll++;
							}
							break;
						}
					}
				}
			} else {
				if(pieceSelectedPos==19){
					for(int i = 0;i<6;i++){
						if(location==blackTrail[i]){
							roll = i+1;
							break;
						}
					}
				}
				else{
					for (int i = 0; i < blackTrail.length; i++) {
						if (blackTrail[i] == pieceSelectedPos) {
							for (int j = i; j < blackTrail.length; j++) {
								if (blackTrail[j] == location)
									break;
								roll++;
							}
							break;
						}
					}
				}
			}
			if (!(diceOptions.size() == 1)) {
				for (int i = 0; i < diceOptions.size(); i++) {
					if (diceOptions.get(i) == roll) {
						diceOptions.remove(i);
						break;
					}
				}
			} else if (diceOptions.size() == 1) {

				diceOptions.clear();
			}

			for (int i = 0; i < diceOptions.size(); i++) {
				temp = temp + diceOptions.get(i);
			}

			if (temp.length() == 1) {
				output.println("VALID_ROLL " + temp + "000");
				this.opponent.output.println("OPPONENT_ROLL " + temp + "000");
			} else if (temp.length() == 2) {
				output.println("VALID_ROLL " + temp + "00");
				this.opponent.output.println("OPPONENT_ROLL " + temp + "00");
			} else if (temp.length() == 3) {
				output.println("VALID_ROLL " + temp + "0");
				this.opponent.output.println("OPPONENT_ROLL " + temp + "0");
			} else {
				output.println("VALID_ROLL 0000");
				this.opponent.output.println("OPPONENT_ROLL 0000");

				changeTurn(this);
			}

		}

		/**
		 * The run method of this thread.
		 */
		@Override
		public void run() {
			try {
				// The thread is only started after everyone connects.
				output.println("MESSAGE All players connected");
				// Tell the first player that it is her turn.
				// For our game White Player always goes first.
				if (mark == 'W') {
					output.println("MESSAGE Your move, Roll dice");
				}
				// Repeatedly get commands from the client and process them.
				while (true) {
					String command = input.readLine();
					if (command.startsWith("ROLL")) {
						if (legalDiceTurn(this)) {
							if (diceOptions.size() == 0) {
								rollDice();
								if(!houseCondition(this)){
									if (!anyMoveLeft(this) && anyMoveInBar(this)) {
										this.output.println("NO_MOVES");
										this.opponent.output.println("OPPONENT_NO_MOVES");
										changeTurn(this);
									}
								}

							}

							else
								output.println("MESSAGE You already roll, make your moves");
						} else {
							output.println("MESSAGE Not your turn to roll, wait for opponent");
						}
					} else if (command.startsWith("SELECT")) {
						int location = Integer.parseInt(command.substring(7));
						possibleMoves.clear();
						if(anyPieceInBar(this)){
							if(legalPieceSelection(location, this)){
								if(anyMoveInBar(this)){
									if(this.mark=='W' && location == 6){
										//Add possible moves to the possibleMoves array
										for(int i = 0; i<diceOptions.size();i++){

											if(board[whiteTrail[diceOptions.get(i)-1]]==null){
												possibleMoves.add(whiteTrail[diceOptions.get(i)-1]);
											}else if(board[whiteTrail[diceOptions.get(i)-1]]==this && numberOfPieces[whiteTrail[diceOptions.get(i)-1]]<5){
												possibleMoves.add(whiteTrail[diceOptions.get(i)-1]);
											}
											else if(board[whiteTrail[diceOptions.get(i)-1]]==this.opponent&&numberOfPieces[whiteTrail[diceOptions.get(i)-1]]==1){
												possibleMoves.add(whiteTrail[diceOptions.get(i)-1]);
											}
										}


										String temp = "POSSIBLE_MOVES ";
										for (int i = 0; i < possibleMoves.size(); i++) {
											temp = temp.concat(Integer
													.toString(possibleMoves.get(i))
													+ ",");
										}
										output.println(temp);
										if(possibleMoves.size()==0) changeTurn(this);

									}else if(this.mark=='B' && location==19){
										//Add possible moves to the possibleMoves array
										for(int i = 0; i<diceOptions.size();i++){


											if(board[blackTrail[diceOptions.get(i)-1]]==null){
												possibleMoves.add(blackTrail[diceOptions.get(i)-1]);
											}else if(board[blackTrail[diceOptions.get(i)-1]]==this && numberOfPieces[blackTrail[diceOptions.get(i)-1]]<5){
												possibleMoves.add(blackTrail[diceOptions.get(i)-1]);
											}
											else if(board[blackTrail[diceOptions.get(i)-1]]==this.opponent && numberOfPieces[blackTrail[diceOptions.get(i)-1]]==1){
												possibleMoves.add(blackTrail[diceOptions.get(i)-1]);
											}
										}

										String temp = "POSSIBLE_MOVES ";
										for (int i = 0; i < possibleMoves.size(); i++) {
											temp = temp.concat(Integer
													.toString(possibleMoves.get(i))
													+ ",");
										}
										output.println(temp);
										if(possibleMoves.size()==0) changeTurn(this);
									}
									else this.output.println("MESSAGE You still have pieces in the bar");


								}
								else{
									changeTurn(this);
								}

							}
						}



						else if (legalPieceSelection(location, this)) {
							if(houseCondition(this)){
								houseConditionMoves(this,location);
								continue;
							}
							pieceSelectedPos = location;
							alreadySelect = true;

							// Make calculations of possible moves locations
							// with the array of dice
							// and get the user mark to know which array trail
							// to use. Send this locations to the output.

							if (this.mark == 'W') {
								for (int i = 0; i < diceOptions.size(); i++) {
									for (int j = 0; j < whiteTrail.length; j++) { 
										if (j + diceOptions.get(i) < 24) {
											if (whiteTrail[j] == location) {
												if (board[whiteTrail[j
												                     + diceOptions.get(i)]] == this
												                     && numberOfPieces[whiteTrail[j
												                                                  + diceOptions
												                                                  .get(i)]] < 5) {
													possibleMoves
													.add(whiteTrail[j
													                + diceOptions
													                .get(i)]);
												} else if (board[whiteTrail[j
												                            + diceOptions.get(i)]] == null) {
													possibleMoves
													.add(whiteTrail[j
													                + diceOptions
													                .get(i)]);
												} else if ((board[whiteTrail[j
												                             + diceOptions.get(i)]] == this.opponent && numberOfPieces[whiteTrail[j
												                                                                                                  + diceOptions.get(i)]] == 1)) {
													possibleMoves
													.add(whiteTrail[j
													                + diceOptions
													                .get(i)]);
												}
											}
										}
									}
								}
								String temp = "POSSIBLE_MOVES ";
								for (int i = 0; i < possibleMoves.size(); i++) {
									temp = temp.concat(Integer
											.toString(possibleMoves.get(i))
											+ ",");
								}
								output.println(temp);
							} else {
								for (int i = 0; i < diceOptions.size(); i++) {
									for (int j = 0; j < blackTrail.length; j++) {
										if (j + diceOptions.get(i) < 24) {
											if (blackTrail[j] == location
													&& blackTrail[j
													              + diceOptions
													              .get(i)] < 26) {
												if (board[blackTrail[j
												                     + diceOptions.get(i)]] == this
												                     && numberOfPieces[blackTrail[j
												                                                  + diceOptions
												                                                  .get(i)]] < 5) {
													possibleMoves
													.add(blackTrail[j
													                + diceOptions
													                .get(i)]);
												} else if (board[blackTrail[j
												                            + diceOptions.get(i)]] == null) {
													possibleMoves
													.add(blackTrail[j
													                + diceOptions
													                .get(i)]);
												} else if ((board[blackTrail[j
												                             + diceOptions.get(i)]] == this.opponent && numberOfPieces[blackTrail[j
												                                                                                                  + diceOptions.get(i)]] == 1)) {
													possibleMoves
													.add(blackTrail[j
													                + diceOptions
													                .get(i)]);
												}
											}
										}
									}
								}
								String temp = "POSSIBLE_MOVES ";
								for (int i = 0; i < possibleMoves.size(); i++) {
									temp = temp.concat(Integer
											.toString(possibleMoves.get(i))
											+ ",");
								}
								output.println(temp);
							}
							output.println("VALID_SELECT");
						} else if(incorrectPiece(location,this)){//Check if it chose a opponent dice.
							output.println("MESSAGE Choose a correct piece!");
						}else{
							output.println("MESSAGE Not your turn, wait for opponnent");
						}
					} else if (command.startsWith("MOVE")) {
						int location = Integer.parseInt(command.substring(5));
						if (legalPieceMove(location, this)) { 
							updateBoard(pieceSelectedPos, location, this);
							alreadySelect = false;
							output.println("VALID_MOVE");
							this.opponent.output.println("OPPONENT_MOVED "+ pieceSelectedPos + "," + location + ",");
						} else {
							output.println("MESSAGE ?");
						}
					} else if (command.startsWith("QUIT")) {
						return;
					}
				}
			} catch (IOException e) {
				System.out.println("Player disconnected: " + e);
			} finally {
				try {
					socket.close();
					System.out.println("Closing Server Socket");
				} catch (IOException e) {
				}
			}
		}
	}
}