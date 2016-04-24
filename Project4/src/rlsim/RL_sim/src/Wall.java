/*
 * Created on Nov 8, 2004
 *
 */

import java.io.Serializable;

class Wall implements Serializable
{
	int x;
	int y;
	int dir;
	int penalty;
	public static final int UP=0,RIGHT=1,DOWN=2,LEFT=3;//location of wall
	
	public Wall()
	{
		
	}
	
	public Wall(int _x, int _y, int _dir, int _penalty)
	{
		x = _x;
		y = _y;
		dir = _dir;
		penalty = _penalty;
	}
	
	public Wall(int _x, int _y, int _dir)
	{
		x = _x;
		y = _y;
		dir = _dir;
		penalty = 0;
	}
	
	public boolean equals(Object obj)
	{
		Wall w=(Wall)obj;

		return (x==w.x && y==w.y && dir==w.dir);
	}
	
}