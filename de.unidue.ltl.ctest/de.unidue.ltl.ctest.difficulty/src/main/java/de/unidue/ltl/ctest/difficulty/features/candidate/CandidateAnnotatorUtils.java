package de.unidue.ltl.ctest.difficulty.features.candidate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.WordUtils;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.provider.FrequencyCountProvider;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.frequency.Web1TFileAccessProvider;

//TODO: Check applicability of de.unidue.ltl.ctest.gapscheme.preprocessing
public class CandidateAnnotatorUtils

{

    public static Set<String> getCtestCandidates(String prefix, String word, int lengthVariability,
            HashMap<String, Set<String>> wordsMap, String lang)
        throws Exception
    {

        Set<String> possibleCandidates = new HashSet<String>();
        int min_length;
        int max_length;
        if (prefix.length() > 0) {

            // compound treatment currently only works for English
            // TODO: German compound treatment!!
            if (lang.equals("fr")) {
                if (prefix.contains("'")) {

                    String[] prefixparts = prefix.split("'");

                    String newPrefix = "";
                    try {
                        newPrefix = prefixparts[1];
                        min_length = Math.max(prefix.length() * 2 - lengthVariability,
                                prefix.length() + 1);
                        max_length = prefix.length() * 2 + 1 + lengthVariability;
                    }
                    catch (IndexOutOfBoundsException e) {
                        // empty prefix
                        min_length = 2;
                        max_length = 3;
                    }
                    Web1TFileAccessProvider frequencyProvider = new Web1TFileAccessProvider("fr",
                            new File(new DkproContext().getWorkspace("web1t").getAbsolutePath()
                                    + "/fr"), 1, 3);
                    for (String cand : getSimpleCtestCandidates(newPrefix, min_length, max_length,
                            wordsMap)) {

                        String candidate = prefixparts[0] + "'" + cand;

                        if (!Double.isInfinite(frequencyProvider.getLogProbability(prefixparts[0]
                                + "' " + cand))) {
                            possibleCandidates.add(candidate);
                        }

                    }
                }
                else {
                    possibleCandidates = getSimpleCtestCandidates(prefix, lengthVariability,
                            wordsMap);
                }
            }

            else {

                if (!isCompound(prefix)) {
                    // normal treatment here
                    possibleCandidates = getSimpleCtestCandidates(prefix, lengthVariability,
                            wordsMap);
                }

                else {
                    // compound treatment here:
                    // for compounds like carbon-fr__ the length constraint is different
                    String[] prefixElems = prefix.split("-");
                    // get candidates for carbon-fr__
                    int lengthAddition = 0;
                    if (prefixElems.length > 1) {
                        lengthAddition = prefixElems[1].length();
                    }
                    else {
                        lengthAddition = prefix.length() - 1;
                    }
                    min_length = Math.max(prefix.length() + 1, prefix.length() + lengthAddition
                            - lengthVariability);
                    max_length = prefix.length() + lengthAddition + 1 + lengthVariability;
                    possibleCandidates = getSimpleCtestCandidates(prefix, min_length, max_length,
                            wordsMap);

                    // remove the "-"
                    // get candidates for carbonfr__
                    min_length = Math.max(prefix.length() - 1 + 1, prefix.length() - 1
                            + lengthAddition - lengthVariability);
                    max_length = prefix.length() - 1 + lengthAddition + 1 + lengthVariability;
                    if (prefixElems.length > 1) {
                        possibleCandidates.addAll(getSimpleCtestCandidates(prefixElems[0]
                                + prefixElems[1], min_length, max_length, wordsMap));
                    }
                    else {
                        possibleCandidates.addAll(getSimpleCtestCandidates(prefixElems[0],
                                min_length, max_length, wordsMap));
                    }
                    // if candidate list is small, get candidates for fr__
                    if ((!possibleCandidates.contains(word) || possibleCandidates.size() < 4)
                            && prefixElems.length > 1) {

                        Set<String> singleCandidates = getSimpleCtestCandidates(prefixElems[1],
                                lengthVariability, wordsMap);

                        // filter by bigram probability with carbon
                        File web1TDir = new File(new DkproContext().getWorkspace("web1t")
                                .getAbsolutePath() + "/" + lang);
                        FrequencyCountProvider frequencyProvider = new Web1TFileAccessProvider(
                                lang, web1TDir, 1, 2);
                        for (String cand : singleCandidates) {

                            Double biprob = frequencyProvider.getLogProbability(prefixElems[0]
                                    + " " + cand);
                            Double singleprob = frequencyProvider.getLogProbability(cand);
                            if (Double.isNaN(biprob)) {
                                biprob = 0.0;
                            }
                            if (!Double.isInfinite(singleprob) && !Double.isInfinite(biprob)
                                    && (biprob / singleprob) > 1) {

                                possibleCandidates.add(prefixElems[0] + "-" + cand);
                            }
                        }
                    }
                }
            }
        }
        // prefix is empty, should not happen!
        // exception: some English texts erroneously contain a gap with the solution "a"
        else {
            possibleCandidates.add("a");
        }

        return possibleCandidates;
    }

