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
package de.unidue.ltl.ctest.difficulty.features.candidate;

import java.util.List;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.similarity.algorithms.api.SimilarityException;
import org.dkpro.similarity.algorithms.api.TermSimilarityMeasure;
import org.dkpro.similarity.algorithms.lexical.string.LongestCommonSubsequenceNormComparator;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.unidue.ltl.ctest.difficulty.types.GapCandidate;

/**
 * Check if there are candidates that have a very high string similarity to the
 * solution they are often confused e.g. "base" and "basis", "of" and "off"
 */
public class StringSimilarityCandidatesExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {
	public static final String FN_STRING_SIM_CANDIDATES = "MaxStringSimWithCandidate";

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {

		List<GapCandidate> candidates = JCasUtil.selectCovered(GapCandidate.class, target);
		// TODO choice of similarity metric will have quite an impact - not sure LCS is the best choice
		TermSimilarityMeasure comparator = new LongestCommonSubsequenceNormComparator();

		double maxScore = 0.0;
		for (GapCandidate cand : candidates) {
			
			// do not compare gap with itself
			if (!cand.getCandidateWord().equals(target.getCoveredText())) {
				try {
					double score = comparator.getSimilarity(cand.getCandidateWord(), target.getCoveredText());
					maxScore = Math.max(maxScore, score);
				} catch (SimilarityException e) {
					throw new TextClassificationException(e);
				}
			}
			
		}
		return new Feature(FN_STRING_SIM_CANDIDATES, maxScore, FeatureType.NUMERIC).asSet();
	}
}