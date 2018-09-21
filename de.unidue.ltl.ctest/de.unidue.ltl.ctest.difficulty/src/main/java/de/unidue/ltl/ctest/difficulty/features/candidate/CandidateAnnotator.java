package de.unidue.ltl.ctest.difficulty.features.candidate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.unidue.ltl.ctest.core.TestType;
import de.unidue.ltl.ctest.difficulty.types.GapCandidate;
import de.unidue.ltl.ctest.type.Gap;

public class CandidateAnnotator
    extends JCasAnnotator_ImplBase
{

    public static final String LENGTH_VARIABILITY = "length_variability";
    @ConfigurationParameter(name = LENGTH_VARIABILITY, description = "The variability of the length constraint", mandatory = false, defaultValue = "1")
    private int lengthVariability;

    public static final String PARAM_LANGUAGE = "languageCode";
    @ConfigurationParameter(name = PARAM_LANGUAGE, description = "The language code", mandatory = false, defaultValue = "1")
    private String language;

    public static final String PARAM_LEXICON_FILE = "LexiconFile";
    @ConfigurationParameter(name = PARAM_LEXICON_FILE, mandatory = true)
    protected static String lexiconFile;
    // It might make sense to add a frequency filter and only annotate candidate above a certain
    // threshold.
    // Currently, all candidates are annotated
    
    public static final String PARAM_TESTTYPE = "testtype";
    @ConfigurationParameter(name = PARAM_TESTTYPE, mandatory = true)
    private TestType testType;

    private static HashMap<String, Set<String>> wordsMap;
    private String lang;

    // private double threshold;

    @Override
    public void initialize(UimaContext aContext)
        throws ResourceInitializationException
    {

        super.initialize(aContext);
        wordsMap = new HashMap<String, Set<String>>();
        try {
            initializeLexiconResource();
        }
        catch (IOException e) {

            e.printStackTrace();
            throw new ResourceInitializationException(e);
        }
    }

    @Override
    public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {
        lang = jcas.getDocumentLanguage();
        // System.out.println("Annotating candidates for: " + jcas.getDocumentLanguage());
        for (Gap item : JCasUtil.select(jcas, Gap.class)) {
        	//TODO: -> this.getSolutionCandidates(item)
            Set<String> candidates = new HashSet<String>();
            try {
                if (item.getPrefix().length() > 0) {
                    candidates = CandidateAnnotatorUtils.getCtestCandidates(item.getPrefix(), item
                            .getCoveredText().toLowerCase(), lengthVariability, wordsMap, lang);

                }
                else if (item.getPostfix().length() > 0) {
                    candidates = CandidateAnnotatorUtils.getSimpleCandidatesPrefixGap(item.getPostfix(),
                            item.getCoveredText(), lengthVariability, wordsMap);
                }
            }
            catch (Exception e) {
                throw new AnalysisEngineProcessException(e);
            }

            //TODO: -> this.annotate(candidate)
            // annotate the candidates
            for (String cand : candidates) {
                if (isGoodCandidate(cand, item)) {
                    GapCandidate candidate = new GapCandidate(jcas, item.getBegin(), item.getEnd());
                    candidate.setCandidateWord(cand);
                    candidate.setLengthDifference(candidate.getCandidateWord().length()
                            - item.getCoveredText().length());

                    candidate.addToIndexes();
                }
            }

        }

    }

    private boolean isGoodCandidate(String cand, Gap item)
    {
        // TODO currently no filtering but might be useful
        return true;
    }

    private void initializeLexiconResource()
        throws ResourceInitializationException, IOException
    {
        // sort words by prefix to enable faster lookup
        // this could be probably be done a lot faster

        // store short words with extra key in case of empty prefix e.g. d'(une)
        wordsMap.put("prefix0", new HashSet<String>());

        for (String line : FileUtils.readLines(new File(lexiconFile))) {
            String word = line.trim().toLowerCase();
            
            if (word.length() == 0) {
            	continue;
            }

            String part = "";
            if (testType.equals(TestType.ctest)) {
                try {
                    part = word.substring(0, 1);
                }
                catch (StringIndexOutOfBoundsException e) {
                    throw new ResourceInitializationException(e);
                }
            }
            
            if (testType.equals(TestType.xtest)) {
                part = word.substring(word.length() - 1, word.length());
            }

            Set<String> words;
            if (wordsMap.containsKey(part)) {
                words = wordsMap.get(part);
            }
            else {
                words = new HashSet<String>();
            }
            words.add(word);
            wordsMap.put(part, words);

            if (language.equals("fr") && word.length() <= 3) {
                // store short words with extra key in case of empty prefix e.g. d'(une)
                Set<String> shortwords = wordsMap.get("prefix0");

                shortwords.add(word);
                wordsMap.put("prefix0", shortwords);

            }

        }

    }
}