    public static Set<String> getSimpleCtestCandidates(String prefix, int lengthVariability,
            HashMap<String, Set<String>> wordsMap)
        throws IOException
    {
        int min_length = Math.max(prefix.length() * 2 - lengthVariability, prefix.length() + 1);
        int max_length = prefix.length() * 2 + 1 + lengthVariability;

        return getSimpleCtestCandidates(prefix, min_length, max_length, wordsMap);
    }

    private static Set<String> getSimpleCtestCandidates(String prefix, int minLength,
            int maxLength, HashMap<String, Set<String>> wordsMap)
        throws IOException
    {
        Set<String> possibleCandidates = new HashSet<String>();
        String key;

        if (prefix.length() > 0) {
            key = prefix.toLowerCase().substring(0, 1);
        }
        else {
            // empty prefix
            key = "prefix0";

        }
        try {
            for (String word : wordsMap.get(key)) {
                // select candidates with correct prefix and length)

                if ((key.equals("prefix0") || word.startsWith(prefix.toLowerCase()))
                        && (word.length() >= minLength) && (word.length() <= maxLength)) {

                    // lexicon is in lowercase, recase if necessary
                    if (prefix.length() > 0 && Character.isUpperCase(prefix.charAt(0))) {
                        possibleCandidates.add(WordUtils.capitalize(word.trim()));
                    }

                    else {
                        possibleCandidates.add(word.trim());
                    }
                }
            }
        }
        catch (NullPointerException e) {
            System.out.println("No candidates for " + prefix);
            // maybe re-run candidate calculation with higher length variability in this case?
            // in the german tests, the length constraint is not always respected for compounds

        }
        return possibleCandidates;
    }

    public static boolean isCompound(String word)
    {

        return word.contains("-");

        // TODO add compound splitting for German

    }

    public static Set<String> getSimpleCandidatesPrefixGap(String postfix, String word,
            int lengthVariability, HashMap<String, Set<String>> wordsMap)
        throws IOException
    {
        int min_length = Math.max(postfix.length() * 2 - lengthVariability, postfix.length() + 1);
        int max_length = postfix.length() * 2 + 1 + lengthVariability;
        return getSimpleCandidatesPrefixGap(postfix, word, min_length, max_length, wordsMap);
    }

    private static Set<String> getSimpleCandidatesPrefixGap(String postfix, String word,
            int minLength, int maxLength, HashMap<String, Set<String>> wordsMap)
        throws IOException
    {
        Set<String> possibleCandidates = new HashSet<String>();
        String part = postfix.toLowerCase().substring(postfix.length() - 1, postfix.length());

        if (wordsMap.containsKey(part)) {
            for (String candidateWord : wordsMap.get(part)) {

                // select candidates with correct prefix and length)

                if (candidateWord.endsWith(postfix.toLowerCase())
                        && (candidateWord.length() >= minLength)
                        && (candidateWord.length() <= maxLength)) {

                    // lexicon is in lowercase, recase if necessary
                    if (Character.isUpperCase(word.charAt(0))) {
                        possibleCandidates.add(WordUtils.capitalize(candidateWord.trim()));
                    }

                    else {
                        possibleCandidates.add(candidateWord.trim());
                    }

                }
            }
        }

        return possibleCandidates;
    }
}