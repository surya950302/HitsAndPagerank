import java.io.*;
import java.util.*;
import java.lang.*;


public class Hits_6108
{
	public void display(double arr1[],double arr2[], int nonodes, int iterno)
	{

		String output="Base  : " + iterno + "  :";
        for (int i = 0; i < nonodes; i++)
        {
            output += " A/H["+ i +"] = "
            + String.format("%.7f", arr1[i])
            + "/"
            + String.format("%.7f", arr2[i]);
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
		Resourse1 obj=new Resourse1();
		Hits_6108 obj1=new Hits_6108();
		String inputFile="";
		double inputVal=0.0;
		int iterations=0;
		int noNodes=0;
		double AdjMat[][];
		double TransMat[][];
		double initialValue=0.0;
		double errorRate = 0.0;
		double AuthMat[];
		double HubMat[];
		double NewAuthMat[];
		double NewHubMat[];
		int iterationNo = 0;

		//Initialize the value for Convergence
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

			//Making Adjacency Matrix
			AdjMat=new double[noNodes][noNodes];
			AdjMat=obj.makeAdjMat(inputFile,noNodes);

			//Transposing Adjacency Matrix
			TransMat=new double[noNodes][noNodes];
			TransMat=obj.makeTransMat(AdjMat,noNodes);

			//set initial value
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

            //Initialize Authority and Hub arrays
            AuthMat = new double[noNodes];
            AuthMat = obj.initialize(AuthMat,noNodes,initialValue);
            HubMat = new double[noNodes];
            HubMat = obj.initialize(HubMat,noNodes,initialValue);

            //Display Base values for Authority and Hub Arrays
            obj1.display(AuthMat,HubMat,noNodes,iterationNo);

            //Incrementing iterationNo
            iterationNo+=1;

            //Run Iterations number of times
            if(iterations > 0)
            {
            	while(iterationNo <= iterations)
            	{
            		//Authoritative Step - Update Authority and Hub Array values
                    AuthMat=obj.step(TransMat,HubMat,noNodes);
                    HubMat=obj.step(AdjMat,AuthMat,noNodes);

                    //Scaling Step for Authority and Hub Values
                    NewAuthMat = obj.scaling(AuthMat,noNodes);
                    NewHubMat = obj.scaling(HubMat,noNodes);

                    //Display Authority and Hub Values for Iterations
                    obj1.display(NewAuthMat,NewHubMat,noNodes,iterationNo);

                    iterationNo++;
            	}

            }
            else
            {
            	while(!checkConvergence)
            	{
            		//Authoritative Step - Update Authority and Hub Array values
                    NewAuthMat=obj.step(TransMat,HubMat,noNodes);
                    NewHubMat=obj.step(AdjMat,AuthMat,noNodes);

                    //Scaling Step for Authority and Hub Values
                    NewAuthMat = obj.scaling(AuthMat,noNodes);
                    NewHubMat = obj.scaling(HubMat,noNodes);

                    //Display Authority and Hub Values for Iterations
                    obj1.display(NewAuthMat,NewHubMat,noNodes,iterationNo);

                    //Get value from Convergence method to continue the loop
                    boolean checkauthConvergence = obj.checkConvergence(NewAuthMat, AuthMat, errorRate, noNodes);
                    boolean checkhubConvergence = obj.checkConvergence(NewHubMat, HubMat, errorRate, noNodes);
                    
                    if(checkauthConvergence && checkhubConvergence)
                        break;
                    else
                    {
                        //Assign the current values to earlier arrays
                        for(int i = 0; i< noNodes; i++)
                        {
                            AuthMat[i] = NewAuthMat[i];
                            HubMat[i] = NewHubMat[i];
                        }
                    }

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
//class that helps make the data available for the algorithm
class Resourse1
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
	public double[][] makeTransMat(double adjmat[][], int nonodes)
	{
		double[][] transmat = new double[nonodes][nonodes];
        for(int i = 0; i < nonodes; i++)
        {
         	for(int j=0; j < nonodes; j++)
            {
                transmat[i][j] = adjmat[j][i];
            }
        }
        return transmat;
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
	public double[] step(double arr1[][], double arr2[], int nonodes)
	{
		double brr[]=new double[nonodes];
		
		for(int i=0;i<nonodes;i++)
		{
			double sum=0;
			for(int j=0;j<nonodes;j++)
			{
				sum += arr1[i][j] * arr2[j];
			}
			brr[i]=sum;
		}

		return brr;
	}
	public double[] scaling(double arr[],int nonodes)
	{
		double sum=0;
		double[] brr = new double[nonodes];
		for(int i=0; i < nonodes; i++)
            sum += Math.pow(arr[i], 2);
        
        double Sqr_root = 0.0;
        Sqr_root = Math.sqrt(sum);
        
        if(Sqr_root==0)
           	Sqr_root = 1;
        
        
        for(int i=0; i < nonodes; i++)
          	brr[i] = (double) (arr[i]/Sqr_root);
        return brr;
	}
	//Check the difference in the old and new values
    public boolean checkConvergence(double[] newval, double[] oldval, double errorRate, int noNodes)
    {
        boolean checkConvergence = true;
        for (int i = 0; i < noNodes; i++)
        {
            if(Math.abs((newval[i] - oldval[i])) > errorRate)
            {
				checkConvergence = false;
        	}
        }
        return checkConvergence;
    }
} 