package test;

import java.io.FileNotFoundException;
import java.io.IOException;

import shacl.ShaclGenerator;
import shacl.ShaclValidator;

public class Main {
	public static void main(String[] args) {

		// Test generator
		testGenerateShacl("./pattern/", "./shapes/");
		
		// Test grouped generator
		testGenerateGroupedShacl("./pattern/", "./shapes/");
		
		// Test one shacl generator
		testGenerateOneShacl("./pattern/", "./shapes/");
				
		// Test validator by path
		testValidateDataPathSchemaPath("./data/dataset_demo.ttl", "./shapes/shape_demo.ttl", "./reports/report_demo.ttl");

		// Test validator by endpoint
		testValidateDataEndpointSchemaPath("http://dbpedia.org/sparql", "./shapes/shape_Berlin.ttl", "./reports/report_Berlin.ttl");
		
		// Test validate all
		// testValidateAll("./data/dataset_demo.ttl", "./shapes/", "./reports/");
		
		try {
			ShaclGenerator.simulatePrediction("./pattern/");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void testGenerateGroupedShacl(String inPath, String outPath) {
		System.out.println("Shacl grouped generator");
		long startTime = System.currentTimeMillis();

		try {
			ShaclGenerator.generateGroupedShacl("./pattern/", "./shapes/");

		} catch (FileNotFoundException e) {
			System.out.println("\tExecution failed.");
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		System.out.println("\tExecution time: " + (endTime - startTime) + "ms");

	}

	public static void testGenerateShacl(String inPath, String outPath) {
		System.out.println("Shacl generator");
		long startTime = System.currentTimeMillis();

		try {
			ShaclGenerator.generateShacl(inPath, outPath);

		} catch (FileNotFoundException e) {
			System.out.println("\tExecution failed.");
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		System.out.println("\tExecution time: " + (endTime - startTime) + "ms");

	}
	
	public static void testGenerateOneShacl(String inPath, String outPath) {
		System.out.println("Shacl generator (all shapes in one file)");
		long startTime = System.currentTimeMillis();

		try {
			ShaclGenerator.generateOneShacl(inPath, outPath);

		} catch (FileNotFoundException e) {
			System.out.println("\tExecution failed.");
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		System.out.println("\tExecution time: " + (endTime - startTime) + "ms");

	}
	
	public static void testValidateDataPathSchemaPath(String dataPath, String schemaPath, String reportPath) {
		System.out.println("Shacl validator by path");
		long startTime = System.currentTimeMillis();

		try {
			ShaclValidator.validateDataPathSchemaPath(dataPath, schemaPath, reportPath);
		} catch (IOException e) {
			System.out.println("\tExecution failed.");
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();		
		System.out.println("\tExecution time: " + (endTime - startTime) + "ms");
	}

	public static void testValidateDataEndpointSchemaPath(String dataEndpoint, String schemaPath, String reportPath) {
		System.out.println("Shacl validator by endpoint");	
		long startTime = System.currentTimeMillis();

		try {
			ShaclValidator.validateDataEndpointSchemaPath(dataEndpoint, schemaPath, reportPath);
		} catch (IOException e) {
			System.out.println("\tExecution failed.");
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();		
		System.out.println("\tExecution time: " + (endTime - startTime) + "ms");
	}
	
	// TODO: modificare quando ci sar� opzione endpoint
	public static void testValidateAll(String dataPath, String schemaFolderPath, String reportPath) {
		System.out.println("Shacl validator (all by path)");	
		long startTime = System.currentTimeMillis();

		try {
			ShaclValidator.validateAll(dataPath, schemaFolderPath, reportPath);
		} catch (IOException e) {
			System.out.println("\tExecution failed.");
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();		
		System.out.println("\tExecution time: " + (endTime - startTime) + "ms");
	}


}
