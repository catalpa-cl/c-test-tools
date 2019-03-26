/*******************************************************************************
 * Copyright 2014
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
import java.util.HashSet;
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

/**
 *  We would prefer to have the wordListLocation and the feature name as parameters.
 *  Unfortunately, we cannot instantiate the same feature extractor several times with
 *  different parameters in one experiments. Therefore, we use subclasses of this extractor
 *  and change the initialization-method accordingly.
 */
public abstract class IsInWordListExtractor
    extends FeatureExtractorResource_ImplBase
    implements FeatureExtractor
{

    public final static String PARAM_LOWERCASE_LIST = "lowercaseList";
    @ConfigurationParameter(name = PARAM_LOWERCASE_LIST, mandatory = false, defaultValue="true")
    private boolean lowercaseList;
    
    public static final String FN_isInList = "isInList";
    
    private Set<String> words;
    
    protected abstract File getWordList();
    
    protected abstract String getFeatureSuffix();

    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
        throws ResourceInitializationException
    {
        super.initialize(aSpecifier, aAdditionalParams);
        
        loadWordList(getWordList());

        return true;
    }
    
    private void loadWordList(File wordList)
        throws ResourceInitializationException
    {
        words = new HashSet<String>();
        
        try {
			for (String line : FileUtils.readLines(wordList)) {
				if (lowercaseList) {
					line = line.toLowerCase();
				}
			    words.add(line.trim());
			}
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
    }

    @Override
    public Set<Feature> extract(JCas view, TextClassificationTarget classificationTarget)
        throws TextClassificationException
    {

        Token tok = JCasUtil.selectCovered(Token.class, classificationTarget).get(0);
        String lemma = tok.getLemma().getValue();
        
        if (lowercaseList) {
            lemma = lemma.toLowerCase();
        }
        
        return new Feature(FN_isInList + getFeatureSuffix(), words.contains(lemma), FeatureType.BOOLEAN).asSet();
    }
}