/*******************************************************************************
 * Copyright 2015
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.Level;
import org.dkpro.similarity.algorithms.api.SimilarityException;
import org.dkpro.similarity.algorithms.api.TermSimilarityMeasureBase;
import org.dkpro.similarity.algorithms.lexical.string.LevenshteinSecondStringComparator;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.difficulty.features.util.PhonetisaurusPronunciation;

public class SpellingDifficultyExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {
	// ADJUST THIS FEATURE; CURRENTLY WORKS ONLY FOR ENGLISH
	// CHECK IF PHONETISAURUS EXECUTION WORKS ON OTHER COMPUTER

	public static final String PHON_SCORE = "PhoneticScore";
	public static final String CMU_SIM = "StringSimilarityBasicEnglishCMU";

	Map<String, PhonetisaurusPronunciation> collectedBEProunciations = new HashMap<String, PhonetisaurusPronunciation>();
	Map<String, String> cmu = new HashMap<String, String>();
	File pronunciationsFile;
	Double cmuSimilarity;

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {

		super.initialize(aSpecifier, aAdditionalParams);

		try {
			setPronunciations();
			setCmuPronunciations();
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		return true;
	}

	private void setCmuPronunciations() throws ResourceInitializationException, IOException {
		String cmuline = "";
		File cmuDictFile;

		cmuDictFile = new File("src/main/resources/wordLists/cmudict_nostress.csv");

		BufferedReader cmuReader = new BufferedReader(new FileReader(cmuDictFile));

		while ((cmuline = cmuReader.readLine()) != null) {

			String[] cmuinput = cmuline.split("\t");
			try {
				cmu.put(cmuinput[0], cmuinput[1]);
			} catch (IndexOutOfBoundsException e) {
				getLogger().log(Level.INFO, "wrong cmu input: " + cmuline);
			}
		}

		cmuReader.close();

	}

	private void setPronunciations() throws ResourceInitializationException, IOException {

		pronunciationsFile = new File("src/main/resources/wordLists/pronunciationsEN.txt");

		String line = "";

		BufferedReader phonetisaurusReader = new BufferedReader(new FileReader(pronunciationsFile));

		while ((line = phonetisaurusReader.readLine()) != null) {

			PhonetisaurusPronunciation be = new PhonetisaurusPronunciation(line);
			collectedBEProunciations.put(be.getInputWord(), be);
		}
		phonetisaurusReader.close();
	}

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException

	{
		if (!jcas.getDocumentLanguage().equals("en")) {
			throw new TextClassificationException(
					"the SpellingDifficultyExtractor currently only works for English. Feel free to adapt it for other languages");
		}
		// default values
		cmuSimilarity = target.getCoveredText().length() * 0.5;
		double phonscore = 35.0;

		// this is the pronunciation produced by the phonetisaurus using a model trained
		// on
		// BasicEnglish only (850 words)
		PhonetisaurusPronunciation bePronunciation;

		// this is the gold pronunciation, as described by CMU
		// the phonetic symbols are encoded in arpabet for both pronunciations
		String cmuPronunciation = "";

		String[] helpverbs = { "am", "are", "is", "was", "were", "be", "been", "have", "has", "had", "can", "could",
				"may", "will", "would", "might", "give", "gave" };

		String solution = target.getCoveredText();

		if (StringUtils.isAlpha(solution)) {
			// do not lemmatize helbverbs, lemma is too different e.g. are -> is
			if (!Arrays.asList(helpverbs).contains(solution)) {
				Token tok = JCasUtil.selectCovered(Token.class, target).get(0);
				solution = tok.getLemma().getValue().toLowerCase();
			}
			cmuPronunciation = getCmuPronunciation(solution);
			try {
				bePronunciation = getBePronunciation(solution);
			} catch (Exception e) {
				throw new TextClassificationException(e);
			}
			// Levenshtein seems to be a good measure to compare the symbols, but I have not
			// checked
			// any others.
			TermSimilarityMeasureBase tm = new LevenshteinSecondStringComparator();

			try {
				if (bePronunciation != null) {
					phonscore = bePronunciation.getPhoneticScore();

					if (cmuPronunciation != null) {

						cmuSimilarity = getCmuSimilarity(cmuPronunciation, bePronunciation, tm);
					}

				}

			} catch (SimilarityException e) {
				throw new TextClassificationException(e);
			}
		}
		Set<Feature> features = new HashSet<Feature>();
		features.add(new Feature(PHON_SCORE, phonscore, FeatureType.NUMERIC));
		features.add(new Feature(CMU_SIM, cmuSimilarity, FeatureType.NUMERIC));

		return features;
	}

	private Double getCmuSimilarity(String cmuPronunciation, PhonetisaurusPronunciation bePronunciation,
			TermSimilarityMeasureBase tm) throws SimilarityException {

		cmuSimilarity = tm.getSimilarity(
				cmuPronunciation.trim().replace(" ", ""), 
				bePronunciation.getPronunciation().trim().replace(" ", ""));
		return Math.abs(cmuSimilarity);
	}

	private PhonetisaurusPronunciation getBePronunciation(String solution) throws IOException, InterruptedException {
		PhonetisaurusPronunciation bePronunciation;
		if (collectedBEProunciations.containsKey(solution)) {
			bePronunciation = collectedBEProunciations.get(solution);

		} else {
			// TODO we do not have Phonetisaurus at the moment
			bePronunciation = null;
		}
		return bePronunciation;

	}

	private String getCmuPronunciation(String solution) {
		solution.replace("-", "");
		if (cmu.containsKey(solution)) {
			return cmu.get(solution);
		} else {
			getLogger().log(Level.INFO, "No cmu pronunciation for: " + solution);
			return "";
		}
	}

	public PhonetisaurusPronunciation getPhonetisaurusPronunciation(String inputword, String modelName)
			throws IOException, InterruptedException

	{
		// this is executed as external script, there should be a better solution
		getLogger().log(Level.INFO, "Getting Phonetisaurus pronunciation for " + inputword);
		String path = "src/main/resources/phonetisaurus/";

		// the learner vocabulary is modeled using basicEnglish for training
		String lib = path + "/lib";
		Process proc = Runtime.getRuntime().exec(
				path + "phonetisaurus-g2p --model=" + path + modelName + " --input=" + inputword,
				new String[] { "LD_LIBRARY_PATH=" + lib });
		proc.waitFor();

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		String output = stdInput.readLine();
		// read the phonetisaurus putput
		if (output != null) {
			getLogger().log(Level.INFO, output);
			return new PhonetisaurusPronunciation(output);

		} else {
			getLogger().log(Level.INFO, "no output for " + inputword);
			getLogger().log(Level.INFO, stdError.readLine());
			return null;
		}

	}
}
