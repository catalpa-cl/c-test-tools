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

import java.util.Collection;
import java.util.HashSet;
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

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

/**
 * Computes the average amount of unique entities per sentence (named entities
 * and noun chunks). Computes the amount of unique entities of the target's
 * cover sentence
 */
public class EntityMentionsExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {

	public static final String FN_UNIQUE_ENTITIES_PER_SENTENCE = "UniqueEntitiesPerSentence";
	public static final String FN_UNIQUE_ENTITIES_IN_COVERING_SENTENCE = "UniqueEntitiesInCoversentence";

	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		return extractEntityMentions(jcas, getLogger(), target);

	}

	public static Set<Feature> extractEntityMentions(JCas jcas, ExtendedLogger logger,
			TextClassificationTarget target) throws TextClassificationException {
		Collection<Sentence> sents = JCasUtil.select(jcas, Sentence.class);
		double nrOfSentences = 1.0;
		try {
			nrOfSentences = sents.size();
		} catch (NullPointerException e) {
			logger.log(Level.INFO, "No sentence annotation found. Assuming a single sentence.");
		}

		Set<Feature> featList = new HashSet<Feature>();

		// Document Feature
		featList.add(new Feature(FN_UNIQUE_ENTITIES_PER_SENTENCE,
				ReadabilityFeaturesUtil.getNumberOfUniqueEntities(jcas) / nrOfSentences,
				FeatureType.NUMERIC));

		if (JCasUtil.selectCovering(jcas, Sentence.class, target).size() != 0) {
			Sentence coverSent = JCasUtil.selectCovering(jcas, Sentence.class, target).get(0);

			// Target Feature
			featList.add(new Feature(FN_UNIQUE_ENTITIES_IN_COVERING_SENTENCE,
					ReadabilityFeaturesUtil.getNumberOfUniqueEntities(coverSent), 
					FeatureType.NUMERIC));
		}
		return featList;
	}
}