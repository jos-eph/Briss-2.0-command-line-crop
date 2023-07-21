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
	private Map<List<Float>, Set<PageEvenOddEnum>> uniqueBiggestRectsEvennness = new HashMap<>();

	public Map<List<Float>, Set<PageEvenOddEnum>> getUniqueBiggestRectsEvennness() {
		return uniqueBiggestRectsEvennness;
	}

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

	protected Boolean computeBiggestRects() {
		if (pageRectangles.size() == 0) {
			throw new IllegalStateException("No rectangles stored in the collector.");
		}
		if (Boolean.TRUE.equals(computedRects)) {
			System.out.println("Rects already computed; returning existing computation");
			return Boolean.FALSE;
		}
		for (Entry<Integer, Set<List<Float>>> pageRectSet : this.pageRectangles.entrySet()) {
			Integer pageNumber = pageRectSet.getKey();
			Set<List<Float>> allPageRects = pageRectSet.getValue();

			this.biggestPageRects.put(pageNumber, RectangleInfo.findBiggestPdfRectangle(allPageRects));
		}
		computeUniqueBiggestRects();

		computedRects = Boolean.TRUE;

		return Boolean.TRUE;
	}

	private void computeUniqueBiggestRects() {

		for (Entry<Integer, List<Float>> biggestRectPerPage : biggestPageRects.entrySet()) {
			Integer pageNumber = biggestRectPerPage.getKey();
			List<Float> biggestRect = biggestRectPerPage.getValue();

			uniqueBiggestRects.computeIfAbsent(biggestRect, (absentRect) -> new HashSet<>());
			Set<Integer> addToThisPageSet = this.uniqueBiggestRects.get(biggestRect);
			addToThisPageSet.add(pageNumber);

			uniqueBiggestRectsEvennness.computeIfAbsent(biggestRect, (absentRect) -> new HashSet<>());
			uniqueBiggestRectsEvennness.get(biggestRect)
					.add(pageNumber % 2 == 0 ? PageEvenOddEnum.EVEN : PageEvenOddEnum.ODD);
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