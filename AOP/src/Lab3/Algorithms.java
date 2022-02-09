package Lab3;

public class Algorithms {
    public static String rightPadding(String text, int length) {
        return String.format("%" + length + "." + length + "s", text);
    }

    public static String leftPadding(String text, int length) {
        return String.format("%-" + length + "." + length + "s", text);
    }
}
