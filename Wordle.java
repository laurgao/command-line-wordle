import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Color;

public class Wordle {

    final static class MyResult {
        private final ArrayList<String> first;
        private final boolean second;

        public MyResult(ArrayList<String> first, boolean second) {
            this.first = first;
            this.second = second;
        }

        public ArrayList<String> getFirst() {
            return first;
        }

        public boolean getSecond() {
            return second;
        }
    }

    // Text colors
    public static final String RESET = "\033[0m"; // Text Reset
    public static final String BLACK = "\033[0;30m"; // BLACK
    public static final String RED = "\033[0;31m"; // RED
    public static final String GREEN = "\033[1;92m"; // GREEN
    public static final String YELLOW = "\033[1;93m";// YELLOW
    public static final String WHITE = "\033[1;97m"; // WHITE

    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);

        // Infinite while loop such that the program runs until the user quits
        while (true) {
            int totalScore = 0;
            int level = printWelcomeScreen(in);
            clearScreen();
            if (level == 1) {
                totalScore = round1(in, totalScore);
                totalScore = round2(in, totalScore);
                totalScore = round3(in, totalScore);
            } else {
                if (level == 2) {
                    round2(in, totalScore);
                } else {
                    round3(in, totalScore);
                }
            }
        }
    }

    private static MyResult mainWordle(Scanner in, String answer, ArrayList<String> words_all)
            throws InterruptedException {
        // This method carries out the logic of the wordle game
        boolean successful = false;
        ArrayList<String> allGuesses = new ArrayList<String>(); // Store all guesses in a variable so we can print
                                                                // em all out later.
        while (true) {
            String guess = getGuess(in, answer, words_all, allGuesses);

            if (guess.equals("RQ")) {
                successful = false;
                break;
            }
            clearScreen();
            allGuesses.forEach((prevGuess) -> printColoredWord(prevGuess, answer, false));
            printColoredWord(guess, answer, true);
            allGuesses.add(guess);
            if (guess.equals(answer)) {
                System.out.println("Heck yea, you got the correct answer in " + allGuesses.size() + " guesses!");
                successful = true;
                break;
            }
        }

        return new MyResult(allGuesses, successful);
    }

    private static int round3(Scanner in, int totalScore) throws InterruptedException {
        // 6 letter words
        clearScreen();
        System.out.println("ROUND 3: 6 LETTER WORDLE");
        System.out.println();
        System.out.println("You have 10 tries to guess the word.");
        System.out.println("Enter your first guess:");

        ArrayList<String> words = getWords("./wordlist_6_all.txt");
        String answer = words.get((int) (Math.random() * words.size()));

        MyResult result = mainWordle(in, answer, words);
        ArrayList<String> allGuesses = result.getFirst();
        boolean successful = result.getSecond();

        int newPoints = (successful && allGuesses.size() <= 6) ? (int) Math.pow((7 - allGuesses.size()), 2) * 100 : 0;
        totalScore += newPoints;
        System.out.println("This earns you an additional " + newPoints + " points for a total score of " + totalScore);
        System.out.println();
        System.out.println("Press enter to continue...");
        in.nextLine();
        clearScreen();
        return totalScore;

    }

    private static int round2(Scanner in, int totalScore) throws InterruptedException {
        clearScreen();
        System.out.println("ROUND 2: 4 LETTER WORDLE");
        System.out.println("Enter your first guess:");

        ArrayList<String> words_possible_ansnwers = getWords("./wordlist_4_answers.txt");
        ArrayList<String> words_all = getWords("./wordlist_4_all.txt");
        String answer = words_possible_ansnwers.get((int) (Math.random() * words_possible_ansnwers.size()));

        MyResult result = mainWordle(in, answer, words_all);
        int newPoints = (result.getSecond() && result.getFirst().size() <= 6)
                ? (int) Math.pow((7 - result.getFirst().size()), 2) * 100
                : 0;
        totalScore += newPoints;
        System.out.println("This earns you an additional " + newPoints + " points for a total score of " + totalScore);
        System.out.println();
        System.out.println("Press enter to continue...");
        in.nextLine();
        clearScreen();
        return totalScore;

    }

    private static int round1(Scanner in, int totalScore) throws InterruptedException {
        clearScreen();
        System.out.println("ROUND 1: CLASSIC WORDLE");
        System.out.println("You will be given a 5-letter word and you must guess it within 6 tries.");
        System.out.println();
        System.out.println("Press enter to continue...");
        System.out.println("If you already know how to play Wordle, enter 'S' to skip the rules.");
        System.out.println();
        while (true) {
            String input = in.nextLine();
            if (input.toUpperCase().equals("S")) {
                break;
            } else {
                // Print wordle rules
                System.out.println("RULES:");
                System.out.println("Type a 5 letter word and press enter. Each letter will be highlighted either...");
                System.out.println(WHITE + "White: this letter does not exist in the word.");
                System.out.println(YELLOW + "Yellow: this letter exists in the word but is not in the right location.");
                System.out.println(GREEN + "Green: this letter exists in the word and is in the right location.");
                System.out.println(RESET);
                System.out.println("Press enter to continue...");
                in.nextLine();
                break;
            }
        }
        clearScreen();
        System.out.println(
                "You can type 'RQ' to rage quit the current level and skip to the next level at any time.");
        System.out.println("Enter your first guess:");
        System.out.println();

        ArrayList<String> words_possible_ansnwers = getWords("./wordlist_answers.txt");
        ArrayList<String> words_all = getWords("./wordlist_all.txt");
        String answer = words_possible_ansnwers.get((int) (Math.random() * words_possible_ansnwers.size()));

        MyResult result = mainWordle(in, answer, words_all);
        ArrayList<String> allGuesses = result.getFirst();
        boolean successful = result.getSecond();

        int newPoints = (successful && allGuesses.size() <= 6) ? (int) Math.pow((7 - allGuesses.size()), 2) * 100 : 0;
        totalScore += newPoints;
        System.out.println("This earns you an additional " + newPoints + " points for a total score of " + totalScore);
        System.out.println();
        System.out.println("Press enter to continue...");
        in.nextLine();
        clearScreen();
        return totalScore;
    }

    private static void printColoredWord(String guess, String answer, boolean showAnimation) {
        int wordLength = answer.length();
        for (int i = 0; i < wordLength; i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                System.out.print(GREEN + guess.charAt(i));
            } else if (answer.contains(guess.charAt(i) + "")) {
                System.out.print(YELLOW + guess.charAt(i));
            } else {
                System.out.print(WHITE + guess.charAt(i));
            }
            if (showAnimation) {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.print(RESET);
        System.out.println();
    }

    private static String getGuess(Scanner in, String answer, ArrayList<String> words, ArrayList<String> allGuesses)
            throws InterruptedException {
        int wordLength = answer.length();
        String guess;
        while (true) {
            guess = in.nextLine();
            guess = guess.toUpperCase();

            if (guess.toUpperCase().equals("RQ")) {
                System.out.println("You rage quit. The answer is: " + answer);
                return "RQ";
            }

            // Check that guess has correct number of characters
            if (guess.length() != wordLength) {
                clearScreen();
                System.out.println(RED + "Please enter a word of length " + wordLength + RESET);
                System.out.println();
                allGuesses.forEach((prevGuess) -> printColoredWord(prevGuess, answer, false));

                continue;
            }

            // Check that guess is a word
            if (!words.contains(guess)) {
                clearScreen();
                System.out.println(RED + guess + " is not a word." + RESET);
                System.out.println();
                allGuesses.forEach((prevGuess) -> printColoredWord(prevGuess, answer, false));
                continue;
            }

            break;
        }
        return guess;
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static int printWelcomeScreen(Scanner in) {
        clearScreen();
        System.out.println("********************************************************");
        System.out.println("WELCOME TO THE WORLD'S FIRST WORDLE GAME SHOW!");
        System.out.println("********************************************************");
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("S - Start :)");
        System.out.println("Q - Quit :(");
        System.out.println("C - Choose specific round");
        System.out.println();

        while (true) {
            String input = in.nextLine();
            if (input.toUpperCase().equals("S")) {
                return 1;
            } else if (input.toUpperCase().equals("Q")) {
                System.out.println("Bye bye!");
                System.exit(0);
            } else if (input.toUpperCase().equals("C")) {
                clearScreen();
                return roundsScreen(in);
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    private static int roundsScreen(Scanner in) {
        System.out.println("ROUNDS:");
        System.out.println("1 - Classic 5-letter Wordle");
        System.out.println("2 - 4-letter Wordle");
        System.out.println("3 - 6-letter Wordle");
        System.out.println();

        System.out.println(
                "By playing a specific level, you will not be able to accrue a total score and will not be on the leaderboard.");
        System.out.println("This is for practicing a specific level to get better.");
        System.out.println();
        System.out.println("Enter the number of the round you want to jump to.");
        System.out.println("Enter 'R' to return to the main menu.");
        System.out.println();
        String level = in.nextLine();
        while (true) {
            if (level.equals("1")) {
                return 1;
            } else if (level.equals("2")) {
                return 2;
            } else if (level.equals("3")) {
                return 3;
            } else if (level.toUpperCase().equals("R")) {
                printWelcomeScreen(in);
            } else {
                System.out.println("Invalid input. Please try again.");
                level = in.nextLine();
            }
        }
    }

    private static ArrayList<String> getWords(String fileName) {
        ArrayList<String> words = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(fileName));

            String s;
            while ((s = br.readLine()) != null) {
                // skip line breaks
                if (s.length() == 0) {
                    continue;
                }
                // skip comments
                if (s.charAt(0) == '/' && s.charAt(1) == '/') {
                    continue;
                }
                words.add(s.toUpperCase());
            }
            br.close();
        } catch (IOException ex) {
            System.out.println(RED +
                    "Error reading file '" + fileName
                    + "'. Check if the file exists in the same directory as the program." + RESET);
            System.exit(0);
        }
        return words;
    }
}
