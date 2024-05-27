package code;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JButton;

/**
 * public class SchellingEngine 
 * @author KALee
 * 19-Feb-2022: Initial Coding
 * 
 * This is the engine that computes values used for Schelling Segregation.  It can be run
 * standalone, though it is specific to requiring JButton[][] buttons for data.  Currently
 * set to only look at two colors, Color.RED and Color.BLUE, and Color.WHITE represents an
 * empty cell.  All percentages use [0,1] for values, NOT [0,100].
 * 
 * This algorithm is based on the Schelling Segregation model in the book,
 * "Netwoks, Crowds and Markets: Reaoning about a Highly Conneted World" by David Easley 
 * and Jon KleinBerg, 2010.  (page 96)
 * 
 * The example matches the simple example from the link:
 * http://nifty.stanford.edu/2014/mccown-schelling-model-segregation/
 * The Blue color X is represented by b or Color.BLUE
 * The Red color O is represented by r or Color.RED
 * The empty squares are represented by blanks when printed, but by w or Color.WHITE
 *
 */
public class SchellingEngine {
	private int    MAXVAL;
	private double T_THRESHOLD;
	private double[][] HAPPINESS;
	private double[][] CROSS_DEGREES; // Array of CTF Numerators
	private double[][] DEGREES; // Array of CTF Denomenators  
	
	//HashMap<String, JButton> hmb = new HashMap<>();

	/********** Constructor **********/
	public SchellingEngine(int maxVal, double satisfactionThreshold) {
		this.MAXVAL      = maxVal;
		//this.PCT_EMPTY   = pctEmpty;
		this.T_THRESHOLD = satisfactionThreshold;

		this.HAPPINESS = new double[this.MAXVAL][this.MAXVAL];
		this.CROSS_DEGREES = new double[this.MAXVAL][this.MAXVAL];
		this.DEGREES = new double[this.MAXVAL][this.MAXVAL];
	} // SchellingEngine (constructor)
	
	
	/**
	 * public double step (JButton[][] buttons)
	 * @param JButton[][] buttons
	 * @return double: the satisfactionRatio for all cells in the grid
	 * 
	 * red_blue_split.  not needed
	 * t: satisfaction. not needed 
	 * 	 Percentage as a decimal from 0-1
	 * pct_empty: not needed  
	 *   Percentage of squares that are empty from 0-1.  If 
	 *   pct_empty = 1, the grid is completely empty.  If pct_empty = 0.1, 
	 *   the grid is only 10 percent empty.  The value is between 0 and 1 
	 *   inclusive. 
	 */
	public double step (JButton[][] buttons) {
		ArrayList<int[]> dissatisfied = new ArrayList<>();
		ArrayList<int[]> empty = new ArrayList<>();
		for (int i=0;i<this.MAXVAL;i++) {
			for (int j=0;j<this.MAXVAL;j++) {
				if (buttons[i][j].getBackground().equals(Color.WHITE)) {
					int[] temp = new int[2];
					temp[0] = i;
					temp[1] = j;
					empty.add(temp);
				} else {
					if (!isSatisfied(i, j, buttons)) {
						int[] temp = new int[2];
						temp[0] = i;
						temp[1] = j;
						dissatisfied.add(temp);
					}
				}
			}
		}

		if (dissatisfied.size() == 0) { // nothing do do
			return 1.0;
		}
		
		if (empty.isEmpty()) { // Can't do anything if there are no empty cells.
			return 1.0;
		}
		
		// Default random way to move items
		moveItems(buttons,dissatisfied,empty);

		// How to move items for question 4.  Will attempt to put data in satisfied location, 
		// and will fall back to the first empty cell available if no satisfied location is found. 
		//moveItems4(buttons,dissatisfied,empty);
		
		// Recalculate after running through the data to return the current satisfiedRatio
		//return satisfiedRatio(buttons, satisfied, dissatisfied, empty);
		return satisfiedRatio(buttons);
	} // step

	
	
	/**
	 * public void moveItems(JButton[][] buttons, ArrayList<int[]> dissatisfied, ArrayList<int[]> empty)
	 * @param buttons
	 * @param dissatisfied
	 * @param empty
	 */
	public void moveItems(JButton[][] buttons, ArrayList<int[]> dissatisfied, ArrayList<int[]> empty) { 		
		Collections.shuffle(empty);
		while (!dissatisfied.isEmpty()) {
			int[] dissattisfiedArray = dissatisfied.remove(0);
			int[] emptyArray = empty.remove(0);
			buttons[emptyArray[0]][emptyArray[1]].setBackground(buttons[dissattisfiedArray[0]][dissattisfiedArray[1]].getBackground());
			buttons[dissattisfiedArray[0]][dissattisfiedArray[1]].setBackground(Color.WHITE);  // Make empty
			empty.add(dissattisfiedArray);
			Collections.shuffle(empty);
		}
	}
	
