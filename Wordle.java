import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Wordle {

    @FunctionalInterface
    interface VoidFunction {
        // Dummy interface for typing of functions with no inputs and no outputs
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

    final static class LeaderboardEntry {
        // Data class to store information about a leaderboard entry.
        private final String name;
        private final int score;

        public LeaderboardEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }

    final static class Round {
        // Data class to store information about a round.
        private final String name;
        private final String description;
        private final int numLetters; // 0 if length of different words is different

        public Round(String name, String description, int numLetters) {
            this.name = name;
            this.description = description;
            this.numLetters = numLetters;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getNumLetters() {
            return numLetters;
        }
    }

    // Text colors
    public static final String RESET = "\033[0m"; // Text reset (clears all styles that have been applied)
    public static final String BLACK = "\033[0;30m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[1;92m";
    public static final String ORANGE = "\033[1;38;2;255;165;0m";
    public static final String WHITE = "\033[1;97m";

    // Each entry of the 2D array represents a round.
    // Each entry of each nested array is [name of round, description of round,
    // number of letters in this word's round]
    private static final Round[] ROUNDS = {
            new Round("CLASSIC WORDLE", "You will be given a 5-letter word and you must guess it within 6 tries.", 5),
            new Round("4-LETTER WORDLE", "You have 6 tries to guess the word.", 4),
            new Round("6-LETTER WORDLE", "You have 10 tries to guess the word.", 6),
            new Round("DOUBLE WORDLE",
                    "You have six tries to guess two 5-letter words. Each guess will be used on both words simultaneously.",
                    5),
            new Round("SPECIAL EDITION - JAVA KEYWORDS",
                    "The answer of this special round will be one of the 67 possible Java reserved words. You will have 6 tries to guess it. You do not know how many letters the answer contains.",
                    0),
    };

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        welcomeScreen(in);
    }

    public static void beginGame(Scanner in) {
        clearScreen();
        int totalScore = 0;
        beginRound(in, 1, false);
        totalScore = round1(in, totalScore);
        beginRound(in, 2, true);
        totalScore = round2(in, totalScore);
        beginRound(in, 3, true);
        totalScore = round3(in, totalScore);
        beginRound(in, 4, true);
        totalScore = round4(in, totalScore);
        beginRound(in, 5, true);
        totalScore = round5(in, totalScore);

        // Save final score to leaderboard.
        int ranking = 1;
        System.out.println("Congrats on completing the Wordle game show!\n"
                + "Your final score is: " + totalScore + " which gives you a ranking of #" + ranking + "!");
        System.out.println("Enter your name to save your score to the leaderboard: ");
        String name = in.nextLine();
        saveScore(name, totalScore);
        System.out.println();
        System.out.println("Thank you for playing, " + name + "! Press enter to return to the main menu.");
        in.nextLine();

        welcomeScreen(in);
    }

    private static void playSpecificRound(Scanner in, int round) {
        beginRound(in, round, false);
        if (round == 1) {
            round1(in, 0);
        } else if (round == 2) {
            round2(in, 0);
        } else if (round == 3) {
            round3(in, 0);
        } else if (round == 4) {
            round4(in, 0);
        } else {
            round5(in, 0);
        }
        welcomeScreen(in);
    }

    private static void beginRound(Scanner in, int roundIndex, boolean skipRules) {
        Round round = ROUNDS[roundIndex - 1];
        clearScreen();
        System.out.println("ROUND " + roundIndex + ": " + round.getName());
        System.out.println();
        System.out.println(round.getDescription());
        System.out.println("If you don't get the word in the allotted number of guesses, you will not get any points.");
        System.out.println();
        if (!skipRules) {
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
                    System.out
                            .println(
                                    "Type a "
                                            + (round.getNumLetters() > 0 ? round.getNumLetters() + "-letter word"
                                                    : "word")
                                            + " and press enter. Each letter will be highlighted either...");
                    System.out.println(WHITE + "White: this letter does not exist in the word.");
                    System.out.println(
                            ORANGE + "Orange: this letter exists in the word but is not in the right location.");
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
        }
        System.out.println("Enter your first guess:");
        System.out.println();
    }

    private static void saveScore(String name, int score) {
        try {
            FileWriter fw = new FileWriter("leaderboard.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println(name + " " + score);
            out.close();
        } catch (IOException e) {
            System.out.println();
            System.out.println(RED + "Error saving score to leaderboard." + RESET);
            System.out.println();
        }
    }

    private static MyResult mainWordle(Scanner in, String answer, ArrayList<String> allWords) {
        // Overload variant of `mainWordle` where the number of letters is simply the
        // length of the answer.
        return mainWordle(in, answer, allWords, answer.length());
    }

    private static MyResult mainWordle(Scanner in, String answer, ArrayList<String> allWords, int numLetters) {
        // This method carries out the logic of a single wordle game (the UI of the user
        // guessing.)
        boolean successful = false;
        ArrayList<String> allGuesses = new ArrayList<String>(); // Store all guesses in a variable so we can print
                                                                // them all out later.
        while (true) {
            String guess = getGuess(in, numLetters, allWords,
                    () -> {
                        for (int i = 0; i < allGuesses.size(); i++) {
                            printColoredWord(allGuesses.get(i), answer, false);
                            System.out.println();
                        }
                    });

            if (guess.equals("RQ")) {
                System.out.println("You rage quit. The answer is: " + answer);
                successful = false;
                break;
            }
            clearScreen();
            System.out.println();
            for (int i = 0; i < allGuesses.size(); i++) {
                printColoredWord(allGuesses.get(i), answer, false);
                System.out.println();
            }
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

    private static int round5(Scanner in, int totalScore) {
        final ArrayList<String> allWords = getWords("./wordlist_java_keywords.txt");
        final String answer = allWords.get((int) (Math.random() * allWords.size()));

        MyResult result = mainWordle(in, answer, allWords, 0);
        ArrayList<String> allGuesses = result.getFirst();
        boolean successful = result.getSecond();

        int newPoints = (successful && allGuesses.size() <= 6) ? (int) Math.pow((7 - allGuesses.size()), 2) * 100 : 0;
        totalScore += newPoints;
        finishRound(newPoints, totalScore, in);
        return totalScore;
    }

    private static int round4(Scanner in, int totalScore) {
        // Double wordle
        final String gap = "               "; // Gap between the two wordles when they are printed out.

        final ArrayList<String> possibleAnswers = getWords("./wordlist_5_answers.txt");
        final ArrayList<String> allWords = getWords("./wordlist_5_all.txt");
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

        // These two variables how many guesses it takes to solve each answer
        // and is 0 if the answer is unsolved.
        int answer1Solved = 0;
        int answer2Solved = 0;

        while (true) {
            VoidFunction recreateCurrentScreen = () -> {
                boolean ans1solved = false;
                boolean ans2solved = false;
                for (int i = 0; i < allGuesses.size(); i++) {
                    if (!ans1solved) {
                        printColoredWord(allGuesses.get(i), answer1, false);
                        ans1solved = answer1.equals(allGuesses.get(i));
                    } else {
                        System.out.print("     ");
                    }
                    System.out.print(gap);
                    if (!ans2solved) {
                        printColoredWord(allGuesses.get(i), answer2, false);
                        ans2solved = answer2.equals(allGuesses.get(i));
                    }
                    System.out.println();
                }
            };
            String guess = getGuess(in, 5, allWords, recreateCurrentScreen);
            if (guess.equals("RQ")) {
                System.out.println("You rage quit. The answers are: " + answer1 + " and " + answer2);
                break;
            }

            // If an answer has already been solved, do not print out this guess for the
            // answer.
            clearScreen();
            recreateCurrentScreen.run();
            if (answer1Solved == 0) {
                printColoredWord(guess, answer1, true);
            } else {
                System.out.print("     ");
            }
            System.out.print(gap);
            if (answer2Solved == 0) {
                printColoredWord(guess, answer2, true);
            }
            System.out.println();
            allGuesses.add(guess);

            if (guess.equals(answer1))
                answer1Solved = allGuesses.size();
            if (guess.equals(answer2))
                answer2Solved = allGuesses.size();
            if (answer1Solved > 0 && answer2Solved > 0) {
                System.out.println("Congrats, you solved both answers in " + allGuesses.size() + " guesses!");
                break; // Exit when both answers are solved.
            }
        }

        // Calculate score.
        int newPoints = 0;
        // Add points for each answer solved under the allowed number of guesses.
        newPoints += (answer1Solved > 0 && answer1Solved <= 6) ? (int) Math.pow((7 - answer1Solved), 2) * 50
                : 0;
        newPoints += (answer2Solved > 0 && answer2Solved <= 6) ? (int) Math.pow((7 - answer2Solved), 2) * 50
                : 0;

        // Add bonus points if both answers are solved under the allowed number of
        // guesses.
        newPoints += (answer1Solved > 0 && answer2Solved > 0 && allGuesses.size() <= 6)
                ? (int) Math.pow((7 - allGuesses.size()), 2) * 100
                : 0;
        totalScore += newPoints;
        finishRound(newPoints, totalScore, in);

        return totalScore;
    }

    private static int round3(Scanner in, int totalScore) {
        // 6 letter words

        final ArrayList<String> allWords = getWords("./wordlist_6_all.txt");
        final ArrayList<String> possibleAnswers = getWords("./wordlist_6_answers.txt");
        final String answer = possibleAnswers.get((int) (Math.random() * possibleAnswers.size()));

        MyResult result = mainWordle(in, answer, allWords);
        ArrayList<String> allGuesses = result.getFirst();
        boolean successful = result.getSecond();

        int newPoints = (successful && allGuesses.size() <= 6) ? (int) Math.pow((7 - allGuesses.size()), 2) * 100 : 0;
        totalScore += newPoints;
        finishRound(newPoints, totalScore, in);
        return totalScore;

    }

    private static int round2(Scanner in, int totalScore) {

        final ArrayList<String> words_possible_ansnwers = getWords("./wordlist_4_answers.txt");
        final ArrayList<String> allWords = getWords("./wordlist_4_all.txt");
        final String answer = words_possible_ansnwers.get((int) (Math.random() * words_possible_ansnwers.size()));

        MyResult result = mainWordle(in, answer, allWords);
        int newPoints = (result.getSecond() && result.getFirst().size() <= 6)
                ? (int) Math.pow((7 - result.getFirst().size()), 2) * 100
                : 0;
        totalScore += newPoints;
        finishRound(newPoints, totalScore, in);
        return totalScore;

    }

    private static int round1(Scanner in, int totalScore) {
        final ArrayList<String> possibleAnswers = getWords("./wordlist_5_answers.txt");
        final ArrayList<String> allWords = getWords("./wordlist_5_all.txt");
        final String answer = possibleAnswers.get((int) (Math.random() * possibleAnswers.size()));

        MyResult result = mainWordle(in, answer, allWords);
        ArrayList<String> allGuesses = result.getFirst();
        boolean successful = result.getSecond();

        int newPoints = (successful && allGuesses.size() <= 6) ? (int) Math.pow((7 - allGuesses.size()), 2) * 100 : 0;
        totalScore += newPoints;
        finishRound(newPoints, totalScore, in);
        return totalScore;
    }

    public static void printColoredWord(String guess, final String answer, boolean showAnimation) {
        // This method accepts a guess and the correct answer. It will print out the
        // guess with each letter hightlighted with the appropriate color according to
        // the rules of Wordle.
        // Note that this method does not print a linebreak after the word.
        String output = "";
        int wordLength = guess.length();
        int answerLength = answer.length();
        for (int i = 0; i < wordLength; i++) {
            char thisLetter = guess.charAt(i);
            if (i < answerLength) {
                if (thisLetter == answer.charAt(i)) {
                    output += GREEN + thisLetter;
                } else if (answer.contains(thisLetter + "")) {
                    int index = answer.indexOf(thisLetter);
                    if (guess.indexOf(thisLetter) == i) {
                        // This is the first occurance of this letter in the guess
                        if (guess.length() <= index || guess.charAt(index) != thisLetter
                                || answer.substring(index + 1).contains(thisLetter + "")) {
                            // The letter at the position of the the answer and guess is not the same OR
                            // This letter appears twice in this answer
                            output += ORANGE + thisLetter;
                        } else {
                            output += WHITE + thisLetter;
                        }
                    } else if (answer.substring(index + 1).contains(thisLetter + "")
                            && answer.substring(index + 1).indexOf(thisLetter) == i) {
                        // This letter appears twice in this answer and this is the second occurance of
                        // this letter in the guess
                        output += WHITE + thisLetter;
                    } else {
                        output += WHITE + thisLetter;
                    }
                } else {
                    output += WHITE + thisLetter;
                }
            } else {
                // If the guess is longer than the answer, then we don't need to worry about any
                // letter being green.
                if (answer.contains(thisLetter + "")) {
                    if (guess.indexOf(thisLetter) == i) {
                        // This is the first occurance of this letter in the guess
                        output += ORANGE + thisLetter;
                    } else if (answer.substring(answer.indexOf(thisLetter) + 1).contains(thisLetter + "")
                            && answer.substring(answer.indexOf(thisLetter) + 1).indexOf(thisLetter) == i) {
                        // This letter appears twice in this answer and this is the second occurance of
                        // this letter in the guess
                        output += WHITE + thisLetter;
                    } else {
                        output += WHITE + thisLetter;
                    }
                } else {
                    output += WHITE + thisLetter;
                }
            }
        }
        System.out.print(output + RESET);
    }

    private static String getGuess(Scanner in, int wordLength, ArrayList<String> words,
            VoidFunction recreateCurrentScreen) {
        String guess;
        while (true) {
            guess = in.nextLine().toUpperCase();

            if (guess.equals("RQ"))
                break;

            // Check that guess has correct number of characters
            if (wordLength > 0 && guess.length() != wordLength) {
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
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void welcomeScreen(Scanner in) {
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
                beginGame(in);
            } else if (input.toUpperCase().equals("Q")) {
                System.out.println("Bye bye!");
                System.exit(0);
            } else if (input.toUpperCase().equals("C")) {
                clearScreen();
                roundsScreen(in);
            } else if (input.toUpperCase().equals("L")) {
                leaderboardScreen(in);
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    private static ArrayList<LeaderboardEntry> getLeaderboardData() {
        ArrayList<LeaderboardEntry> leaderboardEntries = new ArrayList<LeaderboardEntry>();
        try {
            // Read the leaderboard file
            BufferedReader br = new BufferedReader(
                    new FileReader("./leaderboard.txt"));

            String s;
            while ((s = br.readLine()) != null) {
                final String name = s.split(" ")[0];
                final int score;
                try {
                    String scoreStr = s.split(" ")[1];
                    score = Integer.parseInt(scoreStr);
                } catch (NumberFormatException e) {
                    // This is not a valid score, so we skip this line of the file.
                    continue;
                }
                leaderboardEntries.add(new LeaderboardEntry(name, score));
            }
            br.close();
        } catch (IOException ex) {
            // If the file doesn't exist, then there are no entries on the leaderboard,
            // and we will return an empty ArrayList.
        }
        return leaderboardEntries;
    }

    private static void leaderboardScreen(Scanner in) {
        ArrayList<LeaderboardEntry> leaderboardEntries = getLeaderboardData();

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
                String name = leaderboardEntries.get(i).name;
                int score = leaderboardEntries.get(i).score;
                System.out.println(rank + "\t" + name + "\t" + score);
            }
        }
        System.out.println();
        System.out.println("********************************************************");
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

    private static void roundsScreen(Scanner in) {
        final String rounds = "12345";
        System.out.println("ROUNDS:");
        System.out.println("1 - Classic 5-letter Wordle");
        System.out.println("2 - 4-letter Wordle");
        System.out.println("3 - 6-letter Wordle");
        System.out.println("4 - Double Wordle");
        System.out.println("5 - Special edition: Wordle with Java keywords");
        System.out.println();

        System.out.println(
                "By playing a specific level, you will not be able to accrue a total score and will not be competing for a position on the leaderboard.");
        System.out.println("This is for practicing a specific level that you want to improve or try out.");
        System.out.println();
        System.out.println("Enter the number of the round you want to jump to.");
        System.out.println("Or, enter 'R' to return to the main menu.");
        System.out.println();
        String round = in.nextLine();
        while (true) {
            if (round.length() == 1 && rounds.indexOf(round) > -1) {
                playSpecificRound(in, Integer.parseInt(round));
            } else if (round.toUpperCase().equals("R")) {
                welcomeScreen(in);
            } else {
                System.out.println("Invalid input. Please try again.");
                round = in.nextLine();
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

    private static void finishRound(int newPoints, int totalScore, Scanner in) {
        System.out.println("This earns you an additional " + newPoints + " points for a total score of " + totalScore);
        System.out.println();
        System.out.println("Press enter to continue...");
        in.nextLine();
        clearScreen();
    }
}
