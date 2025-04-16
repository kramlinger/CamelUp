import java.util.*;
import java.util.stream.Collectors;

public class Player {

    private final int playerID;
    private int money = 0;
    private int pyramidCards = 0;

    private final List<LapCards> playerLapCards = new ArrayList<>();
    private List<RaceCards> raceCards;

    public Player(int playerID, Camel[] camels) {
        this.playerID = playerID;
        raceCards = new ArrayList<>();
        for (Camel c : camels) {
            raceCards.add(new RaceCards(c, this));
        }
    }

    public void addLapCard(LapCards card) {
        playerLapCards.add(card);
    }

    public void addPyramidCard() {
        this.pyramidCards = +1;
    }

    public void resetMoney() {
        this.money = 0;
    }

    public void returnRaceCards(RaceCards card) {
        raceCards.add(card);
    }

    public void calculateLapResults(Camel firstCamel, Camel secondCamel) {

        // Pyramid cards
        this.money += this.pyramidCards;
        this.pyramidCards = 0;

        // Leg bets
        for (LapCards card : playerLapCards) {
            if (card.camel() == firstCamel) {
                this.money += card.value();
            }
            else if (card.camel() == secondCamel) {
                this.money += 1;
            }
            else {
                this.money -= 1;
            }
        }

    }

    public void addMoney(int m) {
        this.money = m;
    }

    public int getMoney() {return this.money; }

    public int getPlayerID() {return this.playerID; }

    public String lapBets() {
        return playerLapCards.stream()
                .map(card -> card.camel().getName())
                .collect(Collectors.joining(", "));
    }

    public RaceCards setRaceCard(Camel c) {
        for (RaceCards card : raceCards) {
            if (card.camel() == c) {
                // remove the card from stack and pass it to game stack
                raceCards.remove(card);
                return card;
            }
        }
        System.out.printf("Player %d tries to take a non-existent card!", playerID);
        return null; // should not happen
    }


    public List<LapCards> returnPlayerLapCards() {
        List<LapCards> cards = new ArrayList<>(this.playerLapCards);
        this.playerLapCards.clear();
        return cards;
    }

    public List<RaceCards> getRaceCards() {
        return this.raceCards;
    }

}
