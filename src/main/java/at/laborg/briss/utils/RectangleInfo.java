package at.laborg.briss.utils;

import java.util.Collection;
import java.util.List;

import com.itextpdf.text.Rectangle;

public class RectangleInfo {

	public static List<Float> rectangleToList(Rectangle rectangle) {
		Float llx = rectangle.getLeft(); // want smallest
		Float lly = rectangle.getBottom();
		Float urx = rectangle.getRight(); // want biggest
		Float ury = rectangle.getTop();
		return List.of(llx, lly, urx, ury);
	}

	public static String rectangleToString(Rectangle rectangle) {
		return String.format("%s ", RectangleInfo.rectangleToList(rectangle).toString());
	}

	public static List<Float> findBiggestPdfRectangle(Collection<List<Float>> rectangles) {
		Float smallestLlx = Float.POSITIVE_INFINITY;
		Float smallestLly = Float.POSITIVE_INFINITY;
		Float biggestUrx = Float.NEGATIVE_INFINITY;
		Float biggestUry = Float.NEGATIVE_INFINITY;

		for (List<Float> rectangle : rectangles) {
			if (rectangle.size() != 4) {
				throw new IllegalArgumentException("PDF rectangles must be of length 4.");
			}

			Float thisLlx = rectangle.get(0);
			Float thisLly = rectangle.get(1);
			Float thisUrx = rectangle.get(2);
			Float thisUry = rectangle.get(3);

			if (thisLlx < smallestLlx)
				smallestLlx = thisLlx;
			if (thisLly < smallestLly)
				smallestLly = thisLly;
			if (thisUrx > biggestUrx)
				biggestUrx = thisUrx;
			if (thisUry > biggestUry)
				biggestUry = thisUry;
		}

		return List.of(smallestLlx, smallestLly, biggestUrx, biggestUry);
	}
}
