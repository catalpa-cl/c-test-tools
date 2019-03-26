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

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_ADP;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_CONJ;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_DET;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_PRON;
import de.unidue.ltl.ctest.difficulty.annotations.CTest;
import de.unidue.ltl.ctest.difficulty.annotations.Cloze;
import de.unidue.ltl.ctest.difficulty.annotations.XTest;

/**
 *  In this feature, all articles, prepositions, conjunctions and pronouns are considered
 *  to be function words following this definition:
 *  http://dictionary.cambridge.org/dictionary/british/function-word
 * 
 *  Wider definitions often also count modal verbs, auxiliary verbs and quantifiers as
 *  function words. e.g. http://en.wikipedia.org/wiki/Function_word
 */
@CTest
@XTest
@Cloze
public class IsFunctionWordExtractor
    extends FeatureExtractorResource_ImplBase
    implements FeatureExtractor
{

    public static final String FN_IS_FUNC_WORD = "IsFunctionWord";

    @Override
    public Set<Feature> extract(JCas view, TextClassificationTarget classificationTarget)
        throws TextClassificationException
    {

    	boolean isFunctionWord = false;
    	
    	List<POS> tags = JCasUtil.selectCovered(POS.class, classificationTarget);
    	if (tags.size() > 0) {
    		POS pos = tags.get(0);
            isFunctionWord = (pos instanceof POS_DET || pos instanceof POS_PRON || pos instanceof POS_ADP || pos instanceof POS_CONJ);
    	}
    	else {
    		System.out.println("No POS for gap: " + classificationTarget);
    	}
    	
    	return new Feature(FN_IS_FUNC_WORD, isFunctionWord, FeatureType.BOOLEAN).asSet();
    }
}