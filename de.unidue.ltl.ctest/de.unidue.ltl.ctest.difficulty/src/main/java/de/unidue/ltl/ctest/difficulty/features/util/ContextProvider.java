package de.unidue.ltl.ctest.difficulty.features.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

public class ContextProvider
{

    public final static String BOS = "<S>";
    public final static String EOS = "</S>";

    public static <T extends Annotation> List<T> getLeftContext(JCas jcas, Class<T> aType,
            AnnotationFS targetAnnotation, int size)
    {
        List<T> annotationsInSentence = getAnnotationsInSentence(jcas, aType, targetAnnotation);
        int targetAnnotationPosition = getTargetPosition(annotationsInSentence, targetAnnotation);

        int startPosition = targetAnnotationPosition - size;

        if (startPosition < 0) {
            startPosition = 0;
        }

        return annotationsInSentence.subList(startPosition, targetAnnotationPosition);
    }

    public static <T extends Annotation> List<T> getRightContext(JCas jcas, Class<T> aType,
            AnnotationFS targetAnnotation, int size)
    {
        List<T> annotationsInSentence = getAnnotationsInSentence(jcas, aType, targetAnnotation);
        int targetAnnotationPosition = getTargetPosition(annotationsInSentence, targetAnnotation);

        int endPosition = targetAnnotationPosition + 1 + size;

        if (endPosition > annotationsInSentence.size()) {
            endPosition = annotationsInSentence.size();
        }

        return annotationsInSentence.subList(targetAnnotationPosition + 1, endPosition);
    }

    public static <T extends Annotation> String getLeftContextString(JCas jcas, Class<T> aType,
            AnnotationFS targetAnnotation, int size)
    {

        List<String> leftStrings = new ArrayList<String>();
        for (T left : getLeftContext(jcas, aType, targetAnnotation, size)) {
            leftStrings.add(left.getCoveredText());
        }

        // if the context is smaller than requested, add begin of sentence markers
        for (int i = 0; i < size - leftStrings.size(); i++) {
            leftStrings.add(0, BOS);
        }

        return StringUtils.join(leftStrings, " ");
    }

    public static <T extends Annotation> String getRightContextString(JCas jcas, Class<T> aType,
            AnnotationFS targetAnnotation, int size)
    {

        List<String> rightStrings = new ArrayList<String>();
        for (T right : getRightContext(jcas, aType, targetAnnotation, size)) {
            rightStrings.add(right.getCoveredText());
        }

        // if the context is smaller than requested, add end of sentence markers
        for (int i = 0; i < size - rightStrings.size(); i++) {
            rightStrings.add(0, EOS);
        }

        return StringUtils.join(rightStrings, " ");
    }

    private static <T extends Annotation> List<T> getAnnotationsInSentence(JCas jcas,
            Class<T> aType, AnnotationFS targetAnnotation)
    {
        Sentence coveringSentence = JCasUtil
                .selectCovering(jcas, Sentence.class, targetAnnotation.getBegin(),
                        targetAnnotation.getEnd()).iterator().next();
        return JCasUtil.selectCovered(jcas, aType, coveringSentence);
    }

    private static int getTargetPosition(List<? extends Annotation> annotationsInSentence,
            AnnotationFS targetAnnotation)
    {
        int targetAnnotationPosition = 0;
        int i = 0;
        for (Annotation anno : annotationsInSentence) {
            if (anno.getBegin() == targetAnnotation.getBegin()
                    && anno.getEnd() == targetAnnotation.getEnd()) {
                targetAnnotationPosition = i;
            }
            i++;
        }
        return targetAnnotationPosition;
    }
}