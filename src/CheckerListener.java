import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class CheckerListener implements MouseListener {
	Border border = new LineBorder(Color.GREEN, 6);
	public static String tempSelected = "", tempSelectedPos = "";

	@Override
	public void mouseClicked(MouseEvent e) {
		JChecker b = (JChecker) e.getSource();
		BoardViewer.removeBorderButtons();
		if (!(tempSelected.equals("SELECT"))) {
			BoardViewer.pieces[Integer.parseInt(b.getPos())]
					.setBorderPainted(true);
			BoardViewer.pieces[Integer.parseInt(b.getPos())].setBorder(border);
		}

		if (!b.getPos().equals(tempSelectedPos)) {
			if (!(tempSelected.equals("SELECT"))) {
				BoardClient.out.println("SELECT " + b.getPos());
				BoardClient.from = Integer.parseInt(b.getPos());
				
				tempSelectedPos = b.getPos();
				tempSelected = "SELECT";
			} else {

				BoardClient.out.println("MOVE " + b.getPos());
				BoardClient.to = Integer.parseInt(b.getPos());
				tempSelectedPos = "";
				tempSelected = "";

			}
		} else {
			BoardViewer.removeBorderButtons();
			BoardClient.posMov.clear();
			tempSelected = "";
			tempSelectedPos = "";
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
