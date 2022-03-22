// This file contains the logic specific to the Wordle game.
// Code for the main gameshow is in GameShow.java

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class MyResult {
    // Dummy data class to store the output of `mainWordle` method.
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

abstract class WordleRound {
    private final String name;
    private final String description;
    private final int numLetters; // 0 if number of letters of different words is different
    private final String typeOfWords;

    public WordleRound(String name, String description, int numLetters) {
        this.name = name;
        this.description = description;
        this.numLetters = numLetters;
        // By default, Wordle rounds include all words.
        this.typeOfWords = "word";
    }

    public WordleRound(String name, String description, int numLetters, String typeOfWords) {
        this.name = name;
        this.description = description;
        this.numLetters = numLetters;
        this.typeOfWords = typeOfWords;
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

    public String getTypeOfWords() {
        return typeOfWords;
    }

    public void begin(Scanner in, int roundIndex, boolean skipRules) {
        Utils.clearScreen();
        System.out.println("ROUND " + roundIndex + ": " + this.getName());
        System.out.println();
        System.out.println(this.getDescription());
        if (!skipRules) {
            System.out.println(
                    "If you don't get the word in the allotted number of guesses, you will not get any points.");
            System.out.println();
            System.out.println(
                    "For your viewing pleasure, it is recommended that this game is played in a console whose background colour is dark.");
        }
        System.out.println();
        if (!skipRules) {
            System.out.println();
            System.out.println("Press enter to continue...");
            System.out.println("(If you already know how to play Wordle, enter 'S' to skip the rules.)");
            System.out.println();
            while (true) {
                String input = in.nextLine();
                if (input.toUpperCase().equals("S")) {
                    break;
                } else {
                    // Print wordle rules
                    Utils.clearScreen();
                    System.out.println("RULES:");
                    System.out.println();
                    System.out
                            .println(
                                    "Type a "
                                            + (this.getNumLetters() > 0 ? this.getNumLetters() + "-letter word"
                                                    : "word")
                                            + " and press enter. Each letter will be highlighted either...");
                    System.out.println();
                    System.out.println(Utils.WHITE + "WHITE: this letter does not exist in the word.");
                    System.out.println(
                            Utils.ORANGE
                                    + "ORANGE: this letter exists in the word but is not in the right location.");
                    System.out.println(
                            Utils.GREEN + "GREEN: this letter exists in the word and is in the right location.");
                    System.out.println(Utils.RESET);
                    System.out.println("Press enter to continue...");
                    in.nextLine();
                    break;
                }
            }
            Utils.clearScreen();
            System.out.println(
                    "You can type 'RQ' to rage quit the current level and skip to the next level at any time.");
        }
        System.out.println("Enter your first guess:");
        System.out.println();
    }

    // This method is called to play the round. the main method of the round that
    // child classes have to implement.
    abstract int play(Scanner in, int prevScore, boolean includeTotalScore);

    // The following are utility methods that may be used in the `play` method of
    // child class implementations.
    static void printFinishedRoundMessage(int newPoints, int totalScore, Scanner in, boolean includeTotalScore) {
        System.out.println("This earns you an additional " + newPoints
                + (includeTotalScore ? " points for a total score of " + totalScore : "points."));
        System.out.println();
        System.out.println("Press enter to continue...");
        in.nextLine();
        Utils.clearScreen();
    }

    static ArrayList<String> getWords(String fileName) {
        ArrayList<String> words = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));

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
            System.out.println(Utils.RED + "Error reading file '" + fileName
                    + "'. Check if the file exists in the same directory as the program." + Utils.RESET);
            System.exit(0);
        }
        return words;
    }

    MyResult mainWordle(Scanner in, String answer, ArrayList<String> allWords) {
        // Overload variant of `mainWordle` where the number of letters is simply the
        // length of the answer.
        return this.mainWordle(in, answer, allWords, answer.length());
    }

    MyResult mainWordle(Scanner in, String answer, ArrayList<String> allWords, int numLetters) {
        // This method carries out the logic of a single wordle game (the UI of the user
        // guessing.)
        boolean successful = false;
        ArrayList<String> allGuesses = new ArrayList<String>(); // Store all guesses in a variable so we can print
                                                                // them all out later.
        while (true) {
            String guess = this.getGuess(in, numLetters, allWords,
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
            Utils.clearScreen();
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
                    output = Utils.GREEN + thisLetter;
                } else if (answer.contains(thisLetter + "")) {
                    int index = answer.indexOf(thisLetter);
                    if (guess.indexOf(thisLetter) == i) {
                        // This is the first occurance of this letter in the guess
                        if (guess.length() <= index || guess.charAt(index) != thisLetter
                                || answer.substring(index + 1).contains(thisLetter + "")) {
                            // The letter at the position of the the answer and guess is not the same OR
                            // This letter appears twice in this answer
                            output = Utils.ORANGE + thisLetter;
                        } else {
                            output = Utils.WHITE + thisLetter;
                        }
                    } else if (answer.substring(index + 1).contains(thisLetter + "")
                            && answer.substring(index + 1).indexOf(thisLetter) == i) {
                        // This letter appears twice in this answer and this is the second occurance of
                        // this letter in the guess
                        output = Utils.WHITE + thisLetter;
                    } else {
                        output = Utils.WHITE + thisLetter;
                    }
                } else {
                    output = Utils.WHITE + thisLetter;
                }
            } else {
                // If the guess is longer than the answer, then we don't need to worry about any
                // letter being green.
                if (answer.contains(thisLetter + "")) {
                    if (guess.indexOf(thisLetter) == i) {
                        // This is the first occurance of this letter in the guess
                        output = Utils.ORANGE + thisLetter;
                    } else if (answer.substring(answer.indexOf(thisLetter) + 1).contains(thisLetter + "")
                            && answer.substring(answer.indexOf(thisLetter) + 1).indexOf(thisLetter) == i) {
                        // This letter appears twice in this answer and this is the second occurance of
                        // this letter in the guess
                        output = Utils.WHITE + thisLetter;
                    } else {
                        output = Utils.WHITE + thisLetter;
                    }
                } else {
                    output = Utils.WHITE + thisLetter;
                }
            }
            if (showAnimation) {
                System.out.print(output);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.print(output);
            }
        }
        System.out.print(Utils.RESET);
    }

    String getGuess(Scanner in, int wordLength, ArrayList<String> words,
            Utils.VoidFunction recreateCurrentScreen) {
        String guess;
        while (true) {
            guess = in.nextLine().toUpperCase();

            if (guess.equals("RQ"))
                break;

            // Check that guess has correct number of characters
            if (wordLength > 0 && guess.length() != wordLength) {
                Utils.clearScreen();
                System.out.println(Utils.RED + "Please enter a word of length " + wordLength + Utils.RESET);
                System.out.println();
                recreateCurrentScreen.run();
                continue;
            }

            // Check that guess is a word
            if (!words.contains(guess)) {
                Utils.clearScreen();
                System.out.println(Utils.RED + guess + " is not a " + this.getTypeOfWords() + "." + Utils.RESET);
                System.out.println();
                recreateCurrentScreen.run();
                continue;
            }
            break;
        }
        return guess;
    }

}

