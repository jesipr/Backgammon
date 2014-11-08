import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;


public class MenuListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem m = (JMenuItem) e.getSource();
		if(m.getText().equals("GIVE UP")){
			//BoardClient.out.println("GIVE_UP");
			
			JOptionPane.showMessageDialog(BoardClient.getFrame(),
				    "You Lose!",
				    "",
				    JOptionPane.INFORMATION_MESSAGE,
				    new ImageIcon("src/no_moves.png"));
			
			BoardClient.closeFrame();
			
			try {
				BoardClient.s.close();
			} catch (IOException e1) {
				
				
			}
			System.out.println("GIVE UP");
		}	else if(m.getText().equals("QUIT")){
			//BoardClient.out.println("QUIT");
			System.out.println("QUIT");
		}
	}

	
	
}
