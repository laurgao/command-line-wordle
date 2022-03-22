// This is a class for utility methods/variables shared between classes. These
// utilities are not directly related to Wordle but are common across
// console-based games.

import java.io.IOException;

public class Utils {
    @FunctionalInterface
    interface VoidFunction {
        // Dummy interface for typing of functions with no inputs and no outputs
        void run();
    }

    // Text colors
    public static final String RESET = "\033[0m"; // Text reset (clears all styles that have been applied)
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[1;92m";
    public static final String ORANGE = "\033[1;38;2;255;165;0m";
    public static final String WHITE = "\033[1;97m";

    public static void clearScreen() {
        // This method clears the window command prompt. From
        // https://stackoverflow.com/a/33379766
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getNumDigits(int n) {
        n = Math.abs(n);
        if (n == 0)
            return 1;
        int i = 0;
        while (n / (int) Math.pow(10, i) > 0) {
            i++;
        }
        return i;
    }

    public static void prettyPrint(String str, boolean lineBreak) {
        char[] chars = str.toCharArray();
        for (char character : chars) {
            System.out.print(character);
            sleep(20);
        }
        if (lineBreak)
            System.out.println();
    }

    public static void prettyPrint(String str) {
        // By default, this method adds a line break.
        prettyPrint(str, true);
    }

    public static void sleep(long millis) {
        // Wrapper around `Thread.sleep` that catches exceptions.
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}