	/**
	 * public void moveItems(JButton[][] buttons, ArrayList<int[]> dissatisfied, ArrayList<int[]> empty)
	 * @param buttons
	 * @param dissatisfied
	 * @param empty
	 */
	public void moveItems4(JButton[][] buttons, ArrayList<int[]> dissatisfied, ArrayList<int[]> empty) { 		
	    // Find a satified location if possible, otherwise first available.
		//Collections.shuffle(empty);
	    while (!dissatisfied.isEmpty()) {
	    	int[] dissatisfiedArray = dissatisfied.remove(0);
	    	// attempt to move to an empty cell where satisfied, if not, use the first cell by default
	    	Color dissatisfiedColor = buttons[dissatisfiedArray[0]][dissatisfiedArray[1]].getBackground();
	    	boolean changeFlag = false;
	    	for(int i = 0; i < empty.size(); i++) {
	    		int[] test = empty.get(i);
	    		Color testColor = buttons[test[0]][test[1]].getBackground();
	    		buttons[test[0]][test[1]].setBackground(dissatisfiedColor);
	    		if (isSatisfied(test[0], test[1], buttons)) {
	    			buttons[dissatisfiedArray[0]][dissatisfiedArray[1]].setBackground(Color.WHITE);
	    			changeFlag = true;
	    			empty.remove(i);
	    			empty.add(dissatisfiedArray);
	    			//Collections.shuffle(empty);
	    			break;
	    		} else {
	    			buttons[test[0]][test[1]].setBackground(testColor);
	    			buttons[dissatisfiedArray[0]][dissatisfiedArray[1]].setBackground(dissatisfiedColor);
	    		}
	    	}
	    	if (!changeFlag) {
		        int[] emptyArray = empty.remove(0);
		        buttons[emptyArray[0]][emptyArray[1]].setBackground(buttons[dissatisfiedArray[0]][dissatisfiedArray[1]].getBackground());
		        buttons[dissatisfiedArray[0]][dissatisfiedArray[1]].setBackground(Color.WHITE);  // Make empty
		        empty.add(dissatisfiedArray);
		        //Collections.shuffle(empty);
	    	}
	    }
	}
	
	
	
	/**
	 * public double satisfiedRatio(JButton[][] buttons)
	 * @param buttons
	 * @return double
	 */
	public double satisfiedRatio(JButton[][] buttons) {
		ArrayList<int[]> satisfied = new ArrayList<>();
		ArrayList<int[]> dissatisfied = new ArrayList<>();
		ArrayList<int[]> empty = new ArrayList<>();
		for (int i=0;i<this.MAXVAL;i++) {
			for (int j=0;j<this.MAXVAL;j++) {
				int[] temp = new int[2];
				temp[0] = i;
				temp[1] = j;
				if (buttons[i][j].getBackground().equals(Color.WHITE)) {
					empty.add(temp);
					this.HAPPINESS[i][j] = 0.0;
					this.CROSS_DEGREES[i][j] = 0.0;
					this.DEGREES[i][j] = 0.0;
				} else {
					if (isSatisfied(i, j, buttons)) {
						satisfied.add(temp);
					} else {
						dissatisfied.add(temp);
						
					}
				}
			}
		}
		// this.MAXVAL*this.MAXVAL - empty.size();
		int countRedBlue = this.getBlueCount(buttons) + this.getRedCount(buttons);  
		double satisfactionRatio = Double.valueOf(satisfied.size())/Double.valueOf(countRedBlue);
		return satisfactionRatio;
	} // satisfiedRatio
	
	
	/**
	 * public boolean isSatisfied (int i, int j, JButton[][] buttons)
	 * @param i
	 * @param j
	 * @param buttons
	 * @return true or false based on whether number of neighbors "look like"
	 * the current cell is equal or greater to the threshold value.
	 */
	public boolean isSatisfied (int i, int j, JButton[][] buttons) {
		// build a list of neighbor coordinates for button
		int[][] directions = {{-1,0},{-1,-1}, {0,-1}, {1,-1}, {1,0}, {1,1}, {0,1}, {-1,1}};
		int counterLooksLike = 0;
		int counterNotLooksLike = 0;
		int counterTotal = 0;
		Color looksLike = buttons[i][j].getBackground();
		if (looksLike.equals(Color.WHITE)) {  // This code should never run
			System.out.println("Error Condition:  Checking isSatisfied on Empty Cell");
			return true;  // 
		}

		for(int[] dir : directions) {
			int newI = i + dir[0];
			int newJ = j + dir[1];
			//if ((newI >= 0) && (newI < this.MAXVAL) && (newJ >= 0) && (newJ < this.MAXVAL)) {  // ensure that new cell is on the grid.
			if (newI >= 0 && newI < this.MAXVAL && newJ >= 0 && newJ < this.MAXVAL) {  // ensure that new cell is on the grid.
				//if(!(buttons[newI][newJ].getBackground().equals(Color.WHITE))) {  // If not an empty cell
				if(!buttons[newI][newJ].getBackground().equals(Color.WHITE)) {  // If not an empty cell
					counterTotal++;
					if (buttons[newI][newJ].getBackground().equals(looksLike)) {
						counterLooksLike++;
					} else {
						counterNotLooksLike++;
					}
				}
			}
		}
		
		// A few extra steps to setup the CTF arrays
		if (counterTotal > 0) {
			this.HAPPINESS[i][j] = Double.valueOf(counterLooksLike)/Double.valueOf(counterTotal);
			this.CROSS_DEGREES[i][j] = Double.valueOf(counterNotLooksLike);
			this.DEGREES[i][j] = Double.valueOf(counterTotal);
		} else {
			//System.out.println("counterTotal = 0");
			this.CROSS_DEGREES[i][j] = 0.0;
			this.DEGREES[i][j] = 0.0;
		}
	
		return (Double.valueOf(counterLooksLike)/Double.valueOf(counterTotal) >= this.T_THRESHOLD);
	} // isSatisfied
	

