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
package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;

import org.dkpro.tc.api.features.FeatureExtractor;

import de.unidue.ltl.ctest.difficulty.annotations.CTest;
import de.unidue.ltl.ctest.difficulty.annotations.Cloze;
import de.unidue.ltl.ctest.difficulty.annotations.LanguageDependency;
import de.unidue.ltl.ctest.difficulty.annotations.XTest;

/**
 * This extractor looks up if the target word is a Uby cognate word.
 */
@CTest
@XTest
@Cloze
@LanguageDependency(ids={"en", "de", "fr"})
public class HasUbyCognateExtractor
    extends HasCognateExtractor
    implements FeatureExtractor
{

    public static final String FN_UBY_COGNATENESS = "HasUbyCognate";

	@Override
	protected String getFeatureName() {
		return FN_UBY_COGNATENESS;
	}  
}