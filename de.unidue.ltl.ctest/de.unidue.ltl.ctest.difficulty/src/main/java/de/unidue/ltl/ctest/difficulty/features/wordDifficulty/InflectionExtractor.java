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

import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_ADJ;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_NOUN;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_VERB;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.unidue.ltl.ctest.difficulty.annotations.CTest;
import de.unidue.ltl.ctest.difficulty.annotations.Cloze;
import de.unidue.ltl.ctest.difficulty.annotations.LanguageDependency;
import de.unidue.ltl.ctest.difficulty.annotations.XTest;

/**
 *  In this feature, we assume that the word is inflected if it does not equal the lemma.
 *  This is slightly simplified. From a strict linguistic view, there also exists
 *  so-called zero derivation. The distinction between derivation and inflection is also
 *  a little weak in this feature extractor.
 * 
 */
@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
        "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma",
        "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.POS" })
@CTest
@XTest
@Cloze
@LanguageDependency(ids={"en", "de", "fr"})
public class InflectionExtractor
    extends FeatureExtractorResource_ImplBase
    implements FeatureExtractor
{
    public static final String FN_IS_LEMMA = "IsLemma";
    public static final String FN_INFLECTED_ADJ = "IsInflectedAdjective";
    public static final String FN_INFLECTED_NOUN = "IsInflectedNoun";
    public static final String FN_DERIVED_ADJ = "IsDerivedAdjective";
    public static final String FN_INFLECTED_VERB = "IsInflectedVerb";

    @Override
    public Set<Feature> extract(JCas jcas, TextClassificationTarget classificationTarget)
        throws TextClassificationException
    {

        String token = classificationTarget.getCoveredText().toLowerCase();
        String solutionLemma = selectCovered(jcas, Lemma.class, classificationTarget).get(0).getValue();
        POS solutionPos = selectCovered(jcas, POS.class, classificationTarget).get(0);

        String lang = jcas.getDocumentLanguage();

        Set<Feature> featList = new HashSet<Feature>();
        boolean inflectedAdjective = false;
        boolean inflectedNoun = false;
        boolean inflectedVerb = false;
        boolean derivedAdjective = false;
        
        if (solutionPos != null) {
            if (solutionPos instanceof POS_ADJ) {
                String[] adjectiveEndings = de.unidue.ltl.ctest.difficulty.features.util.WordFilters.getAdjectiveEndings(lang);
                for (String ending : adjectiveEndings) {
                    if (token.endsWith(ending)) {
                        derivedAdjective = true;
                        break;
                    }
                }   
            }
            
            // is inflected
            if (!token.equals(solutionLemma.toLowerCase())) {
                if (solutionPos instanceof POS_ADJ) {
                    inflectedAdjective = true;
                }
                else if (solutionPos instanceof POS_NOUN) {
                    inflectedNoun = true;
                }
                else if (solutionPos instanceof POS_VERB) {
                    inflectedVerb = true;
                }
            }
        }

        featList.add(new Feature(FN_IS_LEMMA,       solutionLemma.equals(token), FeatureType.BOOLEAN));
        featList.add(new Feature(FN_INFLECTED_NOUN, inflectedNoun, FeatureType.BOOLEAN));
        featList.add(new Feature(FN_INFLECTED_ADJ,  inflectedAdjective, FeatureType.BOOLEAN));
        featList.add(new Feature(FN_INFLECTED_VERB, inflectedVerb, FeatureType.BOOLEAN));
        featList.add(new Feature(FN_DERIVED_ADJ,    derivedAdjective, FeatureType.BOOLEAN));
        
        return featList;
    }
}