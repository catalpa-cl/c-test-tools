package de.unidue.ltl.ctest.difficulty.features.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.provider.FrequencyCountProvider;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.provider.FrequencyCountProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyUtils;

// TODO: Check corpus size.
/**
 * Provides frequencies from a file.
 */
public class NoCorpusInMemoryFrequencyCountProvider extends FrequencyCountProviderBase implements FrequencyCountProvider {

	private String language = "en";
	private int order = -1;
	private Map<String, Double> log10Probabilities;
	private double outOfVocabulary = -Double.MAX_VALUE;
	
	public NoCorpusInMemoryFrequencyCountProvider(String filePath, String language, String separator, boolean header) throws IOException {
		this.language = language;
		this.log10Probabilities = new HashMap<>();
		Files.lines(Paths.get(filePath))
			.skip(header ? 1 : 0)
			.forEach(line -> addToMapAndUpdateOrder(line, separator));
	}
	
	private void addToMapAndUpdateOrder(String line, String separator) {
		String[] parts = line.split(separator);
		
		if (parts.length < 2) {
			System.err.print(String.format("Broken line detected: \"%s\"! Ignoring line.", line));
			return;
		}
		
		String phrase = parts[0];
		double log10Probability = outOfVocabulary;
		
		try {
			log10Probability = Double.parseDouble(parts[1]);
		} catch(NullPointerException e) {
			e.printStackTrace();
			System.err.print(String.format("Cannot parse probability value: \"%s\" in line \"%s\"! Ignoring line.", parts[1], line));
			return;
		}
		
		log10Probabilities.put(phrase, log10Probability);
		
		int n = FrequencyUtils.getPhraseLength(phrase);
		if (n > order) {
			order = n;
		}
	}

	@Override
	public double getProbability(String phrase) {
		return Math.pow(10, getLog10Probability(phrase));
	}
	
	@Override
	public double getLogProbability(String phrase) {
		return Math.log(getProbability(phrase));
	}
	
	public double getLog10Probability(String phrase) {
		if (phrase == null) {
			return outOfVocabulary;
		}
		int n = FrequencyUtils.getPhraseLength(phrase); 
		if (n > order) {
			try {
				throw new Exception("Phrase length of " + n + " exceeds language model's order (" + order + ")! Phrase: " + phrase);				
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(String.format("Returning %s instead", outOfVocabulary));
				return outOfVocabulary;
			}
		}
		return log10Probabilities.getOrDefault(phrase, outOfVocabulary);
	}
	
	@Override
	public long getNrOfTokens() throws IOException {
		throw new IOException("Corpus information not available in this Provider!");
	}

	@Override
	public long getNrOfNgrams(int n) throws IOException {
		throw new IOException("Corpus information not available in this Provider!");
	}

	@Override
	public long getNrOfDistinctNgrams(int n) throws IOException {
		throw new IOException("Corpus information not available in this Provider!");
	}

	@Override
	public Iterator<String> getNgramIterator(int n) throws IOException {
		throw new IOException("Corpus information not available in this Provider!");
	}

	@Override
	public String getLanguage() throws IOException {
		return this.language;
	}

	@Override
	protected long getFrequencyFromProvider(String phrase) throws IOException {
		throw new IOException("Corpus information not available in this Provider!");
	}

}
