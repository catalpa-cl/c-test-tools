package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;

import java.util.List;
import java.util.Set;

import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.unidue.ltl.ctest.difficulty.annotations.CTest;
import de.unidue.ltl.ctest.type.Gap;

@TypeCapability(inputs = { "de.unidue.ltl.ctest.type.Gap" })
@CTest()
public class GapLengthExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {

	public static final String FN_GAP_LENGTH = "GapLength";
	
	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		List<Gap> coveredGaps = JCasUtil.selectCovered(jcas, Gap.class, target);
		
		if (coveredGaps.isEmpty()) {
			return new Feature(FN_GAP_LENGTH, -1, FeatureType.NUMERIC).asSet();
		}

		return new Feature(FN_GAP_LENGTH, coveredGaps.get(0).getPostfix().length(), FeatureType.NUMERIC).asSet();
	}

}
