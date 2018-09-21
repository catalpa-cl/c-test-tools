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

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.ADJC;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.ADVC;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.NC;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.PC;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.VC;
import de.unidue.ltl.ctest.difficulty.annotations.CTest;
import de.unidue.ltl.ctest.difficulty.annotations.Cloze;
import de.unidue.ltl.ctest.difficulty.annotations.XTest;

/**
 * Extracts the phrase type that contains the gap.
 *
 */
@CTest
@XTest
@Cloze
public class PhraseTypeExtractor
    extends FeatureExtractorResource_ImplBase
    implements FeatureExtractor
{
    public static final String FN_IS_NC = "GapIsNC";
    public static final String FN_IS_VC = "GapIsVC";
    public static final String FN_IS_ADJC = "GapIsADJC";
    public static final String FN_IS_ADVC = "GapIsADVC";
    public static final String FN_IS_SBAR = "GapIsSBar";
    public static final String FN_IS_PC = "GapIsPC";

    public Set<Feature> extract(JCas jcas, TextClassificationTarget target)
    {

        boolean nc = false;
        boolean vc = false;
        boolean adjc = false;
        boolean advc = false;
        boolean sbar = false;
        boolean pc = false;

        Chunk c = JCasUtil.selectCovering(jcas, Chunk.class, target).get(0);

        Set<Feature> featList = new HashSet<Feature>();

        if (c instanceof NC) {
            nc = true;
        }
        else if (c instanceof VC) {
            vc = true;
        }
        else if (c instanceof PC) {
            pc = true;
        }
        else if (c instanceof ADJC) {
            adjc = true;
        }
        else if (c instanceof ADVC) {
            advc = true;
        }

        // SBARS has chunkType 0, same as punctuation
        // we do not want to collect punctuation chunks
        else if (c.getChunkValue().startsWith("SBAR")) {
            sbar = true;
        }
        // phrase type of gap
        featList.add(new Feature(FN_IS_NC, nc));
        featList.add(new Feature(FN_IS_VC, vc));
        featList.add(new Feature(FN_IS_ADJC, adjc));
        featList.add(new Feature(FN_IS_ADVC, advc));
        featList.add(new Feature(FN_IS_PC, pc));
        featList.add(new Feature(FN_IS_SBAR, sbar));

        return featList;
    }
}