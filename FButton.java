package code;

import java.security.SecureRandom;
import java.util.Random;

import javax.swing.JButton;

public class FButton extends JButton implements Comparable<FButton> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int i;
	private int j;
	private int marketValue;
	private int[][] marketValues;
	
	FButton(int i, int j, int maxValue) {
		setI(i);
		setJ(j);
		Random rand = new SecureRandom();
		setMarketValue(rand.nextInt(10)+1);
		marketValues = new int[maxValue][maxValue];
		for (int iCounter=0;iCounter<maxValue;iCounter++) {
			for (int jCounter=0;jCounter<maxValue;jCounter++) {
				setMarketValues(iCounter, jCounter, rand.nextInt(10)+1); 
			}
		}
	}
	
	public int getMarketValues(int i , int j) {
		return marketValues[i][j];
	}
	public void setMarketValues(int i , int j, int value) {
		marketValues[i][j] = value;
	}
	public void setI(int iValue) {
		this.i = iValue;
	}
	public int getI() {
		return i;
	}
	public void setJ(int jValue) {
		this.j = jValue;
	}
	public int getJ() {
		return j;
	}
	public int getMarketValue() {
		return marketValue;
	}
	public void setMarketValue(int marketValue) {
		this.marketValue = marketValue;
	}

	@Override
	public int compareTo(FButton fb) {
		if (fb.getMarketValue() < this.getMarketValue()) {
			return -1;
		} else if (fb.getMarketValue() > this.getMarketValue()) {
			return 1;
		} else {
			return 0;
		}
	}
	
}