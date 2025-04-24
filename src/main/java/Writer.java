import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Writer {

    private int[] gameState;
    private int playerID;
    private int action;


    public static void writeGameState(Game game, int move) {
        game.getGameState().updateGameState();

        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File outputFile = new File(dataDir, game.getUniqueFileName());

        int[] gameStateNumeric = game.getGameState().getGameState();

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile, true))) {
            writer.print("[(");
            for (int i = 0; i < gameStateNumeric.length; i++) {
                writer.print(gameStateNumeric[i]);
                if (i < gameStateNumeric.length - 1) {
                    writer.print(",");
                } else {
                    writer.print("), ");
                }
            }
            writer.print(String.valueOf(move));
            writer.print(", (");
            writer.print(game.getCurrentPlayer().playerID);
            writer.println(); // move to next line
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeResult(Game game) {

        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File outputFile = new File(dataDir, game.getUniqueFileName());


        // 1. Read all lines from the file
        List<String> lines = null;
        try {
            lines = Files.readAllLines(outputFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Get winner:
        int winnerMoney = Integer.MIN_VALUE;
        for (Player p : game.getPlayers()) {
            if (p.getMoney() > winnerMoney) {
                winnerMoney = p.getMoney();
            }
        }

        ArrayList<Player> winners = new ArrayList<>();
        for (Player p : game.getPlayers()) {
            if (p.getMoney() == winnerMoney) { winners.add(p); }
        }
        String playerWinner = "";
        if (winners.size() == 1) {
            playerWinner = String.valueOf(winners.get(0).getPlayerID());
        } else if (winners.size() > 1) {
            playerWinner = winners.stream()
                    .map(player -> String.valueOf(player.getPlayerID()))
                    .collect(Collectors.joining(", "));
        }

        // 2. Modify each line (example: add " - updated" to each line)
        List<String> updatedLines = new ArrayList<>();
        for (int l = 0; l < lines.size() - 2; l++) {
            String line = lines.get(l);
            updatedLines.add(line + ", " + playerWinner + ")], ");
        }
        updatedLines.add(lines.get(lines.size() - 1) + ", " + playerWinner + ")]] ");


        // 3. Write the updated lines back to the file
        try {
            Files.write(outputFile.toPath(), updatedLines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void beginTxt(Game game) {

        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File outputFile = new File(dataDir, game.getUniqueFileName());


        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile, true))) {
            writer.print("[");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


