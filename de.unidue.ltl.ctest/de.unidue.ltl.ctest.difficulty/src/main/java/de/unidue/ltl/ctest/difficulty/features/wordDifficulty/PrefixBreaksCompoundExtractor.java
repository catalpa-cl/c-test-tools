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
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_NOUN;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.decompounding.dictionary.SimpleDictionary;
import de.tudarmstadt.ukp.dkpro.core.decompounding.splitter.DecompoundedWord;
import de.tudarmstadt.ukp.dkpro.core.decompounding.splitter.Fragment;
import de.tudarmstadt.ukp.dkpro.core.decompounding.splitter.JWordSplitterAlgorithm;
import de.unidue.ltl.ctest.difficulty.annotations.CTest;
import de.unidue.ltl.ctest.type.Gap;

/**
 * If the gap is on a compound extracts whether the prefix breaks the compound.
 *
 */
@CTest
public class PrefixBreaksCompoundExtractor
    extends FeatureExtractorResource_ImplBase
    implements FeatureExtractor
{

	public static final String UTF8 = "UTF-8";
	
    public static final String FN_IS_COMPOUND_BREAK = "IsCompoundBreak";
    public static final String FN_IS_COMPOUND = "IsCompound";
    
    public static final String PARAM_DICTIONARY_FILE = "dictionaryFile";
    @ConfigurationParameter(name = PARAM_DICTIONARY_FILE, mandatory = true)
    protected static File dictionaryFile;

    private JWordSplitterAlgorithm splitter;
    boolean isInitialized;

    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
        throws ResourceInitializationException
    {
        super.initialize(aSpecifier, aAdditionalParams);
        splitter = new JWordSplitterAlgorithm();
        try {
			splitter.setDictionary(new SimpleDictionary(dictionaryFile, UTF8));
		} catch (IOException e) {
			System.out.println("Could not load dictionary!");
			e.printStackTrace();
			return false;
		}
        return true;
    }

    @Override
    public Set<Feature> extract(JCas view, TextClassificationTarget classificationTarget)
        throws TextClassificationException
    {

        boolean isCompoundBreak = false;
        boolean isCompound = false;
        Set<Feature> featList = new HashSet<Feature>();
        Token tok = JCasUtil.selectCovered(Token.class, classificationTarget).get(0);
        POS pos = tok.getPos();
        String word = tok.getLemma().getValue()
                .toLowerCase();
        // only check decompounding of nouns
        if (pos instanceof POS_NOUN) {
            isCompound = isCompound(word);

        }
        featList.add(new Feature(FN_IS_COMPOUND, isCompound, FeatureType.BOOLEAN));

        Gap gap = JCasUtil.selectCovered(Gap.class, classificationTarget).get(0);

        if (pos instanceof POS_NOUN) {
            String prefix = "";
            String solution = gap.getCoveredText();
            if (gap.getPrefix().length() > 0) {
                prefix = gap.getPrefix();

            }

            if (gap.getPostfix().length() > 0) {

                String postfix = gap.getPostfix();

                prefix = solution.substring(0, solution.length() - postfix.length());

            }
            isCompoundBreak = isCompoundBreak(word, prefix);
        }
        featList.add(new Feature(FN_IS_COMPOUND_BREAK, isCompoundBreak, FeatureType.BOOLEAN));

        return featList;
    }

    public boolean isCompound(String word)
    {

        return getSplits(word).size() > 1;
    }

    public boolean isCompoundBreak(String word, String prefix)
    {
        List<DecompoundedWord> splitresult = getSplits(word);
        if (splitresult.size() < 2) {
            return false;
        }
        List<Fragment> splits = splitresult.get(1).getSplits();
        prefix = prefix.trim().toLowerCase();
        String compoundBegin = "";
        for (Fragment f : splits) {
            compoundBegin = compoundBegin + f.toString().trim();
            if (compoundBegin.contains("(")) {
                String compound1 = compoundBegin.replace("(", "").replace(")", "");
                String compound2 = compoundBegin.replaceAll("\\(.+?\\)", "");
                compoundBegin = compound1;
                if (compound2.equals(prefix)) {
                    return true;
                }
            }

            if (compoundBegin.length() > prefix.length()) {
                break;
            }
            if (compoundBegin.trim().toLowerCase().equals(prefix)) {
                return true;
            }

        }
        return false;

    }

    public List<DecompoundedWord> getSplits(String word)
    {
        return splitter.split(word).getAllSplits();
    }

    public void setDictionaryFile(String dictionaryFile) throws IOException
    {
        splitter = new JWordSplitterAlgorithm();
        splitter.setDictionary(new SimpleDictionary(new File(dictionaryFile), UTF8));
    }

}
