package shacl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShaclValidator {
	/** Validates local dataset through local shapes
	 * 
	 * @param dataPath path of the dataset (.ttl / .nt format)
	 * @param schemaPath path of the shapes (.ttl / .nt format)
	 * @param reportPath path to save the validation report (.ttl format)
	 * @throws IOException
	 */
	public static void validateDataPathSchemaPath(String dataPath, String schemaPath, String reportPath) throws IOException {
		// TODO: aspettare Labra per sistemare opzioni
		String cmd = "cmd /c java -jar shaclex.jar -d " + dataPath 
				+ " -s " + schemaPath
				+ " -f " + reportPath
				+ " --engine ShaClex --showValidationReport";

		runValidator(cmd);
	}
	
	/** Validates dataset via endpoint through local shapes
	 * 
	 * @param dataEndpoint endpoint of the dataset
	 * @param schemaPath path of the shapes (.ttl / .nt format)
	 * @param reportPath path to save the validation report (.ttl format)
	 * @throws IOException
	 */
	public static void validateDataEndpointSchemaPath(String dataEndopoint, String schemaPath, String reportPath) throws IOException {
		// TODO: aspettare Labra per sistemare opzioni
		String cmd = "cmd /c java -jar shaclex.jar -e " + dataEndopoint 
				+ " -s " + schemaPath
				+ " -f " + reportPath
				+ " --engine ShaClex --showValidationReport";

		runValidator(cmd);
	}

	private static void runValidator(String cmd) throws IOException {

		//TODO: valutare se restituire output o esito del processo

		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(cmd);


		// print process output 
		String line;
		BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));  

		while ((line = input.readLine()) != null) {  
			// System.out.println(line);  
		}  

		input.close();  
		pr.destroy();


	}

	/**
	 * Test 
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
