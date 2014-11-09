import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

public class RHListener implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent e) {
		JButton b = (JButton) e.getSource();
		if (b.getToolTipText().equals("ROLL")) {
			BoardClient.out.println("ROLL");
		} else if (b.getToolTipText().startsWith("DISCHARGE")) {
			String tempHouse = b.getToolTipText().substring(11);
			BoardClient.out.print("DISCHARGE");
			
			if (tempHouse.equals("WHITE")) {
				BoardHouse.hW++;
			} else
				BoardHouse.hB++;

			CheckerListener.tempSelectedPos = "";
			CheckerListener.tempSelected = "";
			BoardViewer.removeBorderButtons();
		}
		if(BoardHouse.hB == 15 || BoardHouse.hW == 15){
			
			BoardClient.out.println("WIN");
			BoardHouse.hB = 0;
			BoardHouse.hW = 0;

		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
