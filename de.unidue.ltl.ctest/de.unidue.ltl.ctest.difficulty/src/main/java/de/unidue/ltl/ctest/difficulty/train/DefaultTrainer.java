package de.unidue.ltl.ctest.difficulty.train;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.lab.Lab;
import org.dkpro.lab.reporting.BatchReportBase;
import org.dkpro.lab.task.BatchTask.ExecutionPolicy;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.core.ml.TCMachineLearningAdapter;
import org.dkpro.tc.ml.ExperimentCrossValidation;
import org.dkpro.tc.ml.ExperimentSaveModel;
import org.dkpro.tc.ml.ExperimentTrainTest;
import org.dkpro.tc.ml.Experiment_ImplBase;
import org.dkpro.tc.ml.report.BatchCrossValidationReport;
import org.dkpro.tc.ml.report.BatchTrainTestReport;
import org.dkpro.tc.ml.report.ScatterplotReport;
import org.dkpro.tc.ml.uima.TcAnnotator;
import org.dkpro.tc.ml.weka.WekaClassificationAdapter;
import org.dkpro.tc.ml.weka.WekaRegressionAdapter;

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
	
	@Override
	public void runTrainTest(Experiment experiment, Class<? extends CTestReader> reader, String trainPath,
			String testPath) {
		runTrainTest(experiment, reader, trainPath, testPath, null);
	}

	@Override
	public void runTrainTest(Experiment experiment, Class<? extends CTestReader> reader, String trainPath,
			String testPath, List<Class<? extends BatchReportBase>> reports) {
		Experiment_ImplBase batch;
		try {
			batch = new ExperimentTrainTest(getNameAndDateString(experiment) + "-CTest_Experiment-TrainTest", getAdapter(experiment));
		} catch (TextClassificationException e) {
			System.out.println("Could not generate Experiment! No Analysis is run!");
			e.printStackTrace();
			return;
		}
		
		configureBatch(batch, experiment, trainPath, testPath, reader, reports);
		
		batch.addReport(BatchTrainTestReport.class);
		batch.addReport(ScatterplotReport.class);
		
		try {
			Lab.getInstance().run(batch);
		} catch (Exception e) {
			System.out.println("Could not run Experiment!");
			e.printStackTrace();
		}
	}

	@Override
	public void runCrossValidation(Experiment experiment, Class<? extends CTestReader> reader, String trainPath, int numFolds) {
		runCrossValidation(experiment, reader, trainPath, numFolds, null);
	}

	@Override
	public void runCrossValidation(Experiment experiment, Class<? extends CTestReader> reader, String trainPath,
			int numFolds, List<Class<? extends BatchReportBase>> reports) {

		Experiment_ImplBase batch;
		try {
			batch = new ExperimentCrossValidation(getNameAndDateString(experiment) + "-CTest_Experiment-CrossValidation", getAdapter(experiment), numFolds);
		} catch (TextClassificationException e) {
			System.out.println("Could not generate Experiment! No Analysis is run!");
			e.printStackTrace();
			return;
		}

		configureBatch(batch, experiment, trainPath, null, reader, reports);
		
		batch.addReport(BatchCrossValidationReport.class);
		batch.addReport(ScatterplotReport.class);

		try {
			Lab.getInstance().run(batch);
		} catch (Exception e) {
			System.out.println("Could not run Experiment!");
			e.printStackTrace();
		}
	}

	@Override
	public void saveModel(Experiment experiment, Class<? extends CTestReader> reader, String trainPath,
			String modelPath) {
		ExperimentSaveModel batch;
		try {
			batch = new ExperimentSaveModel(getNameAndDateString(experiment), getAdapter(experiment), new File(modelPath));
		} catch (TextClassificationException e) {
			System.out.println("Could not generate Experiment!");
			e.printStackTrace();
			return;
		}

		configureBatch(batch, experiment, trainPath, null, reader, null);
		
		try {
			Lab.getInstance().run(batch);
		} catch (Exception e) {
			System.out.println("Could not save model to disk!");
			e.printStackTrace();
		}
	}

	@Override
	public Model loadModel(String modelPath) throws IOException {
		try {
			AnalysisEngine estimator = AnalysisEngineFactory.createEngine(
					TcAnnotator.class,
					TcAnnotator.PARAM_NAME_UNIT_ANNOTATION, Gap.class,
					TcAnnotator.PARAM_TC_MODEL_LOCATION, modelPath);
			
			return new DKProTCModel(estimator);
		} catch (ResourceInitializationException e) {
			System.out.println("Could not load model from disk!");
			throw new IOException(e);
		}
	}
	
	private String getNameAndDateString(Experiment experiment) {
		return experiment.getExperimentName() + new Date().getTime();
	}
	
	private boolean configureBatch(Experiment_ImplBase batch, Experiment experiment, String trainPath, String testPath,
			Class<? extends CTestReader> reader, List<Class<? extends BatchReportBase>> reports) {
		try {
			Map<String, Object> readers = getReaders(trainPath, testPath, reader);
			batch.setParameterSpace(getParameterSpace(readers, experiment));
			batch.setPreprocessing(getPreprocessing(experiment));
			batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
		} catch (ResourceInitializationException e) {
			System.out.println("Could not create Preprocessing!");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.out.println("Could not create Parameterspace!");
			e.printStackTrace();
			return false;
		}
		
		if (reports != null) {
			for(Class<? extends BatchReportBase> report : reports) {
				batch.addReport(report);
			}
		}
		
		return true;
	}
	
	private Class<? extends TCMachineLearningAdapter> getAdapter(Experiment experiment) {
		return experiment.isRegression() ? WekaRegressionAdapter.class : WekaClassificationAdapter.class;
	}
	
	private Map<String, Object> getReaders(String trainPath, String testPath, Class<? extends CTestReader> readerClass) throws ResourceInitializationException {
		if (trainPath == null) { 
			throw new IllegalArgumentException("trainPath must not be null!"); 
		}
		
		Map<String, Object> readers = new HashMap<>();
		readers.put(Constants.DIM_READER_TRAIN, getCollectionReader(trainPath, readerClass));
		if (testPath != null) {
			readers.put(Constants.DIM_READER_TEST, getCollectionReader(trainPath, readerClass));
		}
		
		return readers;
	}

	private CollectionReaderDescription getCollectionReader(String collectionPath, Class<? extends CTestReader> readerClass) throws ResourceInitializationException {
		return CollectionReaderFactory.createReaderDescription(
				CTestCollectionReader.class,
				CTestCollectionReader.PARAM_CTEST_READER, readerClass.getName(),
				CTestCollectionReader.PARAM_SOURCE_LOCATION, collectionPath);
	}
	
	private ParameterSpace getParameterSpace(Map<String, Object> readersDimension, Experiment experiment) throws IOException, ResourceInitializationException {
		
		String estimator = experiment.isRegression() ? SMOreg.class.getName() : SMO.class.getName();
		String learningMode = experiment.isRegression() ?  Constants.LM_REGRESSION : Constants.LM_SINGLE_LABEL;
		
		return new ParameterSpace(
				Dimension.createBundle("readers", readersDimension),
				Dimension.create(Constants.DIM_FEATURE_SET, experiment.getFeatureSet()),
				Dimension.create(Constants.DIM_CLASSIFICATION_ARGS, Arrays.asList(estimator)), 
				Dimension.create(Constants.DIM_LEARNING_MODE, learningMode),
				Dimension.create(Constants.DIM_FEATURE_MODE, Constants.FM_UNIT));
	}
	
	private AnalysisEngineDescription getPreprocessing(Experiment experiment) throws ResourceInitializationException {
		List<AnalysisEngineDescription> preprocessingEngines = new ArrayList<>();
		preprocessingEngines.add(createEngineDescription(OutcomeSetter.class,
				OutcomeSetter.PARAM_IS_REGRESSION, experiment.isRegression()));
		preprocessingEngines.addAll(experiment.getPreprocessing());
		return createEngineDescription(
				preprocessingEngines.toArray(new AnalysisEngineDescription[preprocessingEngines.size()]));
		
	}
}
