package de.unidue.ltl.ctest.difficulty.features.interItemDependency;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.util.FeatureUtil;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.TestFrequencyCountResource;
import de.unidue.ltl.ctest.difficulty.features.interItemDependency.NeighbourProbabilityExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class NeighbourProbabilityExtractorTest
{
    @Test
    public void testNeighbourProbabilityExtractor()
        throws Exception   
    {
    	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("This", true);
    	generator.addToken("isdfgdfgd", false);
    	generator.addToken("an", false);
    	generator.addToken("example", false);
    	generator.addSentence(0, generator.getCurrentSentenceOffset());
 

        ExternalResourceDescription web1tResource = ExternalResourceFactory.createExternalResourceDescription(
        		TestFrequencyCountResource.class);
        
    	NeighbourProbabilityExtractor extractor = FeatureUtil.createResource(
    			NeighbourProbabilityExtractor.class,
    			NeighbourProbabilityExtractor.PARAM_FREQUENCY_PROVIDER, web1tResource,
    			NeighbourProbabilityExtractor.PARAM_UNIQUE_EXTRACTOR_NAME, NeighbourProbabilityExtractor.class.getName(),
    			NeighbourProbabilityExtractor.PARAM_LANGUAGE, "en"
    	);
 
        JCas jcas = generator.getJCas();
        
        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());

        assertFeature(features, "LeftTrigramLogProbability", 10.81977, 0.00001);
        assertFeature(features, "RightTrigramLogProbability", 37.96449, 0.00001);
    }
}
