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

import java.io.File;

import org.apache.uima.fit.descriptor.ConfigurationParameter;

import de.unidue.ltl.ctest.difficulty.annotations.CTest;
import de.unidue.ltl.ctest.difficulty.annotations.Cloze;
import de.unidue.ltl.ctest.difficulty.annotations.LanguageDependency;
import de.unidue.ltl.ctest.difficulty.annotations.XTest;
/**
 * This Feature Extractor tests if the solution is in a list of academic words.
 * 
 * Two different academic word lists are recommended for English:
 * The COCA list of academic words: http://www.academicvocabulary.info/samples/general-core.pdf and
 * the Coxhead list: http://www.victoria.ac.nz/lals/resources/academicwordlist/
 * were merged into one.
 * For French and German, no lists of academic words have been found yet.
 */
@CTest
@XTest
@Cloze
@LanguageDependency(ids={"en"})
public class IsAcademicWordExtractor
    extends IsInWordListExtractor
{

    public static final String PARAM_ACADEMIC_WORDLIST = "AcademicWordListFile";
    @ConfigurationParameter(name = PARAM_ACADEMIC_WORDLIST, mandatory = true)
    protected File academicWordList;

    public static final String FEATURE_SUFFIX = "academic";
    
    @Override
	protected File getWordList() {
		return academicWordList;
	}

	@Override
	protected String getFeatureSuffix() {
		return FEATURE_SUFFIX;
	}
}