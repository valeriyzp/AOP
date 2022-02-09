package Lab4;

public class Algorithms {
    public static String rightPadding(String text, int length) {
        return String.format("%" + length + "." + length + "s", text);
    }

    public static String leftPadding(String text, int length) {
        return String.format("%-" + length + "." + length + "s", text);
    }

    public static int portForSocket = 9099;
}
