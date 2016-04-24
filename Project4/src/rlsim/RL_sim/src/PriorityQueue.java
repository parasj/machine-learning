 /*
 * Created on Jan 13, 2005
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
import java.util.Vector;
public class PriorityQueue {

	private Vector pQueue;
	public PriorityQueue()
	{
		pQueue = new Vector();
	}
	
	public void insert(State st, double priority)
	{
		PQueueNode newNode = new PQueueNode(st, priority);
		int position = pQueue.indexOf(newNode);
		
		//if element already exists in the pqueue then remove that element if its priority is 
		//less than new priority
		
		if(-1 != position)
		{
			if( ((PQueueNode)pQueue.get(position)).priority < newNode.priority)
				pQueue.remove(position);
			else 
				return;
		}
		
		//re-insert the element at the appropriate position with appropriate priority
		PQueueNode node;
		int i;
		for(i=0;i<pQueue.size();i++)
		{
			node = (PQueueNode)pQueue.get(i);
			if(newNode.priority > node.priority)
				break;
		}
		pQueue.insertElementAt(newNode,i);
	}
	
	public boolean isEmpty()
	{
		if(pQueue.isEmpty())
			return true;
		else return false;
	}
	
	public State pop()
	{
		return ((PQueueNode)pQueue.remove(0)).state;
	}
	
	void printQueue()
	{
		PQueueNode node;
		System.out.println("==============");
		System.out.println("Queue has "+pQueue.size()+" elements");
		for(int i=0;i<pQueue.size();i++)
		{
			node = (PQueueNode)pQueue.get(i);
			node.state.printState();
			Utility.show0("|priority: "+node.priority);
		}
	}
	
	public void placeOnTop(State st, double priority)
	{
		PQueueNode node = new PQueueNode(st, priority);
		pQueue.add(0,node);
	}
	
	public Vector getQueue()
	{
		return pQueue;
	}
	
}
