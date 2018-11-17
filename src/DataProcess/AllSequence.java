package DataProcess;

import java.io.IOException;
import ClassificationAsSimilarity.Feature2TraditionalMatrixTest;
import ClassificationAsSimilarity.Feature2TraditionalMatrix;
public class AllSequence {
	public static void main(String[] args) throws IOException
	{
		Preprocessing.main(args);
		WordList.main(args);
		Twitter2Feature.main(args);
		Twitter2FeatureTest.main(args);
		Feature2TraditionalMatrix.main(args);
		Feature2TraditionalMatrixTest.main(args);
	}
}
