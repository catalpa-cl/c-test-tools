package de.unidue.ltl.ctest.difficulty.features.util;

import java.util.HashSet;
import java.util.Set;

import de.tudarmstadt.ukp.dkpro.core.ngrams.util.CharacterNGramStringIterable;

/**
 * This term similarity measure implements the DICE measure, following the description in Inkpen et
 * al 2005: "DICE (Adamson & Boreham 74) is calculated by dividing twice the number of shared letter
 * bigrams by the total number of bigrams in both words: DICE(x, y) = 2 * |bigrams(x) ∩ bigrams (y)|
 * / |bigrams(x)|+|bigrams(y)| where bigrams(x) is a multi-set of character bigrams in word x. E.
 * g., DICE(colour,couleur) = 6/11 = 0.55 (the shared bigrams are co, ou, ur)."
 */
public class DiceMeasure
    extends org.dkpro.similarity.algorithms.api.TermSimilarityMeasureBase
{

    public double getSimilarity(String arg1, String arg2)
        throws org.dkpro.similarity.algorithms.api.SimilarityException
    {
        Set<String> bigrams1 = getBigrams(arg1);
        Set<String> bigrams2 = getBigrams(arg2);
        return calculateSimilarity(bigrams1, bigrams2);
    }

    protected double calculateSimilarity(Set<String> bigrams1, Set<String> bigrams2)
    {
        Set<String> intersection = new HashSet<String>(bigrams1);
        intersection.retainAll(bigrams2);
        double intersect = intersection.size();
        double sim = (2 * intersect) / (bigrams1.size() + bigrams2.size());
        return sim;
    }

    private Set<String> getBigrams(String word)
    {
        Set<String> bigrams = new HashSet<String>();
        for (String bigram : new CharacterNGramStringIterable(word, 2, 2)) {
            bigrams.add(bigram);
        }
        return bigrams;
    }

}
