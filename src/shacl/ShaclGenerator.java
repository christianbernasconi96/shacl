package shacl;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
//import java.io.PrintWriter;
//import java.util.Map;
import java.util.Scanner;
//import java.util.Set;
import java.util.Set;

import utils.OutputManager;
import utils.Stats;
import utils.Helper;

/** Classe che permette di generare file shacl a partire dai dati di un summary */
public class ShaclGenerator {

	// shortcut caratteri per formattazione output
	private static final String TAB_1 = "\t";
	private static final String TAB_2 = "\t\t";
	private static final String TAB_3 = "\t\t\t";
	private static final String NL = "\n";

	/*** Metodo che genera un UNICO file shacl raggruppando PER OGNI subject le rispettive properties
	 * 
	 *  @param inPath percorso in cui sono contenuti i file del summary
	 *  @param outPath percorso in cui si intende salvare il file shacl
	 *   
	 * @throws FileNotFoundException */
	public static void generateGroupedShacl(String inPath, String outPath) throws FileNotFoundException { // TOGLIERE COMMENTO IN Helper.init() PER UTILIZZARE

		OutputManager outputManager = new OutputManager(outPath);
		// grouped=false per permettere il caricamento e l'aggregazione dei pattern
		Helper helper = new Helper(inPath, true);

		String result = "";

		// aggiungo prefixes
		result += "@prefix sh:<http://www.w3.org/ns/shacl#> ." + NL + NL;
		// TODO: aggiungere tutti i prefissi da prefixes.txt

		// ogni subject corrisponde a un sh:NodeShape
		for(String subject : helper.getSubjects()) {

			//System.out.println(subject);

			// Costruisco sh:NodeShape
			// result += subject + " a sh:NodeShape, rdfs:Class;" + NL;
			result += subject + " a sh:NodeShape;" + NL;
			result += TAB_1 + "sh:targetClass " + subject + " ;" + NL;


			// ogni property corrisponde a un sh:property
			for(String property : helper.getProperties(subject)) {

				//System.out.println(TAB_1 + property);

				// ogni object a cui punta la property corrisponde a una partizione sh:qualifiedValueShape
				for(String object : helper.getObjects(subject, property)) {
					//System.out.println(TAB_2 + object);


					// recupero statistiche pattern <subject, property, object>
					Stats stats = helper.getStats(subject, property, object);

					//TODO aggiungere controllo vincolo sul massimo previsto


					// Costruisco sh:Property
					result += TAB_1 + "sh:property [" + NL;
					// result += TAB_2 + "sh:name \"%subj_prop_obj?%\" ;" + NL;
					// result += TAB_2 + "sh:description \"%subj_prop_obj subj-objs?%\" ;" + NL;
					result += TAB_2 + "sh:message \"Vincolo cardinalita subj-objs violato\" ;" + NL;
					result += TAB_2 + "sh:path " + property + " ;" + NL;
					// result += TAB_2 + "sh:nodeKind sh:IRI;" + NL;

					if(helper.isDatatype(object)) {
						result += TAB_2 + "sh:qualifiedValueShape [ sh:datatype " + object + " ; ];" + NL;
					}
					else {
						result += TAB_2 + "sh:qualifiedValueShape [ sh:class " + object + " ; ];" + NL;
					}

					// cardinalita subj-objs
					result += TAB_2 + "sh:qualifiedMinCount " + stats.getMinSubjObjs() + " ;" + NL;
					result += TAB_2 + "sh:qualifiedMaxCount " + stats.getMaxSubjObjs() + " ;" + NL;

					// severity del vincolo
					result += TAB_2 + "sh:severity sh:Warning ;" + NL;

					// cardinalita inverse
					result += TAB_2 + "sh:property [" + NL;
					result += TAB_3 + "sh:path [ sh:inversePath " + property + " ; ];" + NL;
					// result += TAB_3 + "sh:name \"%subj_prop_obj_inverse?%\" ;" + NL;
					// result += TAB_3 + "sh:description \"%subj_prop_obj subjs-obj?%\" ;" + NL;
					result += TAB_3 + "sh:message \"Vincolo cardinalita subjs-obj violato\" ;" + NL;
					result += TAB_3 + "sh:class " + subject + " ;" + NL;
					result += TAB_3 + "sh:minCount " + stats.getMinSubjsObj() + " ;" + NL;
					result += TAB_3 + "sh:maxCount " + stats.getMaxSubjsObj() + " ;" + NL;

					// severity del vincolo
					result += TAB_3 + "sh:severity sh:Warning ;" + NL;

					// chiusura cardinalita inverse
					result += TAB_2 + "];" + NL;

					// chiusura proprieta
					result += TAB_1 + "];" + NL;
				}

			}

			// chiusura shape 
			result += "." + NL + NL;
		}

		outputManager.write("shapes.ttl", result);

	}

