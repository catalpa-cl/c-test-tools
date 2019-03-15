package de.unidue.ltl.ctest.difficulty.test.experiments;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.dkpro.tc.api.features.TcFeatureFactory.create;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.matetools.MateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpChunker;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.difficulty.experiments.Experiment;
import de.unidue.ltl.ctest.difficulty.experiments.Model;
import de.unidue.ltl.ctest.difficulty.features.wordDifficulty.LengthExtractor;
import de.unidue.ltl.ctest.difficulty.train.DefaultTrainer;
import de.unidue.ltl.ctest.difficulty.train.ModelTrainer;
import de.unidue.ltl.ctest.io.CTestFileReader;

public class DefaultTrainerTest {
	private final String basePath = "src/test/resources/texts/";
	private final String trainPath = basePath + "train/";
	private final String testPath = basePath + "test/";
	private final String modelPath = "src/test/resources/model/";
	
	private ModelTrainer trainer = new DefaultTrainer();
	private Experiment experiment;
	
	@Before()
	public void before() throws Exception {
		this.experiment = this.createExperiment();
	}
	
	@Test()
	public void crossValidationTest() throws Exception {
		this.trainer.runCrossValidation(this.experiment, CTestFileReader.class, this.trainPath, 2);
	}
	
	@Test()
	public void trainTestTest() throws Exception {
		this.trainer.runTrainTest(this.experiment, CTestFileReader.class, this.trainPath, this.testPath);
	}
	
	@Test()
	public void saveLoadRoundTripTest() throws Exception {
		this.trainer.saveModel(this.experiment, CTestFileReader.class, this.trainPath, this.modelPath);
		Model model = this.trainer.loadModel(this.modelPath);
		CTestObject ctest = new CTestFileReader().read(testPath);
		
		assertTrue(!model.predict(ctest).isEmpty());
		System.out.println(model.predict(ctest));
	}
	
	private Experiment createExperiment() throws ResourceInitializationException {		
		String languageCode = "en";
		
		List<AnalysisEngineDescription> preprocessing = new ArrayList<>();
		preprocessing.add(createEngineDescription(MateLemmatizer.class, MateLemmatizer.PARAM_LANGUAGE, languageCode));
		preprocessing.add(createEngineDescription(OpenNlpPosTagger.class, OpenNlpPosTagger.PARAM_LANGUAGE, languageCode));
		preprocessing.add(createEngineDescription(OpenNlpChunker.class, OpenNlpChunker.PARAM_LANGUAGE, languageCode));
		preprocessing.add(createEngineDescription(StanfordNamedEntityRecognizer.class, StanfordNamedEntityRecognizer.PARAM_LANGUAGE, languageCode));
		
		
		TcFeatureSet features = new TcFeatureSet();
		features.add(create(LengthExtractor.class));
		
		Experiment experiment = new Experiment();
		experiment.setExperimentName("UnitTestExperiment");
		experiment.setIsRegression(true);
		experiment.setPreprocessing(preprocessing);
		experiment.setFeatureSet(features);
		
		return experiment;
	}
}
