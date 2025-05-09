public class HeadlessMode {

    public static void main(String[] args) {

        for (int i = 0; i < 7000; i++) {
            try {
                Thread.sleep(1); // sleep for 1 second (1000 ms)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

}
