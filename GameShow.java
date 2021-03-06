// Laura Gao (342611589)
// ICS4U1 - Mr. Anthony
// March 12, 2022
// This program is a game show where the player has to solve variations of the Wordle game to win points.

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GameShow {
    private static final WordleRound[] ROUNDS = {
            new Round1(),
            new Round2(),
            new Round3(),
            new Round4(),
            new Round5(),
    };

    // Standardized character to separate name and score in the leaderboard file
    private static final char separator = '/';

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        welcomeScreen(in);
    }

    private static void beginGame(Scanner in) {
        Utils.clearScreen();
        int totalScore = 0;
        int numRounds = ROUNDS.length;
        // Loop through and play all the rounds. Only display rules on the first round.
        WordleRound round1 = ROUNDS[0];
        round1.begin(in, 1, false);
        totalScore = round1.play(in, totalScore, true);
        for (int i = 2; i < numRounds + 1; i++) {
            WordleRound currentRound = ROUNDS[i - 1];
            currentRound.begin(in, i, true);
            totalScore = currentRound.play(in, totalScore, true);
        }

        // Save final score to leaderboard.
        int rank = getRank(totalScore);
        System.out.println("Congrats on completing the Wordle game show!\n"
                + "Your final score is " + totalScore + " which gives you a ranking of #" + rank + "!");
        System.out.println();
        System.out.println("Enter your name to save your score to the leaderboard: ");
        String name = in.nextLine();
        while (true) {
            if (name.contains(separator + "")) {
                System.out.println("Invalid character '" + separator + "' in name. Try again.");
            } else if (name.length() == 0) {
                System.out.println("Invalid name. Try again.");
            } else {
                break;
            }
            name = in.nextLine();
        }
        saveScore(name, totalScore);
        System.out.println();
        System.out.println("Thank you for playing, " + name + "! Press enter to return to the main menu.");
        in.nextLine();

        welcomeScreen(in);
    }

    private static int getRank(int score) {
        // Returns the rank of the player based on their score compared with other
        // players on the leaderboard.
        ArrayList<LeaderboardEntry> leaderboardData = getLeaderboardData();
        for (int i = 0; i < leaderboardData.size(); i++) {
            if (score >= leaderboardData.get(i).getScore()) {
                return i + 1;
            }
        }
        // If the code makes it here, the player's score is lower than all the scores on
        // the leaderboard.
        return leaderboardData.size() + 1;
    }

    private static void playSpecificRound(Scanner in, int roundIndex) {
        // Plays a specific round of the game show and display rules.
        WordleRound round = ROUNDS[roundIndex - 1];
        round.begin(in, roundIndex, false);
        round.play(in, 0, false);
        welcomeScreen(in);
    }

    private static void saveScore(String name, int score) {
        // Appends the player's name and score to the leaderboard file.
        try {
            FileWriter fw = new FileWriter("leaderboard.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println(name + separator + score);
            out.close();
        } catch (IOException e) {
            System.out.println();
            System.out.println(Utils.RED + "Error saving score to leaderboard." + Utils.RESET);
            System.out.println();
        }
    }

    private static void welcomeScreen(Scanner in) {
        // The main menu
        Utils.clearScreen();
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
        System.out.println("A - Acknowledgements");
        System.out.println();

        while (true) {
            String input = in.nextLine();
            if (input.toUpperCase().equals("S")) {
                beginGame(in);
            } else if (input.toUpperCase().equals("Q")) {
                System.out.println("Bye bye!");
                in.close();
                System.exit(0);
            } else if (input.toUpperCase().equals("C")) {
                roundsScreen(in);
            } else if (input.toUpperCase().equals("L")) {
                leaderboardScreen(in);
            } else if (input.toUpperCase().equals("A")) {
                acknowledgementsScreen(in);
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    private static void acknowledgementsScreen(Scanner in) {
        // Displays the acknowledgements screen.
        final int pause = 500; // How long to pause between lines when printed
        Utils.clearScreen();
        Utils.prettyPrint("Wordle Game Show was designed and built by Laura Gao.");
        Utils.sleep(pause);
        System.out.println();
        Utils.prettyPrint("I would like to thank the following people for playtesting and for their valuable feedback: ", false);
        Utils.sleep(pause);
        String names[] = { "Emma", "Colleen", "Ahmed", "Quan", "Susie", "Emily", "Gloria", "Rain", "my sister Angela", "my mom" };
        for (int i = 0; i < names.length - 1; i++) {
            Utils.prettyPrint(names[i] + ", ", false);
            Utils.sleep(250);
        }
        Utils.prettyPrint("and " + names[names.length - 1] + ".", false);
        Utils.sleep(pause);
        System.out.println();
        System.out.println();
        System.out.println("Enter 'R' to return to the main menu.");
        System.out.println();
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

    private static ArrayList<LeaderboardEntry> getLeaderboardData() {
        ArrayList<LeaderboardEntry> leaderboardEntries = new ArrayList<LeaderboardEntry>();
        try {
            // Read the leaderboard file
            BufferedReader br = new BufferedReader(
                    new FileReader("./leaderboard.txt"));

            String s;
            while ((s = br.readLine()) != null) {
                final String name = s.split(separator + "")[0];
                final int score;
                try {
                    String scoreStr = s.split(separator + "")[1];
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
        // Sort the leaderboard entries according to score
        leaderboardEntries.sort((player1, player2) -> player2.getScore() - player1.getScore());

        return leaderboardEntries;
    }

    private static void leaderboardScreen(Scanner in) {
        ArrayList<LeaderboardEntry> leaderboardEntries = getLeaderboardData();

        // Print out the leaderboard
        Utils.clearScreen();
        System.out.println("LEADERBOARD");
        System.out.println();
        if (leaderboardEntries.size() == 0) {
            System.out.println("No one has beat the game yet. Do you want to be the first? ;)");
        } else {
            // Format the leaderboard entries into a table
            Object[][] leaderboardTable = new Object[leaderboardEntries.size()][];
            int longestNameLength = 4; // number of characters in the longest name in the "name" column (including the
                                       // heading "name" which has 4 letters)
            for (int i = 0; i < leaderboardEntries.size(); i++) {
                int rank = i + 1;
                String name = leaderboardEntries.get(i).getName();
                if (name.length() > longestNameLength)
                    longestNameLength = name.length();
                int score = leaderboardEntries.get(i).getScore();
                leaderboardTable[i] = new Object[] { rank, name, score };
            }
            // number of characters in the largest score
            int longestScoreLength = Utils.getNumDigits(leaderboardEntries.get(0).getScore());
            // The heading "score" has 5 letters
            if (longestScoreLength < 5)
                longestScoreLength = 5;

            int longestRankLength = Utils.getNumDigits(leaderboardEntries.size() + 1);
            // The heading "rank" has 5 letters
            if (longestRankLength < 4)
                longestRankLength = 4;

            // Create string used to format the table, which dictates the spacing between
            // columns. The "Score" column is right aligned while the others are left
            // aligned. The columns are separated by a gap of 5 spaces.
            // This method for printing tables is from https://stackoverflow.com/a/18672745
            final int gap = 5; // Number of spaces between table columns
            String formatString = "%-" + (gap + longestRankLength) + "s%-" + (gap + longestNameLength) + "s%"
                    + longestScoreLength + "s%n";

            // Print the column headers
            System.out.format(formatString, new Object[] { "RANK", "NAME", "SCORE" });

            // Create a horizontal line whose width is exactly the width of the table.
            final int tableWidth = longestRankLength + longestNameLength + longestScoreLength + 2 * gap;
            final String border = "*".repeat(tableWidth);
            System.out.println(border);

            // Print the table row-by-row
            for (Object[] row : leaderboardTable) {
                System.out.format(formatString, row);
            }
            System.out.println();
            System.out.println(border);
        }
        System.out.println();
        System.out.println();
        System.out.println("Enter 'R' to return to the main menu.");
        System.out.println();
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
        // Displays the rounds screen and allowers player to choose a round to play.
        Utils.clearScreen();
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
        System.out.println(
                "Select this option if you would like to try out a specific level or practice a specific round to improve.");
        System.out.println();
        Utils.sleep(1000);
        Utils.prettyPrint("Enter the number of the round you want to jump to.");
        Utils.sleep(500);
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
}

class LeaderboardEntry {
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