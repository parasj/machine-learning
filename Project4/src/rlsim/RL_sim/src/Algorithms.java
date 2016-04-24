/*
 * Created on Nov 7, 2004
 *
 */

public interface Algorithms
{
    public static final int ValueIter=1, PolicyIter=2;
    

	public void initialize();
	public boolean step();
	
	public void setProperty (int name, String value);
	
	public ValueFunction getValueFunction();
	public int[][] getPolicy();
	
    public int getNumOfIters();
	public long getTime();
}