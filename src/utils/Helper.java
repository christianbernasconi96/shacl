package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/** Classe di appoggio usata per facilitare l'utilizzo dei dati del summary. */
public class Helper {

	// <subject, <property, <object, stats>>
	private Map<String, Map<String, Map<String, Stats>>> patternCardinalities;

	// <datatype, occurrencies>
	private Map<String, Integer> datatypes;

	// <concepts, occurrencies>
	private Map<String, Integer> concepts;

	private Map<String, String> prefixes;

	/** Costruttore
	 * 
	 * @param path percorso che contiene i file del summary e il file prefixes.txt 
	 * @param grouped settare a true per abilitare il caricamento in memoria e l'aggregazione di tutti i pattern 
	 */
	public Helper(String path, boolean grouped) throws FileNotFoundException {

		patternCardinalities = new HashMap<String, Map<String, Map<String, Stats>>>();
		datatypes = new HashMap<String, Integer>();
		concepts = new HashMap<String, Integer>();
		prefixes = new HashMap<String, String>();

		init(path, grouped);
	}

	private void init(String path, boolean grouped) throws FileNotFoundException {
		initPrefixes(path);
		initDatatypes(path);
		initConcepts(path);
		// evita di caricare tutto il summary
		if(grouped) {
			initPatternCardinalities(path);
		}
		// initAKP(path); // TODO: valutare se serve raccogliere anche queste info
	}



	private void initPatternCardinalities(String path) throws FileNotFoundException {
		// TODO Auto-generated method stub
		String line; 
		String[] splittedLine;
		String[] splittedCardinalities;
		String subjectURI = null;
		String propertyURI = null;
		String objectURI = null;
		String subject = null;
		String property = null;
		String object = null;
		int minSubjsObj = 0;
		int maxSubjsObj = 0;
		int avgSubjsObj = 0;
		int minSubjObjs = 0;
		int maxSubjObjs = 0;
		int avgSubjObjs = 0;

		Scanner patternCardinalities = new Scanner(new File(path + "patternCardinalities.txt"));

		while (patternCardinalities.hasNextLine()) {

			// subject##property##object## minSubjsObj-maxSubjsObj-avgSubjsObj-minSubjObjs-maxSubjObjs-avgSubjObjs
			line = patternCardinalities.nextLine(); 

			// divido in 4 sezioni la linea
			splittedLine = line.split("(##)|( )");

			// estraggo tripla <S, P, O> 
			subjectURI = splittedLine[0];
			propertyURI = splittedLine[1];
			objectURI = splittedLine[2];

			// trasformo URI in prefix:name
			subject = this.convertURI(subjectURI);
			object = this.convertURI(objectURI);
			property = this.convertURI(propertyURI);

			// divido la sezione delle cardinalita
			splittedCardinalities = splittedLine[3].split("-");
			// estraggo cardinalita
			minSubjsObj = Integer.parseInt(splittedCardinalities[0]);
			maxSubjsObj = Integer.parseInt(splittedCardinalities[1]);
			avgSubjsObj = Integer.parseInt(splittedCardinalities[2]);
			minSubjObjs = Integer.parseInt(splittedCardinalities[3]);
			maxSubjObjs = Integer.parseInt(splittedCardinalities[4]);
			avgSubjObjs = Integer.parseInt(splittedCardinalities[5]);


			this.addPatternCardinalities(subject, property, object, minSubjsObj, maxSubjsObj, avgSubjsObj, minSubjObjs, maxSubjObjs, avgSubjObjs);
		}

		patternCardinalities.close();
	}

	private void initConcepts(String path) throws FileNotFoundException {

		String line; 
		String[] splittedLine;
		String concept;
		int occurrencies = 0;

		Scanner countConcepts = new Scanner(new File(path + "count-concepts.txt"));

		while (countConcepts.hasNextLine()) {
			// concept##occurrencies
			line = countConcepts.nextLine(); 

			// divido in 2 sezioni la linea
			splittedLine = line.split("##");

			concept = this.convertURI(splittedLine[0]);
			occurrencies = Integer.parseInt(splittedLine[1]);

			this.addConcept(concept, occurrencies);
		}

		countConcepts.close();
	}

	private void initDatatypes(String path) throws FileNotFoundException {

		String line; 
		String[] splittedLine;
		String datatype;
		int occurrencies = 0;

		Scanner countDatatypes = new Scanner(new File(path + "count-datatype.txt"));

		while (countDatatypes.hasNextLine()) {
			// concept##occurrencies
			line = countDatatypes.nextLine(); 

			// divido in 2 sezioni la linea
			splittedLine = line.split("##");

			datatype = this.convertURI(splittedLine[0]);
			occurrencies = Integer.parseInt(splittedLine[1]);

			this.addDatatype(datatype, occurrencies);
		}

		countDatatypes.close();
	}

	private void initPrefixes(String path) throws FileNotFoundException {

		String line;
		String[] splittedLine;
		String url;
		String prefix;
		// TODO: prendere prefixes da un path a parte
		Scanner prefixes = new Scanner(new File(path + "prefixes.txt"));

		while(prefixes.hasNextLine()) {
			// url prefix
			line = prefixes.nextLine();

			// divido in 2 sezioni la linea
			splittedLine = line.split(" ");

			// estraggo url e prefix
			url = splittedLine[0];
			prefix = splittedLine[1];

			// mappo prefisso
			this.addPrefix(url, prefix);

		}

		prefixes.close();
	}

