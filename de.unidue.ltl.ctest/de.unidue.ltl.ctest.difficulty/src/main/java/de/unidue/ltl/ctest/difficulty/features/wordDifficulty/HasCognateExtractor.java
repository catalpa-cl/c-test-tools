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
import java.util.Arrays;
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

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_ADJ;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_NOUN;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_VERB;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
/**
 * Base class for cognate extractors.
 */
public abstract class HasCognateExtractor
    extends FeatureExtractorResource_ImplBase
    implements FeatureExtractor
{

    public static final String PARAM_COGNATESFILE = "CognatesFile";
    @ConfigurationParameter(name = PARAM_COGNATESFILE, mandatory = true)
    protected static File cognatesFile;

    private Set<String> cognates = new HashSet<String>();

    
    protected abstract String getFeatureName();
    
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
        throws ResourceInitializationException
    {
        super.initialize(aSpecifier, aAdditionalParams);
        
        try {
			for (String line : FileUtils.readLines(cognatesFile)) {
			    String[] word = line.split("\t");
			    cognates.add(word[0]);
			}
		} catch (IOException e) {
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
        POS pos = tok.getPos();

        boolean cognateFound = false;
 
        if (pos instanceof POS_NOUN || pos instanceof POS_VERB || pos instanceof POS_ADJ) {
            String[] helpverbs = de.unidue.ltl.ctest.difficulty.features.util.WordFilters.getHelpverbs(jcas.getDocumentLanguage());
            if (!Arrays.asList(helpverbs).contains(tok.getCoveredText())) {
                cognateFound = cognates.contains(lemma.toLowerCase());
            }
        }
        
        return new Feature(getFeatureName(), cognateFound, FeatureType.NUMERIC).asSet();        
    }
}