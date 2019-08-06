import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class StringReverser {
	public static void main(String[] args) {
		String myString = "This is dummy string";
		System.out.println("Original: " + myString);
		System.out.println("Reversed: " + reverseSentence(myString));
	}

	private static String reverseSentence(String sentence) {
		return Optional.ofNullable(sentence).map(s -> {
			String[] strArray = s.split(" ");
			return Arrays.stream(strArray).map(StringReverser::reverse).collect(Collectors.joining(" "));
		}).orElse(null);
	}

	private static String reverse(String word) {
		return Optional.ofNullable(word).map(w -> {
			int length = w.length() - 1;
			char[] chars = w.toCharArray();
			char[] revWord = new char[length + 1];
			for (int i = 0; i < w.length(); i++) {
				revWord[i] = chars[length--];
			}
			return String.valueOf(revWord);
		}).orElse(null);
	}
}
