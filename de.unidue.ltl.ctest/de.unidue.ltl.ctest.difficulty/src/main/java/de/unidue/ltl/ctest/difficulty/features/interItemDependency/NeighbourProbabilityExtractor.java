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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.MissingValue;
import org.dkpro.tc.api.features.MissingValue.MissingValueNonNominalType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.provider.FrequencyCountProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.difficulty.features.util.CTestFeaturesUtil;
import de.unidue.ltl.ctest.difficulty.features.util.ContextProvider;

public class NeighbourProbabilityExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {
	public static final String PARAM_LANGUAGE = "languageCode";
	@ConfigurationParameter(name = PARAM_LANGUAGE, description = "The language code", mandatory = true)
	private String language;

	public final static String PARAM_FREQUENCY_PROVIDER = "FrequencyProvider";
	@ExternalResource(key = PARAM_FREQUENCY_PROVIDER)
	private FrequencyCountProvider provider;

	public static final String PROB_OF_LEFT_TRIGRAM = "LeftTrigramLogProbability";
	public static final String PROB_OF_RIGHT_TRIGRAM = "RightTrigramLogProbability";

	// Extracting the left and right trigram probability to account for dependencies
	// with preceding
	// or succeeding gaps.
	// Does it make sense to do this, when there is no left or right gap? Probably
	// yes, for
	// completeness.
	// The process would be faster, if we add this calculation to the
	// GapFrequencyExtractor
	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();

		try {
			// Get probability of left trigram
			try {
				String leftTrigram = ContextProvider.getLeftContextString(jcas, Token.class, target, 3);
				featList = CTestFeaturesUtil.addLogProbability(featList, PROB_OF_LEFT_TRIGRAM,
						provider.getLogProbability(leftTrigram));
			}
			// There is no left trigram
			catch (IndexOutOfBoundsException e) {
				featList.add(new Feature(PROB_OF_LEFT_TRIGRAM, new MissingValue(MissingValueNonNominalType.NUMERIC)));
			}
			// Get probability of right trigram
			try {
				String rightTrigram = ContextProvider.getRightContextString(jcas, Token.class, target, 3);

				featList = CTestFeaturesUtil.addLogProbability(featList, PROB_OF_RIGHT_TRIGRAM,
						provider.getLogProbability(rightTrigram));
			}
			// There is no right trigram
			catch (IndexOutOfBoundsException e) {
				featList.add(new Feature(PROB_OF_LEFT_TRIGRAM, new MissingValue(MissingValueNonNominalType.NUMERIC)));
			}
		} catch (IOException e) {
			throw new TextClassificationException(e);
		}
		
		return featList;
	}

}