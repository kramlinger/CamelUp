public abstract class AIPlayer extends Player {

    protected Game game;

    public AIPlayer(int playerID, String name) {
        super(playerID, name);
    }

    protected boolean isLegal(int move) {

        // 0: Camel advance - this is always legal.
        if (move == 0) { return true; }

        // 1 - 5: Bet on leg victory
        Camel[] camels = game.getCamels();
        if (move >= 1 & move <= 5) { // candidateMove either 1, 2, 3, 4, 5
            Camel targetCamel = camels[move - 1];
            return game.getFreeLapCards().stream().anyMatch(card -> card.camel().equals(targetCamel));
        }

        // 6 - 10: Bet on race victory
        if (move >= 6 & move <= 10) { // candidateMove either 6, 7, 8, 9, 10
            Camel targetCamel = camels[move - 6];
            return game.getRaceCards().stream().anyMatch(card -> card.camel().equals(targetCamel));
        }

        // 11 - 16: Bet on race loser
        if (move >= 11) { // candidateMove either 11, 12, 13, 14, 15
            Camel targetCamel = camels[move - 11];
            return game.getRaceCards().stream().anyMatch(card -> card.camel().equals(targetCamel));
        }

        return false; // should not happen
    }

    protected void makeMove(int move) {
        // sanity check - should never happen.
        if (!isLegal(move)) {
            System.out.print("Tried to make an illegal move!");
        }

        // 0: Camel advance - this is always legal.
        if (move == 0) { game.advanceCamel(); }

        // 1 - 5: Bet on leg victory
        Camel[] camels = game.getCamels();
        if (move >= 1 & move <= 5) { // candidateMove either 1, 2, 3, 4, 5
            Camel targetCamel = camels[move - 1];
            game.takeLapCard(targetCamel);
        }

        // 6 - 10: Bet on race victory
        if (move >= 6 & move <= 10) { // candidateMove either 6, 7, 8, 9, 10
            Camel targetCamel = camels[move - 6];
            game.takeWinnerRaceCard(targetCamel);
        }

        // 11 - 16: Bet on race loser
        if (move >= 11) { // candidateMove either 11, 12, 13, 14, 15
            Camel targetCamel = camels[move - 11];
            game.takeLoserRaceCard(targetCamel);
        }

    }

    public void setGame(Game game) { this.game = game; }
    public boolean hasGame() {
        if (game == null) {
            return false;
        } else {
            return true;
        }
    }

}
