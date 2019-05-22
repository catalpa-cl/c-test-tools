package de.unidue.ltl.ctest.difficulty.features.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.provider.FrequencyCountProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.difficulty.types.GapCandidate;
import edu.stanford.nlp.util.ArraySet;

// TODO all public methods should be documented
public class CTestFeaturesUtil
{

    public static Set<String> getTopN(TreeSet<GapCandidate> candidates, int n)
    {
        TreeSet<GapCandidate> copy = new TreeSet<GapCandidate>(candidates);
        Set<String> topN = new ArraySet<String>();
        int limit = Math.min(candidates.size(), n);
        for (int i = 0; i < limit; i++) {
            topN.add(copy.pollFirst().getCandidateWord());
        }
        return topN;
    }

    public static int getSolutionRank(String[] solutions, TreeSet<GapCandidate> candidateSet)
    {
        int solutionRank = 1;

        for (GapCandidate cand : candidateSet) {
            // any of the solutions is sufficient
            if (Arrays.asList(solutions).contains(cand.getCandidateWord())) {
                return solutionRank;

            }
            else {
                solutionRank++;
            }
        }
        // solution is not in list, return default
        return -1;
    }

    public static Double getProbability(FrequencyCountProvider frequencyProvider, String language,
            String phrase)
        throws IOException
    {
        return frequencyProvider.getProbability(getWeb1TFormat(phrase, language));
    }
    
    public static double getLogProbability(FrequencyCountProvider frequencyProvider,
            String language, String phrase)
        throws IOException
    {
        return frequencyProvider.getLogProbability(getWeb1TFormat(phrase, language));

    }

    public static Set<Feature> addLogProbability(FrequencyCountProvider frequencyProvider,
            String language, Set<Feature> featureList, String featureName, String phrase)
        throws IOException, TextClassificationException
    {

        Double prob = getLogProbability(frequencyProvider, language, phrase);

        return addLogProbability(featureList, featureName, prob);
    }

    public static Set<Feature> addLogProbability(Set<Feature> featureList, String featureName,
            Double prob)
        throws IOException, TextClassificationException
    {

        if (prob.isInfinite() || prob.isNaN() || prob == null) {
            featureList.add(new Feature(featureName, 0.0, FeatureType.NUMERIC));
        }
        else {
            featureList.add(new Feature(featureName, prob, FeatureType.NUMERIC));
        }
        return featureList;
    }

    public static Set<Feature> addProbability(Set<Feature> featureList, String featureName,
            Double prob)
        throws IOException, TextClassificationException
    {

        if (prob == 0.0 || prob == null) {
            featureList.add(new Feature(featureName, 0.0, FeatureType.NUMERIC));
        }
        else {
            featureList.add(new Feature(featureName, prob, FeatureType.NUMERIC));
        }
        return featureList;
    }

    public static String getWeb1TFormat(String token, String language)
    {
        if (language.equals("fr")) {
            // Example: "d'organisation" should be "d' organisation"
            // check if it is at the beginning to avoid splitting words like aujourd'hui
            String[] contractions = { "c'", "C'", "d'", "D'", "s'", "S'", "l'", "L'", "m'", "M'",
                    "n'", "N'", "t'", "T'", "j'", "J'", "qu'", "Qu'" };
            for (String c : contractions) {
                if (token.startsWith(c)) {
                    token = token.replace(c, (c + " "));
                }
            }

            return token;
        }
        if (language.equals("en")) {
            // Example: the world's end -> the world 's end
            token = token.replace("'s", " 's");
            // Example: ladies' night -> ladies ' night
            token = token.replace("s'", "s '");
            // Example: colour-blind, colour - blind
            token = token.replace("-", " - ");
        }
        // avoid double spaces that we might have created
        token = token.replaceAll("  ", " ");
        return token;
    }


    public static String getCoveringTrigram(Annotation unit, JCas jcas)
        throws IndexOutOfBoundsException
    {
        String previousWord = "";
        String followingWord = "";

        List<Sentence> sentences = JCasUtil.selectCovering(Sentence.class, unit);
        if (sentences.isEmpty()) {
        	// TODO: universal NA marker?
        	return "<NA>" + " " + unit.getCoveredText() + " " + "<NA>";
        }
        Sentence sent = sentences.get(0);

        if (unit.getBegin() == sent.getBegin()) {
            previousWord = "<S>";
        }
        else if (unit.getEnd() == sent.getEnd()) {
            followingWord = "</S>";
        }
        else {
        	List<Token> previousTokens = JCasUtil.selectPreceding(jcas, Token.class, unit, 1);
            previousWord = previousTokens.isEmpty() ? "<NA>" : previousTokens.get(0).getCoveredText();
            
            List<Token> followingTokens = JCasUtil.selectFollowing(jcas, Token.class, unit, 1);
            followingWord = followingTokens.isEmpty() ? "<NA>" : previousTokens.get(0).getCoveredText();
        }
        
        return previousWord + " " + unit.getCoveredText() + " " + followingWord;

    }

    public static Token getLastMention(JCas jcas, TextClassificationTarget classificationTarget)
    {
        Token tok = JCasUtil.selectCovered(Token.class, classificationTarget).get(0);
        String solution = tok.getLemma().getValue().toLowerCase();
        return getLastMention(jcas, classificationTarget, solution, 0);
    }

    public static Token getLastMention(JCas jcas, TextClassificationTarget classificationTarget,
            String candidateWord, int lengthTolerance)
    {

        Token tok = JCasUtil.selectCovered(Token.class, classificationTarget).get(0);
        Token lastMention = null;

        List<String> contentTags = new ArrayList<String>();
        contentTags.add("NN");
        contentTags.add("V");
        contentTags.add("ADJ");
        contentTags.add("ADV");

        String posValue = tok.getPos().getClass().getSimpleName();

        if (contentTags.contains(posValue)) {

            Annotation spanToGap = new Annotation(jcas, 0, classificationTarget.getBegin());

            for (Token tokenBeforeGap : JCasUtil.selectCovered(jcas, Token.class, spanToGap)) {
                String tokenString = "";
                try {
                    tokenString = tokenBeforeGap.getLemma().getValue().toLowerCase();
                }
                catch (NullPointerException e) {
                    tokenBeforeGap.getCoveredText().toLowerCase();
                }
                // candidate words are not lemmatized, so we only compare if the beginning is the
                // same and the length difference is small
                if (tokenString.length() > 3) {
                    if (candidateWord.startsWith(tokenString)
                            && (candidateWord.length() - tokenString.length()) <= lengthTolerance) {
                        lastMention = tokenBeforeGap;
                    }
                }
            }
        }
        return lastMention;
    }
}
