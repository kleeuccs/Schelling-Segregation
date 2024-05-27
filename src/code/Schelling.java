package code;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class Schelling {
	private static double THRESHOLD = 0.30;
	private static double SATISFACTION = 0.0;
    private static double RED_BLUE= 0.50;
    private static double PCT_EMPTY = 0.10;
	private static double CTF = 0.0;
	private static double twoPQ = 0.0;
	private static int    MAXVAL = 50;
	private static int    STEPS = 0;
	private static int    MAX_STEPS = 5000;

	HashMap<String, JTextField> hm = new HashMap<>();
	//HashMap<String, JButton> hmb = new HashMap<>();
    
    // new BorderLayout(hgap, vgap) 
    // Constructs a border layout with the specified gaps between components. 
    // The horizontal gap is specified by hgap and the vertical gap is specified by vgap.
    private final JPanel gui = new JPanel(
    	new BorderLayout(3, 3)); 

    private JButton[][] boardSquares = 
    	new JButton[Schelling.MAXVAL][Schelling.MAXVAL];

    private JPanel board;

    private JLabel message = new JLabel(
    	"Schelling Segregation");
    
    private int[] nums = 
    	java.util.stream.IntStream.rangeClosed(1, Schelling.MAXVAL+1)
    	.toArray();

    private String[] COLS = 
    	java.util.Arrays.stream(nums)
        .mapToObj(String::valueOf)
        .toArray(String[]::new);


    Schelling() {
        initializeGui();
    }

    public final JComponent getBoard() {
        return board;
    }

    public final JComponent getGui() {
        return gui;
    }

    
    public void initializeGui() {
        // set up the main GUI
        gui.setBorder(new EmptyBorder(10, 10, 10, 10));  // Creates an empty border with the specified insets.
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);


        JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	resetBoard();
            }
        });
        tools.add(reset);

        tools.addSeparator();
        JButton start = new JButton("Start");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	SchellingEngine se = new SchellingEngine(Schelling.MAXVAL, Schelling.THRESHOLD);
            	Schelling.SATISFACTION = se.satisfiedRatio(boardSquares);
        		while (Schelling.STEPS < Schelling.MAX_STEPS && Schelling.SATISFACTION < 1.0) {
        			Schelling.STEPS++;
        			Schelling.SATISFACTION = se.step(boardSquares);
                    Schelling.CTF = se.average_CTF();
        		}
        		DecimalFormat df = new DecimalFormat("#.000");        		
        		hm.get("satisfaction").setText(""+df.format(Schelling.SATISFACTION));
                hm.get("steps").setText(""+Schelling.STEPS);
                hm.get("ctf").setText("" + "CTF: " + df.format(Schelling.CTF));

                SwingUtilities.updateComponentTreeUI(board);
            }
        });
        tools.add(start);


        tools.addSeparator();
        JButton step = new JButton("Step");
        step.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	SchellingEngine se = new SchellingEngine(Schelling.MAXVAL, Schelling.THRESHOLD);
       			Schelling.SATISFACTION = se.step(boardSquares);
            	Schelling.STEPS++;
                Schelling.CTF = se.average_CTF();
                DecimalFormat df = new DecimalFormat("#.000");
                hm.get("satisfaction").setText(""+df.format(Schelling.SATISFACTION));
                hm.get("steps").setText(""+Schelling.STEPS);
                hm.get("ctf").setText("" + "CTF: " + df.format(Schelling.CTF));
                SwingUtilities.updateComponentTreeUI(board);
            }
        });
        tools.add(step);
        
        
        tools.addSeparator();
        JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //SwingUtilities.updateComponentTreeUI(board);
            	saveImage(gui);
            }
        });
        tools.add(save);
        
        
        tools.addSeparator();
        DecimalFormat df = new DecimalFormat("#.000");
        JTextField CTFValue = new JTextField("CTF: "+ df.format(Schelling.CTF));
        CTFValue.setEditable(false);
        hm.put("ctf", CTFValue);
        tools.add(CTFValue);


        
        tools.addSeparator();
        JTextField twoPQValue = new JTextField("2pq: "+ df.format(Schelling.twoPQ));
        twoPQValue.setEditable(false);
        hm.put("2pq", twoPQValue);
        tools.add(twoPQValue);

        
        tools.addSeparator();
        tools.add(message);


        ///////////////////////////////////////////////////////////////////////
        // Add change components
        JPanel contentPane = new JPanel(new GridLayout(5,3));

        JTextField stepValue = new JTextField("Steps: "+ Schelling.STEPS);
        stepValue.setEditable(false);
        hm.put("steps", stepValue);
        
        JTextField satisfactionValue = new JTextField("Satisfaction (0-1): "+ df.format(Schelling.SATISFACTION));
        satisfactionValue.setEditable(false);
        hm.put("satisfaction", satisfactionValue);
        
        JTextField similarValue = new JTextField("Similar: "+ Schelling.THRESHOLD);
        similarValue.setEditable(false);
        hm.put("similar", similarValue);
        JSlider similar = new JSlider(SwingConstants.HORIZONTAL,0,100,Double.valueOf(Schelling.THRESHOLD*100).intValue());
        similar.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
            	similarValue.setText("Similar: "+ similar.getValue());
            	Schelling.THRESHOLD = similar.getValue()/100.0;
            }
        });        

        JTextField redBlueValue = new JTextField("Red/Blue" + " - " + Double.valueOf(Schelling.RED_BLUE*100).intValue() + " %");
        redBlueValue.setEditable(false);
        hm.put("redBlue", redBlueValue);
        JSlider redBlue = new JSlider(SwingConstants.HORIZONTAL,0,100,Double.valueOf(Schelling.RED_BLUE*100).intValue());
        redBlue.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
            	redBlueValue.setText("Red/Blue: " + " - " + Double.valueOf(Schelling.RED_BLUE*100).intValue() + " %");
            	Schelling.RED_BLUE = redBlue.getValue()/100.0;
            	
            }
        });        

        JTextField emptyValue = new JTextField("Empty: "+ Schelling.PCT_EMPTY + " %");
        emptyValue.setEditable(false);
        hm.put("empty", emptyValue);
        JSlider empty = new JSlider(SwingConstants.HORIZONTAL,0,100,Double.valueOf(Schelling.PCT_EMPTY*100).intValue());
        empty.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
            	emptyValue.setText("Empty: "+ empty.getValue() + " %");
            	Schelling.PCT_EMPTY = empty.getValue()/100.0;
            }
        });        
        
        JTextField sizeValue = new JTextField("Size: "+ Schelling.MAXVAL + "x" + Schelling.MAXVAL);
        sizeValue.setEditable(false);
        hm.put("size", sizeValue);
        JSlider size = new JSlider(SwingConstants.HORIZONTAL,0,Schelling.MAXVAL,Schelling.MAXVAL);
        size.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
            	sizeValue.setText("Size: "+ size.getValue() + "x" + size.getValue());
            	Schelling.MAXVAL = size.getValue();
            }
        });        
        
        contentPane.add(stepValue);
        contentPane.add(satisfactionValue);
        
        contentPane.add(similarValue);
        contentPane.add(similar);

        contentPane.add(redBlueValue);
        contentPane.add(redBlue);
        
        contentPane.add(emptyValue);
        contentPane.add(empty);
        
        contentPane.add(sizeValue);
        contentPane.add(size);

        gui.add(contentPane, BorderLayout.SOUTH);
        ///////////////////////////////////////////////////////////////////////
       
        resetBoard();
    }

    
    public void resetBoard() {
        if (gui.isAncestorOf(board)) {
        	gui.remove(board);
        }
        board = null;
    	board = new JPanel(new GridLayout(0, MAXVAL+1));
        board.setBorder(new LineBorder(Color.BLACK));
        gui.add(board);

        // create the board squares
        buildSquares(boardSquares, Schelling.MAXVAL, Schelling.PCT_EMPTY, Schelling.RED_BLUE);
        
        //fill the board
        board.add(new JLabel(""));
        
        // fill the top row
        for (int ii = 0; ii < MAXVAL; ii++) {
            board.add(
                    new JLabel(COLS[ii],
                    SwingConstants.CENTER));
        }
        // fill the middle rows
        for (int ii = 0; ii < MAXVAL; ii++) {
            for (int jj = 0; jj < MAXVAL; jj++) {
                switch (jj) {
                    case 0:
                        board.add(new JLabel("" + (ii + 1),
                                SwingConstants.CENTER));
                    default:
                        board.add(boardSquares[jj][ii]);
                }
            }
        }
        
        SchellingEngine se = new SchellingEngine(Schelling.MAXVAL, Schelling.THRESHOLD);
        Schelling.SATISFACTION = se.satisfiedRatio(boardSquares);
        Schelling.STEPS = 0;
        Schelling.CTF = se.average_CTF();
        
        int blueCount = se.getBlueCount(boardSquares);
        int redCount = se.getRedCount(boardSquares);
        int whiteCount = se.getWhiteCount(boardSquares);
        int totalCount = blueCount+redCount+whiteCount;
        double p = Double.valueOf(redCount)/Double.valueOf(totalCount);
        double q = Double.valueOf(blueCount)/Double.valueOf(totalCount);
        Schelling.twoPQ = 2.0 * p * q; 
        
        DecimalFormat df = new DecimalFormat("#.000");
        hm.get("ctf").setText("" + "CTF: " + df.format(Schelling.CTF));
        hm.get("2pq").setText("" + "2pq: "+ df.format(Schelling.twoPQ));

        hm.get("steps").setText("" + Schelling.STEPS);
        hm.get("satisfaction").setText("" + "Satisfaction (0-1): "+ df.format(Schelling.SATISFACTION));

        hm.get("similar").setText("" + "Similar:" + Schelling.THRESHOLD);
        hm.get("redBlue").setText("" + "Red:" + se.getRedCount(boardSquares) + "/Blue:" + se.getBlueCount(boardSquares) + " - " + Double.valueOf(Schelling.RED_BLUE*100).intValue() + " %");
        hm.get("empty").setText("" + "Empty:" + Double.valueOf(PCT_EMPTY*(Schelling.MAXVAL*Schelling.MAXVAL)).intValue() + " - " + Double.valueOf(Schelling.PCT_EMPTY*100).intValue() + " %");
        hm.get("size").setText("" + "Size: " + Schelling.MAXVAL + "x" + Schelling.MAXVAL);
        
        SwingUtilities.updateComponentTreeUI(board);
    } // resetBoard


    /**
     * public void buildSquares(JButton[][] boardSquares, int MAXVAL, double PCT_EMPTY, double RED_BLUE)
     * @param boardSquares
     * @param MAXVAL
     * @param PCT_EMPTY
     * @param RED_BLUE
     */
    public void buildSquares(JButton[][] boardSquares, int MAXVAL, double PCT_EMPTY, double RED_BLUE) {
        // Set random colors
        int squareCount = MAXVAL*MAXVAL;
        int whiteCount = Double.valueOf(PCT_EMPTY*squareCount).intValue();
        int blueCount = Double.valueOf((squareCount-whiteCount)*RED_BLUE).intValue();
        int redCount = Double.valueOf(squareCount-whiteCount-blueCount).intValue();

        ArrayList<Color> colorList = new ArrayList<>();
		for (int w = 0; w<whiteCount; w++) {
			colorList.add(Color.WHITE);
		}
		for (int b = 0; b<blueCount; b++) {
			colorList.add(Color.BLUE);
		}
		for (int r = 0; r<redCount; r++) {
			colorList.add(Color.RED);
		}

		Collections.shuffle(colorList);
        
        Insets buttonMargin = new Insets(0,0,0,0);
        for (int i=0; i<MAXVAL;i++) {
			for (int j=0;j<MAXVAL; j++) {
                JButton b = new AButton(i,j);
                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SchellingEngine se = new SchellingEngine(Schelling.MAXVAL, Schelling.THRESHOLD);
                        se.satisfiedRatio(boardSquares);
                        DecimalFormat df = new DecimalFormat("#.000");
                        AButton btn = (AButton)e.getSource();
                        JOptionPane.showMessageDialog(
                        		null,                      		 
                        		" CTF:" + df.format(se.get_CTF(btn.getI(),btn.getJ())) + 
                        		" Happiness:" + df.format(se.get_Happiness(btn.getI(),btn.getJ())) +
                        		"Color: " + btn.getBackground()
                        );                        
                    }
                });                
                b.setBackground(colorList.remove(0));
                b.setMargin(buttonMargin);
                // 'fill this in' using a transparent icon..
                ImageIcon icon = new ImageIcon(new BufferedImage(7, 7, BufferedImage.TRANSLUCENT));
                b.setIcon(icon);
                b.setOpaque(true);
                //b.setBorderPainted(false); // Only required if you use the system look and feel on a mac

                boardSquares[i][j] = b;
			}
		}        
	} // buildSquares
    

    private void saveImage(JPanel panel) {
        BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        panel.paint(img.getGraphics());

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String dateStr = dateFormat.format(cal.getTime());
        File file = new File(dateStr + ".png") ;

        try {
            ImageIO.write(img, "png", file);
            System.out.println("panel saved as image");
        } catch (Exception e) {
            System.out.println("panel not saved" + e.getMessage());
        }
    } // saveImage    
    
    
    public static void main(String[] args) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
            	// Provides cross-platform functionality.  Pretty much same look and feel across platforms.
            	try {
            		//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            	
                Schelling cb = new Schelling();

                JFrame f = new JFrame("CS5740 - KALee");
                f.add(cb.getGui());
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.setLocationByPlatform(true);

                // ensures the frame is the minimum size it needs to be
                // in order display the components within it
                f.pack();
                // ensures the minimum size is enforced.
                f.setMinimumSize(f.getSize());
                f.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(r);
    } // main
}

class AButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int i;
	private int j;
	
	AButton(int i1, int j1) {
		this.i = i1;
		this.j = j1;
        this.setOpaque(true);
	}
	
	public int getI() {
		return i;
	}
	public int getJ() {
		return j;
	}
	
}