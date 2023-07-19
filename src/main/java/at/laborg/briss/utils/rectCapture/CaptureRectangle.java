package at.laborg.briss.utils.rectcapture;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Collections;

import com.itextpdf.text.Rectangle;

import at.laborg.briss.utils.RectangleInfo;

public class CaptureRectangle {
	Boolean computedRects = Boolean.FALSE;
	private Map<Integer, Set<List<Float>>> pageRectangles = new HashMap<>();
	private Map<Integer, List<Float>> biggestPageRects = new HashMap<>();
	private Map<List<Float>, Set<Integer>> uniqueBiggestRects = new HashMap<>();

	public void storePageRectangle(Integer page, Rectangle calculatedRectangle) {
		if (Boolean.TRUE.equals(computedRects)) {
			throw new IllegalStateException("Largest already computed; object is now frozen.");
		}
		List<Float> rectangleFloats = RectangleInfo.rectangleToList(calculatedRectangle);
		if (!this.pageRectangles.containsKey(page)) {
			this.pageRectangles.put(page, new HashSet<>());
		}
		Set<List<Float>> addToThisSet = this.pageRectangles.get(page);
		addToThisSet.add(rectangleFloats);
	}

	public void computeBiggestRects() {
		if (pageRectangles.size() == 0) {
			throw new IllegalStateException("No rectangles stored in the collector.");
		}
		for (Entry<Integer, Set<List<Float>>> pageRectSet : this.pageRectangles.entrySet()) {
			Integer pageNumber = pageRectSet.getKey();
			Set<List<Float>> allPageRects = pageRectSet.getValue();

			this.biggestPageRects.put(pageNumber, RectangleInfo.findBiggestPdfRectangle(allPageRects));
		}
		computeUniqueBiggestRects();

	}

	private void computeUniqueBiggestRects() {
		if (Boolean.TRUE.equals(computedRects)) {
			return;
		}

		for (Entry<Integer, List<Float>> biggestRectPerPage : biggestPageRects.entrySet()) {
			Integer pageNumber = biggestRectPerPage.getKey();
			List<Float> biggestRect = biggestRectPerPage.getValue();

			if (!uniqueBiggestRects.containsKey(biggestRect)) {
				uniqueBiggestRects.put(biggestRect, new HashSet<>());
			}
			Set<Integer> addToThisPageList = this.uniqueBiggestRects.get(biggestRect);
			addToThisPageList.add(pageNumber);
		}
	}

	public Map<Integer, List<Float>> getBiggestPageRects() {
		computeBiggestRects();
		return Collections.unmodifiableMap(biggestPageRects);
	}

	public Map<List<Float>, List<Integer>> getUniqueBiggestRects() {
		computeBiggestRects();
		Map<List<Float>, List<Integer>> uniqueBiggestRectsSorted = new HashMap<>();
		uniqueBiggestRects.forEach((rectangle, setOfPages) -> uniqueBiggestRectsSorted.put(rectangle,
				setOfPages.stream().sorted().collect(Collectors.toList())));
		return Collections.unmodifiableMap(uniqueBiggestRectsSorted);
	}

	public void debugPrintOutMappings() {
		computeBiggestRects();
		System.out.println("\n\tPrinting pageRectangles...\n");
		System.out.println(pageRectangles);
		System.out.println("\n\tPrinting biggestPageRect...\n");
		System.out.println(getBiggestPageRects());
		System.out.println("\n\tPrinting uniqueBiggestRects...\n");
		System.out.println(getUniqueBiggestRects());
	}

}