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

import static de.unidue.ltl.ctest.difficulty.features.readability.ReadabilityFeaturesUtil.getSize;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.uima.fit.internal.ExtendedLogger;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_ADJ;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_ADV;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_NOUN;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_VERB;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

public class PosTypesExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {
	/**
	 * This Extractor counts the occurrences of different Part-Of-Speech types in
	 * the text. It also counts the occurrences of different Part-Of-Speech types in
	 * the cover sentence of the target.
	 * @throws TextClassificationException 
	 */

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		return extractPosTypes(jcas, getLogger(), target);
	}

	public static Set<Feature> extractPosTypes(JCas jcas, ExtendedLogger logger, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		Collection<Sentence> sents = JCasUtil.select(jcas, Sentence.class);
		double nrOfSentences;

		if (sents.size() == 0) {
			nrOfSentences = 1.0;
		} else {
			nrOfSentences = sents.size();
		}

		try {
			Map<String, Integer> posTypeCounters = getPosTypeCounters(jcas);
			for (String counter : posTypeCounters.keySet()) {
				featList.add(new Feature(counter + "PerSentence", posTypeCounters.get(counter) / nrOfSentences, FeatureType.NUMERIC));
			}
		} catch (NullPointerException e) {
			logger.log(Level.INFO, "POS information not available.");
		}

		if (JCasUtil.selectCovering(jcas, Sentence.class, target).size() != 0) {
			Sentence coverSent = JCasUtil.selectCovering(jcas, Sentence.class, target).get(0);
			Map<String, Integer> posTypeCountersSentence = getPosTypeCountersForSentence(coverSent);

			for (String sentenceCounter : posTypeCountersSentence.keySet()) {
				featList.add(new Feature(sentenceCounter, posTypeCountersSentence.get(sentenceCounter), FeatureType.NUMERIC));
			}
		}
		return featList;
	}

	private static Map<String, Integer> getPosTypeCounters(JCas jcas) throws NullPointerException {
		// This function counts nouns, verbs, adverbs and adjectives in the text
		Map<String, Integer> counters = new HashMap<String, Integer>();

		counters.put("NounsInDocument", getSize(JCasUtil.select(jcas, POS_NOUN.class)));
		counters.put("VerbsInDocument", getSize(JCasUtil.select(jcas, POS_VERB.class)));
		counters.put("AdverbsInDocument", getSize(JCasUtil.select(jcas, POS_ADV.class)));
		counters.put("AdjectivesInDocument", getSize(JCasUtil.select(jcas, POS_ADJ.class)));

		return counters;
	}

	private static Map<String, Integer> getPosTypeCountersForSentence(Sentence sent) throws NullPointerException {
		// This function counts nouns, verbs, adverbs and adjectives in the cover
		// sentence
		Map<String, Integer> counters = new HashMap<String, Integer>();
		counters.put("Nouns", getSize(JCasUtil.selectCovered(POS_NOUN.class, sent)));
		counters.put("Verbs", getSize(JCasUtil.selectCovered(POS_VERB.class, sent)));
		counters.put("Adverbs", getSize(JCasUtil.selectCovered(POS_ADV.class, sent)));
		counters.put("Adjectives", getSize(JCasUtil.selectCovered(POS_ADJ.class, sent)));

		return counters;
	}
}