class Round1 extends WordleRound {
    Round1() {
        super("CLASSIC WORDLE", "You will be given a 5-letter word and you must guess it within 6 tries.", 5);
    }

    @Override
    int play(Scanner in, int prevScore, boolean includeTotalScore) {
        final ArrayList<String> possibleAnswers = getWords("./wordlist_5_answers.txt");
        final ArrayList<String> allWords = getWords("./wordlist_5_all.txt");
        final String answer = possibleAnswers.get((int) (Math.random() * possibleAnswers.size()));

        MyResult result = mainWordle(in, answer, allWords);
        ArrayList<String> allGuesses = result.getFirst();
        boolean successful = result.getSecond();

        int newPoints = (successful && allGuesses.size() <= 6) ? (int) Math.pow((7 - allGuesses.size()), 2) * 100 : 0;
        int totalScore = newPoints + prevScore;
        printFinishedRoundMessage(newPoints, totalScore, in, includeTotalScore);
        return totalScore;
    }
}

class Round2 extends WordleRound {
    Round2() {
        super("4-LETTER WORDLE", "You have 6 tries to guess the word.", 4);
    }

    @Override
    int play(Scanner in, int prevScore, boolean includeTotalScore) {
        final ArrayList<String> words_possible_ansnwers = getWords("./wordlist_4_answers.txt");
        final ArrayList<String> allWords = getWords("./wordlist_4_all.txt");
        final String answer = words_possible_ansnwers.get((int) (Math.random() * words_possible_ansnwers.size()));

        MyResult result = mainWordle(in, answer, allWords);
        int newPoints = (result.getSecond() && result.getFirst().size() <= 6)
                ? (int) Math.pow((7 - result.getFirst().size()), 2) * 100
                : 0;
        int totalScore = prevScore + newPoints;
        printFinishedRoundMessage(newPoints, totalScore, in, includeTotalScore);
        return totalScore;
    }
}

