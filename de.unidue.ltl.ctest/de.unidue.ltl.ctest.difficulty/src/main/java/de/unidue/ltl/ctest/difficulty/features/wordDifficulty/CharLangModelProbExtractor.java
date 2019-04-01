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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.ngrams.util.CharacterNGramStringIterable;
import de.unidue.ltl.ctest.core.TestType;
import de.unidue.ltl.ctest.type.Gap;
import edu.berkeley.nlp.lm.NgramLanguageModel;
import edu.berkeley.nlp.lm.io.LmReaders;

/** 
 * This Feature Extractor returns the probability of the character sequence in the 
 * word provided by a character-based language model.
 * The score returned by the language model is normalized by the length of the word.
 * 
 */
public class CharLangModelProbExtractor
    extends FeatureExtractorResource_ImplBase
    implements FeatureExtractor
{
    public static final String FN_LM_PROB = "LMProb";
    public static final String FN_LM_PROB_PREFIX = "LMProbPrefix";
    public static final String FN_LM_PROB_POSTFIX = "LMProbPostfix";
    public static final String FN_LM_PROB_SOLUTION = "LMProbSolution";
    
    public static final String PARAM_LM_FILE = "characterBasedLMFile";
    @ConfigurationParameter(name = PARAM_LM_FILE, mandatory = true)
    protected static String characterBasedLMFile;

    public static final String PARAM_TESTTYPE = "testtype";
    @ConfigurationParameter(name = PARAM_TESTTYPE, mandatory = true)
    private TestType testType;

    private NgramLanguageModel<String> lm;

    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
        throws ResourceInitializationException
    {
        super.initialize(aSpecifier, aAdditionalParams);
        this.lm = LmReaders.readLmBinary(characterBasedLMFile);
        return true;
    }

    @Override
    public Set<Feature> extract(JCas jcas, TextClassificationTarget classificationTarget)
        throws TextClassificationException
    {
        Set<Feature> featList = new HashSet<Feature>();

        double lmProb = 0.0;
        double lmProbSolution = 0.0;
        double lmProbPrefix = 0.0;
        double lmProbPostfix = 0.0;
        
        List<Gap> gapsCovered = JCasUtil.selectCovered(Gap.class, classificationTarget);
        
        if (gapsCovered.size() == 0) {
        	featList = addLmScoreFeature(featList, FN_LM_PROB, 0);
        	featList = addLmScoreFeature(featList, FN_LM_PROB_PREFIX, 0);
        	featList = addLmScoreFeature(featList, FN_LM_PROB_POSTFIX, 0);
        	featList = addLmScoreFeature(featList, FN_LM_PROB_SOLUTION, 0);
        	return featList;
        }
        
        Gap gap = gapsCovered.get(0);

        // add markers to beginning and end of word
        String word = "#" + gap.getCoveredText().toLowerCase() + "$";

        List<String> wordNgrams = getNgrams(word, 3);
        lmProb = lm.scoreSentence(wordNgrams);
        
        // The lm score is biased by the length of the word, so we normalize it by the number of
        // ngrams
        featList = addLmScoreFeature(featList, FN_LM_PROB,
                lmProb / (double) Math.max(1, wordNgrams.size()));

        
        String solution = word;

        // prefix is provided
        if (testType.equals(TestType.ctest)) {

            String prefix = "#" + gap.getPrefix().toLowerCase();
            List<String> prefixNgrams = getNgrams(prefix, 3);
            lmProbPrefix = lm.scoreSentence((List<String>) prefixNgrams);

            // solution = solution -prefix
            solution = solution.substring(prefix.length(), solution.length());
            featList = addLmScoreFeature(featList, FN_LM_PROB_PREFIX,
                    lmProbPrefix / (double) Math.max(1, prefixNgrams.size()));

        }
        // postfix is provided
        else if (testType.equals(TestType.xtest)) {

            String postfix = gap.getPostfix().toLowerCase() + "$";
            List<String> postfixNgrams = getNgrams(postfix, 3);
            lmProbPostfix = this.lm.scoreSentence((List<String>) postfixNgrams);

            // solution = solution -postfix
            solution = solution.substring(0, solution.length() - postfix.length());
            featList = addLmScoreFeature(featList, FN_LM_PROB_POSTFIX,
                    lmProbPostfix / (double) Math.max(1, postfixNgrams.size()));

        }
        
        // the solution it the part that is missing due to the gap
        List<String> solutionNgrams = getNgrams(solution, 3);
        lmProbSolution = this.lm.scoreSentence((List<String>) solutionNgrams);

        featList = addLmScoreFeature(featList, FN_LM_PROB_SOLUTION,
                lmProbSolution / (double) Math.max(1, solutionNgrams.size()));
        
        return featList;
    }

    private Set<Feature> addLmScoreFeature(Set<Feature> featList, String name, double score) throws TextClassificationException
    {
        if (Double.isNaN(score)) {
        	// MISSING VALUE
            featList.add(new Feature(name, 0.0, FeatureType.NUMERIC));
        }
        else {
            featList.add(new Feature(name, score, FeatureType.NUMERIC));
        }
        return featList;
    }

    private List<String> getNgrams(String token, int size) {
    	List<String> ngrams = new ArrayList<>();
        for (String charNgram : new CharacterNGramStringIterable(token, size, size)) {
            ngrams.add(charNgram);
        }
        return ngrams;
    }
}