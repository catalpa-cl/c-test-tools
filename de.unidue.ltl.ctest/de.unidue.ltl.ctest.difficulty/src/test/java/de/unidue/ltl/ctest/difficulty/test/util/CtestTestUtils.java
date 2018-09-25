package de.unidue.ltl.ctest.difficulty.test.util;

import java.util.Set;

import org.dkpro.tc.api.features.Feature;
import org.junit.Assert;

public class CtestTestUtils {

	public static void assertFeature(Set<Feature> features, String featureName, Object value) {
        Assert.assertNotNull(features);
    	boolean found = false;
    	for (Feature f : features) {
    		if (f.getName().equals(featureName)) {
    			found = true;
                Assert.assertEquals(value, f.getValue());	
            }
    	}
    	Assert.assertTrue(found);
	}
	
	public static void assertFeature(Set<Feature> features, String featureName, double value, double delta) {
        Assert.assertNotNull(features);
    	boolean found = false;
    	for (Feature f : features) {
    		if (f.getName().equals(featureName)) {
    			found = true;
                Assert.assertEquals(value, (double) f.getValue(), delta);	
            }
    	}
    	Assert.assertTrue(found);
	}
}
