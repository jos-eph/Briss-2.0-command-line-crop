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

		String excludeString = excludes.isEmpty() ? "" : String.format("--exclude-pages %s", toCliList(excludes));

		System.out
				.println("\t\t\t\\/ Run a command similar to the below to approximate this crop: \n\t\t\\/\n\t\\/\n\n");
		String commandLine = "\njava -jar ./build/libs/briss-command-crop.jar -s %s -d $HOME/Desktop/testcrop.pdf --odd-rects %s --even-rects %s %s\n";

		return String.format(commandLine, sourceFilePath, toCliList(oddRects), toCliList(evenRects), excludeString);

	}
}
