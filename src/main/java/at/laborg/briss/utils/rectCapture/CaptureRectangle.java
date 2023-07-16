package at.laborg.briss.utils.rectCapture;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.itextpdf.text.Rectangle;

import at.laborg.briss.utils.RectangleInfo;

public class CaptureRectangle {
	private Map<Integer, Set<List<Float>>> pageRectangles = new HashMap<>();
	private Map<Integer, List<Float>> biggestPageRect = new HashMap<>();
	private Map<List<Float>, Set<Integer>> uniqueBiggestRects = new HashMap<>();

	public void storePageRectangle(Integer page, Rectangle calculatedRectangle) {
		List<Float> rectangleFloats = RectangleInfo.rectangleToList(calculatedRectangle);
		if (!this.pageRectangles.containsKey(page)) {
			this.pageRectangles.put(page, new HashSet<>());
		}
		Set<List<Float>> addToThisSet = this.pageRectangles.get(page);
		addToThisSet.add(rectangleFloats);
	}

	public void computeBiggestRects() {
		for (Entry<Integer, Set<List<Float>>> pageRectSet : this.pageRectangles.entrySet()) {
			Integer pageNumber = pageRectSet.getKey();
			Set<List<Float>> allPageRects = pageRectSet.getValue();

			this.biggestPageRect.put(pageNumber, RectangleInfo.findBiggestPdfRectangle(allPageRects));
		}
		computeUniqueBiggestRects();

	}

	private void computeUniqueBiggestRects() {
		if (!uniqueBiggestRects.isEmpty())
			throw new IllegalStateException("Biggest rects already computed");
		for (Entry<Integer, List<Float>> biggestRectPerPage : biggestPageRect.entrySet()) {
			Integer pageNumber = biggestRectPerPage.getKey();
			List<Float> biggestRect = biggestRectPerPage.getValue();

			if (!uniqueBiggestRects.containsKey(biggestRect)) {
				uniqueBiggestRects.put(biggestRect, new HashSet<>());
			}
			Set<Integer> addToThisPageList = this.uniqueBiggestRects.get(biggestRect);
			addToThisPageList.add(pageNumber);
		}
	}

	public void debugPrintOutMappings() {
		computeBiggestRects();
		System.out.println("\n\tPrinting pageRectangles...\n");
		System.out.println(pageRectangles);
		System.out.println("\n\tPrinting biggestPageRect...\n");
		System.out.println(biggestPageRect);
		System.out.println("\n\tPrinting uniqueBiggestRects...\n");
		System.out.println(uniqueBiggestRects);
	}

}