	/**
	 *  Metodo che genera un file shacl per ogni pattern <subject, property, object> presente in patternCardinalities.txt .
	 *  @param inPath percorso in cui sono contenuti i file del summary
	 *  @param outPath percorso in cui si intendono salvare i file shacl generati per ogni pattern
	 *   */
	public static void generateShacl(String inPath, String outPath) throws FileNotFoundException {

		OutputManager outputManager = new OutputManager(outPath);
		// false=true per ottimizzare evitando il caricamento in memoria e l'aggregazione dei pattern
		Helper helper = new Helper(inPath, false);

		String result = null;

		String line; 
		String[] splittedLine;
		String[] splittedCardinalities;
		String subjectURI = null;
		String propertyURI = null;
		String objectURI = null;
		String subjectNS = null;
		String objectNS = null;
		String propertyNS = null;
		String subject = null;
		String property = null;
		String object = null;
		String minSubjsObj = null;
		String maxSubjsObj = null;
		String avgSubjsObj = null;
		String minSubjObjs = null;
		String maxSubjObjs = null;
		String avgSubjObjs = null;

		Scanner patternCardinalities = new Scanner(new File(inPath + "patternCardinalities.txt"));

		while (patternCardinalities.hasNextLine()) {
			result = "";
			// System.out.println(patternCardinalities.nextLine());

			// subject##property##object## minSubjsObj-maxSubjsObj-avgSubjsObj-minSubjObjs-maxSubjObjs-avgSubjObjs
			line = patternCardinalities.nextLine(); 

			// divido in 4 sezioni la linea
			splittedLine = line.split("(##)|( )");

			// estraggo tripla <S, P, O> 
			subjectURI = splittedLine[0];
			propertyURI = splittedLine[1];
			objectURI = splittedLine[2];

			// aggiungo prefissi
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

			// trasformo URI in prefix:name
			subject = helper.convertURI(subjectURI);
			object = helper.convertURI(objectURI);
			property = helper.convertURI(propertyURI);

			// divido la sezione delle cardinalita
			splittedCardinalities = splittedLine[3].split("-");
			// estraggo cardinalita
			minSubjsObj = splittedCardinalities[0];
			maxSubjsObj = splittedCardinalities[1];
			avgSubjsObj = splittedCardinalities[2];
			minSubjObjs = splittedCardinalities[3];
			maxSubjObjs = splittedCardinalities[4];
			avgSubjObjs = splittedCardinalities[5];


			// genero nodo
			// Costruisco sh:NodeShape
			// result +="<" + subject + ">a sh:NodeShape, rdfs:Class;" + NL; // shortcut
			result += subject + " a sh:NodeShape;" + NL;
			result += TAB_1 + "sh:targetClass " + subject + " ;" + NL;

			
			//System.out.println(TAB_2 + object);

			// Costruisco sh:Property
			result += TAB_1 + "sh:property [" + NL;
			// result += TAB_2 + "sh:name \"%subj_prop_obj?%\" ;" + NL;
			// result += TAB_2 + "sh:description \"%subj_prop_obj subj-objs?%\" ;" + NL;
			result += TAB_2 + "sh:message \"Vincolo cardinalita diretta (subj-objs) violato\" ;" + NL;
			result += TAB_2 + "sh:path " + property + " ;" + NL;
			// result += TAB_2 + "sh:nodeKind sh:IRI;" + NL; // ridondante
			
			// ogni object a cui punta la property corrisponde a una partizione sh:qualifiedValueShape
			if(helper.isDatatype(object)) {
				result += TAB_2 + "sh:qualifiedValueShape [ sh:datatype " + object + " ; ];" + NL;
			}
			else {
				result += TAB_2 + "sh:qualifiedValueShape [ sh:class " + object + " ; ];" + NL;
			}
			
			 
			// cardinalita subj-objs
			result += TAB_2 + "sh:qualifiedMinCount " + minSubjObjs + " ;" + NL;
			//TODO: calcolare previsione cardinalita max e mettere quella
			result += TAB_2 + "sh:qualifiedMaxCount " + maxSubjObjs + " ;" + NL;

			// severity del vincolo, di default viene messo tutto come Warning
			result += TAB_2 + "sh:severity sh:Warning ;" + NL;

			// cardinalita inverse
			result += TAB_2 + "sh:property [" + NL;
			result += TAB_3 + "sh:path [ sh:inversePath " + property + " ; ];" + NL;
			// result += TAB_3 + "sh:name \"%subj_prop_obj_inverse?%\" ;" + NL;
			// result += TAB_3 + "sh:description \"%subj_prop_obj subjs-obj?%\" ;" + NL;
			result += TAB_3 + "sh:message \"Vincolo cardinalita inversa (subjs-obj) violato\" ;" + NL;
			result += TAB_3 + "sh:class " + subject + " ;" + NL;
			result += TAB_3 + "sh:minCount " + minSubjsObj + " ;" + NL;
			//TODO: calcolare previsione cardinalita max e mettere quella
			result += TAB_3 + "sh:maxCount " + maxSubjsObj + " ;" + NL;

			// severity del vincolo, di default viene messo tutto come Warning
			result += TAB_3 + "sh:severity sh:Warning ;" + NL;

			// chiusura cardinalita inverse
			result += TAB_2 + "];" + NL;

			// chiusura proprieta
			result += TAB_1 + "];" + NL;

			// chiusura shape 
			result += "." + NL + NL;

			// creo shacl file del pattern
			outputManager.write(Helper.generateFilename(subject, property, object), result);
			// System.out.println(result);
			// System.out.println(subject + " " + property + " " + object);
		}

		patternCardinalities.close();
	}


}