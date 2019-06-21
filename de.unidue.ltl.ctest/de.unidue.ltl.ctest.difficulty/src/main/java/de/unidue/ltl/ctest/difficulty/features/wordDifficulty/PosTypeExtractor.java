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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.cas.Type;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.unidue.ltl.ctest.difficulty.annotations.CTest;
import de.unidue.ltl.ctest.difficulty.annotations.Cloze;
import de.unidue.ltl.ctest.difficulty.annotations.XTest;

/**
 *  extracts the POS of the gap solution and represents it as a one-hot list of features using coarse-grained classes
 *  (adjective, adverb, article, conjunction, noun, proper noun, preposition, pronoun or verb)
 *   
 */
@CTest
@XTest
@Cloze
public class PosTypeExtractor
    extends FeatureExtractorResource_ImplBase
    implements FeatureExtractor
{	
	
	/**
	 * List of POS Types that are checked by the extractor.
	 * Each POS Type will result in its own feature.
	 */
	public static final List<String> POS_TYPES = Collections.unmodifiableList(Arrays.asList("ADJ", "ADV", "ART", "CONJ", "NN", "NP", "PP", "PR", "V"));
	
	/**
	 * Extracts POS Type features, one for each supported POS Type.
	 * 
	 * @see #POS_TYPES
	 */
    public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
        Set<Feature> featureList = new HashSet<Feature>();
        List<POS> tags = JCasUtil.selectCovered(jcas, POS.class, target);
        String targetPos = tags.size() > 0 ? tags.get(0).getPosValue() : "";
        
        if (targetPos.equals("")) {
        	System.out.println("No POS for target: " + target);
        }
        
        for (String pos : POS_TYPES) {
            featureList.add(new Feature("GapIs" + pos, pos.equals(targetPos), FeatureType.BOOLEAN));
        }

        return featureList;
    }
}