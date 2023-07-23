package at.laborg.briss.utils.rectcapture;

import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

public class CommandArgumentGenerator {
	private static String toCliList(Collection<? extends Object> objectCollection) {
		return objectCollection.stream().map(Object::toString).collect(Collectors.joining(","));
	}

	public static String getCommandArguments(String sourceFilePath, Collection<Float> oddRects,
			Collection<Float> evenRects, Collection<Integer> excludes) {

		String commandLine = "\njava -jar ./build/libs/briss-command-crop.jar -s %s -d /Users/joe/Desktop/testcrop.pdf --odd-rects %s --even-rects %s --exclude-pages %s\n";

		return String.format(commandLine, sourceFilePath, toCliList(oddRects), toCliList(evenRects),
				toCliList(excludes));

	}
}
