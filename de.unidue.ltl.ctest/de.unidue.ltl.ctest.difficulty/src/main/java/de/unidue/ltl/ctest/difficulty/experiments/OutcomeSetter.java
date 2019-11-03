package de.unidue.ltl.ctest.difficulty.experiments;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.unidue.ltl.ctest.type.Gap;

/**
 * An {@code Annotator}, adding {@code TextClassificationOutcome} and {@code TextClassificationTarget}
 * Annotations to a JCas. Requires {@code Gap} Annotations as input.
 *
 */
public class OutcomeSetter extends JCasAnnotator_ImplBase {

	/**
	 * Indicates, whether Outcomes will be used for classification or regression.
	 * If set to false, Gap Error Rates will be divided into evenly spaced classes.
	 * The number of classes is determined by {@code PARAM_CLASS_COUNT}.
	 */
	public static final String PARAM_IS_REGRESSION = "outcomeMode";
    @ConfigurationParameter(name = PARAM_IS_REGRESSION, mandatory = false, defaultValue = "true")
    private boolean isRegression;
	
    /**
     * The number of class labels for classification.
     * Each class takes the same proportion of error rates.
     * Example:
     * <pre>
     * class count = 3
     * label | range
     *     1 | [0.00, 0.33]
     *     2 | (0.33, 0.66]
     *     3 | (0.66, 1.00]
     * </pre>
     */
    public static final String PARAM_CLASS_COUNT = "numberOfClasses";
    @ConfigurationParameter(name = PARAM_CLASS_COUNT, mandatory = false, defaultValue = "3")
    private int classCount;
    
    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
    	super.initialize(context);
    	if(classCount < 1) {
			throw new IllegalArgumentException("The number of categories must be a positive integer.");
		}
    }
    
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		for (Gap gap : JCasUtil.select(aJCas, Gap.class)) {
			TextClassificationTarget target = generateTarget(gap, aJCas);
			TextClassificationOutcome outcome = generateOutcome(gap, aJCas);
			target.addToIndexes();
			outcome.addToIndexes();
		}
	}
	
	protected TextClassificationTarget generateTarget(Gap gap, JCas aJCas) {
		TextClassificationTarget target = new TextClassificationTarget(aJCas, gap.getBegin(), gap.getEnd());
		target.setId(gap.getId());
		target.setSuffix(gap.getCoveredText());
		return target;
	}
	
	protected TextClassificationOutcome generateOutcome(Gap gap, JCas aJCas) {		
		TextClassificationOutcome outcome = new TextClassificationOutcome(aJCas, gap.getBegin(), gap.getEnd());
		Double errorRate = gap.getErrorRate();
		
		// assign errorRate a value between 1 and n, where n is the number of classes
		if(!isRegression) {
			Double max = (double) classCount;
			for (int i = 1; i <= classCount; i++) {
				Double val = (double) i;
				if (errorRate <= val/max) {
					errorRate = val;
					break;
				}
			}
		}
		
		outcome.setOutcome(errorRate.toString());
		return outcome;
	}
}
