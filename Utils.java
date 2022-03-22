import java.io.IOException;

public class Utils {
    // This is a class for utility methods/variables shared between classes. These
    // utilities are not directly related to Wordle but are common across
    // console-based games.

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
        // From https://stackoverflow.com/a/33379766
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}