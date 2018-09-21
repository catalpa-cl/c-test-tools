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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.provider.FrequencyCountProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.difficulty.features.util.CTestFeaturesUtil;
import de.unidue.ltl.ctest.difficulty.features.util.ContextProvider;
import de.unidue.ltl.ctest.difficulty.types.GapCandidate;
import de.unidue.ltl.ctest.type.Gap;

public class CandidateEvaluator extends FeatureExtractorResource_ImplBase implements FeatureExtractor {

	public final static String PARAM_FREQUENCY_PROVIDER = "FrequencyProvider";
	@ExternalResource(key = PARAM_FREQUENCY_PROVIDER)
	private FrequencyCountProvider provider;

	public static final String PARAM_LANGUAGE = "languageCode";
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = true)
	protected static String language;

	// the thresholds can be set according to the profile of the learner group and
	// the language
	public static final String PARAM_UNIGRAM_THRESHOLD = "ProbabilityThresholdForUnigrams";
	@ConfigurationParameter(name = PARAM_UNIGRAM_THRESHOLD, mandatory = false)
	protected static float unigramThreshold;

	public static final String PARAM_BIGRAM_THRESHOLD = "ProbabilityThresholdForBigramModel";
	@ConfigurationParameter(name = PARAM_BIGRAM_THRESHOLD, mandatory = false)
	protected static float bigramThreshold;

	public static final String PARAM_TRIGRAM_THRESHOLD = "ProbabilityThresholdForTrigrams";
	@ConfigurationParameter(name = PARAM_TRIGRAM_THRESHOLD, mandatory = false)
	protected static float trigramThreshold;

	public static final String NR_CANDIDATES = "NrOfCandidates";
	public static final String UNIGRAM_CANDIDATES_SIZE = "NrOfUnigramCandidates";
	public static final String BIGRAM_CANDIDATES_SIZE = "NrOfBigramCandidates";
	public static final String TRIGRAM_CANDIDATES_SIZE = "NrOfTrigramCandidates";

	public static final String UNIGRAM_SOLUTION_RANK = "UnigramSolutionRank";
	public static final String BIGRAM_SOLUTION_RANK = "BigramSolutionRank";
	public static final String TRIGRAM_SOLUTION_RANK = "TrigramSolutionRank";

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget classificationTarget)
			throws TextClassificationException {

		Set<Feature> featList = new HashSet<Feature>();
		Gap gap = JCasUtil.selectCovered(Gap.class, classificationTarget).get(0);
		Collection<GapCandidate> candidates = JCasUtil.selectCovered(GapCandidate.class, gap);

		// get micro context
		String left = ContextProvider.getLeftContextString(jcas, Token.class, classificationTarget, 1);
		String right = ContextProvider.getRightContextString(jcas, Token.class, classificationTarget, 1);

		// initialize collectors
		TreeSet<GapCandidate> unigramCandidates = new TreeSet<GapCandidate>();
		TreeSet<GapCandidate> bigramCandidates = new TreeSet<GapCandidate>();
		TreeSet<GapCandidate> trigramCandidates = new TreeSet<GapCandidate>();

		// iterate through candidates
		for (GapCandidate cand : candidates) {

			String word = cand.getCandidateWord();
			String trigram = left + " " + word + " " + right;
			String leftBigram = left + " " + word;
			String rightBigram = word + " " + right;

			double trigramProb = 0.0;
			double bigramModel = 0.0;

			try {

				double wordProb = provider.getProbability(CTestFeaturesUtil.getWeb1TFormat(word, language));

				if (wordProb > unigramThreshold) {
					// collect unigram candidates above threshold
					GapCandidate uni = new GapCandidate(jcas, cand.getBegin(), cand.getEnd());
					uni.setCandidateWord(cand.getCandidateWord());
					uni.setSuitability(wordProb);
					unigramCandidates.add(uni);

					// get bigram and trigram probabilities
					trigramProb = provider.getProbability(CTestFeaturesUtil.getWeb1TFormat(trigram, language))
							/ wordProb;

					double leftBigramProb = provider
							.getProbability(CTestFeaturesUtil.getWeb1TFormat(leftBigram, language));
					double rightBigramProb = provider
							.getProbability(CTestFeaturesUtil.getWeb1TFormat(rightBigram, language));
					bigramModel = (leftBigramProb * rightBigramProb) / wordProb;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// collect trigram candidates
			if (trigramProb > trigramThreshold) {
				// generate new candidates so that we can order them according to their
				// probability
				GapCandidate tri = new GapCandidate(jcas, cand.getBegin(), cand.getEnd());
				tri.setCandidateWord(cand.getCandidateWord());
				tri.setSuitability(trigramProb);
				trigramCandidates.add(tri);
			}
			// collect bigram candidates
			if (bigramModel > bigramThreshold) {
				GapCandidate bi = new GapCandidate(jcas, cand.getBegin(), cand.getEnd());
				bi.setCandidateWord(cand.getCandidateWord());
				bi.setSuitability(bigramModel);
				bigramCandidates.add(bi);
			}

		}

		// extract candidate space
		featList.add(new Feature(NR_CANDIDATES, candidates.size()));
		featList.add(new Feature(UNIGRAM_CANDIDATES_SIZE, unigramCandidates.size()));
		featList.add(new Feature(BIGRAM_CANDIDATES_SIZE, bigramCandidates.size()));
		featList.add(new Feature(TRIGRAM_CANDIDATES_SIZE, trigramCandidates.size()));

		// extract rank of solution in the candidate space
		int unigramSolutionRank = CTestFeaturesUtil.getSolutionRank(gap.getSolutions().toArray(), unigramCandidates);
		int bigramSolutionRank = CTestFeaturesUtil.getSolutionRank(gap.getSolutions().toArray(), bigramCandidates);
		int trigramSolutionRank = CTestFeaturesUtil.getSolutionRank(gap.getSolutions().toArray(), trigramCandidates);

		// if the solution is not in the candidate space, return the last position in
		// the candidate
		// space (at least 50)
		if (unigramSolutionRank < 0) {
			unigramSolutionRank = Math.max(candidates.size() + 1, 50);
		}

		if (bigramSolutionRank < 0) {
			bigramSolutionRank = Math.max(candidates.size() + 1, 50);
		}

		if (trigramSolutionRank < 0) {
			trigramSolutionRank = Math.max(candidates.size() + 1, 50);
		}

		featList.add(new Feature(UNIGRAM_SOLUTION_RANK, unigramSolutionRank));
		featList.add(new Feature(BIGRAM_SOLUTION_RANK, bigramSolutionRank));
		featList.add(new Feature(TRIGRAM_SOLUTION_RANK, trigramSolutionRank));

		return featList;
	}
}
