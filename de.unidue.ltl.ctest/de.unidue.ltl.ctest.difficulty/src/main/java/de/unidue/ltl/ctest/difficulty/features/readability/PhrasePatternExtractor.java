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
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.ADVC;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.NC;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.PC;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.VC;

public class PhrasePatternExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor

{
	/**
	 * This Extractor counts the occurrences of different chunk types in the text.
	 * (noun, verb, prepositional and adverbial chunks)
	 * @throws TextClassificationException 
	 */
	public Set<org.dkpro.tc.api.features.Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		return extractPhraseTypes(jcas, getLogger(), target);
	}

	public static Set<Feature> extractPhraseTypes(JCas jcas, ExtendedLogger logger, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		Collection<Sentence> sents = JCasUtil.select(jcas, Sentence.class);
		double nrOfSentences = sents.size();

		// counts of phrase types in the complete text
		Map<String, Integer> phraseTypeCounters = getPhraseTypeCounters(jcas);
		for (String counter : phraseTypeCounters.keySet()) {
			featList.add(new Feature(counter + "PerSentence", phraseTypeCounters.get(counter) / nrOfSentences, FeatureType.NUMERIC));
		}

		// counts of phrase types in the cover sentence of the target only
		Sentence coverSent = JCasUtil.selectCovering(jcas, Sentence.class, target).get(0);
		Map<String, Integer> phraseTypeCountersSentence = getPhraseTypeCountersForSentence(coverSent);
		for (String sentenceCounter : phraseTypeCountersSentence.keySet()) {
			featList.add(new Feature(sentenceCounter, phraseTypeCountersSentence.get(sentenceCounter), FeatureType.NUMERIC));
		}

		return featList;
	}

	private static Map<String, Integer> getPhraseTypeCounters(JCas jcas) {
		Map<String, Integer> counters = new HashMap<String, Integer>();
		Collection<Chunk> chunks = JCasUtil.select(jcas, Chunk.class);
		counters.put("Chunks", getSize(chunks));
		// can be included
		counters.put("NounChunks", getSize(JCasUtil.select(jcas, NC.class)));
		counters.put("VerbChunks", getSize(JCasUtil.select(jcas, VC.class)));
		counters.put("PrepositionalChunks", getSize(JCasUtil.select(jcas, PC.class)));
		counters.put("AdverbalChunks", getSize(JCasUtil.select(jcas, ADVC.class)));

		int nrOfSbars = 0;
		// SBARS have the same type as punctuation, namely "0"
		for (Chunk c : chunks) {
			if (c.getChunkValue().startsWith("SBAR")) {
				nrOfSbars++;
			}
		}
		counters.put("SBars", nrOfSbars);
		return counters;
	}

	public static Map<String, Integer> getPhraseTypeCountersForSentence(Sentence sent) {
		Map<String, Integer> counters = new HashMap<String, Integer>();
		Collection<Chunk> chunks = JCasUtil.selectCovered(Chunk.class, sent);

		counters.put("ChunksInSentence", getSize(chunks));
		counters.put("NounChunksInSentence", getSize(JCasUtil.selectCovered(NC.class, sent)));
		counters.put("VerbChunksInSentence", getSize(JCasUtil.selectCovered(VC.class, sent)));
		counters.put("PrepositionalChunksInSentence", getSize(JCasUtil.selectCovered(PC.class, sent)));
		counters.put("AdverbalChunksInSentence", getSize(JCasUtil.selectCovered(ADVC.class, sent)));

		int nrOfSbars = 0;
		// SBARS have the same type as punctuation, namely "0"
		for (Chunk c : chunks) {
			if (c.getChunkValue().startsWith("SBAR")) {
				nrOfSbars++;
			}
		}
		counters.put("SBarsInSentence", nrOfSbars);
		return counters;
	}
}