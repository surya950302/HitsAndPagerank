import java.io.*;
import java.util.*;
import java.lang.*;

public class pgrk_6108
{
	public void display(double arr[], int nonodes, int iterno)
	{
		String output = "Base  : " + iterno + "  :";
        for (int i = 0; i < nonodes; i++) 
        {
            output += " P["+ i +"] = "
            + String.format("%.7f", arr[i]);
        }
        System.out.println(output);
	}
	
	public static void main(String args[]) throws IOException
	{
		if(args.length != 3)
		{
			System.out.println("Please Enter all 3 Parameters");
			return;
		}
		Resourse2 obj=new Resourse2();
		pgrk_6108 obj1=new pgrk_6108();

		String inputFile="";
		double inputVal=0.0;
		int iterations=0;
		int iterationNo=0;
		int noNodes=0;
		double AdjMat[][];
		double OutLinks[];
		double initialValue=0.0;
		double errorRate = 0.0;
		double d = 0.85;
        double constantVal;
        double PreviousPageRank[];
        double CurrentPageRank[];
        double NonConstant[];

        //Initialize the value for Convergence & icreasing iterationNo
        boolean checkConvergence = false;

		try
		{
			inputFile=args[2];
			inputVal=Double.parseDouble(args[1]);
			iterations=Integer.parseInt(args[0]);
			noNodes=obj.getNoNodes(inputFile);
			if(noNodes==0)
			{
				System.out.println("Cant Get No. of Nodes");
			}

			AdjMat = new double[noNodes][noNodes];
			AdjMat = obj.makeAdjMat(inputFile,noNodes);

			//Create array to store outgoing links
            OutLinks = new double[noNodes];
            OutLinks = obj.setOutlink(AdjMat,noNodes);

            initialValue =obj.getInitialValue(inputVal,noNodes);
            if(initialValue == 100)
            {
                System.out.println("Incorrect Initial Value. Please enter 1, -1 or -2.");
                return;
            }

			//Determine value of errorRate
            if(iterations < 0)
                errorRate = (double)Math.pow(10, (iterations));
            else if(iterations == 0)
                errorRate = (double)Math.pow(10, (-5));

            //Computing constants
            constantVal = (1-d)/noNodes;


            //Initialize Previous Page rank array
            PreviousPageRank = new double[noNodes];
            PreviousPageRank = obj.initialize(PreviousPageRank,noNodes,initialValue);

            //Current PageRank Array
            CurrentPageRank = new double[noNodes];

			//Display Base values for Previous Page Rank Array
            obj1.display(PreviousPageRank,noNodes,iterationNo);

            iterationNo = 1;
            
            if(iterations > 0)
            {
                while (iterationNo < (iterations+1))
                {
                	//Store bracket value
                    NonConstant = new double[noNodes];
                    NonConstant=obj.setNonConstant(AdjMat, PreviousPageRank, OutLinks, noNodes);


                    CurrentPageRank=obj.setCurrentPageRank(constantVal, d, NonConstant ,noNodes);

                   	obj1.display(CurrentPageRank,noNodes,iterationNo);

                   	//Copy current to previous
                    for(int i = 0; i < noNodes; i++)
                        PreviousPageRank[i] = CurrentPageRank[i];

                    iterationNo++;

                }
            }
            else
            {
            	while(!checkConvergence)
                {
                	//Store bracket value
                    NonConstant = new double[noNodes];
                    NonConstant=obj.setNonConstant(AdjMat, PreviousPageRank, OutLinks, noNodes);

                    CurrentPageRank=obj.setCurrentPageRank(constantVal, d, NonConstant ,noNodes);

                    obj1.display(CurrentPageRank,noNodes,iterationNo);

                    boolean checkPageConvergence =obj.checkConvergence(CurrentPageRank, PreviousPageRank, errorRate, noNodes);
                    
                    if(checkPageConvergence)
                        break;

                    //Copy current to previous
                    for(int i = 0; i < noNodes; i++)
                         PreviousPageRank[i] = CurrentPageRank[i];
                    
                    iterationNo++;

                }

            }

		}
		catch(Exception e)
		{
			System.out.println("Here is the problem: "+e);
			return;
		}

	}
}
class Resourse2
{
	public int getNoNodes(String inputfile) throws IOException
	{
		int noNodes = 0;
		BufferedReader bufr = new BufferedReader(new FileReader(inputfile));
        
        //Finding the number of nodes
        String temp = bufr.readLine();
        String[] separator = temp.split(" ");
            
        if(temp != null)
            noNodes = Integer.parseInt(separator[0]);

        return noNodes;
	}
	public double[][] makeAdjMat(String inputfile, int nonodes) throws IOException
	{
		int r;
        int c;
        String cursor;
        
        BufferedReader bufr = new BufferedReader(new FileReader(inputfile));    
        bufr.readLine();
        //Create the Adjacency Matrix
        double[][] adjMat = new double[nonodes][nonodes];
        //Assign values to the Adjacency Matrix
            
        while ((cursor = bufr.readLine()) != null)
       	{
            String[] another_separator = cursor.split(" ");
            r = Integer.parseInt(another_separator[0]);
            c = Integer.parseInt(another_separator[1]);
            adjMat[r][c] = 1;
        }
        return adjMat;
	}
	public double[] setOutlink(double adjmat[][], int nonodes)
	{
		double outlink[]= new double[nonodes];
		for(int i = 0; i < nonodes; i++)
        {
            for(int j = 0; j < nonodes; j++)
                outlink[i] += adjmat[i][j];
        }
        return outlink;
	}
	public double getInitialValue(double inputval, int nonodes)
	{
		double initialvalue=0.0;
		if(inputval == 0 || inputval == 1)
            initialvalue = inputval;
        else if(inputval == -1)
            initialvalue = (double)1/nonodes;
        else if(inputval == -2)
            initialvalue = (double)1/Math.sqrt(nonodes);
        else
        	initialvalue = 100;

        return initialvalue;
	}
	public double[] initialize(double arr[] ,int nonodes,double initialvalue)
	{
		for(int i=0; i<nonodes; i++)
        {
            arr[i] = initialvalue;
        }
		return arr;
	}
	public double[] setNonConstant(double brr[][],double arr1[], double arr2[], int nonodes)
	{
		double arr[]= new double[nonodes];
		for(int i = 0; i < nonodes; i++)
            arr[i] = 0.0;

        for(int i = 0; i < nonodes; i++)
        {
            for(int j = 0; j < nonodes; j++)
            {
                if(brr[j][i] == 1)
                    arr[i] += arr1[j]/arr2[j];
            }
        }

        return arr;
    }
    public double[] setCurrentPageRank(	double constantval, double D, double nonconstant[],int nonodes)
    {
    	double arr[]= new double[nonodes];

    	for(int i = 0; i < nonodes; i++)
            arr[i] = constantval + D*nonconstant[i];

    	return arr;
    }
    //Check the difference in the old and new values
    public static boolean checkConvergence(double[] newval, double[] oldval, double errorRate, int nonodes)
    {
        boolean checkConvergence = true;
        for (int i = 0; i < nonodes; i++)
        {
            if(Math.abs(oldval[i] - newval[i]) > errorRate)
                checkConvergence = false;
        }
        return checkConvergence;
    }
}
