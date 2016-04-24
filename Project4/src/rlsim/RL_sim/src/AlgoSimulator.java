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
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

public class AlgoSimulator extends javax.swing.JFrame 
implements ActionListener
{
    private JPanel jPanel;
    private JButton jLoadButton;
    private JPanel jGridPanel;
    private JSeparator jSeparator1;
    private JButton jInitialiseButton;
    private JButton jStepButton;
    private JButton jExecuteButton;
    private JFileChooser fc;
    private JLabel jLabelSqSize;
    private JCheckBox jPolicyCheckBox;
    private JCheckBox jValuesCheckBox;
    private JButton jUpdateButton;
    private JButton jRefreshButton;
    private JLabel jStatusLabel;
    private JTextField jFileNameTextField;
    private JTextField jCyclesTextField;
    private JLabel jCyclesLabel;
    private JTextField sqSizeTextField;
    private JTextField pjogTextField;
    private JLabel JLabelPJOG;
    private JLabel jConvErrorLabel;
    private JTextField jConverErrorTextField;
    private JLabel jDelayLabel;
    private JTextField jDelayTextField;
    
    private int algoType;
    private Algorithms alg = null;
    private Maze myMaze = null;
    private boolean ShowValue = true;
    private boolean ShowPolicy = true;
    private DecimalFormat df = new DecimalFormat("0");
    private String mazeStatus = "Load Maze First...";
    private String basicAlgStatus = "";
    private String additionalAlgStatus = "";
    private JLabel jLabelVFLimit;
    private JTextField jVFLimitTextField;
    private JLabel jLabelIterLimit;
    private JTextField jIterLimitTextField;
 
    {
        //Set Look & Feel
        try {
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public AlgoSimulator(int _algoType)
    {
        super("RL-MDP:Simulation");
        algoType = _algoType;
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
				jLoadButton.setBounds(25, 62, 110, 28);
				jLoadButton.setActionCommand("LoadMaze");
				jLoadButton.addActionListener(this);
			}
			if(algoType==Algorithms.PolicyIter) 
			{
			    {
					jLabelVFLimit = new JLabel();
					jPanel.add(jLabelVFLimit);
					jLabelVFLimit.setText("Value Limit");
					jLabelVFLimit.setBounds(25, 115, 53, 21);
					jLabelVFLimit.setHorizontalAlignment(SwingConstants.RIGHT);
				}
				{
					jVFLimitTextField = new JTextField();
					jPanel.add(jVFLimitTextField);
					jVFLimitTextField.setText("5000");
					jVFLimitTextField.setBounds(88, 117, 45, 20);
					jVFLimitTextField.setHorizontalAlignment(SwingConstants.RIGHT);
				}
				{
					jLabelIterLimit = new JLabel();
					jPanel.add(jLabelIterLimit);
					jLabelIterLimit.setText("Iter. Limit");
					jLabelIterLimit.setBounds(25, 136, 53, 21);
					jLabelIterLimit.setHorizontalAlignment(SwingConstants.RIGHT);
				}
				{
					jIterLimitTextField = new JTextField();
					jPanel.add(jIterLimitTextField);
					jIterLimitTextField.setText("500");
					jIterLimitTextField.setBounds(88, 138, 45, 20);
					jIterLimitTextField.setHorizontalAlignment(SwingConstants.RIGHT);
				}
			}
			
			{
				JLabelPJOG = new JLabel();
				jPanel.add(JLabelPJOG);
				JLabelPJOG.setText("PJOG");
				JLabelPJOG.setBounds(25, 157, 53, 21);
				JLabelPJOG.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			{
				pjogTextField = new JTextField();
				jPanel.add(pjogTextField);
				pjogTextField.setText("0.3");
				pjogTextField.setBounds(88, 159, 45, 20);
				pjogTextField.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			{
				jConvErrorLabel = new JLabel();
				jPanel.add(jConvErrorLabel);
				jConvErrorLabel.setText("Precision");
				jConvErrorLabel.setBounds(29, 178, 51, 21);
				jConvErrorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			{
				jConverErrorTextField = new JTextField();
				jPanel.add(jConverErrorTextField);
				jConverErrorTextField.setText("0.001");
				jConverErrorTextField.setBounds(88, 180, 45, 20);
				jConverErrorTextField.setHorizontalAlignment(SwingConstants.RIGHT);
			}
            {
                jInitialiseButton = new JButton();
                jPanel.add(jInitialiseButton);
                jInitialiseButton.setText("Initialize");
                jInitialiseButton.setBounds(25, 210, 110, 28);
                jInitialiseButton.addActionListener(this);
                jInitialiseButton.setActionCommand("Initialize");
            }
            {
				jUpdateButton = new JButton();
				jPanel.add(jUpdateButton);
				jUpdateButton.setText("Update");
				jUpdateButton.setBounds(25, 250, 110, 28);
				jUpdateButton.setActionCommand("Update");
				jUpdateButton.addActionListener(this);
			}
			{
				jStepButton = new JButton();
				jPanel.add(jStepButton);
				jStepButton.setText("Step");
				jStepButton.setBounds(25, 317, 110, 28);
				jStepButton.addActionListener(this);
				jStepButton.setActionCommand("Step");
			}
			
			{
				jExecuteButton = new JButton();
				jPanel.add(jExecuteButton);
				jExecuteButton.setText("Execute");
				jExecuteButton.setBounds(25, 357, 110, 28);
				jExecuteButton.addActionListener(this);
				jExecuteButton.setActionCommand("Execute");
			}
			
//            {
//				jCyclesLabel = new JLabel();
//				jPanel.add(jCyclesLabel);
//				jCyclesLabel.setText("Cycles");
//				jCyclesLabel.setBounds(49, 313, 35, 20);
//			}
//			{
//				jCyclesTextField = new JTextField();
//				jPanel.add(jCyclesTextField);
//				jCyclesTextField.setText("20");
//				jCyclesTextField.setBounds(89, 313, 29, 22);
//			}
			{
				jLabelSqSize = new JLabel();
				jPanel.add(jLabelSqSize);
				jLabelSqSize.setText("Square Size");
				jLabelSqSize.setBounds(20, 420, 70, 21);
				jLabelSqSize.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			{
				sqSizeTextField = new JTextField();
				jPanel.add(sqSizeTextField);
				sqSizeTextField.setText("40");
				sqSizeTextField.setBounds(96, 421, 40, 20);
				sqSizeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
				sqSizeTextField.addActionListener(this);
			}
			{
				jDelayLabel = new JLabel();
				jPanel.add(jDelayLabel);
				jDelayLabel.setText("Delay(in ms)");
				jDelayLabel.setBounds(22, 442, 70, 21);
				jDelayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			{
				jDelayTextField = new JTextField();
				jPanel.add(jDelayTextField);
				jDelayTextField.setText("0");
				jDelayTextField.setBounds(96, 444, 40, 20);
				jDelayTextField.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			{
				jValuesCheckBox = new JCheckBox();
				jPanel.add(jValuesCheckBox);
				jValuesCheckBox.setText("Show Values");
				jValuesCheckBox.setBounds(34, 536, 93, 21);
				jValuesCheckBox.setOpaque(false);
				jValuesCheckBox.setSelected(true);
				jValuesCheckBox.addActionListener(this);
			}
			{
				jPolicyCheckBox = new JCheckBox();
				jPanel.add(jPolicyCheckBox);
				jPolicyCheckBox.setText("Show Policy");
				jPolicyCheckBox.setBounds(34, 558, 85, 22);
				jPolicyCheckBox.setOpaque(false);
				jPolicyCheckBox.setSelected(true);
				jPolicyCheckBox.addActionListener(this);
			}
			{
				jRefreshButton = new JButton();
				jPanel.add(jRefreshButton);
				jRefreshButton.setText("Refresh");
				jRefreshButton.setBounds(26, 481, 110, 28);
				jRefreshButton.setActionCommand("Refresh");
				jRefreshButton.addActionListener(this);
			}
            {
                jSeparator1 = new JSeparator();
                jPanel.add(jSeparator1);
                jSeparator1.setBounds(161, 2, 4, jPanel.getBounds().height);
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
                jStatusLabel.setText("Load Maze First...");
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
            int returnVal = fc.showOpenDialog(AlgoSimulator.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fc.getSelectedFile();
                    FileInputStream fis = new FileInputStream(file);
                    GZIPInputStream gzis = new GZIPInputStream(fis);
                    ObjectInputStream in = new ObjectInputStream(gzis);
                    myMaze = (Maze)in.readObject();
                    in.close();
                    mazeStatus = " [ "+myMaze.width+" X "+myMaze.height+" Maze, Wall Penalty:"+((Wall)myMaze.walls.get(0)).penalty +"] | ";
                    
                    if(algoType==Algorithms.ValueIter)
                        basicAlgStatus = " Value Iteration";
                    else
                        basicAlgStatus = " Policy Iteration";
                    
                    additionalAlgStatus = "-> Click Initialise...";
                    
                    sqSize = (int)Math.min(Math.floor((this.getSize().width-X-10)/myMaze.width),
					 		  Math.floor((this.getSize().height-Y-10)/myMaze.height));
                    if(sqSize>200)
                        sqSize=200;
                    sqSizeTextField.setText(""+sqSize);
                    
                } catch(Exception e) {
                    Utility.show(e.getMessage());
                }
                alg=null;
                
            } 
            repaint();
        }
        
        else if(evt.getActionCommand().equals("Initialize") && myMaze!=null)
        {
            Utility.show("Initialize");
            
            double pjog = Double.parseDouble(pjogTextField.getText());
            double precision = Double.parseDouble(jConverErrorTextField.getText());
            
            additionalAlgStatus = "";
            
            if(algoType == Algorithms.ValueIter) {    
                alg = new ValueIteration(myMaze,pjog,precision);
            }
            if(algoType == Algorithms.PolicyIter) {
                int valueLimit = Integer.parseInt(jVFLimitTextField.getText());
                int iterLimit = Integer.parseInt(jIterLimitTextField.getText());
                alg = new PolicyIteration(myMaze,pjog,precision,valueLimit,iterLimit);
            }
            repaint();
        }
        
        else if(evt.getActionCommand().equals("Update")) {
            if(algoType == Algorithms.ValueIter) {
                alg.setProperty(ValueIteration.Properties.PJOG, pjogTextField.getText());
                alg.setProperty(ValueIteration.Properties.ConvergenceError, jConverErrorTextField.getText());
            }
            else {
                alg.setProperty(PolicyIteration.Properties.PJOG, pjogTextField.getText());
                alg.setProperty(PolicyIteration.Properties.ConvergenceError, jConverErrorTextField.getText());
                alg.setProperty(PolicyIteration.Properties.ValueFunctionLimit,jVFLimitTextField.getText());
                alg.setProperty(PolicyIteration.Properties.IterationLimit,jIterLimitTextField.getText());
            }
        }
        
        else if(evt.getActionCommand().equals("Step")) {
            Utility.show("step");
            if(alg!=null) {
                if(alg.step()) {
                    additionalAlgStatus = " -- Converged after "+alg.getNumOfIters()+" steps!!";
                	additionalAlgStatus += " TimeTaken: "+alg.getTime()+"ms";
                }
                else
                    additionalAlgStatus = " -- "+alg.getNumOfIters()+" steps.";
            }
            repaint();
        }
        
        else if (evt.getActionCommand().equals("Execute")) {
            Utility.show("execute");
            int delay = Integer.parseInt(jDelayTextField.getText());
            
            if(alg!=null) {
                while(!alg.step()){
                    additionalAlgStatus = " -- "+alg.getNumOfIters()+" steps.";
                    myUpdate();
                    Utility.delay(delay);
                }
                additionalAlgStatus = " -- Converged after "+alg.getNumOfIters()+" steps!!";
                additionalAlgStatus += " TimeTaken: "+alg.getTime()+"ms";
                myUpdate();
            }   
        }
        
        else if(evt.getActionCommand().equals("Refresh")) {
            Utility.show("Refresh");
            repaint();
        }
        
        else if(evt.getSource().getClass().getName().equals("javax.swing.JTextField")){
            repaint();
            //System.out.println(sqSizeTextField.getText());
        }
        else if(evt.getSource().getClass().getName().equals("javax.swing.JCheckBox")){
            JCheckBox jcb = (JCheckBox)evt.getSource();
            
            if(jcb.getText().equals("Show Values"))
                ShowValue = jcb.isSelected();
            
            if(jcb.getText().equals("Show Policy"))
                ShowPolicy = jcb.isSelected();
            repaint();
        }
    }
    
    int sqSize=40;
    int X= 192;
    int Y= 100;
    Color GoldColor = new Color(200,100,55);
    
    public void paint(Graphics g)
    {
        super.paint(g);
        this.myUpdate(g);
    }
    
    public void myUpdate()
    {
        myUpdate(getGraphics());
    }
    
    private void myUpdate(Graphics g)
    {
        jSeparator1.setSize(4,jPanel.getBounds().height-2);
        jStatusLabel.setText(mazeStatus+basicAlgStatus+additionalAlgStatus);
        showGrid(getGraphics());
    }
    
    public void showGrid(Graphics g)
    {
        sqSize = Integer.parseInt(sqSizeTextField.getText());
        
       if(alg!=null) {
            drawInfo(g);
        }
        
        if(myMaze!=null) {
            if(alg==null){
                g.setColor(new Color(220,220,220));
                g.fillRect(X,Y,sqSize*myMaze.width,sqSize*myMaze.height);
                drawGoal(g);
            }
            drawMaze(g);
            drawWalls(g);
        }
    }
    
    private void drawMaze(Graphics g)
    {
        g.setColor(Color.black);
        for (int counter = 0 ; counter <= myMaze.width; counter++) {
            g.drawLine(X+sqSize*counter,Y+0,X+sqSize*counter,Y+sqSize*myMaze.height);
        }
        for (int counter = 0 ; counter <= myMaze.height; counter++) {
            g.drawLine(X+0,Y+sqSize*counter,X+sqSize*myMaze.width,Y+sqSize*counter);
        }
    }
    
    private void drawGoal(Graphics g)
    {
        Vector goals = myMaze.goals;
        State s;
        for (int i=0;i<goals.size();i++)
        {
            s = (State)goals.get(i);
            g.setColor(GoldColor);
            g.fillRect(X+s.x*sqSize+1, Y+(myMaze.height-1-s.y)*sqSize+1, sqSize-2, sqSize-2);
        }
    }
    
    private void drawWalls(Graphics g)
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
                //						Utility.show("left wall ax,ay,bx,by= "+aX+","+aY+","+bX+","+bY);
                	GraphicsUtil.drawLine(g,X+aX,Y+aY,X+bX,Y+bY,5);
                break;
            }
        }
    }
    
    private void drawInfo(Graphics g)
    {
        ValueFunction valuefunc = alg.getValueFunction();
        double[][] values = valuefunc.stateValue;
        int max = 1+(int)Math.ceil(valuefunc.getMax());
        
        int[][] policy = alg.getPolicy();
        
        for (int xval=0 ; xval <myMaze.width; xval++) {
            for (int y=0; y < myMaze.height; y++) {
                int yval = myMaze.height-1-y;
                if (values[xval][y] > 0) {
                    int red = 155-Math.min((int)(255.0*(values[xval][y])/max),155);
                    int green = 155-Math.min((int)(255.0*(values[xval][y])/max),155);
                    int b = 255-Math.min((int)(255.0*(values[xval][y])/max),220);
                    
                    g.setColor(new Color(red,green,b));
                }
                else
                    g.setColor(GoldColor);
                
                g.fillRect(X+xval*sqSize+1,Y+yval*sqSize+1,sqSize-1,sqSize-1);
                
                g.setColor(Color.white);
                if(ShowValue)
                    g.drawString(df.format(values[xval][y]),X+xval*sqSize+5,Y+(yval+1)*sqSize-5);
                
                if(ShowPolicy) {
                    int x1 = xval*sqSize+sqSize/2;
                    int y1 = yval*sqSize+sqSize/2;
                    int x2 = x1;
                    int y2 = y1;
                    switch (policy[xval][y]) {
                        case Action.UP:
                            y2=yval*sqSize;
                        	break;
                        case Action.LEFT:
                            x2=xval*sqSize;
                        	break;
                        case Action.DOWN:
                            y2=yval*sqSize+sqSize;
                        	break;
                        case Action.RIGHT:
                            x2=xval*sqSize+sqSize;
                        	break;
                        default:
                            continue;
                    }
                    g.drawLine(X+x1,Y+y1,X+x2,Y+y2);
                }
            }
        }
    }	
    
}
