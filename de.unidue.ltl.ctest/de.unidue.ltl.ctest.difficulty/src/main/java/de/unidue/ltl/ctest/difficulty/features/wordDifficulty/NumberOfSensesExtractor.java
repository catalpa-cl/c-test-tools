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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
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

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.difficulty.annotations.CTest;
import de.unidue.ltl.ctest.difficulty.annotations.Cloze;
import de.unidue.ltl.ctest.difficulty.annotations.XTest;

/**
 * This Feature Extractor returns an integer which contains the number of UBY senses for the given target.
 */
@CTest
@XTest
@Cloze
public class NumberOfSensesExtractor
    extends FeatureExtractorResource_ImplBase
    implements FeatureExtractor
{

    public static final String FN_NR_OF_SENSES = "NrOfUbySenses";
    
    public static final String PARAM_SENSES_FILE = "ubySensesFile";
    @ConfigurationParameter(name = PARAM_SENSES_FILE, mandatory = true)
    private File ubySensesFile;
    
    private Map<String, Integer> senses = new HashMap<String, Integer>();
    
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
        throws ResourceInitializationException
    {
        super.initialize(aSpecifier, aAdditionalParams);

        try {
		    for (String line : FileUtils.readLines(ubySensesFile)) {
		        String[] item = line.split("\t");
		        senses.put(item[0].toLowerCase(), Integer.parseInt(item[1]));
		    }
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
                		
        return true;
    }

    @Override
    public Set<Feature> extract(JCas jcas, TextClassificationTarget classificationTarget)
        throws TextClassificationException
    {
        Token tok = JCasUtil.selectCovered(Token.class, classificationTarget).get(0);
        String lemma = tok.getLemma().getValue();
        
        int nrOfSenses = 0;
        
        if (senses.containsKey(lemma)) {
            nrOfSenses = senses.get(lemma);
        }
        else if (senses.containsKey(lemma.toLowerCase())) {
            nrOfSenses = senses.get(lemma.toLowerCase());
        }
        
        return new Feature(FN_NR_OF_SENSES, nrOfSenses, FeatureType.NUMERIC).asSet();
    }
}