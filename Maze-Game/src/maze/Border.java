package maze;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Border extends JPanel {
	
	//paints two lines that act as a border when added to the JFrame
	public void paintComponent(Graphics g) {
		g.setColor(Color.black);
		g.drawLine(7, 365, 349, 365);
		g.drawLine(367, 5, 367, 347);

	}
}
