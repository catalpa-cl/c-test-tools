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
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.provider.FrequencyCountProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.difficulty.annotations.CTest;
import de.unidue.ltl.ctest.difficulty.annotations.Cloze;
import de.unidue.ltl.ctest.difficulty.annotations.XTest;
import de.unidue.ltl.ctest.difficulty.features.util.CTestFeaturesUtil;
import de.unidue.ltl.ctest.difficulty.features.util.ContextProvider;

/**
 * This extractor calculates the log probability of the trigram (target + 2 words), 
 * the log probability of the two preceeding words 
 * as well as the log probability of the 2 succeeding words.
 * Additionally the unigram probability of the target word is calculated.
 */
@CTest
@XTest
@Cloze
public class GapProbabilityExtractor
    extends FeatureExtractorResource_ImplBase
    implements FeatureExtractor
{

	public static final String FN_UNIGRAM_PROB = "UnigramLogProbability";
	public static final String FN_TRIGRAM_PROB = "TrigramLogProbability";
	public static final String FN_LEFT_BIGRAM_PROB = "LeftBigramLogProbability";
	public static final String FN_RIGHT_BIGRAM_PROB = "RightBigramLogProbability";
	
    public static final String PARAM_LANGUAGE = "languageCode";
    @ConfigurationParameter(name = PARAM_LANGUAGE, description = "The language code", mandatory = true)
    private String language;

    public final static String PARAM_FREQUENCY_PROVIDER = "FrequencyProvider";
    @ExternalResource(key = PARAM_FREQUENCY_PROVIDER)
    private FrequencyCountProvider provider;


    public Set<Feature> extract(JCas jcas, TextClassificationTarget target)
        throws TextClassificationException
    {
        Set<Feature> featList = new HashSet<Feature>();

        String word = target.getCoveredText();
        String trigram = CTestFeaturesUtil.getCoveringTrigram(target, jcas);
        String leftBigram = ContextProvider.getLeftContextString(jcas, Token.class, target, 2);
        String rightBigram = ContextProvider.getRightContextString(jcas, Token.class, target, 2);

        try { 
            Double unigramProb = CTestFeaturesUtil.getLogProbability(provider, language, word);
            Double trigramProb = CTestFeaturesUtil.getLogProbability(provider, language, trigram);
            Double leftBigramProb = CTestFeaturesUtil.getLogProbability(provider, language, leftBigram);
            Double rightBigramProb = CTestFeaturesUtil.getLogProbability(provider, language, rightBigram);


            CTestFeaturesUtil.addLogProbability(featList, FN_UNIGRAM_PROB, unigramProb);
            CTestFeaturesUtil.addLogProbability(featList, FN_TRIGRAM_PROB, trigramProb);
            CTestFeaturesUtil.addLogProbability(featList, FN_LEFT_BIGRAM_PROB, leftBigramProb);
            CTestFeaturesUtil.addLogProbability(featList, FN_RIGHT_BIGRAM_PROB, rightBigramProb);
        }
        catch (IOException e) {
            throw new TextClassificationException(e);
        }

        return featList;
    }
}