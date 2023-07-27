package at.laborg.briss.cli.utilities.models;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CropPreferences {
	private List<Float> evenRects;
	private List<Float> oddRects;
	private Set<Integer> excludedPages;

	public List<Float> getEvenRects() {
		return evenRects;
	}
	public void setEvenRects(List<Float> evenRects) {
		this.evenRects = Collections.unmodifiableList(evenRects);
	}

	public List<Float> getOddRects() {
		return oddRects;
	}
	public void setOddRects(List<Float> oddRects) {
		this.oddRects = Collections.unmodifiableList(oddRects);
	}

	public Set<Integer> getExcludedPages() {
		return excludedPages;
	}

	public void setExcludedPages(Collection<Integer> excludedPages) {
		this.excludedPages = excludedPages.stream().collect(Collectors.toSet());
	}

	public CropPreferences(List<Float> oddRects, List<Float> evenRects, Collection<Integer> excludedPages) {
		setOddRects(oddRects);
		setEvenRects(evenRects);
		setExcludedPages(excludedPages);
	}

}
