package de.unidue.ltl.ctest.difficulty.test.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.tudarmstadt.ukp.dkpro.core.ngrams.util.CharacterNGramStringIterable;
import edu.berkeley.nlp.lm.io.MakeKneserNeyArpaFromText;
import edu.berkeley.nlp.lm.io.MakeLmBinaryFromArpa;

public class LanguageModelUtils {

    public static void trainModels(String vocfile, String lmfile, String binaryfile)
    {
        // make sure that vocfile is in ngram format!

        MakeKneserNeyArpaFromText.main(new String[] { "5", lmfile, vocfile });
        MakeLmBinaryFromArpa.main(new String[] { lmfile, binaryfile });

        // EXAMPLES
        // String basic2 = new DkproContext().getWorkspace() + "/basicEnglish_2grm.binary";
        // String web1t2 = new DkproContext().getWorkspace() + "/web1t_2grm.binary";
        //
        // NgramLanguageModel lmbasic2 = LmReaders.readLmBinary(binaryfile);
        // System.out
        // .println(lmbasic2.scoreSentence(Arrays.asList("#h ha ap py y$".trim().split(" "))));
        // System.out.println(lmbasic2.scoreSentence(Arrays.asList("#a aw wk kw wa a$".trim().split(
        // " "))));
        // NgramLanguageModel lmweb1t2 = LmReaders.readLmBinary(web1t2);
        //
        // String infile = "/home/likewise-open/UKP/beinborn/ctests/commonSpellingErrors.2grm";
        // String outfile =
        // "/home/likewise-open/UKP/beinborn/ctests/commonSpellingErrors_LMcomparison2grm.csv";
        // BufferedReader br = new BufferedReader(new FileReader(infile));
        // BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
        // String line = "";
        // while ((line = br.readLine()) != null) {
        // float basic = lmbasic2.scoreSentence(Arrays.asList(line.trim().split(" ")));
        // float web1t = lmweb1t2.scoreSentence(Arrays.asList(line.trim().split(" ")));
        // bw.write(line.trim() + "\t" + basic + "\t" + web1t + "\n");
        //
        // }
        //
        // bw.close();
        // }
    }
    
    public static void makeNgramFormat(String infile, String outfile, int ngramsize)
        throws IOException
    {

    	StringBuilder sb = new StringBuilder();
    	for (String line : FileUtils.readLines(new File(infile))) {
            line = line.toLowerCase();
            line = "#" + line + "$";
            for (String charNgram : new CharacterNGramStringIterable(line, ngramsize, ngramsize)) {
                sb.append(charNgram + " ");
            }
            sb.append("\n");
        }
    	FileUtils.write(new File(outfile), sb.toString());
    }
}
