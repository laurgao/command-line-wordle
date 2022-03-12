import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Wordle {

    interface VoidFunction {
        void run();
    }

    final static class MyResult {
        // Dummy class to store the output of `mainWordle` method.
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
    public static final String ORANGE = "\033[1;38;2;255;165;0m"; // ORANGE
    public static final String WHITE = "\033[1;97m"; // WHITE

    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);

        // Infinite while loop such that the program runs until the user quits
        while (true) {
            int level = welcomeScreen(in);
            clearScreen();
            System.out.println(level);
            if (level == 0) {
                int totalScore = 0;
                totalScore = round1(in, totalScore);
                totalScore = round2(in, totalScore);
                totalScore = round3(in, totalScore);
                totalScore = round4(in, totalScore);

                // Save final score to leaderboard.
                clearScreen();
                int ranking = 1;
                System.out.println("Congrats on completing the Wordle game show!\n"
                        + "Your final score is: " + totalScore + " which gives you a ranking of #" + ranking + "!");
                System.out.println("Enter your name to save your score to the leaderboard: ");
                String name = in.nextLine();
                saveScore(name, totalScore);
                System.out.println();
                System.out.println("Thank you for playing. Press enter to return to the main menu.");
                in.nextLine();
            } else {
                if (level == 1) {
                    round1(in, 0);
                } else if (level == 2) {
                    round2(in, 0);
                } else if (level == 3) {
                    round3(in, 0);
                } else {
                    round4(in, 0);
                }
            }
        }
    }

    private static void saveScore(String name, int score) {
        try {
            FileWriter fw = new FileWriter("leaderboard.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println(name + " " + score);
            out.close();
        } catch (IOException e) {
            System.out.println("Error saving score to leaderboard.");
        }
    }

    private static MyResult mainWordle(Scanner in, final String answer, ArrayList<String> allWords)
            throws InterruptedException {
        // This method carries out the logic of the wordle game (the UI of the user
        // guessing.)
        boolean successful = false;
        ArrayList<String> allGuesses = new ArrayList<String>(); // Store all guesses in a variable so we can print
                                                                // em all out later.
        while (true) {
            String guess = getGuess(in, answer.length(), allWords,
                    () -> allGuesses.forEach((prevGuess) -> {
                        printColoredWord(prevGuess, answer, false);
                        System.out.println();
                    }));

            if (guess.equals("RQ")) {
                System.out.println("You rage quit. The answer is: " + answer);
                successful = false;
                break;
            }
            clearScreen();
            System.out.println();
            allGuesses.forEach((prevGuess) -> {
                printColoredWord(prevGuess, answer, false);
                System.out.println();
            });
            printColoredWord(guess, answer, true);
            System.out.println();
            allGuesses.add(guess);
            if (guess.equals(answer)) {
                System.out.println("Heck yea, you got the correct answer in " + allGuesses.size() + " guesses!");
                successful = true;
                break;
            }
        }

        return new MyResult(allGuesses, successful);
    }

    private static int round4(Scanner in, int totalScore) throws InterruptedException {
        // Double wordle
        System.out.println("ROUND 4: DOUBLE WORDLE");
        System.out.println();
        System.out.println("You have six tries to guess two 5-letter words.");
        System.out.println("Each guess will be used on both words simultaneously.");
        System.out.println();
        System.out.println("Enter your first guess:");

        final ArrayList<String> possibleAnswers = getWords("./wordlist_answers.txt");
        final ArrayList<String> allWords = getWords("./wordlist_all.txt");
        final String answer1 = possibleAnswers.get((int) (Math.random() * possibleAnswers.size()));
        final String answer2;
        String s;
        while (true) {
            if (!(s = possibleAnswers.get((int) (Math.random() * possibleAnswers.size())))
                    .equals(answer1)) {
                answer2 = s;
                break;
            }
        }
        ArrayList<String> allGuesses = new ArrayList<String>();

        boolean answer1Solved = false;
        boolean answer2Solved = false;

        while (true) {
            VoidFunction printCurrentScreen = () -> {
                clearScreen();
                allGuesses.forEach(prevGuess -> {
                    printColoredWord(prevGuess, answer1, false);
                    System.out.print("                   ");
                    printColoredWord(prevGuess, answer2, false);
                    System.out.println();
                });
            };
            String guess = getGuess(in, 5, allWords, () -> {
                clearScreen();
                allGuesses.forEach(prevGuess -> {
                    printColoredWord(prevGuess, answer1, false);
                    System.out.print("                   ");
                    printColoredWord(prevGuess, answer2, false);
                    System.out.println();
                });
            });
            if (guess.equals("RQ")) {
                System.out.println("You rage quit. The answers are: " + answer1 + " and " + answer2);
                break;
            }

            clearScreen();
            allGuesses.forEach(prevGuess -> {
                printColoredWord(prevGuess, answer1, false);
                System.out.print("                   ");
                printColoredWord(prevGuess, answer2, false);
                System.out.println();
            });
            printColoredWord(guess, answer1, true);
            System.out.print("                   ");
            printColoredWord(guess, answer2, true);
            System.out.println();
            allGuesses.add(guess);

            if (guess.equals(answer1))
                answer1Solved = true;
            if (guess.equals(answer2))
                answer2Solved = true;
            if (answer1Solved && answer2Solved)
                break;
        }

        int newPoints = (answer1Solved && allGuesses.size() <= 6) ? (int) Math.pow((7 - allGuesses.size()), 2) * 50
                : 0;
        newPoints += (answer2Solved && allGuesses.size() <= 6) ? (int) Math.pow((7 - allGuesses.size()), 2) * 50 : 0;
        newPoints += (answer1Solved && answer2Solved && allGuesses.size() <= 6)
                ? (int) Math.pow((7 - allGuesses.size()), 2) * 100
                : 0;
        totalScore += newPoints;
        System.out.println("This earns you an additional " + newPoints + " points for a total score of " + totalScore);
        System.out.println();
        System.out.println("Press enter to continue...");

        return totalScore;
    }

    private static int round3(Scanner in, int totalScore) throws InterruptedException {
        // 6 letter words
        clearScreen();
        System.out.println("ROUND 3: 6 LETTER WORDLE");
        System.out.println();
        System.out.println("You have 10 tries to guess the word.");
        System.out.println("Enter your first guess:");

        final ArrayList<String> words = getWords("./wordlist_6_all.txt");
        final String answer = words.get((int) (Math.random() * words.size()));

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

        final ArrayList<String> words_possible_ansnwers = getWords("./wordlist_4_answers.txt");
        final ArrayList<String> allWords = getWords("./wordlist_4_all.txt");
        final String answer = words_possible_ansnwers.get((int) (Math.random() * words_possible_ansnwers.size()));

        MyResult result = mainWordle(in, answer, allWords);
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
                System.out.println(ORANGE + "Orange: this letter exists in the word but is not in the right location.");
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

        final ArrayList<String> possibleAnswers = getWords("./wordlist_answers.txt");
        final ArrayList<String> allWords = getWords("./wordlist_all.txt");
        final String answer = possibleAnswers.get((int) (Math.random() * possibleAnswers.size()));

        MyResult result = mainWordle(in, answer, allWords);
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

    private static void printColoredWord(String guess, final String answer, boolean showAnimation) {
        // Note that this method does not print linebreak after the word.
        int wordLength = answer.length();
        for (int i = 0; i < wordLength; i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                System.out.print(GREEN + guess.charAt(i));
            } else if (answer.contains(guess.charAt(i) + "")) {
                System.out.print(ORANGE + guess.charAt(i));
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
    }

    private static String getGuess(Scanner in, int wordLength, ArrayList<String> words,
            VoidFunction recreateCurrentScreen)
            throws InterruptedException {
        String guess;
        while (true) {
            guess = in.nextLine().toUpperCase();

            if (guess.equals("RQ"))
                break;

            // Check that guess has correct number of characters
            if (guess.length() != wordLength) {
                clearScreen();
                System.out.println(RED + "Please enter a word of length " + wordLength + RESET);
                System.out.println();
                recreateCurrentScreen.run();

                continue;
            }

            // Check that guess is a word
            if (!words.contains(guess)) {
                clearScreen();
                System.out.println(RED + guess + " is not a word." + RESET);
                System.out.println();
                recreateCurrentScreen.run();
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

    private static int welcomeScreen(Scanner in) {
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
        System.out.println("L - View leaderboard");
        System.out.println();

        while (true) {
            String input = in.nextLine();
            if (input.toUpperCase().equals("S")) {
                return 0;
            } else if (input.toUpperCase().equals("Q")) {
                System.out.println("Bye bye!");
                System.exit(0);
            } else if (input.toUpperCase().equals("C")) {
                clearScreen();
                return roundsScreen(in);
            } else if (input.toUpperCase().equals("L")) {
                leaderboardScreen(in);
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    private static void leaderboardScreen(Scanner in) {
        // Read the leaderboard file
        ArrayList<String> leaderboardEntries = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader("./leaderboard.txt"));

            String s;
            while ((s = br.readLine()) != null) {
                leaderboardEntries.add(s);
            }
            br.close();
        } catch (IOException ex) {

        }

        // Print out the leaderboard
        clearScreen();
        System.out.println("********************************************************");
        System.out.println("LEADERBOARD");
        System.out.println();
        if (leaderboardEntries.size() == 0) {
            System.out.println("No one has beat the game yet. Do you want to be the first? ;)");
        } else {
            System.out.println("RANK\tNAME\tSCORE");
            for (int i = 0; i < leaderboardEntries.size(); i++) {
                int rank = i + 1;
                String name = leaderboardEntries.get(i).split(" ")[0];
                String score = leaderboardEntries.get(i).split(" ")[1];
                System.out.println(rank + "\t" + name + "\t" + score);
            }
        }
        System.out.println();
        System.out.println("********************************************************");
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Press 'R' to return to the main menu.");
        String input = in.nextLine();
        while (true) {
            if (input.toUpperCase().equals("R")) {
                welcomeScreen(in);
            } else {
                System.out.println("Invalid input. Please try again.");
                input = in.nextLine();
            }
        }
    }

    private static int roundsScreen(Scanner in) {
        System.out.println("ROUNDS:");
        System.out.println("1 - Classic 5-letter Wordle");
        System.out.println("2 - 4-letter Wordle");
        System.out.println("3 - 6-letter Wordle");
        System.out.println("4 - Double Wordle");
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
            if (level.length() == 1 && "1234".indexOf(level) > -1) {
                return Integer.parseInt(level);
            } else if (level.toUpperCase().equals("R")) {
                welcomeScreen(in);
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
                if (s.length() == 0)
                    continue;
                // skip comments
                if (s.charAt(0) == '/' && s.charAt(1) == '/')
                    continue;
                words.add(s.toUpperCase());
            }
            br.close();
        } catch (IOException ex) {
            System.out.println(RED + "Error reading file '" + fileName
                    + "'. Check if the file exists in the same directory as the program." + RESET);
            System.exit(0);
        }
        return words;
    }
}
