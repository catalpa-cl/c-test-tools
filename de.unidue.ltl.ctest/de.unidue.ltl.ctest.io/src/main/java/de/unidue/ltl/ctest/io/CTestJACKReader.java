package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jsoup.Jsoup;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;

public class CTestJACKReader implements CTestReader {

	@Override
	public CTestObject read(Path path) throws IOException {
		Document document = readXMLDocument(path.toFile());
		
		List<String> tokenSolutions = extractSolutions(document);
		String[] xmlLines = extractXMLLines(document);
		
		String title = extractTitle(xmlLines);		
		String[] words = extractWords(xmlLines);
		List<CTestToken> tokens = extractTokens(words, tokenSolutions);
		
		CTestObject ctest = new CTestObject("UNKNOWN"); //TODO: No language information?
		ctest.setId(title);
		tokens.forEach(ctest::addToken);
		return ctest;
	}

	@Override
	public CTestObject read(String filePath) throws IOException {
		return this.read(Paths.get(filePath));
	}

	@Override
	public CTestObject read(File file) throws IOException {
		return this.read(file.getAbsolutePath());
	}
	
	
	private Document readXMLDocument(File xmlFile) throws IOException {
		try {
			return new SAXBuilder().build(xmlFile);
		} catch (JDOMException e) { 
			System.err.println("Could not parse XML with JDOM SAXBuilder. File: " + xmlFile.getAbsolutePath());
			throw new IOException(e); 
		}
	}

	private String[] extractWords(String[] xmlLines) {
		//TODO: Shouldn't this start 1 line later, if the doc title is in line 1? Implement sanity check?
		String xmlDoc = String.join("", Arrays.copyOfRange(xmlLines, 1, xmlLines.length)); // Skip title line	
		return Jsoup.parse(xmlDoc).text().split(" ");
	}

	private String extractTitle(String[] xmlLines) {
		String xmlTitle = xmlLines[0].length() < 2 ? xmlLines[1] : xmlLines[0]; // Take second line if header is too short.
		return Jsoup.parse(xmlTitle)
				.text()
				.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
	}

	private String[] extractXMLLines(Document document) {
		return document
				.getRootElement()
				.getChild("task")
				.getValue()
				.split("\n");
	}
	
	private List<String> extractSolutions(Document document){
		List<String> solutions = new ArrayList<>();
		
		List<Element> options = document
				.getRootElement()
				.getChild("feedback")
				.getChildren("option");
		
		int currentID = 1;
		for(Element e : options){
			
			String result = e.getAttributeValue("result");
			String val = result.split(",")[1]; //TODO: Check file for clarity.
			val = val.substring(1, val.length() - 2);
			
			//TODO: What is this supposed to mean? Have a look at file. 
			if(result.startsWith("equals('[pos=" + (currentID - 1))){
				solutions.set(currentID - 2, solutions.get(currentID - 2) + "," + val);
			} else {
				solutions.add(val);
				currentID++;
			}
		}
		
		return solutions;
	}
	
	private List<CTestToken> extractTokens(String[] words, List<String> tokenSolutions) {
		List<CTestToken> tokens = new ArrayList<>();
		
		int gapPosition = 0;
		
		//TODO: LastTokenInSentence?
		for(String word : words){
			CTestToken token = new CTestToken(word);
			
			// Add specific information for gapped tokens
			if(word.contains("[fillIn")) {
				int pos = word.indexOf("[fillIn");
				String stem = word.substring(0, pos);
				String[] solutions = tokenSolutions.get(gapPosition).split(",");				
				String text = stem + solutions[0];
				List<String> otherSolutions = Arrays.asList(solutions).subList(1, solutions.length); // All solutions except the first.
				
				token.setText(text);
				token.setGap(true);
				token.setGapIndex(pos);
				token.setPrompt(stem);
				token.setOtherSolutions(otherSolutions);
				token.setId(Integer.toString(gapPosition));
				
				gapPosition++;
			}
			tokens.add(token);
		}
		
		return tokens;
	}
}
