import javax.swing.*;

public class HeadlessMode {

    public static void main(String[] args) {

        // Generate data with 5 players, 3 Greedy and 2 Random
        Player[] players = new Player[4];
        players[0] = new GreedyPlayer(0, "Aaron");
        players[1] = new RandomPlayer(1, "Barbara");
        players[2] = new GreedyPlayer(2, "Charles");
        players[3] = new RandomPlayer(3, "Daniel");

        Game game = new Game(players);
        game.start();

    }

}
