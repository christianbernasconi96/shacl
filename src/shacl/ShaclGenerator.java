package shacl;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import utils.Helper;

public class ShaclGenerator {

	// chars shortcuts
	private static final String TAB_1 = "\t";
	private static final String TAB_2 = "\t\t";
	private static final String TAB_3 = "\t\t\t";
	private static final String NL = "\n";
	// attributes
	private String patternsPath;
	private String outPath;
	private Helper helper;


	/**
	 * Constructor
	 * @param patternsPath path of summary files
	 * @param outPath path to save shapes
	 * @throws FileNotFoundException
	 */
	public ShaclGenerator(String patternsPath, String outPath) throws FileNotFoundException {
		super();
		this.patternsPath = patternsPath;
		this.outPath = outPath;
		helper = new Helper(patternsPath); 
	}

	/**
	 * Generates a shacl file (.ttl) for each pattern read from predictedCardinalities.txt
	 * @throws FileNotFoundException
	 */
	public void generateShacl() throws FileNotFoundException {

		String shape = null;
		String line; 
		String[] splittedLine;
		String subject = null;
		String object = null;
		String property = null;
		String minSubjsObj = "1"; // pattern exists => 1
		String maxSubjsObj = null;
		String minSubjObjs = "1"; // pattern exists => 1
		String maxSubjObjs = null;

		Scanner predictedCardinalities = new Scanner(new File(patternsPath + "/predictedCardinalities.txt"));
		while (predictedCardinalities.hasNextLine()) {
			shape = "";

			// subject##property##object##maxSubjsObj##maxSubjObjs
			line = predictedCardinalities.nextLine(); 

			splittedLine = line.split("##");
			// extracts and converts URI in prefix:name
			subject = helper.convertURI(splittedLine[0]);
			property = helper.convertURI(splittedLine[1]);
			object = helper.convertURI(splittedLine[2]);
			// extracts cardinalities prediction
			maxSubjsObj = splittedLine[3];
			maxSubjObjs = splittedLine[4];
			
			// adding prefixes (requires passing complete uri)
			shape += generatePrefixesDeclaration(splittedLine[0], splittedLine[1], splittedLine[2]);
			
			// adding nodeShape
			shape += generateNodeShape(subject, property, object, maxSubjsObj, minSubjsObj, maxSubjObjs, minSubjObjs);

			// saving shacl file
			saveShape(outPath, subject, property, object, shape);
		}

		predictedCardinalities.close();

	}

	private String generateNodeShape(String subject, String property, String object, String maxSubjsObj, String minSubjsObj, String maxSubjObjs, String minSubjObjs) {

		String result = "";

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
		return result;
	}

	private String generatePrefixesDeclaration(String subjectURI, String propertyURI, String objectURI) {
		// adding prefixes
		String subjectNS = Helper.extractNamespace(subjectURI);
		String objectNS = Helper.extractNamespace(objectURI);
		String propertyNS = Helper.extractNamespace(propertyURI);
		Set<String> nss = new HashSet<String>();
		// nss.add("http://www.w3.org/ns/shacl#"); // shacl namespace
		nss.add(subjectNS);
		nss.add(objectNS);
		nss.add(propertyNS);
		String result = "@prefix sh: <http://www.w3.org/ns/shacl#> ." + NL;
		for(String ns : nss) {
			result += "@prefix "+ helper.getPrefix(ns) + ": <" + ns + "> ." + NL;
		}

		result += NL;

		return result;
	}

	private void saveShape(String path, String subject, String property, String object, String shape) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(path + "/" + Helper.generateFilename(subject, property, object));
		writer.print(shape);
		writer.close();
	}

}