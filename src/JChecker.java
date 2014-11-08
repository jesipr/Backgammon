import javax.swing.JButton;

public class JChecker extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String pos;
	private int stoneNum;

	/**
	 * @param row
	 *            initial row position
	 * @param col
	 *            initial number of stones
	 */
	JChecker(int pos, int stoneNum) {
		super();
		this.pos = Integer.toString(pos);
		this.stoneNum = stoneNum;
	}
	
	

	/**
	 * @return the stoneNum
	 */
	public int getStoneNum() {
		return stoneNum;
	}

	/**
	 * @param stoneNum
	 *            the stoneNum to set
	 */
	public void setStoneNum(int stoneNum) {
		this.stoneNum = stoneNum;
	}

	/**
	 * @return the pos
	 */
	public String getPos() {
		return pos;
	}

	/**
	 * @param pos
	 *            the pos to set
	 */
	public void setPos(String pos) {
		this.pos = pos;
	}

	private String color;

	/**
	 * @return the actual color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

}
