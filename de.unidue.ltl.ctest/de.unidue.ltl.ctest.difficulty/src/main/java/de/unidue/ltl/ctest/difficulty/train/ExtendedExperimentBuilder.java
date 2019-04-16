package de.unidue.ltl.ctest.difficulty.train;

import org.dkpro.tc.ml.base.Experiment_ImplBase;
import org.dkpro.tc.ml.experiment.ExperimentCrossValidation;
import org.dkpro.tc.ml.experiment.ExperimentLearningCurve;
import org.dkpro.tc.ml.experiment.ExperimentLearningCurveTrainTest;
import org.dkpro.tc.ml.experiment.ExperimentSaveModel;
import org.dkpro.tc.ml.experiment.ExperimentTrainTest;
import org.dkpro.tc.ml.experiment.builder.ExperimentBuilder;
import org.dkpro.tc.ml.experiment.builder.ExperimentType;

public class ExtendedExperimentBuilder extends ExperimentBuilder {

	private static final Class[] ALLOWED_EXPERIMENT_CLASSES = new Class[] {
		ExperimentTrainTest.class, 
		ExperimentCrossValidation.class, 
		ExperimentLearningCurve.class, 
		ExperimentLearningCurveTrainTest.class, 
		ExperimentSaveModel.class
	};
	
	private static final String INVALID_EXPERIMENT_TYPE_MESSAGE = String.format(
			"experiment must extend an appropriate superclass. Allowed classes: %s", 
			ALLOWED_EXPERIMENT_CLASSES.toString());

	private Experiment_ImplBase customExperiment = null;
	
	/**
	 * Sets the experiment in the builder to the given experiment.
	 * 
	 * @param experiment the experiment, must extend the appropriate 
	 * @param type the type of the experiment
	 * @param experimentName the name of the experiment
	 * @return the updated builder
	 * 
	 * @throws IllegalArgumentException, if experiment does not extend the correct experiment type.
	 */
	public ExtendedExperimentBuilder experiment(Experiment_ImplBase experiment, ExperimentType type, String experimentName) {
	    if (!extendsDefaultExperimentType(experiment, type)) {
	    	throw new IllegalArgumentException(INVALID_EXPERIMENT_TYPE_MESSAGE);
	    }
	    this.customExperiment = experiment;
		super.experiment(type, experimentName);
		return this;
	}
	
	/**
	 * Checks if the given experiment extends the appropriate class for its intended experiment type.
	 */
	private boolean extendsDefaultExperimentType(Experiment_ImplBase experiment, ExperimentType type) {
		if (experiment == null) return false;
		
		Class superClass;
		
		switch (type) {
		case TRAIN_TEST:
			superClass = ExperimentTrainTest.class;
			break;
		case CROSS_VALIDATION:
			superClass = ExperimentCrossValidation.class;
			break;
		case LEARNING_CURVE:
			superClass = ExperimentLearningCurve.class;
			break;
		case LEARNING_CURVE_FIXED_TEST_SET:
			superClass = ExperimentLearningCurveTrainTest.class;
			break;
		case SAVE_MODEL:
			superClass = ExperimentSaveModel.class;
			break;
		default:
			return false;
		}
		
		return superClass.isAssignableFrom(experiment.getClass());
	}

	@Override
	protected ExtendedExperimentBuilder configureExperiment(ExperimentType type, String experimentName) throws Exception {
		super.configureExperiment(type, experimentName);
		
		if (customExperiment != null) {
			experiment = customExperiment;
		}
		
		return this;
	}
}
