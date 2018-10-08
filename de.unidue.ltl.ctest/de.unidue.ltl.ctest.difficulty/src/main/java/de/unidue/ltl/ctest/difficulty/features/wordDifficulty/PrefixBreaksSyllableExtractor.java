package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;
///*******************************************************************************
// * Copyright 2015
// * Ubiquitous Knowledge Processing (UKP) Lab
// * Technische Universität Darmstadt
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// ******************************************************************************/
//package testDifficulty.features.wordDifficultyFeatures;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.apache.uima.fit.descriptor.ConfigurationParameter;
//import org.apache.uima.fit.util.JCasUtil;
//import org.apache.uima.jcas.JCas;
//import org.apache.uima.resource.ResourceInitializationException;
//import org.apache.uima.resource.ResourceSpecifier;
//import org.apache.uima.util.Level;
//import org.apache.uima.util.Logger;
//import org.dkpro.tc.api.exception.TextClassificationException;
//import org.dkpro.tc.api.features.Feature;
//import org.dkpro.tc.api.features.FeatureExtractor;
//import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
//import org.dkpro.tc.api.type.TextClassificationTarget;
//
//import types.Gap;
//import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
//import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
//import de.tudarmstadt.ukp.lexcompare.text.phonology.ISyllable;
//import de.tudarmstadt.ukp.lexcompare.text.phonology.hyphenation.Syllable;
//import de.tudarmstadt.ukp.lexcompare.text.phonology.hyphenation.TeXHyphenator;
///**
// * This extractor looks up if the prefix of the target breaks a syllable.
// */
//public class PrefixBreaksSyllableExtractor
//    extends FeatureExtractorResource_ImplBase
//    implements FeatureExtractor
//{
//
//    public static final String FN_IS_SYLLABLE_BREAK = "IsSyllableBreak";
//
//    Map<String, String> syllableDictEn = new HashMap<String, String>();
//
//    public static final String PARAM_HYPHENATION_FILE = "hyphenationFile";
//    @ConfigurationParameter(name = PARAM_HYPHENATION_FILE, mandatory = true)
//    protected static String hyphenationFile;
//
//    private Logger logger;
//    boolean hyphenationDictEnLoaded;
//    TeXHyphenator hyphenator = new TeXHyphenator();
//
//    @Override
//    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
//        throws ResourceInitializationException
//    {
//        super.initialize(aSpecifier, aAdditionalParams);
//        try {
//            hyphenator.loadPatterns(new File(hyphenationFile));
//        }
//        catch (IOException e) {
//            throw new ResourceInitializationException(e);
//        }
//        return true;
//    }
//
//    @Override
//    public Set<Feature> extract(JCas jcas, TextClassificationTarget classificationTarget)
//        throws TextClassificationException
//    {
//
//        if (jcas.getDocumentLanguage().equals("en") && !hyphenationDictEnLoaded) {
//            loadEnHyphenationDict();
//            hyphenator.setMinNonHyphened(2, 2);
//            hyphenationDictEnLoaded = true;
//        }
//        Set<Feature> featList = new HashSet<Feature>();
//        Gap gap = JCasUtil.selectCovered(Gap.class, classificationTarget).get(0);
//        String word = gap.getCoveredText();
//        Token tok = JCasUtil.selectCovered(Token.class, classificationTarget).get(0);
//        POS pos = tok.getPos();
//        String lemma = tok.getLemma().getValue()
//                .toLowerCase();
//        boolean isSyllableBreak = false;
//
//        String[] syllables;
//        syllables = getSyllables(word, lemma, jcas.getDocumentLanguage());
//
//        // In words with a single syllable, breaks are not so relevant , eg. d__ (der, die, das),
//        // gi__ (gibt), so we only check words with at least two syllables
//        if (syllables != null && syllables.length > 1) {
//            String prefix = "";
//            if (gap.getPrefix().length() > 0) {
//                prefix = gap.getPrefix();
//            }
//            if (gap.getPostfix().length() > 0) {
//
//                String postfix = gap.getPostfix();
//                prefix = word.substring(0, word.length() - postfix.length());
//
//            }
//            isSyllableBreak = prefixBreaksSyllable(prefix, syllables);
//
//        }
//
//        featList.add(new Feature(FN_IS_SYLLABLE_BREAK, isSyllableBreak));
//        return featList;
//    }
//
//    private void loadEnHyphenationDict()
//    {
//        String line = "";
//        logger = getUimaContext().getLogger();
//
//        try {
//            BufferedReader br;
//            br = new BufferedReader(new FileReader(
//                    "src/main/resources/wordLists/EnglishHyphDict_v108.csv"));
//
//            while ((line = br.readLine()) != null) {
//                String[] item = line.split(" ");
//                if (item.length == 2) {
//                    syllableDictEn.put(item[0].toLowerCase(), item[1].toLowerCase());
//                }
//                else {
//                    logger.log(Level.INFO, "Wrong input format for line: " + line);
//                }
//            }
//            br.close();
//        }
//        catch (Exception e) {
//            logger.log(Level.INFO, "hyphenation dict not loaded, only using latex hyphenation");
//        }
//    }
//
//    private String[] getSyllables(String word, String lemma, String language)
//    {
//
//        if (language.equals("en")) {
//            // tex hyphenation is not so good for English, first check in Syllable dictionary
//
//            if (syllableDictEn.containsKey(word)) {
//                return syllableDictEn.get(word).split("-");
//            }
//            else if ((word.length() - lemma.length()) == 1 && syllableDictEn.containsKey(lemma)) {
//                return syllableDictEn.get(lemma).split("-");
//            }
//            else {
//                return splitWord(word);
//
//            }
//        }
//        else if (language.equals("de")) {
//
//            String hyphenatedText = Syllable.makeHyphenatedText(hyphenator.hyphenate(word), "|");
//
//            String[] syllables = hyphenatedText.split("\\|");
//            if (syllables[syllables.length - 1].length() == 1) {
//                // avoid that the last syllable consists of a single letter i.e. Mun-d
//                syllables[syllables.length - 2] = syllables[syllables.length - 2]
//                        + syllables[syllables.length - 1];
//                syllables = Arrays.copyOfRange(syllables, 0, syllables.length - 1);
//            }
//            return syllables;
//        }
//        else {
//            return splitWord(word);
//        }
//
//    }
//
//    private String[] splitWord(String word)
//    {
//
//        List<ISyllable> result = hyphenator.hyphenate(word);
//
//        List<String> cleanedResult = new ArrayList<String>();
//
//        if (result.size() > 1) {
//            for (ISyllable isyllable : result) {
//                // Tex-hyphenator does not split "-", for example micro-organism becomes
//                // mi|cro-or|ga|nism
//                String[] parts = isyllable.getText().split("-");
//                for (String part : parts) {
//                    cleanedResult.add(part.replace(":", ""));
//                }
//            }
//
//            return cleanedResult.toArray(new String[cleanedResult.size()]);
//        }
//        else {
//            return new String[] { word };
//        }
//    }
//
//    public boolean prefixBreaksSyllable(String prefix, String[] syllables)
//    {
//
//        String syllablePrefix = "";
//        if (prefix.length() == 0) {
//            return false;
//        }
//        for (String syllable : syllables) {
//            syllablePrefix = syllablePrefix + syllable;
//            if (syllablePrefix.length() == prefix.length()) {
//                return false;
//            }
//
//            if (syllablePrefix.length() > prefix.length()) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//}