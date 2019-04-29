package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class PredictionSimulator {
public static void simulatePrediction(String patternsPath) throws IOException {

		String line; 
		String[] splittedLine;
		String[] splittedCardinalities;
		String akp;
		
		int minSubjsObj;
		int maxSubjsObj;
		int avgSubjsObj;
		int minSubjObjs;
		int maxSubjObjs;
		int avgSubjObjs;
		int maxSubjsObjPredicted;
		int maxSubjObjsPredicted;
		// input
		Scanner patternCardinalities = new Scanner(new File(patternsPath + "/patternCardinalities.txt"));
		// output
		PrintWriter pw = new PrintWriter(new FileWriter(patternsPath + "/predictedCardinalities.txt"));
		
		while (patternCardinalities.hasNextLine()) {
			// subject##property##object## maxSubjsObjPredicted-maxSubjObjsPredicted
			line = patternCardinalities.nextLine(); 

			splittedLine = line.split(" ");

			// estraggo tripla <S, P, O> 
			akp = splittedLine[0];

			// divido la sezione delle cardinalita
			splittedCardinalities = splittedLine[1].split("-");
			// estraggo cardinalita
			minSubjsObj = Integer.parseInt(splittedCardinalities[0]);
			maxSubjsObj = Integer.parseInt(splittedCardinalities[1]);
			avgSubjsObj = Integer.parseInt(splittedCardinalities[2]);
			minSubjObjs = Integer.parseInt(splittedCardinalities[3]);
			maxSubjObjs = Integer.parseInt(splittedCardinalities[4]);
			avgSubjObjs = Integer.parseInt(splittedCardinalities[5]);
			
			// simulo previsione
			maxSubjsObjPredicted = maxSubjsObj > 2 * avgSubjsObj ? 2 * avgSubjsObj : maxSubjsObj;
			maxSubjObjsPredicted = maxSubjObjs > 2 * avgSubjObjs ? 2 * avgSubjObjs : maxSubjObjs;
			
			pw.println(akp + " " + maxSubjsObjPredicted + "-" + maxSubjObjsPredicted);
			// pw.flush();
		}

		patternCardinalities.close();
		pw.close();
	}
}
