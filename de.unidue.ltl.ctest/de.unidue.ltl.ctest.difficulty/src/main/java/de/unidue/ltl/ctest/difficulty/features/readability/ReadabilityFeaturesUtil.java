package de.unidue.ltl.ctest.difficulty.features.readability;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.NC;
import de.unidue.ltl.ctest.difficulty.features.util.ReadabilityUtils;

// TODO add tests for utils
public class ReadabilityFeaturesUtil {

	public static Integer getSize(Collection<?> collection) {
		return collection == null ? 0 : collection.size();
	}

	public static double getNumberOfUniqueEntities(JCas jcas) {
		Collection<NamedEntity> entities = JCasUtil.select(jcas, NamedEntity.class);
		Collection<NC> nounChunks = JCasUtil.select(jcas, NC.class);

		Set<String> uniqueEntities = new HashSet<String>();

		for (NamedEntity ne : entities) {
			uniqueEntities.add(ne.getCoveredText());
		}
		for (NC nc : nounChunks) {
			uniqueEntities.add(nc.getCoveredText());
		}

		return uniqueEntities.size();
	}

	// TODO ever used?
	public static int getNumberOfUniqueEntities(Sentence coverSent) {
		Collection<NamedEntity> entities = JCasUtil.selectCovered(NamedEntity.class, coverSent);
		Collection<NC> nounChunks = JCasUtil.selectCovered(NC.class, coverSent);

		Set<String> uniqueEntities = new HashSet<String>();

		for (NamedEntity ne : entities) {
			uniqueEntities.add(ne.getCoveredText());
		}
		for (NC nc : nounChunks) {
			uniqueEntities.add(nc.getCoveredText());
		}

		return uniqueEntities.size();
	}

	public static int numberOfWords(Annotation unit) {
		int nrOfWords = 0;
		for (Token t : JCasUtil.selectCovered(Token.class, unit)) {
			if (ReadabilityUtils.isWord(t)) {
				nrOfWords++;
			}
		}

		return nrOfWords;
	}
}
