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

package de.unidue.ltl.ctest.difficulty.features.readability;

import static de.unidue.ltl.ctest.difficulty.features.readability.ReadabilityFeaturesUtil.numberOfWords;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.readability.measure.WordSyllableCounter;
import de.unidue.ltl.ctest.difficulty.features.util.ReadabilityUtils;

/**
 * Computes the average word and average sentence length for the document,
 * ignores punctuation Computes the length of the target's cover sentence
 */
public class AvgLengthExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {

	public static final String FN_AVG_SENTENCE_LENGTH = "AvgSentenceLength";
	public static final String FN_AVG_WORD_LENGTH_IN_CHARACTERS = "AvgWordLengthInCharacters";
	public static final String FN_AVG_WORD_LENGTH_IN_SYLLABLES = "AvgWordLengthInSyllables";
	public static final String FN_SENTENCE_LENGTH = "CoverSentenceLength";

	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		return averageLengthExtraction(jcas, target);

	}

	public static Set<Feature> averageLengthExtraction(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();

		// Document Features
		featList.add(new Feature(FN_AVG_SENTENCE_LENGTH, getAverageSentenceLength(jcas), FeatureType.NUMERIC));
		featList.add(new Feature(FN_AVG_WORD_LENGTH_IN_CHARACTERS, getAverageWordLengthInCharacters(jcas), FeatureType.NUMERIC));
		featList.add(new Feature(FN_AVG_WORD_LENGTH_IN_SYLLABLES, getAverageWordLengthInSyllables(jcas), FeatureType.NUMERIC));

		// Target Feature
		Sentence coverSent = JCasUtil.selectCovering(jcas, Sentence.class, target).get(0);
		featList.add(new Feature(FN_SENTENCE_LENGTH, ReadabilityFeaturesUtil.numberOfWords(coverSent), FeatureType.NUMERIC));
		return featList;
	}

	public static double getAverageSentenceLength(JCas jcas) {
		Collection<Sentence> sentences = JCasUtil.select(jcas, Sentence.class);
		// avoid division by 0, assume that there is at least one sentence
		int nrOfSentences = Math.max(1, JCasUtil.select(jcas, Sentence.class).size());
		int lengthSum = 0;
		for (Sentence s : sentences) {
			lengthSum += numberOfWords(s);
		}

		return lengthSum / (double) nrOfSentences;

	}

	public static double getAverageWordLengthInCharacters(JCas jcas) {
		Collection<Token> tokens = JCasUtil.select(jcas, Token.class);
		int nrOfCharacters = 0;
		int words = 0;
		for (Token t : tokens) {
			if (ReadabilityUtils.isWord(t)) {
				words++;
				nrOfCharacters += t.getCoveredText().length();
			}
		}

		return nrOfCharacters / (double) words;

	}

	public static double getAverageWordLengthInSyllables(JCas jcas) {
		Collection<Token> tokens = JCasUtil.select(jcas, Token.class);
		int nrOfSyllables = 0;
		int words = 0;
		WordSyllableCounter counter = new WordSyllableCounter(jcas.getDocumentLanguage());

		for (Token t : tokens) {
			if (ReadabilityUtils.isWord(t)) {
				words++;
				nrOfSyllables += counter.countSyllables(t.getCoveredText());
			}
		}

		return nrOfSyllables / (double) words;

	}
}