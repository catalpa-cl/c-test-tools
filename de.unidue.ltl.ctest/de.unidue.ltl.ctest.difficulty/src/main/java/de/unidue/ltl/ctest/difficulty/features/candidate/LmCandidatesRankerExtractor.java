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
package de.unidue.ltl.ctest.difficulty.features.candidate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unidue.ltl.ctest.difficulty.features.util.CTestFeaturesUtil;
import de.unidue.ltl.ctest.difficulty.types.GapCandidate;
import de.unidue.ltl.ctest.type.Gap;
import edu.berkeley.nlp.lm.NgramLanguageModel;
import edu.berkeley.nlp.lm.io.LmReaders;

public class LmCandidatesRankerExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {
	public static final String PARAM_LMFILE = "BinaryLanguageModel";
	@ConfigurationParameter(name = PARAM_LMFILE, mandatory = false)
	protected static String lmFile;

	public static final String PARAM_SENTENCESCORES_DIR = "DirectoryWithSentenceScores";
	@ConfigurationParameter(name = PARAM_SENTENCESCORES_DIR, mandatory = false)
	protected static String sentenceScoresDir;

	public static final String LM_RANK = "LmRankOfSolution";
	private static BufferedReader br;

	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) throws TextClassificationException {
		Set<Feature> featList = new HashSet<Feature>();
		Gap gap = JCasUtil.selectCovered(Gap.class, target).get(0);
		int rank = getLmRank(gap, jcas, lmFile);

		featList.add(new Feature(LM_RANK, rank));

		return featList;
	}

	private int getLmRank(Gap gap, JCas jcas, String lmfile) throws TextClassificationException {
		String[] solutions = gap.getSolutions().toArray();
		Sentence sentence = JCasUtil.selectCovering(Sentence.class, gap).get(0);
		List<GapCandidate> candidates = JCasUtil.selectCovering(GapCandidate.class, gap);

		// solution might not be part of the candidate list because it was not in the
		// vocabulary,
		// add it (or them if multiple solutions are possible)
		for (String solution : solutions) {
			GapCandidate candidate = new GapCandidate(jcas, gap.getBegin(), gap.getEnd());
			candidate.setCandidateWord(solution);
			candidates.add(candidate);
		}
		// this is the preferred version, store the sentence scores to a file in advance
		// and read
		// them
		if (new File(sentenceScoresDir).isDirectory()) {
			try {
				return getLmRankFromFile(jcas, gap, sentence);
			} catch (IOException e) {
				throw new TextClassificationException(e);
			}
		}
		// scoring the sentences on the fly is much slower
		else if (new File(lmFile).isFile()) {
			return getLmRankDynamic(jcas, candidates, gap, sentence);
		} else {
			throw new TextClassificationException(
					"You need to specify either a binary language model or a directory with sentence scores. ");
		}
	}

	private int getLmRankDynamic(JCas jcas, List<GapCandidate> candidates, Gap gap, Sentence sentence) {
		TreeSet<GapCandidate> lmCandidates = new TreeSet<GapCandidate>();
		for (GapCandidate cand : candidates) {
			GapCandidate lmCandidate = new GapCandidate(jcas, gap.getBegin(), gap.getEnd());
			double score = getLmScore(cand.getCandidateWord(), gap, sentence, lmFile);
			lmCandidate.setCandidateWord(cand.getCandidateWord());
			lmCandidate.setSuitability(score);
			lmCandidates.add(lmCandidate);
		}
		return CTestFeaturesUtil.getSolutionRank(gap.getSolutions().toArray(), lmCandidates);
	}

	private double getLmScore(String candidateWord, Gap gap, Sentence sentence, String lmFile2) {
		NgramLanguageModel<String> lm = LmReaders.readLmBinary(lmFile);
		StringBuffer candidateSentence = new StringBuffer();
		for (Token t : JCasUtil.selectCovered(Token.class, sentence)) {
			if (t.getBegin() == gap.getBegin()) {
				candidateSentence.append(candidateWord);

			} else {
				candidateSentence.append(t.getCoveredText());
			}
			candidateSentence.append(" ");
		}
		return lm.scoreSentence(Arrays.asList(candidateSentence.toString().trim().split(" ")));
	}

	private int getLmRankFromFile(JCas jcas, Gap gap, Sentence sentence) throws IOException {
		String textname = DocumentMetaData.get(jcas).getDocumentId().split(".txt")[0];
		if (!textname.endsWith(".txt")) {
			textname += ".txt";
		}

		// get ids of covering sentence and covering token
		Token token = JCasUtil.selectCovered(Token.class, gap).get(0);
		int sentenceId = getSentenceId(jcas, sentence);
		int tokenId = getTokenId(sentence, token);

		int rank = JCasUtil.selectCovering(GapCandidate.class, gap).size();

		setReader(jcas.getDocumentLanguage(), textname, sentenceId);
		rank = getRank(tokenId, gap.getSolutions().toArray());
		return rank;
	}

	private int getTokenId(Sentence sent, Token gapToken) {
		int tokenId = 0;
		for (Token token : JCasUtil.selectCovered(Token.class, sent)) {
			if (token.equals(gapToken)) {
				break;
			}
			tokenId++;
		}
		return tokenId;
	}

	private int getSentenceId(JCas jcas, Sentence sentence) {
		int sentenceId = 0;
		for (Sentence sent : JCasUtil.select(jcas, Sentence.class)) {
			if (sent.equals(sentence)) {
				break;
			}
			sentenceId++;
		}
		return sentenceId;
	}

	private void setReader(String language, String textname, int sentenceId) throws FileNotFoundException {
		// cloze has only one gap per text

		if (sentenceScoresDir.contains("cloze")) {
			br = new BufferedReader(new FileReader(sentenceScoresDir + textname));
		} else {
			br = new BufferedReader(new FileReader(sentenceScoresDir + textname + "_" + sentenceId + ".txt"));
		}
	}

	private int getRank(int tokenId, String[] solutions) throws IOException

	{
		String line = "";
		int rank = 1;

		while ((line = br.readLine()) != null) {
			String[] elems = line.split("\t");
			String[] linetokens = elems[0].split(" ");

			String chosenCandidate = linetokens[tokenId];
			if (Arrays.asList(solutions).contains(chosenCandidate)) {
				return rank;
			}
			rank++;

		}
		return rank;
	}
}
