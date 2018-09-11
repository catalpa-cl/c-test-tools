package de.unidue.ltl.ctest.io.results;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;

public class CTestCSVResultWriter implements CTestResultWriter {

	@Override
	public void write(CTestObject ctest, List<TokenTestResult> results, Path path) throws IOException {
		if (path.toFile().isDirectory())
			throw new IOException("Input path is a directory, not a file.");
		
		File outFile = path.toFile();
		if (!outFile.exists()) {
			outFile.getParentFile().mkdirs();
			outFile.createNewFile();
		}
		
		String title = ctest.getId() != null ? ctest.getId() : path.getFileName().toString();
		List<String> csvLines = new ArrayList<String>();
		List<CTestToken> gappedTokens = ctest.getTokens().stream()
				.filter(token -> token.isGap())
				.collect(Collectors.toList());
		
		if (gappedTokens.size() != results.size())
			throw new IllegalArgumentException();
		
		csvLines.add(" \"C-Text: " + title + "\"");
		csvLines.add("Wort;Anzahl;Gelöst;\"in %\"");
		
		for(int i = 0; i < results.size(); i++){
			TokenTestResult result = results.get(i);
			CTestToken token = gappedTokens.get(i);
			double percentage = result.getSolveRate() * 100;
			double round2decimals = Math.round(percentage * 100.0) / 100.0;
			
			csvLines.add(token.getText() + ";" + result.getTotal() + ";" + result.getSolved() + ";" + round2decimals);
		}

		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile()), "utf-8"))) {
			for(String line : csvLines){
				writer.write(line);
				writer.newLine();
			}
		}
	}

	@Override
	public void write(CTestObject ctest, List<TokenTestResult> results, File file) throws IOException {
		this.write(ctest, results, file.toPath());
		
	}

	@Override
	public void write(CTestObject ctest, List<TokenTestResult> results, String filePath) throws IOException {
		this.write(ctest, results, Paths.get(filePath));
		
	}
	

}
