package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.util.Transformation;

/**
 * A class, writing {@code CTestObject}s to file as plain text.
 * <p>
 * Each line contains one sentence, words separated by whitespace.
 * Gaps are <b>not</b> marked, making the original c-test irretrievable from the file alone.
 */
public class CTestPlainTextWriter implements CTestWriter {
	private AnalysisEngine engine;

	/**
	 * Creates a new {@code CTestPlainTextWriter}. and creating the
	 * In the process, an {@code AnalysisEngine} used for converting the c-test is generated.
	 * @throws ResourceInitializationException if the creation of the {@code AnalysisEngine} fails.
	 */
	public CTestPlainTextWriter() throws ResourceInitializationException {
		engine = AnalysisEngineFactory.createEngine(BreakIteratorSegmenter.class);
	}
	
	@Override
	public void write(CTestObject ctest, Path path) throws IOException {
		try {
			Files.write(path, getSentences(ctest));
		} catch (UIMAException e) {
			e.printStackTrace();
			throw new IOException("Could not extract sentences from given CTest.");
		}
	}	

	@Override
	public void write(CTestObject ctest, String filePath) throws IOException {
		write(ctest, Paths.get(filePath));
	}

	@Override
	public void write(CTestObject ctest, File file) throws IOException {
		write(ctest, Paths.get(file.toURI()));
	}
	
	private List<String> getSentences(CTestObject ctest) throws UIMAException {
		JCas jcas = Transformation.toJCas(ctest);
		engine.process(jcas);
		
		return JCasUtil.select(jcas, Sentence.class)
				.stream()
				.map(Sentence::getCoveredText)
				.collect(Collectors.toList());
	}
}
