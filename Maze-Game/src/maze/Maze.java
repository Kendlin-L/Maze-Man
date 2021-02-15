package maze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.Stack;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Maze {

	static int rows = 20; // number of Rows in the grid
	static int cols = 20; // number of Columns in the grid
	static int row = 0;
	static int col = 0;
	static int endRow = rows - 1; // sets the end row
	static int endCol = cols - 1; // sets the end column
	static Cells cell[][] = new Cells[rows][cols]; // 2D array of cells objects
	static JPanel panel = new JPanel(); // An instance of a JPanel to be added to JFrame
	static Synthesizer synthesizer; // Sound generator for the game
	static MidiChannel[] midiChannels;
	static Border border = new Border(); // An instance of the Border class created below
	static JFrame frame = new JFrame("Maze"); // Window to dislay the game panels on

	public static void main(String[] args) {
		GUI();
	}

	/*
	 * GUI method sets the parameters for displaying various panels on the window,
	 * by calling specific methods or classes
	 */
	public static void GUI() {

		frame.setSize(390, 410);
		frame.addKeyListener(new KeyAdapter() { // When JFrame is opened, this instance allows for key input from the
												// user to be received
			public void keyReleased(KeyEvent e) {
				int keys = e.getKeyCode(); // gets the number representation of what key was pressed
				moves(keys);
			}
		});
		frame.setResizable(true);
		frame.setBackground(Color.gray);
		gridCanvas();
		PianoListener();
		frame.add(border); // adds the border onto the JFrame window
		border.add(panel); // adds the panel for which displays a maze to the border panel
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/*
	 * gridCanvas method uses a nested for loop to add a Cells object to a specific
	 * index in the 2D array of type Cells
	 */
	public static void gridCanvas() {
		panel.setLayout(new GridLayout(rows, cols));
		cell = new Cells[rows][cols];

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				cell[r][c] = new Cells(r, c);
				panel.add(cell[r][c]);
			}
		}
		aldousBroder();
		cell[endRow][endCol].setExit(true); // sets the exit position
		cell[row][col].setCurrent(true); // sets the starting position of the player icon
	}

	/*
	 * generateMaze method is called after the initial 2D grid is created above This
	 * method is based on the Aldous-Broder algorithm for maze generation
	 */
	public static void aldousBroder() {
		Stack<Cells> cellList = new Stack<Cells>();
		int totalCells = rows * cols;
		int visited = 1;

		// start by selecting a random cell
		Random randCell = new Random();
		int r = randCell.nextInt(rows);
		int c = randCell.nextInt(cols);

		while (visited < totalCells) {
			Stack<Cells> surrounding = new Stack<Cells>();
			if (checkCell(r - 1, c)) {
				surrounding.add(cell[r - 1][c]); // if the cell in the previous row has its walls intact its added to
													// the stack
			}
			if (checkCell(r + 1, c)) {
				surrounding.add(cell[r + 1][c]);// if the cell in the next row has its walls intact its added to the
												// stack
			}
			if (checkCell(r, c - 1)) {
				surrounding.add(cell[r][c - 1]);// if the cell in the previous column has its walls intact its added to
												// the stack
			}
			if (checkCell(r, c + 1)) {
				surrounding.add(cell[r][c + 1]);// if the cell in the next column has its walls intact its added to the
												// stack
			}
			if (surrounding.size() > 0) {
				if (surrounding.size() > 1) {
					cellList.add(cell[r][c]); /*
												 * if the size of the 'surrounding' stack, i.e the amount of adjacent
												 * cells found with their walls intact, they're added to a separate
												 * stack for their walls to be removed later
												 */
				}
				int pick = randCell.nextInt(surrounding.size()); // chooses a random cell from the adjacent cell Stack
				Cells next = surrounding.get(pick);
				cell[r][c].breakWall(next); // removes a wall from the cell chose

				/*
				 * Setting r to be the current cells row index and c to be the current cells
				 * column index allows for the process to be repeated
				 */
				r = next.getRow();
				c = next.getCol();
				visited++;
			} else {
				/*
				 * this handles the case where no adjacent cells have their walls intact and
				 * pops the top cell from the top of the stack so the one of the cells added
				 * from earlier can have walls removed
				 */
				Cells recheck = cellList.pop();
				r = recheck.getRow();
				c = recheck.getCol();
			}
		}
	}

	// returns true if the cell is in the grid and has its walls intact, false if
	// not
	public static boolean checkCell(int r, int c) {
		boolean check = false;
		if (r >= 0 && r < rows && c >= 0 && c < cols && cell[r][c].checkWalls()) {
			check = true;
		}
		return check;
	}

	/*
	 * this method creates a MIDI channel to allow for sounds to be played and
	 * throws an exception if it cannot. it also sets the sound generated to a harp
	 */
	public static void PianoListener() {
		try {
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
		} catch (MidiUnavailableException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		midiChannels = synthesizer.getChannels();
		synthesizer.getChannels()[0].programChange(46); // sets the sound generated to a harp

	}

	/*
	 * nextPos() calls Cells setCurrent method to repaint the player icon at the
	 * current row and column position, allowing the player to move through the maze
	 */
	public static void nextPos(int nextR, int nextC) {
		cell[row][col].setCurrent(false);
		row = nextR;
		col = nextC;
		cell[row][col].setCurrent(true);

	}

	/*
	 * This method takes in the direction specified by the user and checks if this
	 * direction is available for them to move into. It also generates game sound
	 * based on user input.
	 */
	public static void moves(int direction) {
		int noteNumber = -1; // sets the MIDI number to be initially off
		Random rando = new Random();
		int myArray[] = { 60, 62, 64, 66, 67, 69, 71, 72 }; // MIDI note values equating to the Lydian scale
		int randIndex = rando.nextInt(myArray.length); // selects a pseudo random MIDI note number from the array above

		if (direction == 37) {
			if (!cell[row][col].isWall(Cells.WEST)) {
				nextPos(row, col - 1); // if there is no Cell wall to the left of the player, they can move into this
										// position
				noteNumber = myArray[randIndex]; // if the player is allowed and chooses to move in this direction a
													// note is played
			}
		} else if (direction == 38) {
			if (!cell[row][col].isWall(Cells.NORTH)) {
				nextPos(row - 1, col);// if there is no Cell wall above of the player, they can move into this
										// position
				noteNumber = myArray[randIndex]; // if the player is allowed and chooses to move in this direction a
													// note is played
			}
		} else if (direction == 39) {
			if (!cell[row][col].isWall(Cells.EAST)) {
				nextPos(row, col + 1);// if there is no Cell wall right of the player, they can move into this
										// position
				noteNumber = myArray[randIndex]; // if the player is allowed and chooses to move in this direction a
													// note is played
			}
		} else if (direction == 40) {
			if (!cell[row][col].isWall(Cells.SOUTH)) {
				nextPos(row + 1, col); // if there is no Cell wall below the player, they can move into this position
				noteNumber = myArray[randIndex]; // if the player is allowed and chooses to move in this direction a
													// note is played
			}
		}

		if (noteNumber != -1) {
			midiChannels[0].noteOn(noteNumber, 200); // turns the MIDI channel on
		}
		if (row == endRow && col == endCol) {
			String message = "Maze Complete!!";

			JOptionPane.showMessageDialog(frame, message); // this displays once the player has reached the end row
			border.setVisible(false); // closes the game
		}

	}

}


