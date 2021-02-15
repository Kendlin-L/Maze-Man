package maze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Cells extends JPanel {

	public static int SIZE = 18;
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public int row = -1;
	public int col = -1;
	public boolean[] walls = { true, true, true, true };
	public boolean current = false;
	public boolean exit = false;

	// Constructor for the Cell class
	public Cells(int row, int col) {
		this.row = row;
		this.col = col;
	}

	/*
	 * This method allows for the initial grid of cells to painted on the JPanel
	 */
	public void paintComponent(Graphics g) {
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, SIZE, SIZE);
		g.setColor(Color.BLACK);
		if (isWall(NORTH)) {
			g.drawLine(0, 0, SIZE, 0); // draws a line representing the north wall
		}
		if (isWall(WEST)) {// draws a line representing the west wall
			g.drawLine(0, 0, 0, SIZE);

		}
		if (current) { // initially draws the stick-man in the starting point
			g.setColor(Color.black);
			g.fillOval(4, 4, SIZE - 14, SIZE - 14);
			g.drawLine(6, 6, 6, SIZE - 5);
			g.drawLine(2, 9, 10, 9);
			g.drawLine(7, 13, 10, SIZE);
			g.drawLine(1, SIZE, 7, 10);
		}
		if (exit) {// sets the endpoint to be a different color
			g.setColor(Color.gray);
			g.fillRect(1, 1, SIZE - 1, SIZE);
		}

	}

	// Returns whether or not the cell has a wall at a given index
	public boolean isWall(int index) {
		return walls[index];
	}

	// gets a specific cell row
	public int getRow() {
		return row;
	}

	// gets a specific cell column
	public int getCol() {
		return col;
	}

	// fits the graphics drawn by paintComponent to whatever JPanel they are put in
	public Dimension getPreferredSize() {
		Dimension size = new Dimension(SIZE, SIZE);
		return size;
	}

	// returns true of all Cell walls are intact
	public boolean checkWalls() {
		if (walls[0] && walls[1] && walls[2] && walls[3])
			return true;
		return false;
	}
	
	/*This method checks if the input cells row and column index
	 * is greater or less than row and column variables and
	 *  removes a specified wall by repainting the Cell without the wall
	  */
	public void breakWall(Cells next) {
		if (row < next.getRow()) {
			walls[SOUTH] = false; 
			repaint();
			next.walls[NORTH] = false;
		} else if (row > next.getRow()) {
			walls[NORTH] = false;
			repaint();
			next.walls[SOUTH] = false;
		} else if (col < next.getCol()) {
			walls[EAST] = false;
			repaint();
			next.walls[WEST] = false;
		} else if (col > next.getCol()) {
			walls[WEST] = false;
			repaint();
			next.walls[EAST] = false;
		}
	}
	
	/*
	 * when current is true, the stickman icon is 
	 * repainted at the specified cell
	 */
	
	public void setCurrent(boolean current) {
		this.current = current;
		repaint();
	}

	//sets the exits of the maze
	public void setExit(boolean exit) {
		this.exit = exit;

	}
}
