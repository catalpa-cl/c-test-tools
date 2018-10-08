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

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.difficulty.annotations.CTest;
import de.unidue.ltl.ctest.difficulty.annotations.Cloze;
import de.unidue.ltl.ctest.difficulty.annotations.XTest;

/**
 * Extracts the position of the gap in the text. Position is indicated as nth
 * token.
 */
@CTest
@XTest
@Cloze
public class AbsolutePositionExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {
	public static final String FN_POSITION = "PositionOfGap";

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget classificationTarget)
			throws TextClassificationException {
		int position = 0;
		for (Token token : JCasUtil.select(jcas, Token.class)) {
			position++;
			if (token.getBegin() == classificationTarget.getBegin()) {
				return new Feature(FN_POSITION, position).asSet();
			}
		}

		throw new TextClassificationException("Could not locate gap in text.");
	}
}