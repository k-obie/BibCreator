/***
 * 
 * Kamil Obiedzinski
 * 2211219
 * 
 */
package final_project;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;


public class BibCreator {
	private final static int BIB_FILE_MAX = 10;
	private static Scanner[] scanner = new Scanner[BIB_FILE_MAX];
	private static String[] filesContent = new String[BIB_FILE_MAX];
	private static ArrayList<ArrayList<HashMap<String, String> > > collection = new ArrayList<ArrayList<HashMap<String, String> > >();
	private static boolean validFiles[] =  new boolean[BIB_FILE_MAX];
	
	
	public static void main(String[] args) {
		
		System.out.println("Welcome to BibCreator!\n");
		
		for (int i = 0; i < BIB_FILE_MAX ; i++) {
			scanner[i] = null;
			filesContent[i] = "";
			validFiles[i] = true; 
		}

		boolean failedFileOpen = false;
		
		for (int i = 0; i < scanner.length; i++) {
			
			try {
				scanner[i] = new Scanner(new FileInputStream("Latex"+ (i + 1) +".bib"));
			} catch (FileNotFoundException e) {				
				System.out.println("Could not open input file Latex" + (i + 1) + ".bib for reading.\n" + 
				"Please check if file Exists! Program will Terminate after closing opened files");
				failedFileOpen = true;
			}
			
			if (failedFileOpen) {
				break;
			}
		}
		
		
		if (failedFileOpen == true) {
			for (int i = 0; i < scanner.length; i++) {
				if(scanner[i] !=  null) {
					scanner[i].close();
				}
			}
			return; // close app
		}
			
		
		processFilesForValidation();
		
		
		// Write file summary
		int countInvalidFiles = 0;
		
		for (int i = 0; i < validFiles.length; i++) {
			if(validFiles[i] == false)
				countInvalidFiles ++;
		}
		
		if(countInvalidFiles > 0) {
			System.out.println("A total of " + countInvalidFiles + " files were invalid, and could not be processed. " +
					"All other " + (validFiles.length - countInvalidFiles) + " \"Valid\" files have been created.\n");
		} else {
			System.out.println("All " + validFiles.length + " \"Valid\" files have been created.\n");
		}
		
		
		// User review selected file 
		System.out.print("Please enter the name of the file that you need to review:");
		Scanner sca = new Scanner(System.in);
		String fileName = sca.next();
		BufferedReader bufferedReader = null;
		
		try {
			
			bufferedReader = new BufferedReader(new FileReader(fileName));
			System.out.println("Here is the contents of the successfully created Jason File:" + fileName );
			displayFileContent(bufferedReader);
			
		} catch (FileNotFoundException e) {
			
			System.out.println("Could not open input file. File does not exist; possibly it could not be created!\n");
			System.out.println("However, you will be allowed anohter chance to enter another file name.");
			System.out.print("Please enter the name of the file that you need to review:");

			fileName = sca.next();
			
			try {
				
				bufferedReader = new BufferedReader(new FileReader(fileName));
				System.out.println("Here is the contents of the successfully created Jason File:" + fileName );
				displayFileContent(bufferedReader);
				
			} catch (FileNotFoundException e1) {
				
				System.out.println("\nCould not open input file again! Either file does not exist or could not be created.");
				System.out.println("Sorry! I am unable to display your desired files! Program will exit!");
				System.exit(0);
			} catch (IOException e1) {
				
				System.out.println("error - Error occurred while displaying the file.");
				System.out.println("Program will terminate.");
				System.exit(0);
			}
			
		} catch (IOException e) {
			
			System.out.println("error - Error occurred while displaying the file.");
			System.out.println("Program will terminate.");
			System.exit(0);
			
		} finally {
			
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
				
					System.out.println("error - Error occurred while displaying the file.");
					System.out.println("Program will terminate.");
					System.exit(0);
				}
			}
		}
		sca.close();
		System.out.println("Goodbye! Hope you have enjoyed creating the needed files using BibCreator.");
	}



	private static void processFilesForValidation() {
		for (int i = 0; i < scanner.length; i++) {
			
			while(scanner[i].hasNext()) {
				filesContent[i] += scanner[i].nextLine() + "\n";
			}
			
			scanner[i].close();
		}
		
		
		for (int i = 0; i < BIB_FILE_MAX; i++) {
			
			collection.add(new ArrayList<HashMap<String, String> >());
			ArrayList<String> articles = new ArrayList<String>();
			int readIndex = 0;
			int nextFound = filesContent[i].indexOf("@ARTICLE{", readIndex);
			
			if (nextFound == -1 && articles.size() == 0) {
				System.out.println("Error:");
				System.out.println("============================\n");
				System.out.println("Problem detected with input file: Latex" + (i + 1) + ".bib");
				System.out.println("No articles found.\n");
				validFiles[i] = false;
				continue; // next file
			}
			
			while(readIndex != filesContent[i].length()) {
				
				nextFound = filesContent[i].indexOf("@ARTICLE{", readIndex + 1);
				
				if(nextFound == -1) {
					nextFound = filesContent[i].length();
				}
				
				String articleStr = filesContent[i].substring(readIndex, nextFound);
				articles.add(articleStr);
				readIndex = nextFound;
			}
			
			for (int j = 0; j < articles.size(); j++) {
				
				collection.get(i).add(new HashMap<String, String>());
				
				try {
					
					String authorStr = extractField(articles.get(j), "author");
					collection.get(i).get(j).put("author", authorStr);
					
					String journalStr = extractField(articles.get(j), "journal");
					collection.get(i).get(j).put("journal", journalStr);
					
					String titleStr = extractField(articles.get(j), "title");
					collection.get(i).get(j).put("title", titleStr);
					
					String yearStr = extractField(articles.get(j), "year");
					collection.get(i).get(j).put("year", yearStr);
					
					String volumeStr = extractField(articles.get(j), "volume");
					collection.get(i).get(j).put("volume", volumeStr);
					
					String numberStr = extractField(articles.get(j), "number");
					collection.get(i).get(j).put("number", numberStr);
					
					String pagesStr = extractField(articles.get(j), "pages");
					collection.get(i).get(j).put("pages", pagesStr);
					
					String doiStr = extractField(articles.get(j), "doi");
					collection.get(i).get(j).put("doi", doiStr);
					
					String monthStr = extractField(articles.get(j), "month");
					collection.get(i).get(j).put("month", monthStr);
				} catch (FileInvalidException e) {
					System.out.println("Error: Detected Empty Field!");
					System.out.println("============================\n");
					System.out.println("Problem detected with input file: Latex" + (i+1) + ".bib");
					System.out.println(e.getMessage());
					validFiles[i] = false;
					break; // next file
				}
				
			} // articles loop
			
		} // file loop
		
		// Format and write
		try {
		
			for (int i = 0; i < collection.size(); i++) {
					
				String fileNameIEEE = "IEEE" + (i + 1) + ".json"; 
				PrintWriter printWriterIEEE;
				String fileNameAMC = "AMC" + (i + 1) + ".json"; 
				PrintWriter printWriterAMC;
				String fileNameNF = "NJ" + (i + 1) + ".json"; 
				PrintWriter printWriterNJ;
				
				if (validFiles[i] == true) {
					
					printWriterIEEE = new PrintWriter(new FileOutputStream(fileNameIEEE));
					printWriterAMC = new PrintWriter(new FileOutputStream(fileNameAMC));
					printWriterNJ = new PrintWriter(new FileOutputStream(fileNameNF));
					 
					for (int j = 0; j < collection.get(i).size(); j++) {
					
						String bibEntryIEEEStr = genBibEntryIEEE(collection.get(i).get(j));
						String bibEntryAMCStr = genBibEntryAMC(collection.get(i).get(j));
						String bibEntryNJStr = genBibEntryNJ(collection.get(i).get(j));

						printWriterIEEE.write(bibEntryIEEEStr + "\n\n");
						printWriterAMC.write("[" + (j + 1) + "]\t" + bibEntryAMCStr + "\n\n");
						printWriterNJ.write(bibEntryNJStr + "\n\n");		
					} // articles loop
				
					printWriterIEEE.close();
					printWriterAMC.close();
					printWriterNJ.close();
				
				} else {
					
					try {
						
						Files.deleteIfExists(Paths.get(fileNameIEEE));
						Files.deleteIfExists(Paths.get(fileNameAMC));
						Files.deleteIfExists(Paths.get(fileNameNF));
						
					} catch(IOException  e) {
						
						e.printStackTrace();
					}
				}	
			}// file loop
		
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	private static String genBibEntryIEEE(HashMap<String, String> articleData) {
		
		String finalResult = new String();
		StringTokenizer st = new StringTokenizer(articleData.get("author"));
		String authorStr = new String();
		
		while (st.hasMoreTokens()) {
			
			String temp = new String();
			temp = st.nextToken();
			
			if (temp.equals("and")) {
				authorStr += ",";
			}
			else {
				authorStr += " " + temp;
			}
		}
		
		authorStr += ". ";
		finalResult += authorStr;
		String titleStr = "\"" + articleData.get("title") + "\", ";
		finalResult += titleStr;
		String journalStr = articleData.get("journal") + ", ";
		finalResult += journalStr;
		String volStr = "vol. " + articleData.get("volume") + ", ";
		finalResult += volStr;
		String numberStr = "no. " + articleData.get("number") + ", ";
		finalResult += numberStr;
		String pagesStr = "p. " + articleData.get("pages") + ", ";
		finalResult += pagesStr;
		String monthStr = articleData.get("month") + " ";
		finalResult += monthStr;
		String yearStr = articleData.get("year") + ".";
		finalResult += yearStr;
		finalResult = finalResult.strip();
		
		return finalResult;
	}
	
	private static String genBibEntryAMC(HashMap<String, String> articleData) {
		
		String finalResult = new String();
		StringTokenizer st = new StringTokenizer(articleData.get("author"));
		String authorStr = new String();
			
		while (st.hasMoreTokens()) {
			
			String temp = new String();
			temp = st.nextToken();
			
			if (temp.equals("and")) {
				authorStr += " et al";
				break;
			}
			else {
				authorStr += " " + temp;
			}
		}
		
		authorStr += ". ";
		finalResult += authorStr;
		String yearStr = articleData.get("year") + ". ";
		finalResult += yearStr;
		String titleStr = articleData.get("title") + ". ";
		finalResult += titleStr;
		String journalStr = articleData.get("journal") + ". ";
		finalResult += journalStr;
		String volStr = articleData.get("volume") + ", ";
		finalResult += volStr;
		String numberStr = articleData.get("number") + " ";
		finalResult += numberStr;
		String yearStr2 = "(" + articleData.get("year") + "), ";
		finalResult += yearStr2;
		String pagesStr =  articleData.get("pages") + ". ";
		finalResult += pagesStr;
		String doiStr = "DOI:https://doi.org/" + articleData.get("doi") + ".";
		finalResult += doiStr;
		finalResult = finalResult.strip();
		
		return finalResult;
	}
	
	private static String genBibEntryNJ(HashMap<String, String> articleData) {
		
		String finalResult = new String();
		StringTokenizer st = new StringTokenizer(articleData.get("author"));
		String authorStr = new String();
		
		while (st.hasMoreTokens()) {
			
			String temp = new String();
			temp = st.nextToken();
			
			if (temp.equals("and")) {
				authorStr += " &";
			}
			else {
				authorStr += " " + temp;
			}
		}
		
		authorStr += ". ";
		finalResult += authorStr;
		String titleStr = articleData.get("title") + ". ";
		finalResult += titleStr;
		String journalStr = articleData.get("journal") + ". ";
		finalResult += journalStr;
		String volStr = articleData.get("volume") + ", ";
		finalResult += volStr;
		String pagesStr = articleData.get("pages");
		finalResult += pagesStr;
		String yearStr = "(" + articleData.get("year") + ").";
		finalResult += yearStr;
		finalResult = finalResult.strip();
		
		return finalResult;
	}
	
	private static String extractField(String articleStr, String fieldKey) throws FileInvalidException {
		
		String fieldStrTemplate = fieldKey + "={";
		int startField = articleStr.indexOf(fieldStrTemplate);
		
		if (startField == -1) {
			throw new FileInvalidException("File is Invalid: Field \"" + fieldKey + "\" is Missing. Processing stoped at this point.\n");
		}
		
		int endField = articleStr.indexOf("}", startField);
		String fieldStr = articleStr.substring(startField + fieldStrTemplate.length(), endField);
		
		if (fieldStr.isEmpty()) {
			throw new FileInvalidException("File is Invalid: Field \"" + fieldKey + "\" is Empty. Processing stoped at this point. Other empty fields may be present as well!\n");
		}

		return fieldStr;
	}
	
	private static void displayFileContent(BufferedReader readFile) throws IOException {
		
		int fileChar = 0;
		fileChar = readFile.read();
		
		while(fileChar != -1) {
			System.out.print((char)fileChar);
			fileChar = readFile.read();
		}
		readFile.close();
	}
}
