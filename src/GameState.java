public class GameState {

    private Game game;
    private Player player;
    private int[] gameState;

    public GameState(Game game, Player player, int[] gameState) {
        this.game = game;
        this.player = player;
        this.gameState = gameState;
    }

    public void updateGameState() {
        /*
        General information:

        Game
        - int number players
        - int length winnerCamelCards
        - int length loserCamelCards
        - int racetrack length
        - int freeLapCards (per camel 5)

        Camels (5)
        - int x
        - int y
        - boolean hasMoved

        Players
        - int money
        - int raceCards (length)
         */
        gameState[0] = game.getNumberPlayers();
        gameState[1] = game.getWinnerCamelCards().size();
        gameState[2] = game.getLoserCamelCards().size();
        gameState[3] = game.getCols();
        int index = 4;
        for (Camel c : game.getCamels()) {
            gameState[index] = game.getFreeLapCards().stream()
                    .filter(card -> card.camel().equals(c))  // Keep cards that match the camel
                    .toList().size();
            index += 1;
        }
        for (Camel c : game.getCamels()) {
            gameState[index] = c.getX();
            gameState[index + 1] = c.getY();
            gameState[index + 2] = c.getHasMoved() ? 1 : 0; // converts to int
            index += 3;
        }
        for (Player player : game.getPlayers()) {
            gameState[index] = player.getMoney();
            gameState[index + 1] = player.getRaceCards().size();
            index += 2;
        }

        /*
        Player specific information:
            ...
         */
    }



}

