import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class BoardStatus extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public BoardStatus(){
		setOpaque(true);
		setIcon(new ImageIcon("src/statusBar.png"));
		setForeground(Color.WHITE);
		setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 28));
		setText("READY");
		setIconTextGap(-849);
	}
	
	public static void updateStatus(String st){
		BoardClient.bS.setText(st);
	}
}
