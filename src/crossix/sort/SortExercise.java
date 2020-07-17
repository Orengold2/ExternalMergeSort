package crossix.sort;

import java.nio.file.Paths;
/**
 * @author ogoldenberg
 * @date July 11, 2020
 * @description Sort Exercise:  sorts efficiently the csv file based on one of its columns. The program should never have more than k (MAX_BUFFER_SIZE) number of lines in memory.
 *              Time complexity: N(log K)
 *              
 * @parameters: InputFullPath, ColumnNumber, MaxBufferSize (You MUST to put 3 Arguments in the command line).
					1. path to the input file.
					2. column number
					3. max number of lines in memory
 */
public class SortExercise {

		public static void main(String [] args) throws Exception {
			
			
			//String[] arguments = {"C:\\eclipse-2020\\eclipse-workspace\\SorterFiles\\input.csv" ,"5","10"};

			//init default params
			long len = args.length;
			String strInputPath="";
			int columnNumber =FileConstants.COLUMN_NUM_DEFAULT;
			int maxLinesInMemory=FileConstants.MAX_BUFFER_SIZE_DEFAULT;
			
			//setup params from command line arguments
			if (len >0) {
				if (args[0] != null) strInputPath = args[0]; //input path param
				if (args[1] != null) columnNumber = Integer.parseInt(args[1]); //Column Number Param
				if (args[2] != null) maxLinesInMemory = Integer.parseInt(args[2]); //Number of lines in memory Param
				BigFileSortUtil bigFileSortUtil = new BigFileSortUtil();
				System.out.println(String.format("Starting Sort file:[%s] sorted by column Number: %s", strInputPath, columnNumber));
				long startProcessTime = System.currentTimeMillis();
				bigFileSortUtil.sortBigFile(Paths.get(strInputPath), maxLinesInMemory, columnNumber);
				System.out.println(String.format("The total time is %s milli seconds", System.currentTimeMillis() - startProcessTime));
			}else {
				System.out.println(String.format("Arguments [args] is missing in the command line. Please Add 3 args: InputFullPath, ColumnNumber, MaxBufferSize"));
			}
		}
	
		
	}
