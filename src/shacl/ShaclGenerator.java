package shacl;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.HashSet;
//import java.io.PrintWriter;
//import java.util.Map;
import java.util.Scanner;
//import java.util.Set;
import java.util.Set;

import utils.OutputManager;

import utils.Helper;

public class ShaclGenerator {

	// chars shortcuts
	private static final String TAB_1 = "\t";
	private static final String TAB_2 = "\t\t";
	private static final String TAB_3 = "\t\t\t";
	private static final String NL = "\n";

	public static void generateShacl(String patternsPath, String outPath) throws FileNotFoundException {

		OutputManager outputManager = new OutputManager(outPath);
		Helper helper = new Helper(patternsPath);

		String result = null;
		String line; 
		String[] splittedLine;
		String subjectURI = null;
		String propertyURI = null;
		String objectURI = null;
		String subjectNS = null;
		String objectNS = null;
		String propertyNS = null;
		String subject = null;
		String property = null;
		String object = null;
		String minSubjsObj = "1";
		String maxSubjsObj = null;
		String minSubjObjs = "1";
		String maxSubjObjs = null;

		Scanner predictedCardinalities = new Scanner(new File(patternsPath + "/predictedCardinalities.txt"));
		while (predictedCardinalities.hasNextLine()) {
			result = "";

			// subject##property##object##maxSubjsObj##maxSubjObjs
			line = predictedCardinalities.nextLine(); 

			splittedLine = line.split("##");

			subjectURI = splittedLine[0];
			propertyURI = splittedLine[1];
			objectURI = splittedLine[2];

			// adding prefixes
			subjectNS = Helper.extractNamespace(subjectURI);
			objectNS = Helper.extractNamespace(objectURI);
			propertyNS = Helper.extractNamespace(propertyURI);
			Set<String> nss = new HashSet<String>();
			// nss.add("http://www.w3.org/ns/shacl#"); // shacl namespace
			nss.add(subjectNS);
			nss.add(objectNS);
			nss.add(propertyNS);
			result += "@prefix sh: <http://www.w3.org/ns/shacl#> ." + NL;
			for(String ns : nss) {
				result += "@prefix "+ helper.getPrefix(ns) + ": <" + ns + "> ." + NL;
			}

			result += NL;

			// convert URI in prefix:name
			subject = helper.convertURI(subjectURI);
			object = helper.convertURI(objectURI);
			property = helper.convertURI(propertyURI);

			// cardinalities
			maxSubjsObj = splittedLine[3];
			maxSubjObjs = splittedLine[4];

			// node generation
			// TODO: dare nome univoco a nodo per identificare tripla?
			result += subject + " a sh:NodeShape;" + NL;
			result += TAB_1 + "sh:targetClass " + subject + " ;" + NL;

			// generate sh:Property
			result += TAB_1 + "sh:property [" + NL;
			// TODO: valutare se in futuro serve aggiungere info in name e description
			// result += TAB_2 + "sh:name \"%subj_prop_obj?%\" ;" + NL;
			// result += TAB_2 + "sh:description \"%subj_prop_obj subj-objs?%\" ;" + NL;
			result += TAB_2 + "sh:message \"Vincolo cardinalita diretta (subj-objs) violato\" ;" + NL;
			result += TAB_2 + "sh:path " + property + " ;" + NL;
			// result += TAB_2 + "sh:nodeKind sh:IRI;" + NL; // ridondante

			// sh:qualifiedValueShape = partition to validate
			if(helper.isDatatype(object)) {
				result += TAB_2 + "sh:qualifiedValueShape [ sh:datatype " + object + " ; ];" + NL;
			}
			else {
				result += TAB_2 + "sh:qualifiedValueShape [ sh:class " + object + " ; ];" + NL;
			}

			// subj-objs cardinalities
			result += TAB_2 + "sh:qualifiedMinCount " + minSubjObjs + " ;" + NL;
			result += TAB_2 + "sh:qualifiedMaxCount " + maxSubjObjs + " ;" + NL;

			// severity level (Info/Warning/Violation)
			result += TAB_2 + "sh:severity sh:Warning ;" + NL;

			// subjs-obj cardinalities
			result += TAB_2 + "sh:property [" + NL;
			result += TAB_3 + "sh:path [ sh:inversePath " + property + " ; ];" + NL;

			// TODO: valutare se in futuro serve aggiungere info in name e description
			// result += TAB_3 + "sh:name \"%subj_prop_obj_inverse?%\" ;" + NL;
			// result += TAB_3 + "sh:description \"%subj_prop_obj subjs-obj?%\" ;" + NL;
			result += TAB_3 + "sh:message \"Vincolo cardinalita inversa (subjs-obj) violato\" ;" + NL;
			result += TAB_3 + "sh:class " + subject + " ;" + NL;
			result += TAB_3 + "sh:minCount " + minSubjsObj + " ;" + NL;
			result += TAB_3 + "sh:maxCount " + maxSubjsObj + " ;" + NL;

			// severity level (Info/Warning/Violation)
			result += TAB_3 + "sh:severity sh:Warning ;" + NL;

			// closing subjs-obj cardinalities
			result += TAB_2 + "];" + NL;

			// closing property
			result += TAB_1 + "];" + NL;

			// closing node
			result += "." + NL + NL;

			// generate shacl file
			outputManager.write(Helper.generateFilename(subject, property, object), result);

		}

		predictedCardinalities.close();

	}

}