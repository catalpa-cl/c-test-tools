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

import java.util.Arrays;
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

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.difficulty.features.util.ReadabilityUtils;
import de.unidue.ltl.ctest.type.Gap;

/**
 * Checks whether the current gap solution occurs as another gap solution
 * somewhere in the text. The rationale is that re-occurring gaps should be
 * easier as there are two different contexts in which they can be solved. If
 * one is solved, the other is more likely to be solved too.
 */
public class OccursAsGapExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {
	public static final String FN_OCCURS_AS_GAP = "OccursAsGap";

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget classificationTarget)
			throws TextClassificationException {
		boolean occursAsGap = false;

		Token token = JCasUtil.selectCovered(Token.class, classificationTarget).get(0);
		Set<String> helpVerbs = new HashSet<String>(
				Arrays.asList(ReadabilityUtils.getHelpverbs(jcas.getDocumentLanguage())));

		// do not check function verbs and help verbs, they occur everywhere
		boolean isLexicalWord = ReadabilityUtils.isLexicalWord(token, jcas.getDocumentLanguage());
		boolean isHelpVerb = helpVerbs.contains(token.getCoveredText());

		if (isLexicalWord && !isHelpVerb) {
			String lemma = JCasUtil.selectCovered(Lemma.class, classificationTarget).get(0).getValue();

			// need to always check all gaps in order to make sure that lemma is not current
			// gap
			for (Gap gap : JCasUtil.select(jcas, Gap.class)) {
				String gapLemma = "";

				List<Lemma> lemmas = JCasUtil.selectCovered(Lemma.class, gap);
				if (lemmas.size() > 0) {
					gapLemma = lemmas.get(0).getValue();
				} else {
					System.out.println("No lemma for gap: " + gap.getPrefix());
				}

				// found same lemma and not the current gap
				if (gapLemma.equals(lemma) && (gap.getBegin() != classificationTarget.getBegin())) {
					occursAsGap = true;
				}
			}
		}

		// TODO it might be useful to differentiate between cases where the gap
		// repetition is before or after the current gap
		return new Feature(FN_OCCURS_AS_GAP, occursAsGap, FeatureType.BOOLEAN).asSet();
	}
}
