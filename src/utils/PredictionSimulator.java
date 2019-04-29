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
		String akp;
		int maxSubjsObj;
		int avgSubjsObj;
		int maxSubjObjs;
		int avgSubjObjs;
		int maxSubjsObjPredicted;
		int maxSubjObjsPredicted;

		// input
		Scanner patternCardinalities = new Scanner(new File(patternsPath + "/patternCardinalities.txt"));
		// output
		PrintWriter pw = new PrintWriter(new FileWriter(patternsPath + "/predictedCardinalities.txt"));

		while (patternCardinalities.hasNextLine()) {
			// line = subject##property##object## maxSubjsObjPredicted-maxSubjObjsPredicted
			line = patternCardinalities.nextLine(); 

			splittedLine = line.split("##");

			akp = splittedLine[0] + "##" + splittedLine[1] + "##" + splittedLine[2];

			maxSubjsObj = Integer.parseInt(splittedLine[4]);
			avgSubjsObj = Integer.parseInt(splittedLine[5]);
			maxSubjObjs = Integer.parseInt(splittedLine[7]);
			avgSubjObjs = Integer.parseInt(splittedLine[8]);

			// prediction
			maxSubjsObjPredicted = maxSubjsObj > 2 * avgSubjsObj ? 2 * avgSubjsObj : maxSubjsObj;
			maxSubjObjsPredicted = maxSubjObjs > 2 * avgSubjObjs ? 2 * avgSubjObjs : maxSubjObjs;

			pw.println(akp + "##" + maxSubjsObjPredicted + "##" + maxSubjObjsPredicted);
			// pw.flush();
		}

		patternCardinalities.close();
		pw.close();
	}
}
