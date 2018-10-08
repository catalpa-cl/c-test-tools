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
package de.unidue.ltl.ctest.difficulty.features.interItemDependency;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.unidue.ltl.ctest.type.Gap;

public class RelativePositionExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {
	
	public static final String FN_PREVIOUS_GAPS = "NumberOfPrecedingGaps";
	public static final String FN_PREVIOUS_GAPS_IN_COVERSENTENCE = "NumberOfPrecedingGapsInCoverSentence";
	public static final String FN_GAPS_IN_COVERSENTENCE = "NumberOfGapsInCoverSentence";

	// This feature extractor extracts the position of the gap relative to other
	// gaps
	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();

		Sentence coverSent = JCasUtil.selectCovering(jcas, Sentence.class, target).get(0);
		Gap gap = JCasUtil.selectCovered(Gap.class, target).get(0);

		List<Gap> previousGaps = getPreviousGapList(jcas, gap);
		List<Gap> gapsinCoverSent = JCasUtil.selectCovered(Gap.class, coverSent);

		// get the intersection of the two lists
		List<Gap> previousGapsInCoverSent = new ArrayList<Gap>();
		for (Gap gapInSentence : gapsinCoverSent) {
			if (previousGaps.contains(gapInSentence)) {
				previousGapsInCoverSent.add(gapInSentence);
			}
		}

		featList.add(new Feature(FN_PREVIOUS_GAPS, previousGaps.size()));
		featList.add(new Feature(FN_PREVIOUS_GAPS_IN_COVERSENTENCE, previousGapsInCoverSent.size()));
		featList.add(new Feature(FN_GAPS_IN_COVERSENTENCE, gapsinCoverSent.size()));

		return featList;
	}

	private static List<Gap> getPreviousGapList(JCas jcas, Gap gap) {
		List<Gap> gapList = new ArrayList<Gap>();

		for (Gap g : JCasUtil.select(jcas, Gap.class)) {
			if (g.getBegin() == gap.getBegin() && g.getEnd() == gap.getEnd()) {
				return gapList;
			} else {
				gapList.add(g);
			}
		}

		return null;
	}
}