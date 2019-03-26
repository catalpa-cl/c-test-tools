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
package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;

import java.io.IOException;
import java.util.Set;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.provider.FrequencyCountProvider;
import de.unidue.ltl.ctest.difficulty.annotations.CTest;
import de.unidue.ltl.ctest.difficulty.annotations.Cloze;
import de.unidue.ltl.ctest.difficulty.annotations.XTest;

/**
 * Returns the unigram frequency of the gap solution.
 * 
 */
@CTest
@XTest
@Cloze
public class UnigramFrequencyFeatureExtractor
    extends FeatureExtractorResource_ImplBase
    implements FeatureExtractor
{

	public static final String PARAM_LANGUAGE = "languageCode";
    @ConfigurationParameter(name = PARAM_LANGUAGE, description = "The language code", mandatory = true)
    private String language;

    public final static String RESOURCE_FREQUENCY_PROVIDER = "FrequencyProvider";
    @ExternalResource(key = RESOURCE_FREQUENCY_PROVIDER)
    private FrequencyCountProvider provider;


	@Override
    public Set<Feature> extract(JCas jcas, TextClassificationTarget target)
        throws TextClassificationException

    {
        try {
            return new Feature("UnigramFrequency", provider.getProbability(target.getCoveredText()), FeatureType.NUMERIC).asSet();
        }
        catch (IOException e) {
            throw new TextClassificationException(e);
        }
    }
}