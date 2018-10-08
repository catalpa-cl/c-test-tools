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

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.readability.measure.WordSyllableCounter;
import de.unidue.ltl.ctest.difficulty.annotations.CTest;
import de.unidue.ltl.ctest.difficulty.annotations.Cloze;
import de.unidue.ltl.ctest.difficulty.annotations.XTest;

/**
 * Extracts the length of the given gap solution in characters
 * as well as in number of syllables.
 *
 */
@CTest
@XTest
@Cloze
public class LengthExtractor
    extends FeatureExtractorResource_ImplBase
    implements FeatureExtractor
{
	
    public static final String FN_LENGTH = "LengthOfSolutionInCharacters";
    public static final String FN_LENGTH_SYL = "LengthOfSolutionInSyllables";

    @Override
    public Set<Feature> extract(JCas jcas, TextClassificationTarget classificationTarget)
        throws TextClassificationException
    {
        Set<Feature> featList = new HashSet<Feature>();
        
        // number of syllables is not always correct, but a good approximation
        WordSyllableCounter counter = new WordSyllableCounter(jcas.getDocumentLanguage());

        featList.add(new Feature(FN_LENGTH, classificationTarget.getCoveredText().length()));
        featList.add(new Feature(FN_LENGTH_SYL, counter.countSyllables(classificationTarget.getCoveredText())));
        
        return featList;
    }
}