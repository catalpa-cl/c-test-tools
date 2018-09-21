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
 *  This feature extractor tests, if the solution is in a list of words with latin roots.
 *  You need to provide a word list file. For English, the one extracted from
 *  Wikipedia: src/main/resources/wordLists/EnglishWordsWithLatinOrigin.txt is recommended.
 *  For the other languages, no file has been found yet.
 */
@CTest
@XTest
@Cloze
@LanguageDependency(ids={"en"})
public class IsLatinWordExtractor
    extends IsInWordListExtractor
{
	
    public static final String PARAM_LATIN_WORDLIST = "LatinWordListFile";
    @ConfigurationParameter(name = PARAM_LATIN_WORDLIST, mandatory = true)
    protected File latinWordList;

    public static final String FEATURE_SUFFIX = "latin";
    
    @Override
	protected File getWordList() {
		return latinWordList;
	}

	@Override
	protected String getFeatureSuffix() {
		return FEATURE_SUFFIX;
	}
}