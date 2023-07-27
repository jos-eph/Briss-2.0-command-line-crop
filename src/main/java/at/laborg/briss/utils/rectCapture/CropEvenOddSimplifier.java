package at.laborg.briss.utils.rectcapture;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import java.lang.Math;

public class CropEvenOddSimplifier {
	private List<Float> rectangleForEvens = Collections.emptyList();
	private List<Float> rectangleForOdds = Collections.emptyList();
	private Set<Integer> excludes = new HashSet<>();

	Float MAXIMUM_ERROR = 3.0F;

	public List<Float> getRectangleForEvens() {
		return Collections.unmodifiableList(rectangleForEvens);
	}

	public List<Float> getRectangleForOdds() {
		return Collections.unmodifiableList(rectangleForOdds);
	}

	public List<Integer> getExcludes() {
		return excludes.stream().sorted().toList();
	}

	public Boolean approximatelySameRects() {
		Float error = 0.0F;

		for (int i = 0; i < 4; i++) {
			Float evenFloat = rectangleForEvens.get(i);
			Float oddFloat = rectangleForOdds.get(i);

			error = error + Math.abs(evenFloat - oddFloat);
		}

		return (MAXIMUM_ERROR <= error);
	}

	public CropEvenOddSimplifier(Map<List<Float>, ? extends Collection<Integer>> uniqueBiggestRects,
			Map<List<Float>, ? extends Collection<PageEvenOddEnum>> uniqueBiggestRectsEvennness) {

		if (uniqueBiggestRects == null || uniqueBiggestRectsEvennness == null || uniqueBiggestRects.isEmpty()
				|| uniqueBiggestRectsEvennness.isEmpty()) {
			throw new IllegalStateException("Passed null for calculation");
		}

		Integer biggestEvenSize = 0;
		Integer biggestOddSize = 0;

		for (Entry<List<Float>, ? extends Collection<Integer>> largestRectangleAndPages : uniqueBiggestRects
				.entrySet()) {
			List<Float> largestRectangle = largestRectangleAndPages.getKey();
			Collection<Integer> pages = largestRectangleAndPages.getValue();
			Collection<PageEvenOddEnum> evenness = uniqueBiggestRectsEvennness.get(largestRectangle);

			if (evenness.size() == 2) {
				excludes.addAll(pages);
			} else {
				Boolean even = (evenness.stream().findFirst().get() == PageEvenOddEnum.EVEN);
				if (Boolean.TRUE.equals(even)) {
					if (pages.size() > biggestEvenSize) {
						if (biggestEvenSize != 0) {
							Collection<Integer> evenPagesToExclude = uniqueBiggestRects.get(rectangleForEvens);
							excludes.addAll(evenPagesToExclude);
						}
						biggestEvenSize = pages.size();
						rectangleForEvens = largestRectangle;
					} else {
						excludes.addAll(pages);
					}
				} else if (Boolean.FALSE.equals(even)) {
					if (pages.size() > biggestOddSize) {
						if (biggestOddSize != 0) {
							Collection<Integer> oddPagesToExclude = uniqueBiggestRects.get(rectangleForOdds);
							excludes.addAll(oddPagesToExclude);
						}
						biggestOddSize = pages.size();
						this.rectangleForOdds = largestRectangle;
					} else {
						excludes.addAll(pages);
					}
				}

			}
		}
	}

}
