package de.unidue.ltl.ctest.difficulty.features.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_ADJ;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_ADV;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_NOUN;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_VERB;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class ReadabilityUtils

{
    public static boolean isAuxiliaryVerb(Token token, String documentLanguage)
        throws MissingResourceException
    {
        // following the list at http://en.wikipedia.org/wiki/Auxiliary_verb
        POS pos = token.getPos();
        if (documentLanguage.equals("en")) {
            String[] auxVerbs = new String[] { "be", "am", "are", "is", "was", "were", "being",
                    "been", "can", "could", "dare", "do", "does", "did", "have", "has", "had",
                    "having", "may", "might", "must", "need", "ought", "shall", "should", "will",
                    "would" };

            return ((pos instanceof POS_VERB) && Arrays.asList(auxVerbs).contains(token.getCoveredText()));
        }
        else {
            throw new MissingResourceException(
                    "List not yet available for the requested language. Do you want to add it?",
                    "auxiliary verbs", documentLanguage);
        }
    }

    public static boolean isModalVerb(Token token, String documentLanguage)
        throws MissingResourceException
    {
        // following the list at http://en.wikipedia.org/wiki/English_modal_verbs
        POS pos = token.getPos();
        if (documentLanguage.equals("en")) {
            String[] modalVerbs = new String[] { "can", "could", "might", "may", "must", "should",
                    "will", "would", "shall" };
            return ((pos instanceof POS_VERB) && Arrays.asList(modalVerbs)
                    .contains(token.getCoveredText()));
        }
        else {
            throw new MissingResourceException(
                    "Information not yet available for the requested language. Do you want to add it?",
                    "modal verbs", documentLanguage);
        }
    }

    // This is the same check for words as in the readability measures but it also includes hyphens
    // when they occur in a word e.g. color-blind
    public static boolean isWord(String strWord)
    {
        for (int i = 0; i < strWord.length(); ++i) {
            char ch = strWord.charAt(i);
            if (!Character.isLetterOrDigit(ch)) {
                if (ch == '-') {
                    if (strWord.length() == 1) {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isWord(Token tok)
    {
        return isWord(tok.getCoveredText());
    }

    public static boolean isLexicalWord(Token token, String documentLanguage)
    {
        POS p = token.getPos();
        boolean rightPos = (p instanceof POS_NOUN || p instanceof POS_VERB || p instanceof POS_ADJ || p instanceof POS_ADV);
        if (documentLanguage.equals("en")) {
            return (rightPos && !isModalVerb(token, documentLanguage) && !isAuxiliaryVerb(token,
                    documentLanguage));
        }
        else {
            return rightPos;
        }
    }

    public static String[] getAdjectiveEndings(String documentLanguage)
        throws MissingResourceException
    {

        if (documentLanguage.equals("en")) {
            return new String[] { "able", "ible", "al", "ant", "ent", "ar", "ed", "ful", "ic",
                    "ical", "ive", "less", "ory", "ous" };

        }
        if (documentLanguage.equals("de")) {
            return new String[] { "abel", "al", "arm", "artig", "bar", "echt", "eigen", "ens",
                    "er", "fach", "fähig", "fern", "fest", "frei", "gemäß", "gerecht", "getreu",
                    "haft", "halber", "haltig", "ig", "isch", "leer", "lich", "los", "mal", "mals",
                    "maßen", "mäßig", "nah", "ös", "reich", "reif", "s", "sam", "schwer", "seits",
                    "tel", "trächtig", "tüchtig", "voll", "wärts", "weise", "wert", "würdig" };
        }
        if (documentLanguage.equals("fr")) {
            return new String[] { "ien", "ienne ", "éen", "éenne ", "in", "ine ", "ais", "aise ",
                    "ois", "oise ", "ain", "aine ", "an", "ane ", "ite ", "esque ", "ique",
                    "aïque ", "al", "ale ", "el", "elle ", "if", "ive ", "aire ", "eux", "euse ",
                    "ueux", "ueuse ", "ique ", "atique ", "ier", "ière ", "escent", "escente ",
                    "in", "ine" };
        }

        else {
            throw new MissingResourceException(
                    "Information not yet available for the requested language. Do you want to add it?",
                    "adjective endings", documentLanguage);
        }

    }

    public static String[] getHelpverbs(String documentLanguage)
        throws MissingResourceException
    {
        if (documentLanguage.equals("en")) {
            return new String[] { "am", "are", "is", "was", "were", "be", "been", "have", "has",
                    "had", "can", "could", "may", "will", "would", "might", "give", "gave" };
        }

        else if (documentLanguage.equals("de")) {
            return new String[] { "habe", "hast", "hat", "haben", "habt", "hatte", "hattest",
                    "hatten", "hattet", "gehabt", "bin", "war", "bist", "warst", "ist", "war",
                    "sind", "waren", "seid", "wart", "gewesen", "werde", "wurde", "wirst", "warst",
                    "wirt", "werden", "wurden", "werdet", "wurdet" };
        }
        else if (documentLanguage.equals("fr")) {
            return new String[] { "ai", "as", "a", "avons", "avez", "ont", "avais", "avait",
                    "avions", "aviez", "avaient", "aurai", "auras", "aura", "aurons", "aurez",
                    "auront", "aurais", "aurait", "aurions", "auriez", "auraient", "eusse",
                    "eusses", "eût", "eussions", "eussiez", "eussent", "eus", "eut", "eûmes",
                    "eûtes", "eurent", "suis", "es", "est", "sommes", "êtes", "sont", " étais",
                    "était", "étions", "étiez", "étaient", "fus", "fus", "fut", "fûmes", "fûtes",
                    "furent", "serai", "seras", "sera", "serons", "serez", "seront", "sois",
                    "soit", "soyons", "soyez", "soient", "fusse", "fusses", "fût", "fussions",
                    "fussiez", "fussent", "serais", "serait", "serions", "seriez", "seraient" };
        }
        else if (documentLanguage.equals("it")) {
            return new String[] { "ho", "sono", "hai", "sei", "ha", "abbiamo", "avete", "siete", "hanno", "sono"};
        }
        else if (documentLanguage.equals("es")) {
            return new String[] { "soy", "estoy", "eres", "estás", "es", "está", "somos", "estamos", "sois",
            		"estáis", "son", "están", "he", "has", "ha", "hemos", "habéis", "han"};
        }
        else {
            throw new MissingResourceException(
                    "Information not yet available for the requested language. Do you want to add it?",
                    "help verbs", documentLanguage);

        }
    }
    
 	
    // This code is copied from
    // src/main/java/de/tudarmstadt/ukp/similarity/algorithms/style/MTLDComparator.java
    // BETTER: make it public there
    public static double getMTLD(JCas jcas, boolean reverse, double mtldThreshold)
    {
        double factors = 0.0;

        DocumentAnnotation doc1 = new ArrayList<DocumentAnnotation>(JCasUtil.select(jcas,
                DocumentAnnotation.class)).get(0);
        List<Lemma> lemmas = new ArrayList<Lemma>(JCasUtil.selectCovered(jcas, Lemma.class, doc1));

        // Initialize tokens and types
        List<String> tokens = new ArrayList<String>();
        Set<String> types = new HashSet<String>();

        // Reverse lemmas if flag is set
        if (reverse) {
            Collections.reverse(lemmas);
        }

        for (int i = 0; i < lemmas.size(); i++) {
            Lemma lemma = lemmas.get(i);

            try {
                types.add(lemma.getValue().toLowerCase());
                tokens.add(lemma.getCoveredText().toLowerCase());
            }
            catch (NullPointerException e) {
                System.out.println("Couldn't add token: " + lemma.getCoveredText());
            }

            double ttr = types.size() / (double) tokens.size();

            if (ttr < mtldThreshold) {
                // Reset types and tokens
                tokens.clear();
                types.clear();

                // Increment full factor count
                factors++;
            }
            else if (i == lemmas.size() - 1) {
                // If the end of lemma list is reached, and no full factor is reached,
                // add a incomplete factor score

                double ifs = (1.0 - ttr) / (1.0 - mtldThreshold);
                factors += ifs;
            }
        }

        // mtld = number of tokens divided by factor count
        double mtld = (factors == 0) ? (0.0) : (new Integer(lemmas.size()).doubleValue() / factors);

        return mtld;
    }
}