class Round3 extends WordleRound {
    Round3() {
        super("6-LETTER WORDLE", "You have 10 tries to guess the word.", 6);
    }

    @Override
    int play(Scanner in, int prevScore, boolean includeTotalScore) {
        final ArrayList<String> allWords = getWords("./wordlist_6_all.txt");
        final ArrayList<String> possibleAnswers = getWords("./wordlist_6_answers.txt");
        final String answer = possibleAnswers.get((int) (Math.random() * possibleAnswers.size()));

        MyResult result = mainWordle(in, answer, allWords);
        ArrayList<String> allGuesses = result.getFirst();
        boolean successful = result.getSecond();

        int newPoints = (successful && allGuesses.size() <= 6) ? (int) Math.pow((7 - allGuesses.size()), 2) * 100 : 0;
        int totalScore = prevScore + newPoints;
        printFinishedRoundMessage(newPoints, totalScore, in, includeTotalScore);
        return totalScore;
    }
}

class Round4 extends WordleRound {
    Round4() {
        super("DOUBLE WORDLE",
                "You have six tries to guess two 5-letter words. Each guess will be used on both words simultaneously.",
                5);
    }

    @Override
    int play(Scanner in, int prevScore, boolean includeTotalScore) {
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
            Utils.VoidFunction recreateCurrentScreen = () -> {
                boolean ans1Solved = false;
                boolean ans2Solved = false;
                for (int i = 0; i < allGuesses.size(); i++) {
                    if (!ans1Solved) {
                        printColoredWord(allGuesses.get(i), answer1, false);
                        ans1Solved = answer1.equals(allGuesses.get(i));
                    } else {
                        System.out.print("     "); // Filler space for a 5-letter word
                    }
                    System.out.print(gap);
                    if (!ans2Solved) {
                        printColoredWord(allGuesses.get(i), answer2, false);
                        ans2Solved = answer2.equals(allGuesses.get(i));
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
            Utils.clearScreen();
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
        int totalScore = prevScore + newPoints;
        printFinishedRoundMessage(newPoints, totalScore, in, includeTotalScore);

        return totalScore;
    }
}

class Round5 extends WordleRound {
    Round5() {
        super("SPECIAL EDITION - JAVA KEYWORDS",
                "The answer of this special round will be one of the 67 possible Java reserved words. You will have 6 tries to guess it. You do not know how many letters the answer contains.",
                0, "Java reserved word");
    }

    @Override
    int play(Scanner in, int prevScore, boolean includeTotalScore) {
        final ArrayList<String> allWords = getWords("./wordlist_java_keywords.txt");
        final String answer = allWords.get((int) (Math.random() * allWords.size()));

        MyResult result = mainWordle(in, answer, allWords, 0);
        ArrayList<String> allGuesses = result.getFirst();
        boolean successful = result.getSecond();

        int newPoints = (successful && allGuesses.size() <= 6) ? (int) Math.pow((7 - allGuesses.size()), 2) * 100 : 0;
        int totalScore = prevScore + newPoints;
        printFinishedRoundMessage(newPoints, totalScore, in, includeTotalScore);
        return totalScore;
    }
}