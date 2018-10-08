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
import org.dkpro.tc.api.features.MissingValue;
import org.dkpro.tc.api.features.MissingValue.MissingValueNonNominalType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;

/**
 * This Feature Extractor returns a feature list containing the frequency distribution of the 
 * gap POS sequence (preceding, actual and following word) as a double. 
 * Please note: To avoid small numbers the source probability was multiplied by 100 so it is not a real probability.
 * Unseen POS sequences are set to a missing value because the POS-tagging might just
 * go wrong (more likely than a completely unknown pos sequence,
 * the missing value is usually replaced by the mean in weka).
 *
 */
public class PosFrequencyExtractor
    extends FeatureExtractorResource_ImplBase
    implements FeatureExtractor
{

	// TODO what kind of POS distribution is that?
    public static final String FN_POS_PROBABILITY = "posProbability";
    
    public static final String PARAM_POS_DISTRIBUTION = "PosDistributionFile";
    @ConfigurationParameter(name = PARAM_POS_DISTRIBUTION, mandatory = true)
    protected static File posDistributionFile;

    private FrequencyDistribution<String> posDistribution;
    private double numberOfEntries;

    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
        throws ResourceInitializationException
    {

        super.initialize(aSpecifier, aAdditionalParams);
        posDistribution = new FrequencyDistribution<String>();

        try {
            posDistribution.load(posDistributionFile);
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
        catch (ClassNotFoundException e) {
            throw new ResourceInitializationException(e);
        }
        
        numberOfEntries = posDistribution.getN();
        return true;

    }

    @Override
    public Set<Feature> extract(JCas jcas, TextClassificationTarget classificationTarget)
        throws TextClassificationException
    {

        String left = JCasUtil.selectPreceding(jcas, POS.class, classificationTarget, 1).get(0)
                .getType().getShortName();

        String right = JCasUtil.selectFollowing(jcas, POS.class, classificationTarget, 1).get(0)
                .getType().getShortName();

        String solPos = JCasUtil.selectCovered(jcas, POS.class, classificationTarget).get(0)
                .getType().getShortName();

        String posSequence = left + " " + solPos + " " + right;
        long count = posDistribution.getCount(posSequence);

        if (count > 0) {
            double prob = Math.log(count / numberOfEntries);
            return new Feature(FN_POS_PROBABILITY, prob).asSet();
        }
        else {
            // unseen pos sequences are set to a missing value because the pos-tagging might just
            // have gone wrong (more likely than a completely unknown pos sequence
            // the missing value is usually replaced by the mean in weka
        	return new Feature(FN_POS_PROBABILITY, new MissingValue(MissingValueNonNominalType.NUMERIC)).asSet();
        }
    }
}