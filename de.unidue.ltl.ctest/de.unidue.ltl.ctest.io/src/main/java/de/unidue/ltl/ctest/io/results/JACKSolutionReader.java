package de.unidue.ltl.ctest.io.results;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

public class JACKSolutionReader implements CTestSolutionReader {
	
	private static final String MISSING_ANSWER = "ANSWER_MISSING";
	
	/**
	 * Reads the given JACK Solution File and returns a list of all answers given in the file. 
	 * Missing answers are marked by the {@code JackSolutionReader#MISSING_ANSWER} marker.
	 * 
	 * @throws IOException if the file does not exist or could not be processed.
	 */
	@Override
	public List<String> read(File solutionFile) throws IOException {
		if(solutionFile.isDirectory()) 
			solutionFile = findSolutionFile(solutionFile);
			
		
		// readDocument
		Document document = readXMLDocument(solutionFile);
		
		// extractAnswers
		Namespace ns2namespace = document
				.getRootElement()
				.getNamespacesIntroduced()
				.get(0);
		
		List<Element> inputs = document
				.getRootElement()
				.getChild("input")
				.getChildren("pos");
		
		List<String> answers = new ArrayList<>();		
		
		for(Element input : inputs) {	
			Element omobj = input.getChild("OMOBJ",ns2namespace);
			String answer = omobj != null ? omobj.getChild("OMSTR",ns2namespace).getValue() : MISSING_ANSWER;
			answers.add(answer);			
		}
		
		return answers;
	}
	
	/**
	 * Reads the given JACK Solution File and returns a list of all answers given in the file. 
	 * Missing answers are marked by the {@code JackSolutionReader#MISSING_ANSWER} marker.
	 * 
	 * @throws IOException if the file does not exist or could not be processed.
	 */
	@Override
	public List<String> read(Path path) throws IOException {
		return read(path.toFile());
	}
	
	/**
	 * Reads the given JACK Solution File and returns a list of all answers given in the file. 
	 * Missing answers are marked by the {@code JackSolutionReader#MISSING_ANSWER} marker.
	 * 
	 * @throws IOException if the file does not exist or could not be processed.
	 */
	@Override
	public List<String> read(String filePath) throws IOException {
		return read(new File(filePath));
	}
	
	//TODO: Move to JackUtils
	/**
	 * Reads the given {@code XML File} and returns it as {@code Document}.
	 * 
	 * @throws IOException if the file does not exist or could not be processed.
	 */
	private Document readXMLDocument(File xmlFile) throws IOException {
		try {
			return new SAXBuilder().build(xmlFile);
		} catch (JDOMException e) { 
			System.err.println("Could not parse XML with JDOM SAXBuilder. File: " + xmlFile.getAbsolutePath());
			throw new IOException(e); 
		}
	}
	
	/**
	 * Reads all JACK Solution Files under the given directory using {@code JackSolutionReader#read}. 
	 * Returns a list of all answers given in the files. 
	 * Each entry in the returned list is the result of the {@code JackSolutionReader#read} method call on that file.
	 * 
	 * @param directory the directory containing the files to read. Files must be located directly under the directory.
	 * @return  a list containing all answers. Element i corresponds to the answer list of the ith file under the directory.
	 * @throws IOException if the file does not exist or could not be processed.
	 * 
	 * @see JACKSolutionReader#read
	 */
	public List<List<String>> readAll(File directory) throws IOException {
		List<List<String>> results = new ArrayList<>();		
		
		if(directory.isFile()) {
			results.add(read(directory));
			return results;
		}
		
		File[] solutionDirectories = directory.listFiles();
		for (File solutionDirectory : solutionDirectories) {
			try {
				File solutionFile = findSolutionFile(solutionDirectory);
				results.add(read(solutionFile));
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		return results;
	}
	
	public List<List<String>> readAll(Path directory) throws IOException {
		return this.readAll(directory.toFile());
	}
	
	public List<List<String>> readAll(String directory) throws IOException {
		return this.readAll(new File(directory));
	}
	
	/**
	 * Returns a single solution file under the given input directory.
	 * 
	 * @throws IOException if no file can be found under the given input file.
	 */
	private File findSolutionFile(File directory) throws IOException {
		int[] locations = new int[] {1,2};
		for (int loc : locations) {
			File solutionFile = new File(directory + "//solutionData" + loc + ".xml");
			if (solutionFile.exists())
				return solutionFile;
		}
		
		throw new IOException("Can't find solution files under " + directory.getAbsolutePath());
	}
	
}
