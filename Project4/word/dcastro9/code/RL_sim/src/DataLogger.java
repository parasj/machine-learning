import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.Random;
import java.util.zip.GZIPInputStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class DataLogger
{
    public Maze myMaze;
    double precision = 0.001;
    double pjog = 0.3;
    
    double learningRate = 0.7;
    double epsilon = 0.1;
    boolean decayingLR = true;
    
    int maxBackups = 10;
    
    double tinyThreshold = 0.01;
    
    public DataLogger()
	{
	    JFileChooser fc = new JFileChooser("./mazes/");
	    int returnVal = fc.showOpenDialog(new JFrame());
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	        try {
	            File file = fc.getSelectedFile();
	            FileInputStream fis = new FileInputStream(file);
	            GZIPInputStream gzis = new GZIPInputStream(fis);
	            ObjectInputStream in = new ObjectInputStream(gzis);
	            myMaze = (Maze)in.readObject();
	            in.close();
	        }
	        catch(Exception e) {
                Utility.show(e.getMessage());
            }
	    }
	}
    
    public void logValueIteration()
    {
        ValueIteration valItr = new ValueIteration(myMaze,pjog,precision);
	    while(!valItr.step())
	        ;
	    ValueFunction valuefunc = valItr.getValueFunction();
	    System.out.print(0+"\t");
	    valuefunc.displayValues();
	}
    
    public void logQLearning(int series, int cycles)
    {
        
        QLearning ql = new QLearning(myMaze,pjog,learningRate,epsilon,decayingLR);
        long startTime = new Date().getTime();
        for (int i=0; i<series; i++) {
            for (int j=0; j<cycles; j++) {
                while(!ql.step())
                    ;
                long endTime = new Date().getTime();
                System.out.print((endTime-startTime)+"\t");
                System.out.print(ql.evalPolicy()+"\n");
            }
        }
    }
    
    PrioritizedSweeping ps;
    
    public void logPSweeping(int cycles)
    {
        long startTime = new Date().getTime();
        long endTime = new Date().getTime();
        System.out.print(0+"\t");
        System.out.print((endTime-startTime)+"\t");
        System.out.print(ps.evalPolicy()+"\t");
        //System.out.print(ps.maxCounterLast+"\n");
        System.out.print(ps.averageBackups+"\n");
        for (int j=0; j<cycles; j++) {
            while(!ps.step())
                ;
            endTime = new Date().getTime();
            System.out.print(j+1+"\t");
            System.out.print((endTime-startTime)+"\t");
            System.out.print(ps.evalPolicy()+"\t");
            //System.out.print(ps.maxCounterLast+"\n");
            System.out.print(ps.averageBackups+"\n");
            //if(value<=0.0)
            //    break;
        }
    }
    
    public void logPSweeping()
    {
        int i;
        maxBackups = 0;
        Random rand = new Random();
        int seed;
        ps = new PrioritizedSweeping(myMaze,pjog,epsilon,maxBackups,tinyThreshold);
        for(int trials=0; trials<10; trials++) {
            System.err.println("trial:"+trials);
            seed = rand.nextInt();
            for(i=0;i<6;i=i+1) {
            //for(i=5;i<30;i=i+5) {
                //maxBackups = 50+i*150;
                maxBackups = (int)Math.pow(3,i)-1;
                //if(maxBackups>myMaze.height*myMaze.width)
                //    maxBackups = myMaze.height*myMaze.width;
                System.err.println("i==>"+i+"|maxBackups=>"+maxBackups);
                ps.setProperty(PrioritizedSweeping.Properties.MaxBackups,""+maxBackups);
                ps.initialize();
                ps.setSeed(seed);//Setting some seed based on trial
                logPSweeping(100);
            }
        }
    }
    
    public static void main(String[] args)
    {
        DataLogger dl = new DataLogger();
        
        //dl.logQLearning(1,100);
        
        dl.logPSweeping();
        System.exit(0);
    }
}
