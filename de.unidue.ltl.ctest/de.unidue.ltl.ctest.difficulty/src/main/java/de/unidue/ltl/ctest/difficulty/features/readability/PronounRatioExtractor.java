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

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_PRON;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * This extractor calculates the pronoun ratio for the complete document as well
 * as the pronoun ratio for the cover sentence of the target.
 */
public class PronounRatioExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {
	public static final String FN_PRONOUN_RATIO = "PronounRatio";
	public static final String FN_PRONOUN_RATIO_TARGET = "PronounRatio_Target";

	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		
		try {
			Sentence coverSent = JCasUtil.selectCovering(jcas, Sentence.class, target).get(0);
			// Pronoun Ratio of document
			featList.add(new Feature(FN_PRONOUN_RATIO, getPronounRatio(jcas), FeatureType.NUMERIC));
			// Pronoun Ratio of targets cover sentence
			featList.add(new Feature(FN_PRONOUN_RATIO_TARGET, getPronounRatio(jcas, coverSent), FeatureType.NUMERIC));
		} catch (IndexOutOfBoundsException e) {
			featList.add(new Feature(FN_PRONOUN_RATIO, 0, FeatureType.NUMERIC));
			featList.add(new Feature(FN_PRONOUN_RATIO_TARGET, 0, FeatureType.NUMERIC));
		}
		
		return featList;
	}

	public static double getPronounRatio(JCas jcas) {
		double nrOfWords = 0.0;
		int nrOfPronouns = 0;

		for (Token t : JCasUtil.select(jcas, Token.class)) {

			POS pos = t.getPos();
			if (pos != null) {
				if (!t.getPos().getType().getShortName().equals("PUNC")) {
					nrOfWords++;
					if (pos instanceof POS_PRON) {
						nrOfPronouns++;
					}
				}
			}
		}

		double pronounRatio = nrOfPronouns / nrOfWords;
		return pronounRatio;
	}

	public static double getPronounRatio(JCas jcas, Sentence sent) {
		double nrOfWords = 0.0;
		int nrOfPronouns = 0;

		for (Token t : JCasUtil.selectCovered(jcas, Token.class, sent)) {
			POS pos = t.getPos();
			if (pos != null) {
				if (!t.getPos().getType().getShortName().equals("PUNC")) {
					nrOfWords++;
					if (pos instanceof POS_PRON) {
						nrOfPronouns++;
					}
				}
			}
		}
		double pronounRatio = nrOfPronouns / nrOfWords;
		return pronounRatio;
	}
}