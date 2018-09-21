package de.unidue.ltl.ctest.difficulty.features.util;

import java.util.HashSet;
import java.util.Set;

import de.tudarmstadt.ukp.dkpro.core.ngrams.util.CharacterNGramStringIterable;

/**
 * This term similarity measure implements the XDICE measure, following the description in Inkpen et
 * al 2005:
 * "XDICE (Brew & McKelvie 96) is defined in the same way as DICE, but employs “extended bigrams”, which are trigrams without the middle letter."
 * In the original source, the bigrams are actually the union of the standard bigrams and the
 * extended bigrams. The description in Inkpen seems to indicate, that they used *only* the extended
 * bigrams. I did not get any reaction from any of the authors. Here the second interpretation is
 * implemented to make it comparable to previous work.
 */
public class XDiceMeasure
    extends DiceMeasure
{
    @Override
    public double getSimilarity(String arg1, String arg2)
        throws dkpro.similarity.algorithms.api.SimilarityException
    {
        Set<String> bigrams1 = getExtendedBigrams(arg1);
        Set<String> bigrams2 = getExtendedBigrams(arg2);
        
        return super.calculateSimilarity(bigrams1, bigrams2);
    }

    private Set<String> getExtendedBigrams(String word)
    {
        Set<String> xBigrams = new HashSet<String>();

        for (String trigram : new CharacterNGramStringIterable(word, 3, 3)) {
            String bigram = "" + trigram.charAt(0) + trigram.charAt(2);
            xBigrams.add(bigram);
        }
        
        return xBigrams;
    }
}