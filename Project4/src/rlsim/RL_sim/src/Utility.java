/** 
 * Utility class having various miscellaneous functions
 */

public class Utility
{
	public static boolean DEBUG = false;
	
    public static double dec3(double a)
    {
        a = a*100;
        a = Math.round(a);
        a = a/100;
        return a;
    }
    
    public static void show(String str)
    {
    	if(DEBUG)
    		System.out.println(str);
    }
    
    public static void show0(String str)
    {
    		System.out.println(str);
    }
    
    public static void show()
    {
    	if(DEBUG)
    		System.out.println();
    }
    
    public static void delay(int t)
    {
    	try {
    		Thread.sleep(t);
    	}
    	catch(Exception e) {
    		
    	}
    }
}
