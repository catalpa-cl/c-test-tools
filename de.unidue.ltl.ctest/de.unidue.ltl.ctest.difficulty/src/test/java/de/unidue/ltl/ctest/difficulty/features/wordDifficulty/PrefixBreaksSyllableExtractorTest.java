package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;
//package testDifficulty.features.wordDifficultyFeatures;
//
//import static testDifficulty.util.CtestTestUtils.assertFeature;
//
//import java.util.Set;
//
//import org.apache.uima.jcas.JCas;
//import org.dkpro.tc.api.features.Feature;
//import org.dkpro.tc.api.features.util.FeatureUtil;
//import org.junit.Assert;
//import org.junit.Test;
//
//import testDifficulty.util.CTestJCasGenerator;
//
//public class PrefixBreaksSyllableExtractorTest
//{
//    @Test
//    public void testPrefixBreaksSyllableExtractor()
//        throws Exception
//    {
//       	CTestJCasGenerator generator = new CTestJCasGenerator("en");
//    	generator.addToken("In", false);
//    	generator.addToken("the", false);
//    	generator.addToken("modern", false);
//    	generator.addToken("world", false);
//    	generator.addToken("biodiversity", true, "N");
//    	generator.addToken("is", false);
//    	generator.addToken("at", false);
//    	generator.addToken("risk", false);
//    	generator.addToken(".", false);
//
//    	generator.addSentence(0, generator.getCurrentSentenceOffset());
//    	
//    	PrefixBreaksSyllableExtractor extractor = FeatureUtil.createResource(
//    			PrefixBreaksSyllableExtractor.class,
//    			PrefixBreaksSyllableExtractor.PARAM_UNIQUE_EXTRACTOR_NAME, PrefixBreaksSyllableExtractor.class.getName(),
//    			PrefixBreaksSyllableExtractor.PARAM_HYPHENATION_FILE, "src/main/resources/hyph-en-us.tex"
//    	);
//
//        JCas jcas = generator.getJCas();               
//        
//        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());
//
//        Assert.assertEquals(1, features.size());  
//        assertFeature(features, "IsSyllableBreak", true);
//    }
//}