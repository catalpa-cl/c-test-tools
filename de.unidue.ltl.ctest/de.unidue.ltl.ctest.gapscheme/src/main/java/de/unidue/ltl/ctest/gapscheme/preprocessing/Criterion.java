package de.unidue.ltl.ctest.gapscheme.preprocessing;

import java.util.Collection;
import java.util.function.Predicate;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public interface Criterion extends Predicate<Token>{
	public Collection<Class<? extends Annotation>> getRequiredAnnotations();
	public AnalysisEngineDescription getEngineDescription(Class<? extends Annotation> requiredClass);
}
