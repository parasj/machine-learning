/*
 * Created on Jan 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author ryk
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StateAndVisits {
	State state;
	int numVisits;
	
	public StateAndVisits(State _st)
	{
		state = new State(_st.x, _st.y);
//		state.copy(_st);
		numVisits = 1;
	}
	
	public int incrementVisits()
	{
		numVisits += 1;
		return numVisits;
	}
	
	public int getVisits()
	{
		return numVisits;
	}
	
	public boolean equals(Object Obj)
	{
		StateAndVisits snv = (StateAndVisits)Obj;
		return state.equals(snv.state);
	}

}
