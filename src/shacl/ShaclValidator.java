package shacl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/** Classe che permette di validare dati attraverso shapes */
public class ShaclValidator {
	/** Metodo che effettua la validazione di dati locali tramite shapes locali.
	 * 
	 * @param dataPath percorso file del dataset (in formato .ttl)
	 * @param schemaPath percorso delle shapes (in formato .ttl)
	 * @param reportPath percorso in cui salvare il validation report (in formato .ttl)
	 * @throws IOException
	 */
	public static void validateDataPathSchemaPath(String dataPath, String schemaPath, String reportPath) throws IOException {
		validate("DPSP", dataPath, schemaPath, reportPath);
	}
	
	/** Metodo che effettua la validazione di dati recuperati da url tramite shapes locali.
	 * 
	 * @param dataUrl url del dataset (in formato .ttl)
	 * @param schemaPath percorso delle shapes (in formato .ttl)
	 * @param reportPath percorso in cui salvare il validation report (in formato .ttl)
	 * @throws IOException
	 */
	public static void validateDataUrlSchemaPath(String dataUrl, String schemaPath, String reportPath) throws IOException {
		validate("DUSP", dataUrl, schemaPath, reportPath);
	}
	
	/** Metodo che effettua la validazione di dati locali tramite shapes recuperati da url.
	 * 
	 * @param dataPath percorso del file del dataset (in formato .ttl)
	 * @param schemaUrl url delle shapes (in formato .ttl)
	 * @param reportPath percorso in cui salvare il validation report (in formato .ttl)
	 * @throws IOException
	 */
	public static void validateDataPathSchemaUrl(String dataPath, String schemaUrl, String reportPath) throws IOException {
		validate("DPSU", dataPath, schemaUrl, reportPath);
	}
	
	/** Metodo che effettua la validazione di dati recuperati da url tramite shapes recuperati da url.
	 * 
	 * @param dataUrl url del dataset (in formato .ttl)
	 * @param schemaUrl url delle shapes (in formato .ttl)
	 * @param reportPath percorso in cui salvare il validation report (in formato .ttl)
	 * @throws IOException
	 */
	public static void validateDataUrlSchemaUrl(String dataUrl, String schemaUrl, String reportPath) throws IOException {
		validate("DUSU", dataUrl, schemaUrl, reportPath);
	}

	private static void validate(String opts, String data, String schema, String report) throws IOException {
		
		//TODO: valutare se restituire output o esito del processo
		
		String dataOpt = "";
		String schemaOpt = "";

		switch(opts) {
		case "DPSP":
			dataOpt = "-d";
			schemaOpt = "-s";
			break;
		case "DUSP":
			dataOpt = "--dataUrl";
			schemaOpt = "-s";
			break;
		case "DPSU":
			dataOpt = "-d";
			schemaOpt = "--schemaUrl";
			break;
		case "DUSU":
			dataOpt = "--dataUrl";
			schemaOpt = "--schemaUrl";
			break;
		}
		
		// TODO: sostituire path locale
		// cmd = "cmd /c cd ./bin";
		// cmd += " && ";
		
		// TODO: aspettare Labra per sistemare opzioni
		String cmd = "cmd /c java -jar shaclex.jar " + dataOpt + " " + data 
				+ " " + schemaOpt + " " + schema 
				+ " -f " + report
				+ " --engine ShaClex --showValidationReport";
		
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(cmd);
		
		
		// stampo output validazione
		String line;
		BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));  

		while ((line = input.readLine()) != null) {  
			// System.out.println(line);  
		}  
		
		input.close();  
		pr.destroy();


	}
	
	/**
	 * Test validazione scorrendo tutti i file shape.ttl
	 * @param dataPath
	 * @param schemaFolderPath
	 * @param reportFolderPath
	 * @throws IOException
	 */
	public static void validateAll(String dataPath, String schemaFolderPath, String reportFolderPath) throws IOException {
		final File folder = new File(schemaFolderPath);
		
		for (final File fileEntry : folder.listFiles()) {
			
	        if (fileEntry.isFile()) { //TODO: togliere if dopo test
	            //System.out.println(fileEntry.getName());
	        	validateDataPathSchemaPath(dataPath, schemaFolderPath + fileEntry.getName(), reportFolderPath + fileEntry.getName());
	        }
	        
	    }
	}
}
