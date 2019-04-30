package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Helper {

	// <datatype, occurrencies>
	private Map<String, Integer> datatypes;

	// <concepts, occurrencies>
	private Map<String, Integer> concepts;
	
	// <url, prefix>
	private Map<String, String> prefixes;

	public Helper(String path) throws FileNotFoundException {

		datatypes = new HashMap<String, Integer>();
		concepts = new HashMap<String, Integer>();
		prefixes = new HashMap<String, String>();

		init(path);
	}

	private void init(String path) throws FileNotFoundException {
		initPrefixes();
		initDatatypes(path);
		initConcepts(path);
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

			splittedLine = line.split("##");

			datatype = this.convertURI(splittedLine[0]);
			occurrencies = Integer.parseInt(splittedLine[1]);

			this.addDatatype(datatype, occurrencies);
		}

		countDatatypes.close();
	}

	private void initPrefixes() throws FileNotFoundException {

		String line;
		String[] splittedLine;
		String url;
		String prefix;

		Scanner prefixes = new Scanner(new File("./prefixes/prefixes.txt"));

		while(prefixes.hasNextLine()) {
			// url prefix
			line = prefixes.nextLine();
			splittedLine = line.split(" ");
			url = splittedLine[0];
			prefix = splittedLine[1];

			this.addPrefix(url, prefix);

		}

		prefixes.close();
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

	public String getPrefix(String url) {
		return prefixes.get(url);
	}

	public static String extractNamespace(String uri) {
		// CASE 1 uri = http://example.org/.../resource
		// CASE 2 uri = http://example.org/.../resource#name
		// CASE 3 uri = http://example.org/.../Wikidata:Qxxxx
		String result = "";

		String[] splitted = uri.split("#");

		// generate string "prefix:name"
		if(splitted.length > 1) { // CASO 2
			result = splitted[0] + "#";
		}
		else { 

			splitted = uri.split(":");

			if(splitted.length > 2) { // CASE 3
				result = splitted[0] + ":" + splitted[1] + ":";
			}
			else { // CASE 1
				int lastIndex = uri.lastIndexOf("/");
				String name = uri.substring(lastIndex + 1);
				splitted = uri.split(name);
				result = splitted[0];
			}
		}
		return result;
	}

	public String convertURI(String uri) {		
		String namespace = extractNamespace(uri);
		String name = uri.replace(namespace, "");
		String prefix = this.getPrefix(namespace);
		return prefix + ":" + name;
	}

	public boolean isConcept(String str) {
		return concepts.containsKey(str);	
	}
	
	public boolean isDatatype(String str) {
		return datatypes.containsKey(str);	
	}
	
	public static String generateFilename(String subject, String property, String object) {
		return subject.replace(":", "#") + "##" + property.replace(":", "#") + "##" + object.replace(":", "#") + ".ttl";
	}
}