	private void addPatternCardinalities(String subject, String property, String object, int minSubjsObj, int maxSubjsObj, int avgSubjsObj, int minSubjObjs, int maxSubjObjs, int avgSubjObjs) {

		Map<String, Map<String, Stats>> mProp;
		Map<String, Stats> mObj;
		Stats stats = new Stats(minSubjsObj, maxSubjsObj, avgSubjsObj, minSubjObjs, maxSubjObjs, avgSubjObjs);

		// soggetto già comparso nel summary --> aggrego proprieta
		if(patternCardinalities.containsKey(subject)) {

			// recupero <proprieta, <oggetto, stats>> relativi al soggetto
			mProp = patternCardinalities.get(subject);

			// proprietà già comparsa per il soggetto --> aggrego oggetti
			if(mProp.containsKey(property)) {

				// recupero <oggetto, stats> relativi alla proprieta del soggetto
				mObj = mProp.get(property);
				// oggetto di tipo diverso per proprietà già inserita
				mObj.put(object, stats);
			}
			else {
				// proprieta nuova per il soggetto
				mProp = new HashMap<String, Map<String, Stats>>();
				mObj = new HashMap<String, Stats>();
				mObj.put(object, stats);
				mProp.put(property, mObj);
			}
		}
		else {
			// proprieta nuova per il soggetto
			mProp = new HashMap<String, Map<String, Stats>>();
			mObj = new HashMap<String, Stats>();
			mObj.put(object, stats);
			mProp.put(property, mObj);
			patternCardinalities.put(subject, mProp);
		}
	}

	private void addDatatype(String datatype, int occurrencies) {
		datatypes.put(datatype, occurrencies);
	}

	private void addConcept(String concept, int occurrencies) {
		concepts.put(concept, occurrencies);
	}

	private void addPrefix(String url, String prefix) {
		prefixes.put(url, prefix);
	}

	/** Metodo che dato un url restituisce il prefix corrispondente.
	 * 
	 * @param url url
	 * @return prefix
	 */
	public String getPrefix(String url) {
		return prefixes.get(url);
	}

	/** Metodo che dato un uri restituisce il namespace.
	 * 
	 * @param uri uri da cui estrarre il namespace
	 * @return namespace relativo all'uri
	 */
	public static String extractNamespace(String uri) {
		// CASO 1 uri = http://example.org/.../resource
		// CASO 2 uri = http://example.org/.../resource#name
		// CASO 3 uri = http://example.org/.../Wikidata:Qxxxx
		String result = "";

		String[] splitted = uri.split("#");

		// compongo stringa "prefix:name"
		if(splitted.length > 1) { // CASO 2
			result = splitted[0] + "#";
		}
		else { 

			splitted = uri.split(":");

			if(splitted.length > 2) { // CASO 3
				result = splitted[0] + ":" + splitted[1] + ":";
			}
			else { // CASO 1
				int lastIndex = uri.lastIndexOf("/");
				String name = uri.substring(lastIndex + 1);
				splitted = uri.split(name);
				result = splitted[0];
			}
		}
		return result;
	}

	/** Metodo che dato un uri restituisce lo converte sostituendo il namespace con il prefix
	 * 
	 * @param uri uri da convertire
	 * @return iri nella forma "prefix:name"
	 *  */
	public String convertURI(String uri) {		
		String namespace = extractNamespace(uri);
		String name = uri.replace(namespace, "");
		String prefix = this.getPrefix(namespace);
		return prefix + ":" + name;
	}

	/** Metodo che dice se la stringa passata è un concept.
	 * 
	 * @param str stringa nella forma "prefix:name"
	 * @return true se è un concept
	 */
	public boolean isConcept(String str) {
		return concepts.containsKey(str);	
	}
	
	/** Metodo che dice se la stringa passata è un datatype.
	 * 
	 * @param str stringa nella forma "prefix:name"
	 * @return true se è un datatype
	 */
	public boolean isDatatype(String str) {
		return datatypes.containsKey(str);	
	}
	
	/** Metodo che restituisce tutti i soggetti presenti nei pattern.
	 * 
	 * @return Set di stringhe che rappresentano i soggetti
	 */
	public Set<String> getSubjects() {
		return patternCardinalities.keySet();
	}
	
	/** Metodo che restituisce tutte le proprietà presenti nei pattern per uno specifico soggetto.
	 * 
	 * @param subject soggetto di cui si vogliono ottenere le proprietà
	 * @return Set di stringhe che rappresentano le proprietà
	 */
	public Set<String> getProperties(String subject) {
		return patternCardinalities.get(subject).keySet(); 
	}
	
	/** Metodo che restituisce tutti gli oggetti presenti nei pattern per uno specifico soggetto e per una specifica proprietà.
	 * 
	 * @param subject soggetto
	 * @param property proprieta del soggetto
	 * @return Set di stringhe che rappresentano gli oggetti
	 */
	public Set<String> getObjects(String subject, String property) {
		return patternCardinalities.get(subject).get(property).keySet();
	}

	/** Metodo che restituisce le statistiche per uno specifo pattern <subject, property, object> .
	 * 
	 * @param subject soggetto 
	 * @param property proprieta del soggetto
	 * @param object oggetto a cui è riferita la proprietà
	 * @return Set di Stats
	 */
	public Stats getStats(String subject, String property, String object) {
		return patternCardinalities.get(subject).get(property).get(object);
	}
	
	/** Metodo che data la tripla <subject, property, object> restituisce il nome del file
	 * 
	 * @param subject subject nella forma "prefix:subj"
	 * @param property property nella forma "prefix:prop"
	 * @param object object nella forma "prefix:obj"
	 * @return nome del file
	 */
	public static String generateFilename(String subject, String property, String object) {

		return subject.replace(":", "#") + "##" + property.replace(":", "#") + "##" + object.replace(":", "#") + ".ttl";
	}
}