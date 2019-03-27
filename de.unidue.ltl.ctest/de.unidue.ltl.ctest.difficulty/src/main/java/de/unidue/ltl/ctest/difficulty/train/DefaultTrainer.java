package de.unidue.ltl.ctest.difficulty.train;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.ml.builder.FeatureMode;
import org.dkpro.tc.ml.builder.LearningMode;
import org.dkpro.tc.ml.builder.MLBackend;
import org.dkpro.tc.ml.experiment.builder.ExperimentBuilder;
import org.dkpro.tc.ml.experiment.builder.ExperimentType;
import org.dkpro.tc.ml.model.PreTrainedModelProviderUnitMode;
import org.dkpro.tc.ml.weka.WekaAdapter;

import de.unidue.ltl.ctest.difficulty.experiments.DKProTCModel;
import de.unidue.ltl.ctest.difficulty.experiments.Experiment;
import de.unidue.ltl.ctest.difficulty.experiments.Model;
import de.unidue.ltl.ctest.difficulty.experiments.OutcomeSetter;
import de.unidue.ltl.ctest.io.CTestReader;
import de.unidue.ltl.ctest.io.dkpro.CTestCollectionReader;
import de.unidue.ltl.ctest.type.Gap;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SMOreg;

public class DefaultTrainer implements ModelTrainer {
	private static String CTEST = "CTest-Experiment-";
	private static String TRAIN_TEST = "TrainTest-";
	private static String CV = "-Fold-CrossValidation-";
	
	private ExperimentBuilder builder;
	
	/*
	 * Returns a new ExperimentBuilder with the default configuration for the given Experiment.
	 * Run before every kind of experiment.
	 */
	protected ExperimentBuilder getBuilder(Experiment experiment) throws ResourceInitializationException {
		return new ExperimentBuilder()
				.preprocessing(getPreprocessing(experiment))
				.featureSets(experiment.getFeatureSet())
				.featureMode(FeatureMode.UNIT)
				.learningMode(experiment.isRegression() ? LearningMode.REGRESSION : LearningMode.SINGLE_LABEL)
				.machineLearningBackend(getBackend(experiment));
	}
	
	@Override
	public void runTrainTest(Experiment experiment, Class<? extends CTestReader> reader, String trainPath, String testPath) throws Exception {
		getBuilder(experiment)
			.experiment(ExperimentType.TRAIN_TEST,CTEST + TRAIN_TEST + getNameAndDateString(experiment))
			.dataReaderTrain(getCollectionReader(trainPath, reader))
			.dataReaderTest(getCollectionReader(testPath, reader))
			.run();
	}
	@Override
	public void runCrossValidation(Experiment experiment, Class<? extends CTestReader> reader, String trainPath, int numFolds) throws Exception {
		getBuilder(experiment)
			.experiment(ExperimentType.CROSS_VALIDATION, CTEST + numFolds + CV + getNameAndDateString(experiment))
			.numFolds(numFolds)
			.dataReaderTrain(getCollectionReader(trainPath, reader))
			.run();
	}

	@Override
	public void saveModel(Experiment experiment, Class<? extends CTestReader> reader, String trainPath,
			String modelPath) throws Exception {
		getBuilder(experiment)
			.experiment(ExperimentType.CROSS_VALIDATION, CTEST + "Model-" + getNameAndDateString(experiment))
			.dataReaderTrain(getCollectionReader(trainPath, reader))
			.outputFolder(modelPath)
			.run();
	}

	@Override
	public Model loadModel(String modelPath) throws IOException {
		try {
			AnalysisEngine estimator = AnalysisEngineFactory.createEngine(
					PreTrainedModelProviderUnitMode.class,
					PreTrainedModelProviderUnitMode.PARAM_NAME_TARGET_ANNOTATION, Gap.class,
					PreTrainedModelProviderUnitMode.PARAM_TC_MODEL_LOCATION, modelPath);
			return new DKProTCModel(estimator);
		} catch (ResourceInitializationException e) {
			System.out.println("Could not load model from disk!");
			throw new IOException(e);
		}
	}
	
	private String getNameAndDateString(Experiment experiment) {
		return experiment.getExperimentName() + "-" + new Date().getTime();
	}
	
	private CollectionReaderDescription getCollectionReader(String collectionPath, Class<? extends CTestReader> readerClass) throws ResourceInitializationException {
		return CollectionReaderFactory.createReaderDescription(
				CTestCollectionReader.class,
				CTestCollectionReader.PARAM_CTEST_READER, readerClass.getName(),
				CTestCollectionReader.PARAM_SOURCE_LOCATION, collectionPath,
				CTestCollectionReader.PARAM_PATTERNS, "[+]*.*");
	}
	
	private String getEstimator(Experiment experiment) {
		return experiment.isRegression() ? SMOreg.class.getName() : SMO.class.getName();
	}
	
	private AnalysisEngineDescription getPreprocessing(Experiment experiment) throws ResourceInitializationException {
		List<AnalysisEngineDescription> preprocessingEngines = new ArrayList<>();
		preprocessingEngines.add(createEngineDescription(OutcomeSetter.class,
				OutcomeSetter.PARAM_IS_REGRESSION, experiment.isRegression()));
		preprocessingEngines.addAll(experiment.getPreprocessing());
		return createEngineDescription(
				preprocessingEngines.toArray(new AnalysisEngineDescription[preprocessingEngines.size()]));
		
	}
	
	private MLBackend getBackend(Experiment experiment) {
		return new MLBackend(new WekaAdapter(), getEstimator(experiment));
	}
}
