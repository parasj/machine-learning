/*
 * Created on Oct 23, 2004
 * this is just a main to test the different functions. this class does not in any way affect the 
 * working of the algorithm
 */

/**
 * @author ryk
 *
 */

public class Test {

	public static void main(String[] args) 
	{
		test2();
	}
	
	public static void test2() 
	{
		StateInfo[][] model = new StateInfo[2][2];
		for(int i=0;i<model.length;i++)
			for(int j=0;j<model[i].length;j++)
			{
				model[i][j] = new StateInfo();
			}
		model[0][0].addSuccs(Action.UP, new State(1,0), 50);
		model[1][0].addPreds(new State(0,0), Action.UP);
		
		model[0][0].addSuccs(Action.UP, new State(1,0), 50);
		model[1][0].addPreds(new State(0,0), Action.UP);
		
		model[0][0].addSuccs(Action.UP, new State(0,1), 0);
		model[0][1].addPreds(new State(0,0), Action.UP);
	}

}
