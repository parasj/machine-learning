

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

public class MazeEditor extends javax.swing.JFrame 
				   implements ActionListener
{
	/*all the components*/
	private JPanel jPanel;
	private JButton jHeight, jWidth, jWalls, jSetPenalty, jAddGoals, jSaveMaze, jBoxSize, jResetMaze;
	private JTextField jHeightTextField, jWidthTextField, jPenaltyTextField, jBoundaryPenaltyTextField, jBoxSizeTextField;
	private JPanel jGridPanel;
	private JSeparator jSeparator1;
//	private JComboBox jComboBox1;
	
	/*all the listeners*/
	private SymMouse aSymMouse;

	/*other objects and variables*/
	private Maze myMaze;
	private int penalty = 0;	//the penalty to be set for the walls
	private int boundaryPenalty = 0; //the penalty to be set for the boundary walls
	int nodeLength = 40;	//indicates the size of the square box to be displayed in the GUI
							//by default it is 40 pixels. increase to increase magnification factor
	private boolean boundariesAdded = false; 
	private int edit_state;
	public static int EDIT_WALLS =1, ADD_GOALS=2, ADD_START=3, ASSIGN_REWARD=4;
    private JButton jLoadMaze;
	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MazeEditor inst = new MazeEditor();
		inst.setVisible(true);
	}
	
	public MazeEditor() {
		super("RL-MDP:Maze Editor");
		initGUI();
	}
	
	private void initGUI() {
		try {
			
			myMaze = new Maze(2,2);
			setSize(600, 600);
			this.setExtendedState(MAXIMIZED_BOTH);
			//BorderLayout thisLayout = new BorderLayout();
			//this.getContentPane().setLayout(thisLayout);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			aSymMouse = new SymMouse();
			{
				jPanel = new JPanel();
				this.getContentPane().add(jPanel, BorderLayout.CENTER);
				jPanel.setBackground(new java.awt.Color(235, 241, 238));
				jPanel.setLayout(null);
				//jPanel.setPreferredSize(new java.awt.Dimension(20, 20));
				{
					jHeight = new JButton();
					jPanel.add(jHeight);
					jHeight.setText("Height");
					jHeight.setBounds(5 + 65, 5, 5 + 85, 25);
					jHeight.addActionListener(this);
				}
				{
					jHeightTextField = new JTextField("2");
					jHeightTextField.setBounds(5, 5, 65, 25);
					jPanel.add(jHeightTextField);
				}
				{
					jWidth = new JButton();
					jPanel.add(jWidth);
					jWidth.setText("Width");
					jWidth.setBounds(5 + 65, 30 + 5, 5 + 85, 25);
					jWidth.addActionListener(this);
				}
				{
					jWidthTextField = new JTextField("2");
					jWidthTextField.setBounds(5, 30 + 5, 65, 25);
					jPanel.add(jWidthTextField);
				}
				{
					jSetPenalty = new JButton();
					jPanel.add(jSetPenalty);
					jSetPenalty.setText("Set Penalty");
					jSetPenalty.setBounds(5 + 65, 30 + 30 + 5, 5 + 85, 25);
					jSetPenalty.addActionListener(this);
				}
				{
					jPenaltyTextField = new JTextField("50");
					jPenaltyTextField
						.setBounds(5, 30 + 30 + 5, 30 + 30 + 5, 25);
					jPanel.add(jPenaltyTextField);
				}
				{
					jWalls = new JButton();
					jPanel.add(jWalls);
					jWalls.setText("Add Walls");
					jWalls.setBounds(5, 30 + 30 + 30 + 5, 5 + 70 + 80, 25);
					jWalls.addActionListener(this);
				}
				{
					jAddGoals = new JButton();
					jPanel.add(jAddGoals);
					jAddGoals.setText("Add Goals");
					jAddGoals.setBounds(5,30+30+30+30+5,5+70+80,25);
					jAddGoals.addActionListener(this);
				}
				{
					jSaveMaze = new JButton();
					jPanel.add(jSaveMaze);
					jSaveMaze.setText("Save Maze");
					jSaveMaze.setBounds(5,30+30+30+30+30+5,5+70+80,25);
					jSaveMaze.addActionListener(this);
				}
				{
					jBoxSize = new JButton();
					jPanel.add(jBoxSize);
					jBoxSize.setText("Box Size");
					jBoxSize.setBounds(5+70,30+30+30+30+30+30+5,5+80,25);
					jBoxSize.addActionListener(this);
				}
				{
					jBoxSizeTextField = new JTextField("40");
					jBoxSizeTextField.setBounds(5,30+30+30+30+30+30+5, 65, 25);
					jPanel.add(jBoxSizeTextField);
				}
				{
					jResetMaze= new JButton();
					jPanel.add(jResetMaze);
					jResetMaze.setText("Reset Maze");
					jResetMaze.setBounds(5,30+30+30+30+30+30+30+5,5+70+80,25);
					jResetMaze.addActionListener(this);
				}
				{
					jLoadMaze= new JButton();
					jPanel.add(jLoadMaze);
					jLoadMaze.setText("Load Maze");
					jLoadMaze.setBounds(5,30+30+30+30+30+30+30+30+5,5+70+80,25);
					jLoadMaze.addActionListener(this);
				}
				{
					jSeparator1 = new JSeparator();
					jPanel.add(jSeparator1);
					jSeparator1.setBounds(188, 2, 2, 363);
					jSeparator1.setBorder(BorderFactory.createTitledBorder(
						null,
						"",
						TitledBorder.LEADING,
						TitledBorder.TOP,
						new java.awt.Font("MS Sans Serif", 0, 11),
						new java.awt.Color(0, 0, 0)));
				}
				{
					jGridPanel = new GridPanel();
					jPanel.add(jGridPanel);
					//jGridPanel.setBackground(new java.awt.Color(255, 255, 255));
					jGridPanel.addMouseListener(aSymMouse);
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class GridPanel extends JPanel
	{
		public GridPanel()
		{
			GridLayout jGridPanelLayout = new GridLayout(15, 15);
			//setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
			jGridPanelLayout.setRows(10);
			jGridPanelLayout.setColumns(10);
			setLayout(jGridPanelLayout);
			setLocation(230, 50);
			setSize(10,10);
			//setBounds(230, 50, 800, 800);
			//setSize(600,600);
//			setPreferredSize(new Dimension(80,80));
//			setPreferredSize(600,600);
			//revalidate();
		}

		public void paintComponent(Graphics g)
		{
		    super.paintComponent(g);
		    Graphics2D g2= (Graphics2D) g;
		    setSize(myMaze.width*nodeLength+4, myMaze.height*nodeLength+4);
		    drawGoals(g);
		    plotMaze(g2);
		    drawWalls(g2);
		}
	}
	
	   class SymMouse extends java.awt.event.MouseAdapter
		{
			public void mouseClicked(java.awt.event.MouseEvent event)
			{
				Object object = event.getSource();
				int clickX = event.getX();
				int clickY = (nodeLength*myMaze.height) - event.getY();
				int nodeX = (clickX / nodeLength);				
				int nodeY = (clickY / nodeLength);
//				System.out.println("nodes= "+nodeX+","+nodeY);
				switch(edit_state)
				{
					case 1:
						int lowerLimX = (nodeX*nodeLength) + (int)(0.1*nodeLength);
						int upperLimX = ((nodeX+1)*nodeLength) - (int)(0.1*nodeLength);
						if(clickX<lowerLimX)
						{
							myMaze.addWall(new Wall(nodeX, nodeY, Wall.LEFT, penalty));
							myMaze.addWall(new Wall(nodeX-1, nodeY, Wall.RIGHT, penalty));
						}
						else if (clickX>upperLimX)
						{
							myMaze.addWall(new Wall(nodeX,nodeY, Wall.RIGHT, penalty));
							myMaze.addWall(new Wall(nodeX+1, nodeY, Wall.LEFT, penalty));
						}
						
						int lowerLimY = (nodeY*nodeLength) + (int)(0.1*nodeLength);
						int upperLimY = ((nodeY+1)*nodeLength) - (int)(0.1*nodeLength);
						if(clickY<lowerLimY)
						{
							myMaze.addWall(new Wall(nodeX, nodeY, Wall.DOWN, penalty));
							myMaze.addWall(new Wall(nodeX, nodeY-1, Wall.UP, penalty));
						}
						else if (clickY>upperLimY)
						{
							myMaze.addWall(new Wall(nodeX,nodeY, Wall.UP, penalty));
							myMaze.addWall(new Wall(nodeX, nodeY+1, Wall.DOWN, penalty));
						}
						
						repaint();
						break;
					case 2:
						System.out.print("trying to add goals at ");
						System.out.println("nodeX,nodeY= "+nodeX+","+nodeY);
						myMaze.addGoal(new State(nodeX,nodeY));
						myMaze.printGoals();
//						System.out.println("number of goals= "+myMaze.goals.size());
						repaint();
						break;
					case 3:
						System.out.println("trying to add starts");
						break;
					case 4:
						System.out.println("trying to assign rewards");
						break;
				}
			}
		}
	
	public void actionPerformed(ActionEvent evt)
	{
		//System.out.println("event="+ evt);
	    if (evt.getSource() == jLoadMaze )
	    {
	        loadFile();
	        repaint();
	    }
		else if (evt.getSource() == jHeight )
		{
			int a = Integer.parseInt(jHeightTextField.getText());
			myMaze.height = a;
			repaint();
		}
		else if (evt.getSource() == jWidth)
		{
			int a = Integer.parseInt(jWidthTextField.getText());
			myMaze.width = a;
			repaint();
		}
		else if (evt.getSource() == jWalls)
		{
			edit_state = 1;
			if (!boundariesAdded)
			{
				addBoundaries();
			}
			repaint();
		}
		else if (evt.getSource() == jSetPenalty)
		{
			penalty = Integer.parseInt(jPenaltyTextField.getText());
		}
		else if (evt.getSource() == jAddGoals)
		{
			edit_state = 2;
		}
		else if (evt.getSource() == jSaveMaze)
		{
			saveFile();
		}
		else if (evt.getSource() == jBoxSize)
		{
			nodeLength =  Integer.parseInt(jBoxSizeTextField.getText());
			repaint();
		}
		else if (evt.getSource() == jResetMaze)
		{
			myMaze.height = 2;
			myMaze.width= 2;
			myMaze.walls.clear();
			myMaze.goals.clear();
		//	myMaze.starts.clear();
			boundariesAdded = false;
			repaint();
		}

	
	}
	
	void plotMaze(Graphics2D g)
	{
//		final int nodeLength = 40; //declared as a class variable
		final int startx = 0;
		final int starty = 0;
  
		for(int i=0;i<=nodeLength*myMaze.width;i=i+nodeLength)
			g.drawLine(i+startx,starty,i+startx,starty+(nodeLength*myMaze.height));

		for(int i=0;i<=nodeLength*myMaze.height;i=i+nodeLength)
			g.drawLine(startx,i+starty,startx+(nodeLength*myMaze.width),i+starty);
	}
	
	void drawWalls(Graphics g)
	{
		GraphicsUtil gr = new GraphicsUtil();
	
		int aX, aY, bX, bY;	//start and end points of the wall
		Vector wall = new Vector(myMaze.walls);
		Wall w = new Wall();
		myMaze.printWalls();
		for (int i=0;i<myMaze.walls.size();i++)
		{
			w = (Wall)wall.get(i);
			int nodeX = w.x;
			int nodeY = w.y;
			switch(w.dir)
			{
				case Wall.UP:
				 	aX = nodeX*nodeLength;
					bX = (nodeX+1)*nodeLength;
					aY = (myMaze.height - nodeY - 1)*nodeLength;
					bY = (myMaze.height - nodeY - 1)*nodeLength;
//					System.out.println("up wall ax,ay,bx,by= "+aX+","+aY+","+bX+","+bY);
					GraphicsUtil.drawLine(g,aX,aY,bX,bY,5);
					break;
				case Wall.DOWN:
				 	aX = nodeX*nodeLength;
					bX = (nodeX+1)*nodeLength;
					aY = (myMaze.height - nodeY)*nodeLength;
					bY = (myMaze.height - nodeY)*nodeLength;
//					System.out.println("down wall ax,ay,bx,by= "+aX+","+aY+","+bX+","+bY);
					GraphicsUtil.drawLine(g,aX,aY,bX,bY,5);
					break;
				case Wall.RIGHT:
				 	aX = (nodeX+1)*nodeLength;
			 		bX = (nodeX+1)*nodeLength;
				 	aY = (myMaze.height-nodeY-1)*nodeLength;
					bY = (myMaze.height-nodeY)*nodeLength;
//					System.out.println("right wall ax,ay,bx,by= "+aX+","+aY+","+bX+","+bY);
					GraphicsUtil.drawLine(g,aX,aY,bX,bY,5);
					break;
				case Wall.LEFT:
				 	aX = (nodeX)*nodeLength;
					bX = (nodeX)*nodeLength;
					aY = (myMaze.height-nodeY)*nodeLength;
					bY = (myMaze.height-nodeY-1)*nodeLength;
//					System.out.println("left wall ax,ay,bx,by= "+aX+","+aY+","+bX+","+bY);
					GraphicsUtil.drawLine(g,aX,aY,bX,bY,5);
					break;
			}
		}
	}
	
	void drawGoals(Graphics g)
	{
		int left, top, height, width;
		State curr;
//		System.out.println("size of goal vector= "+myMaze.goals.size());
		for(int i=0;i<myMaze.goals.size();i++)
		{
			curr = ((State)myMaze.goals.get(i));
			left = curr.x*nodeLength;
			top = (myMaze.height-curr.y-1)*nodeLength;
			width = nodeLength;
			height = nodeLength;
//			System.out.println("drawing at "+left+","+top+","+width+","+height);
			GraphicsUtil.fillRect(g,left,top,width, height, new Color(252,139,37));
		}
	}
	
	void loadFile()
	{
	    JFileChooser fc = new JFileChooser("./mazes/");
		int returnVal = fc.showOpenDialog(MazeEditor.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				File file = fc.getSelectedFile();
				FileInputStream fis = new FileInputStream(file);
				GZIPInputStream gzis = new GZIPInputStream(fis);
				ObjectInputStream in = new ObjectInputStream(gzis);
				myMaze = (Maze)in.readObject();
				in.close();
			} catch(Exception e) {
				Utility.show(e.getMessage());
			}
		}
	}
	
	void saveFile()
	{
	    JFileChooser fc = new JFileChooser("./mazes/");
		int returnVal = fc.showOpenDialog(MazeEditor.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
			    File file = fc.getSelectedFile();
				FileOutputStream fos = new FileOutputStream(file);
		        GZIPOutputStream gzos = new GZIPOutputStream(fos);
		        ObjectOutputStream out = new ObjectOutputStream(gzos);
		        out.writeObject(myMaze);
		        out.flush();
		        out.close();
			} catch(Exception e) {
				Utility.show(e.getMessage());
			}
		}
	}
	
	void addBoundaries()
	{
		boundariesAdded = true;
		int j=0;
		boundaryPenalty = penalty;
		for(int i=0;i<myMaze.width;i++)
		{
//			System.out.println("adding up walls");
			myMaze.addWall(new Wall(i,j,Wall.DOWN,boundaryPenalty));
		}
		j = myMaze.height-1;
		for(int i=0;i<myMaze.width;i++)
		{
//			System.out.println("adding down walls");
			myMaze.addWall(new Wall(i,j,Wall.UP,boundaryPenalty));
		}
		int i=0;
		for(j=0;j<myMaze.width;j++)
		{
//			System.out.println("adding left walls");
			myMaze.addWall(new Wall(i,j,Wall.LEFT,boundaryPenalty));
		}
		i=myMaze.width-1;
		for(j=0;j<myMaze.width;j++)
		{
//			System.out.println("adding right walls");
			myMaze.addWall(new Wall(i,j,Wall.RIGHT,boundaryPenalty));
		}
	}
}
