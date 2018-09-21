package de.unidue.ltl.ctest.difficulty.features.util;

public class WordFilters

{

    public static String[] getAdjectiveEndings(String documentLanguage)
    {

        if (documentLanguage.equals("en")) {
            return new String[] { "able", "ible", "al", "ant", "ent", "ar", "ed", "ful", "ic",
                    "ical", "ive", "less", "ory", "ous" };

            // adjective endings in submitted version were less, only: "able", "ible", "al", "ful",
            // "ic", "ive", "less", "ous"
            // but full set returns better results

        }
        if (documentLanguage.equals("de")) {
            return new String[] { "abel", "al", "arm", "artig", "bar", "echt", "eigen", "ens",
                    "er", "fach", "fähig", "fern", "fest", "frei", "gemäß", "gerecht", "getreu",
                    "haft", "halber", "haltig", "ig", "isch", "leer", "lich", "los", "mal", "mals",
                    "maßen", "mäßig", "nah", "ös", "reich", "reif", "s", "sam", "schwer", "seits",
                    "tel", "trächtig", "tüchtig", "voll", "wärts", "weise", "wert", "würdig" };
        }
        if (documentLanguage.equals("fr")) {
            return new String[] { "ien", "ienne ", "éen", "éenne ", "in", "ine ", "ais", "aise ",
                    "ois", "oise ", "ain", "aine ", "an", "ane ", "ite ", "esque ", "ique",
                    "aïque ", "al", "ale ", "el", "elle ", "if", "ive ", "aire ", "eux", "euse ",
                    "ueux", "ueuse ", "ique ", "atique ", "ier", "ière ", "escent", "escente ",
                    "in", "ine" };
        }
        else {
            return new String[] {};
        }
    }

    public static String[] getHelpverbs(String documentLanguage)
    {
        if (documentLanguage.equals("en")) {
            return new String[] { "am", "are", "is", "was", "were", "be", "been", "have", "has",
                    "had", "can", "could", "may", "will", "would", "might", "give", "gave" };
        }

        if (documentLanguage.equals("de")) {
            return new String[] { "habe", "hast", "hat", "haben", "habt", "hatte", "hattest",
                    "hatten", "hattet", "gehabt", "bin", "war", "bist", "warst", "ist", "war",
                    "sind", "waren", "seid", "wart", "gewesen", "werde", "wurde", "wirst", "warst",
                    "wirt", "werden", "wurden", "werdet", "wurdet" };
        }
        if (documentLanguage.equals("fr")) {
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
        else {
            return new String[] {};
        }
    }
}