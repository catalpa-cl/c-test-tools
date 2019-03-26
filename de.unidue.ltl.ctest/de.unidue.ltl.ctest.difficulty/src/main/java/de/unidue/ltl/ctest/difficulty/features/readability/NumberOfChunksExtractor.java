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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;

/**
 * This FE extracts the average number of chunks per sentence. If sentence
 * annotations are missing the complete document is treated as one single
 * sentence
 */
public class NumberOfChunksExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {

	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException

	{
		Set<Feature> featList = new HashSet<Feature>();
		List<Sentence> sentences = new ArrayList<Sentence>(JCasUtil.select(jcas, Sentence.class));
		double nrOfSentences;

		if (sentences.size() == 0) {
			nrOfSentences = 1.0;
		} else {
			nrOfSentences = sentences.size();
		}

		Collection<Chunk> chunks = JCasUtil.select(jcas, Chunk.class);

		featList.add(new Feature("NumberOfChunksPerSentence", ReadabilityFeaturesUtil.getSize(chunks) / nrOfSentences, FeatureType.NUMERIC));

		return featList;

	}
}