	/**
	 * public double average_CTF()
	 * @return averageCTF for all cells
	 * Calculates average_CTF by summing each cells CROSS_DEGREES, and
	 * dividing that by the sum of each cells DEGREES.
	 */
	public double average_CTF() {
		double sumCrossDegrees = 0.0;
		double sumDegrees = 0.0;
		for (int i=0;i<this.MAXVAL;i++) {
			for (int j=0;j<this.MAXVAL;j++) {
				sumCrossDegrees += this.CROSS_DEGREES[i][j];
				sumDegrees += this.DEGREES[i][j]; 
			}
		}
		//System.out.printf("%10.2f%10.2f%10.2f\n",sumCrossDegrees, sumDegrees,sumCrossDegrees/sumDegrees);
		return sumCrossDegrees/sumDegrees; 
	} // average_CTF


	
	
	/**
	 * public double get_CTF(int i, int j)
	 * @param i
	 * @param j
	 * @return CTF for specific cell
	 */
	public double get_CTF(int i, int j) {
		if (this.DEGREES[i][j] > 0) {
			return this.CROSS_DEGREES[i][j]/this.DEGREES[i][j];
		} else {
			return 0.0;
		}
	}
	
	/**
	 * public double get_Happiness(int i, int j)
	 * @param i
	 * @param j
	 * @return Happiness factor for specific cell.
	 */
	public double get_Happiness(int i, int j) {
		return this.HAPPINESS[i][j];
	}

	
	
	
	/**
	 * public int getWhiteCount(JButton[][] buttons)
	 * @param buttons
	 * @return count of cells that are Color.WHITE
	 */
	public int getWhiteCount(JButton[][] buttons) {
		int counter = 0;
		for (int i=0;i<this.MAXVAL;i++) {
			for (int j=0;j<this.MAXVAL;j++) {
				if (buttons[i][j].getBackground().equals(Color.WHITE)) {
					counter++;
				}
			}
		}
		return counter;
	} // getWhiteCount


	/**
	 * public int getRedCount(JButton[][] buttons)
	 * @param buttons
	 * @return count of cells that are Color.RED
	 */
	public int getRedCount(JButton[][] buttons) {
		int counter = 0;
		for (int i=0;i<this.MAXVAL;i++) {
			for (int j=0;j<this.MAXVAL;j++) {
				if (buttons[i][j].getBackground().equals(Color.RED)) {
					counter++;
				}
			}
		}
		return counter;
	} // getRedCount


	/**
	 * public int getBlueCount(JButton[][] buttons)
	 * @param buttons
	 * @return count of cells that are Color.BLUE
	 */
	public int getBlueCount(JButton[][] buttons) {
		int counter = 0;
		for (int i=0;i<this.MAXVAL;i++) {
			for (int j=0;j<this.MAXVAL;j++) {
				if (buttons[i][j].getBackground().equals(Color.BLUE)) {
					counter++;
				}
			}
		}
		return counter;
	} // getBlueCount


	/**
	 * public static void main(String[] args)
	 * @param args
	 * Stub routine that returns simple text message.  This
	 * class is meant to be called from external programs.
	 */
	public static void main(String[] args) {
		System.out.println("Stub method for SchellingEngine.");
	} // main
	
} // SchellingEngine
