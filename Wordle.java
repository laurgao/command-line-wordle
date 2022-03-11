import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Wordle {
    // Reset
    public static final String RESET = "\033[0m"; // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;30m"; // BLACK
    public static final String RED = "\033[0;31m"; // RED
    public static final String GREEN = "\033[0;32m"; // GREEN
    public static final String YELLOW = "\033[0;33m"; // YELLOW
    public static final String BLUE = "\033[0;34m"; // BLUE
    public static final String PURPLE = "\033[0;35m"; // PURPLE
    public static final String CYAN = "\033[0;36m"; // CYAN
    public static final String WHITE = "\033[0;37m"; // WHITE

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m"; // BLACK
    public static final String RED_BOLD = "\033[1;31m"; // RED
    public static final String GREEN_BOLD = "\033[1;32m"; // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m"; // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m"; // CYAN
    public static final String WHITE_BOLD = "\033[1;37m"; // WHITE

    // Underline
    public static final String BLACK_UNDERLINED = "\033[4;30m"; // BLACK
    public static final String RED_UNDERLINED = "\033[4;31m"; // RED
    public static final String GREEN_UNDERLINED = "\033[4;32m"; // GREEN
    public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
    public static final String BLUE_UNDERLINED = "\033[4;34m"; // BLUE
    public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
    public static final String CYAN_UNDERLINED = "\033[4;36m"; // CYAN
    public static final String WHITE_UNDERLINED = "\033[4;37m"; // WHITE

    // Background
    public static final String BLACK_BACKGROUND = "\033[40m"; // BLACK
    public static final String RED_BACKGROUND = "\033[41m"; // RED
    public static final String GREEN_BACKGROUND = "\033[42m"; // GREEN
    public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String BLUE_BACKGROUND = "\033[44m"; // BLUE
    public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    public static final String CYAN_BACKGROUND = "\033[46m"; // CYAN
    public static final String WHITE_BACKGROUND = "\033[47m"; // WHITE

    // High Intensity
    public static final String BLACK_BRIGHT = "\033[0;90m"; // BLACK
    public static final String RED_BRIGHT = "\033[0;91m"; // RED
    public static final String GREEN_BRIGHT = "\033[0;92m"; // GREEN
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    public static final String BLUE_BRIGHT = "\033[0;94m"; // BLUE
    public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
    public static final String CYAN_BRIGHT = "\033[0;96m"; // CYAN
    public static final String WHITE_BRIGHT = "\033[0;97m"; // WHITE

    // Bold High Intensity
    public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
    public static final String RED_BOLD_BRIGHT = "\033[1;91m"; // RED
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m"; // BLUE
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m"; // CYAN
    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

    // High Intensity backgrounds
    public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
    public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
    public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
    public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
    public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE
    public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
    public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m"; // CYAN
    public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m"; // WHITE

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        printWelcomeScreen();
        ArrayList<String> words_possible_ansnwers = getWords("./wordlist_answers.txt");
        ArrayList<String> words_all = getWords("./wordlist_all.txt");
        ArrayList<String> allGuesses = new ArrayList<String>(); // Store all guesses in a variable so we can print em
                                                                // all out later.
        String answer = words_possible_ansnwers.get((int) (Math.random() * words_possible_ansnwers.size()));

        while (true) {
            String guess = getGuess(in, answer, words_all, allGuesses);

            allGuesses.add(guess);
            clearScreen();
            allGuesses.forEach((prevGuess) -> printColoredWord(prevGuess, answer));

            if (guess.equals(answer)) {
                break;
            }

        }
        in.close();
    }

    private static void printColoredWord(String guess, String answer) {
        int wordLength = answer.length();

        // 0 - Letter does not exist in word (grey)
        // 1 - Letter exists in word but in wrong location (yellow)
        // 2 - Letter exists in word and in correct location (green)
        int[] correctnessOfGuesses = new int[wordLength];
        for (int i = 0; i < wordLength; i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                correctnessOfGuesses[i] = 2;
            } else if (answer.contains(guess.charAt(i) + "")) {
                correctnessOfGuesses[i] = 1;
            }
        }
        for (int i = 0; i < wordLength; i++) {
            System.out.print((correctnessOfGuesses[i] == 0 ? RESET : (correctnessOfGuesses[i] == 1 ? YELLOW : GREEN))
                    + guess.charAt(i)
                    + RESET);
        }
        System.out.println();
    }

    private static String getGuess(Scanner in, String answer, ArrayList<String> words, ArrayList<String> allGuesses) {
        int wordLength = answer.length();
        String guess;
        while (true) {
            guess = in.nextLine();
            guess = guess.toUpperCase();

            // Check that guess has correct number of characters
            if (guess.length() != wordLength) {
                clearScreen();
                System.out.println(RED + "Please enter a word of length " + wordLength + RESET);
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                allGuesses.forEach((prevGuess) -> printColoredWord(prevGuess, answer));

                continue;
            }

            // Check that guess is a word
            if (!words.contains(guess)) {

                clearScreen();
                System.out.println(RED + guess + " is not a word." + RESET);
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                allGuesses.forEach((prevGuess) -> printColoredWord(prevGuess, answer));
                continue;
            }

            break;
        }
        return guess;
    }

    private static void clearScreen() {
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
    }

    private static void printWelcomeScreen() {
        System.out.println("********************************************************");
        System.out.println("Welcome to Wordle!");
        System.out.println("********************************************************");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        // System.out.println("S - Start game :)");
        // System.out.println("Q - Quit game :(");
        // System.out.println("R - Rules, for uncultured swines who don't know the rules
        // of wordle :(");
    }

    private static ArrayList<String> getWords(String fileName) {
        ArrayList<String> words = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(fileName));

            String s;
            while ((s = br.readLine()) != null) {
                // skip comments
                if (s.charAt(0) == '/' && s.charAt(1) == '/') {
                    continue;
                }
                words.add(s.toUpperCase());
            }
            br.close();
        } catch (Exception ex) {
            System.out.println(
                    "Error reading file '" + fileName
                            + "'. Check if the file exists in the same directory as the program.");
        }
        return words;
    }
}
