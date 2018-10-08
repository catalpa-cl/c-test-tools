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

import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.unidue.ltl.ctest.difficulty.annotations.CTest;
import de.unidue.ltl.ctest.difficulty.annotations.LanguageDependency;
import de.unidue.ltl.ctest.type.Gap;

/**
 * This feature is only relevant for English Gaps starting only with "th" are
 * very referential and can only be solved with context e.g.
 * this/that/there/these/those/then/than I did not find gaps that are very
 * referential for French and German so far.
 */
@CTest
@LanguageDependency(ids = { "en" })
public class ThGapExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {
	public static final String FN_TH_GAP = "IsThGap";

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		Gap gap = JCasUtil.selectCovered(Gap.class, target).get(0);
		boolean isThGap = gap.getPrefix().toLowerCase().equals("th");

		return new Feature(FN_TH_GAP, isThGap).asSet();
	}
}