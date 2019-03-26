/*******************************************************************************
 * Copyright 2015
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unidue.ltl.ctest.difficulty.features.readability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.fit.internal.ExtendedLogger;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_VERB;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * This extractor calculates the ratio between the amount of words and the
 * amount of different Part-Of-Speech types for the complete document. Also
 * calculates how many different verb types occur compared to the overall verb
 * amount. Additionally calculates the Type/Token ratio for the targets cover
 * sentence.
 */
public class TypeTokenRatioExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {
	public static final String FN_TYPE_TOKEN_RATIO = "TypeTokenRatio";
	public static final String FN_TYPE_TOKEN_RATIO_TARGET = "TypeTokenRatio_Target";
	public static final String FN_VERB_VARIATION = "VerbVariation";

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		return extractTTR(jcas, getLogger(), target);
	}

	public Set<Feature> extractTTR(JCas jcas, ExtendedLogger logger, TextClassificationTarget target)
			throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		double ttr = -1.0;
		double vv = -1.0;
		try {
			ttr = getTypeTokenRatio(jcas);
			vv = getVerbVariation(jcas);
		} catch (NullPointerException e) {
			logger.log(Level.INFO,
					"Probably empty token input: No POS information available for " + jcas.getDocumentText());
		}

		featList.add(new Feature(FN_TYPE_TOKEN_RATIO, ttr, FeatureType.NUMERIC));
		featList.add(new Feature(FN_VERB_VARIATION, vv, FeatureType.NUMERIC));

		double ttr_target = -1.0;
		List<Sentence> sentences = JCasUtil.selectCovering(jcas, Sentence.class, target);
		if (sentences.size() > 0) {
			Sentence coverSent = sentences.get(0);
			try {
				ttr_target = getTypeTokenRatio(jcas, coverSent);
			} catch (NullPointerException e) {
				logger.log(Level.INFO,
						"Probably empty token in put: No POS information available for " + jcas.getDocumentText());
			}
		}

		featList.add(new Feature(FN_TYPE_TOKEN_RATIO_TARGET, ttr_target, FeatureType.NUMERIC));
		return featList;

	}

	private double getTypeTokenRatio(JCas jcas) throws NullPointerException {
		int numberOfTokens = 0;
		List<String> types = new ArrayList<String>();

		for (Sentence sent : JCasUtil.select(jcas, Sentence.class)) {
			for (Token t : JCasUtil.selectCovered(jcas, Token.class, sent)) {
				POS pos = t.getPos();

				if (!t.getPos().getType().getShortName().equals("PUNC") && !pos.equals(null)) {
					numberOfTokens++;
					String lemma = t.getLemma().getValue().toLowerCase();
					if (!types.contains(lemma)) {
						types.add(lemma);
					}
				}
			}

		}
		return types.size() / (double) numberOfTokens;
	}

	private double getVerbVariation(JCas jcas) throws NullPointerException {
		int numberOfVerbs = 0;
		List<String> verbtypes = new ArrayList<String>();
		for (Sentence sent : JCasUtil.select(jcas, Sentence.class)) {
			for (Token t : JCasUtil.selectCovered(jcas, Token.class, sent)) {
				POS pos = t.getPos();
				if (pos instanceof POS_VERB) {
					numberOfVerbs++;
					String lemma = t.getLemma().getValue().toLowerCase();
					if (!verbtypes.contains(lemma)) {
						verbtypes.add(lemma);
					}
				}
			}
		}
		return verbtypes.size() / (double) numberOfVerbs;
	}

	public Set<Feature> extractTTR_Target(JCas jcas, Sentence sent, ExtendedLogger logger)
			throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		double ttr = -1.0;

		try {
			ttr = getTypeTokenRatio(jcas, sent);
		}

		catch (NullPointerException e) {
			logger.log(Level.INFO,
					"Probably empty token in put: No POS information available for " + jcas.getDocumentText());
		}

		featList.add(new Feature(FN_TYPE_TOKEN_RATIO_TARGET, ttr, FeatureType.NUMERIC));
		return featList;
	}

	private double getTypeTokenRatio(JCas jcas, Sentence sent) throws NullPointerException {
		int numberOfTokens = 0;

		List<String> types = new ArrayList<String>();
		for (Token t : JCasUtil.selectCovered(jcas, Token.class, sent)) {
			POS pos = t.getPos();
			if (!t.getPos().getType().getShortName().equals("PUNC") && !pos.equals(null)) {
				numberOfTokens++;
				String lemma = t.getLemma().getValue().toLowerCase();
				if (!types.contains(lemma)) {
					types.add(lemma);
				}
			}
		}
		return types.size() / (double) numberOfTokens;
	}
}
