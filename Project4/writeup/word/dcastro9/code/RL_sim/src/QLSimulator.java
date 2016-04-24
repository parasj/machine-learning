
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import javax.swing.BorderFactory;
import javax.swing.DebugGraphics;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

public class QLSimulator extends javax.swing.JFrame 
						 implements ActionListener
{
	private JPanel jPanel;
	private JButton jLoadButton;
	private JSeparator jSeparator1;
	private JButton jInitializeButton;
	private JButton jStepButton;
	private JButton jCycleButton;
	private JFileChooser fc;
	private JLabel jLabelSqSize;
	private JCheckBox jDecayLRCheckBox;
	private JTextField jDelayTextField;
	private JLabel jDelayLabel;
	private JLabel jConvErrorLabel;
	private JTextField jConverErrorTextField;
	private JCheckBox jPolicyCheckBox;
	private JCheckBox jValuesCheckBox;
	private JButton jUpdateButton;
	private JButton jRefreshButton;
	private JLabel jStatusLabel;
	private JTextField jFileNameTextField;
	private JTextField jCyclesTextField;
	private JTextField sqSizeTextField;
	private JTextField jLearnRateTextField; 
	private JTextField pjogTextField;
	private JLabel JLabelPJOG;
	private JLabel jLabelLearningRate;
	//	private Algorithms alg = null;//this is commented for q learning customization
	
	private Maze myMaze = null;
	private QLearning ql;
	private boolean ShowValue = true;
	private boolean ShowPolicy = true;
	private boolean Animate = true;
	
	DecimalFormat df = new DecimalFormat("0.0");
    private JScrollPane jScrollPane;
    
	private String mazeStatus = "Load Maze First...";
	private String algorithmStatus = "";
    private JTextField jEpsilonTextField;
    private JLabel jLabelEpsilon;
    private JCheckBox jAnimateCheckBox;
    private JButton jEpisodeButton;
	
	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		QLSimulator inst = new QLSimulator();
		inst.setVisible(true);
	}
	
	public QLSimulator() {
		super("RL-MDP:Simulation");
		initGUI();
		
	}
	
	private void initGUI() {
		try {
		    this.setSize(1000, 800);
			this.setExtendedState(MAXIMIZED_BOTH);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			{
				jPanel = new JPanel();
				this.getContentPane().add(jPanel, BorderLayout.CENTER);
				jPanel.setLayout(null);
				jPanel.setBackground(new java.awt.Color(235, 241, 238));
				jPanel.setPreferredSize(new java.awt.Dimension(900, 700));
			}
			
			{
				jLoadButton = new JButton();
				jPanel.add(jLoadButton);
				jLoadButton.setText("LoadMaze...");
				jLoadButton.setBounds(25, 58, 110, 28);
				jLoadButton.setActionCommand("LoadMaze");
				jLoadButton.addActionListener(this);
			}
			{
				JLabelPJOG = new JLabel();
				jPanel.add(JLabelPJOG);
				JLabelPJOG.setText("PJOG");
				JLabelPJOG.setBounds(20, 105, 70, 21);
				JLabelPJOG.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			{
				pjogTextField = new JTextField();
				jPanel.add(pjogTextField);
				pjogTextField.setText("0.3");
				pjogTextField.setBounds(95, 104, 40, 20);
				pjogTextField.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			{
				jLabelEpsilon = new JLabel();
				jPanel.add(jLabelEpsilon);
				jLabelEpsilon.setText("Epsilon");
				jLabelEpsilon.setBounds(20, 126, 70, 21);
				jLabelEpsilon.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			{
				jEpsilonTextField = new JTextField();
				jPanel.add(jEpsilonTextField);
				jEpsilonTextField.setText("0.1");
				jEpsilonTextField.setBounds(95, 125, 40, 20);
				jEpsilonTextField.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			{
				jConvErrorLabel = new JLabel();
				jPanel.add(jConvErrorLabel);
				jConvErrorLabel.setText("Precision");
				jConvErrorLabel.setBounds(20, 147, 70, 21);
				jConvErrorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			{
				jConverErrorTextField = new JTextField();
				jPanel.add(jConverErrorTextField);
				jConverErrorTextField.setText("0.001");
				jConverErrorTextField.setBounds(95, 146, 40, 20);
				jConverErrorTextField.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			{
				jLabelLearningRate = new JLabel();
				jPanel.add(jLabelLearningRate);
				jLabelLearningRate.setText("Learning Rate");
				jLabelLearningRate.setBounds(20, 168, 70, 21);
				jLabelLearningRate.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			{
				jLearnRateTextField = new JTextField();
				jPanel.add(jLearnRateTextField);
				jLearnRateTextField.setText("0.7");
				jLearnRateTextField.setBounds(95, 167, 40, 20);
				jLearnRateTextField.setHorizontalAlignment(SwingConstants.RIGHT);
			}
            {
                jDecayLRCheckBox = new JCheckBox();
                jPanel.add(jDecayLRCheckBox);
                jDecayLRCheckBox.setText("Decaying LR");
                jDecayLRCheckBox.setBounds(36, 195, 95, 17);
                jDecayLRCheckBox.setOpaque(false);
                jDecayLRCheckBox.setSelected(true);
                jDecayLRCheckBox.addActionListener(this);
            }
			{
			    jInitializeButton = new JButton();
				jPanel.add(jInitializeButton);
				jInitializeButton.setText("Initialize");
				jInitializeButton.setBounds(25, 223, 110, 28);
				jInitializeButton.addActionListener(this);
				jInitializeButton.setActionCommand("Initialize");
			}
			{
				jUpdateButton = new JButton();
				jPanel.add(jUpdateButton);
				jUpdateButton.setText("Update");
				jUpdateButton.setBounds(25, 258, 110, 28);
				jUpdateButton.setActionCommand("Update");
				jUpdateButton.addActionListener(this);
			}
			{
				jStepButton = new JButton();
				jPanel.add(jStepButton);
				jStepButton.setText("Step");
				jStepButton.setBounds(25, 314, 110, 28);
				jStepButton.addActionListener(this);
				jStepButton.setActionCommand("Step");
			}
			{
				jEpisodeButton = new JButton();
				jPanel.add(jEpisodeButton);
				jEpisodeButton.setText("Episode");
				jEpisodeButton.setBounds(25, 350, 110, 28);
				jEpisodeButton.addActionListener(this);
				jEpisodeButton.setActionCommand("Episode");
			}
			{
			    jCyclesTextField = new JTextField();
			    jPanel.add(jCyclesTextField);
			    jCyclesTextField.setText("1000");
			    jCyclesTextField.setBounds(25, 387, 35, 26);
			}
			{
				jCycleButton = new JButton();
				jPanel.add(jCycleButton);
				jCycleButton.setText("Cycles");
				jCycleButton.setBounds(65, 386, 70, 28);
				jCycleButton.addActionListener(this);
				jCycleButton.setActionCommand("Cycles");
			}

			{
				jLabelSqSize = new JLabel();
				jPanel.add(jLabelSqSize);
				jLabelSqSize.setText("Square Size");
				jLabelSqSize.setBounds(20, 445, 70, 21);
				jLabelSqSize.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			{
				sqSizeTextField = new JTextField();
				jPanel.add(sqSizeTextField);
				sqSizeTextField.setText("80");
				sqSizeTextField.setBounds(96, 446, 40, 20);
				sqSizeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
				sqSizeTextField.addActionListener(this);
			}
			{
				jDelayLabel = new JLabel();
				jPanel.add(jDelayLabel);
				jDelayLabel.setText("Delay(in ms)");
				jDelayLabel.setBounds(22, 470, 70, 21);
				jDelayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			{
				jDelayTextField = new JTextField();
				jPanel.add(jDelayTextField);
				jDelayTextField.setText("30");
				jDelayTextField.setBounds(96, 472, 40, 20);
				jDelayTextField.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			{
				jRefreshButton = new JButton();
				jPanel.add(jRefreshButton);
				jRefreshButton.setText("Refresh");
				jRefreshButton.setBounds(26, 500, 110, 28);
				jRefreshButton.setActionCommand("Refresh");
				jRefreshButton.addActionListener(this);
			}
			{
				jValuesCheckBox = new JCheckBox();
				jPanel.add(jValuesCheckBox);
				jValuesCheckBox.setText("Show Values");
				jValuesCheckBox.setBounds(35, 553, 93, 17);
				jValuesCheckBox.setOpaque(false);
				jValuesCheckBox.setSelected(true);
				jValuesCheckBox.addActionListener(this);
			}
			{
				jPolicyCheckBox = new JCheckBox();
				jPanel.add(jPolicyCheckBox);
				jPolicyCheckBox.setText("Show Policy");
				jPolicyCheckBox.setBounds(35, 573, 85, 17);
				jPolicyCheckBox.setOpaque(false);
				jPolicyCheckBox.setSelected(true);
				jPolicyCheckBox.addActionListener(this);
			}
			{
				jAnimateCheckBox = new JCheckBox();
				jPanel.add(jAnimateCheckBox);
				jAnimateCheckBox.setText("Animate");
				jAnimateCheckBox.setBounds(35, 593, 85, 17);
				jAnimateCheckBox.setOpaque(false);
				jAnimateCheckBox.setSelected(true);
				jAnimateCheckBox.addActionListener(this);
			}
			
			
			{
				jSeparator1 = new JSeparator();
				jPanel.add(jSeparator1);
				jSeparator1.setBounds(161, 2, 4, 400);
				jSeparator1.setBorder(BorderFactory.createTitledBorder(
						null,
						"",
						TitledBorder.LEADING,
						TitledBorder.TOP,
						new java.awt.Font("MS Sans Serif", 0, 11),
						new java.awt.Color(0, 0, 0)));
			}
			{
				jStatusLabel = new JLabel();
				jPanel.add(jStatusLabel);
				jStatusLabel.setText("Load Maze First... | Q-learning");
				jStatusLabel.setBounds(182, 14, 770, 34);
				jStatusLabel.setBackground(new java.awt.Color(192,192,192));
				jStatusLabel.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font("MS Sans Serif",0,11), new java.awt.Color(0,0,0)));
				jStatusLabel.setFont(new java.awt.Font("Georgia",1,12));
				jStatusLabel.setDebugGraphicsOptions(DebugGraphics.NONE_OPTION);
				jStatusLabel.setOpaque(true);
				jStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
				jStatusLabel.setPreferredSize(new java.awt.Dimension(984, 35));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getActionCommand().equals("LoadMaze")) {
			Utility.show("Loading maze");
			//fileChooser
			fc = new JFileChooser("./mazes/");
			
			int returnVal = fc.showOpenDialog(QLSimulator.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					File file = fc.getSelectedFile();
					FileInputStream fis = new FileInputStream(file);
					GZIPInputStream gzis = new GZIPInputStream(fis);
					ObjectInputStream in = new ObjectInputStream(gzis);
					myMaze = (Maze)in.readObject();
					in.close();
					mazeStatus = " [ "+myMaze.width+" X "+myMaze.height+" Maze, Wall Penalty:"+((Wall)myMaze.walls.get(0)).penalty +"] | ";
					algorithmStatus = " Q Learning --> Click Initialize";
					
					sqSize = (int)Math.min(Math.floor((this.getSize().width-X-10)/myMaze.width),
					 		  Math.floor((this.getSize().height-Y-10)/myMaze.height));
					
					if(sqSize>200)
                        sqSize=200;
					
					sqSizeTextField.setText(""+sqSize);
					
				} catch(Exception e) {
					Utility.show(e.getMessage());
				}
				ql=null;
			} 
			repaint();
		}
		else if(evt.getActionCommand().equals("Initialize") && myMaze!=null) {
			Utility.show("Q Learning");
			
			double pjog = Double.parseDouble(pjogTextField.getText());
			double learningRate = Double.parseDouble((jLearnRateTextField.getText()));
			double epsilon =Double.parseDouble((jEpsilonTextField.getText()));
			boolean decayingLR = jDecayLRCheckBox.isSelected();
			ql = new QLearning(myMaze, pjog, learningRate, epsilon, decayingLR);
			
			algorithmStatus = " Q Learning ";
			repaint();
		}
		
		else if (evt.getActionCommand().equals("Update") && ql!=null) {
		    ql.setProperty(QLearning.Properties.PJOG,pjogTextField.getText());
		    ql.setProperty(QLearning.Properties.Epsilon,jEpsilonTextField.getText());
		    ql.setProperty(QLearning.Properties.LearningRate,jLearnRateTextField.getText());
		    if(jDecayLRCheckBox.isSelected())
		        ql.setProperty(QLearning.Properties.DecayingLR,new String("true"));
		    else
		        ql.setProperty(QLearning.Properties.DecayingLR,new String("false"));
		}
		
		else if(evt.getActionCommand().equals("Step")) {
			Utility.show("step");
			if(ql!=null)
				if(ql.step()) {
					jStatusLabel.setText("Goal Reached");
				}
			repaint();
		}
		
		else if (evt.getActionCommand().equals("Episode")) {
			Utility.show("Episode");
			int delay = Integer.parseInt(jDelayTextField.getText());
			if(ql!=null) {
				while(!ql.step()) {
				    if(Animate)
				    {
				        Utility.delay(delay);
				        myUpdate(getGraphics());
				    }
				}
				repaint();
			}
		}
		else if (evt.getActionCommand().equals("Cycles")) {
			Utility.show("Cycles");
			int delay = Integer.parseInt(jDelayTextField.getText());
			int numCycles = Integer.parseInt(jCyclesTextField.getText());
			if(ql!=null) {
			    for(int i=0; i<numCycles; i++)
			    {
					while(!ql.step()) {
					    if(Animate)
					    {
					        Utility.delay(delay);
					        myUpdate(getGraphics());
					    }
					}
			    }
				repaint();
			}
		}
		else if(evt.getActionCommand().equals("Refresh")) {
			Utility.show("Refresh");
			repaint();
		}
		else if(evt.getSource().getClass().getName().equals("javax.swing.JTextField")){
            repaint();
        }
		else if(evt.getSource().getClass().getName().equals("javax.swing.JCheckBox")){
			JCheckBox jcb = (JCheckBox)evt.getSource();
			
			if(jcb.getText().equals("Show Values"))
				ShowValue = jcb.isSelected();
			
			if(jcb.getText().equals("Show Policy"))
				ShowPolicy = jcb.isSelected();
			
			if(jcb.getText().equals("Animate"))
				Animate = jcb.isSelected();
			
			repaint();
		}
	}
	
	public void paint(Graphics g)
	{
	    super.paint(g);
		this.myUpdate(g);
	}
	
	public void myUpdate(Graphics g)
	{
		jSeparator1.setSize(4,jPanel.getBounds().height-2);
		jStatusLabel.setText(mazeStatus+algorithmStatus);
		showGrid(getGraphics());
	}
	
	public int sqSize=80;
	int X= 192;
	int Y= 100;
    Color GoldColor = new Color(200,100,55);
	
	public void showGrid(Graphics g)
	{
		sqSize = Integer.parseInt(sqSizeTextField.getText());

		if(ql!=null) {
			drawValues(g);
			if(ShowPolicy)
				drawPolicy(g);
		}
		if(myMaze!=null) {
			drawMaze(g);
			drawGoal(g);
			drawWalls(g);
		}
		if(ql!=null)
		    drawCurrPosition(g, ql.getCurrState());
	}
	
	void drawMaze(Graphics g)
	{
	//	g.setColor(new Color(200,200,250));
	//	g.fillRect(X,Y,sqSize*myMaze.width,sqSize*myMaze.height);
		g.setColor(Color.black);
		for (int counter = 0 ; counter <= myMaze.width; counter++) {
			g.drawLine(X+sqSize*counter, Y+0, X+sqSize*counter, Y+sqSize*myMaze.height);
		}
		for (int counter = 0 ; counter <= myMaze.height; counter++) {
			g.drawLine(X+0,Y+sqSize*counter,X+sqSize*myMaze.width,Y+sqSize*counter);
		}
	}
	
	void drawGoal(Graphics g)
	{
		Vector goals = myMaze.goals;
		State s;
		for (int i=0;i<goals.size();i++)
		{
			s = (State)goals.get(i);
			GraphicsUtil.fillRect(g, X+s.x*sqSize+1, Y+(myMaze.height-1-s.y)*sqSize+1, sqSize-2, sqSize-2, GoldColor);
		}
	}
	
	void drawWalls(Graphics g)
	{
		GraphicsUtil gr = new GraphicsUtil();
		int aX, aY, bX, bY;	//start and end points of the wall
		
		for (int i=0;i<myMaze.walls.size();i++)
		{
			Wall w = (Wall)myMaze.walls.get(i);
			int nodeX = w.x;
			int nodeY = w.y;
			switch(w.dir)
			{
			case Wall.UP:
				aX = nodeX*sqSize;
				bX = (nodeX+1)*sqSize;
				aY = (myMaze.height - nodeY - 1)*sqSize;
				bY = (myMaze.height - nodeY - 1)*sqSize;
				//Utility.show("up wall ax,ay,bx,by= "+aX+","+aY+","+bX+","+bY);
				GraphicsUtil.drawLine(g,X+aX,Y+aY,X+bX,Y+bY,5);
				break;
			case Wall.DOWN:
				aX = nodeX*sqSize;
				bX = (nodeX+1)*sqSize;
				aY = (myMaze.height - nodeY)*sqSize;
				bY = (myMaze.height - nodeY)*sqSize;
				GraphicsUtil.drawLine(g,X+aX,Y+aY,X+bX,Y+bY,5);
				break;
			case Wall.RIGHT:
				aX = (nodeX+1)*sqSize;
				bX = (nodeX+1)*sqSize;
				aY = (myMaze.height-nodeY-1)*sqSize;
				bY = (myMaze.height-nodeY)*sqSize;
				GraphicsUtil.drawLine(g,X+aX,Y+aY,X+bX,Y+bY,5);
				break;
			case Wall.LEFT:
				aX = (nodeX)*sqSize;
				bX = (nodeX)*sqSize;
				aY = (myMaze.height-nodeY)*sqSize;
				bY = (myMaze.height-nodeY-1)*sqSize;
				//Utility.show("left wall ax,ay,bx,by= "+aX+","+aY+","+bX+","+bY);
				GraphicsUtil.drawLine(g,X+aX,Y+aY,X+bX,Y+bY,5);
				break;
			}
		}
	}
	
	void drawCurrPosition(Graphics g, State s)
	{
		int centreX = sqSize*s.x + sqSize/2; 
		int centreY = sqSize*(myMaze.height-1 - s.y) + sqSize/2;
		int radius = sqSize/5;
		Color c = Color.MAGENTA;
		if(!ql.isBestAct)
		    c = Color.YELLOW;
		    
		GraphicsUtil.fillCircle(g, X+centreX, Y+centreY, radius, c);
		GraphicsUtil.drawCircle(g, X+centreX, Y+centreY, radius, 1, Color.black);
		
		GraphicsUtil.drawCircle(g, X+centreX-radius/3, Y+centreY-radius/3, radius/6, 1, Color.black);
		GraphicsUtil.drawCircle(g, X+centreX-radius/3, Y+centreY-radius/3, 1, 1, Color.black);
		
		GraphicsUtil.drawCircle(g, X+centreX+radius/3, Y+centreY-radius/3, radius/6, 1, Color.black);
		GraphicsUtil.drawCircle(g, X+centreX+radius/3, Y+centreY-radius/3, 1, 1, Color.black);
		
		if(ql.receivedPenalty) {
		    GraphicsUtil.drawArc(g,X+centreX-radius/2,Y+centreY+radius/3,radius,2*radius/3,0,180,Color.black);
			//GraphicsUtil.fillArc(g,X+centreX-radius/2,Y+centreY+radius/3,radius,2*radius/3,0,180,Color.black);
		}
		else {
		    GraphicsUtil.drawArc(g,X+centreX-radius/2,Y+centreY,radius,2*radius/3,0,-180,Color.black);
			//GraphicsUtil.fillArc(g,X+centreX-radius/2,Y+centreY,radius,2*radius/3,0,-180,Color.white);
		}
	}

	void drawPolicy(Graphics g)
	{
		//			System.out.println("drawing policy");
		int[][] p = ql.getPolicy();
		int x1=0,x2=0,y1=0,y2=0;
		g.setColor(Color.WHITE);
		for(int i=0;i<p.length;i++) {
			for (int j=0;j<p[i].length;j++) {
				x1 = (i*sqSize)+(sqSize/2);
				y1 = ((myMaze.height-1-j)*sqSize)+(sqSize/2);
				switch (p[i][j]) {
					case Action.UP:
					    x2 = x1;
					    y2 = ((myMaze.height-1-j))*sqSize;
					    break;
					case Action.LEFT:
						x2 = i* sqSize;
						y2 = y1;
						break;
					case Action.DOWN:
						x2 = x1;
						y2 = ((myMaze.height-1-j)*sqSize)+sqSize;
						break;
					case Action.RIGHT:
						x2 = (i*sqSize) + sqSize;
						y2 = y1;
						break;
					default:
						continue;
				}
				g.drawLine(X+x1,Y+y1,X+x2,Y+y2);
			}
		}
	}

	void drawValues(Graphics g)
	{
	    ValueFunction valuefunc = ql.getValueFunction();
        double[][] values = valuefunc.stateValue;
        int max = 1+(int)Math.ceil(valuefunc.getMax());
        
		double[][][] qsa = ql.getQsa();
		
		for (int xval=0 ; xval <myMaze.width; xval++) {
            for (int y=0; y < myMaze.height; y++) {
                int yval = myMaze.height-1-y;
                if (values[xval][y] >= 0) {
                    int red = 155-Math.min((int)(255.0*(values[xval][y])/max),155);
                    int green = 155-Math.min((int)(255.0*(values[xval][y])/max),155);
                    int b = 255-Math.min((int)(255.0*(values[xval][y])/max),220);
                    
                    g.setColor(new Color(red,green,b));
                }
                else
                    g.setColor(GoldColor);
                
                g.fillRect(X+xval*sqSize+1,Y+yval*sqSize+1,sqSize-1,sqSize-1);
            }
		}
		if(!ShowValue)
		    return;
		
		g.setColor(Color.WHITE);
		for(int i=0;i<qsa.length;i++) {
			for(int j=0;j<qsa[i].length;j++) {
				g.drawString(df.format(qsa[i][j][0]), X+(i*sqSize)+(sqSize/2)-5, Y+((myMaze.height-j)*sqSize)-sqSize+15);
				g.drawString(df.format(qsa[i][j][1]), X+(i*sqSize)+sqSize-20, Y+((myMaze.height-j)*sqSize)-(sqSize/2));
				g.drawString(df.format(qsa[i][j][2]), X+(i*sqSize)+(sqSize/2)-5, Y+((myMaze.height-j)*sqSize)-5);
				g.drawString(df.format(qsa[i][j][3]), X+(i*sqSize)+5, Y+((myMaze.height-j)*sqSize)-(sqSize/2));
			}
		}
		
	}
}