import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Camel implements Cloneable {

    private String name;
    private int x, y;
    private int[] lapCards; // to be filled with PlayerIDs
    private Boolean hasMoved = false; // has not moved at beginning.

    private float lapProbability; // probability to end first in current lap.
    private float raceProbability;




    public Camel(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return name;
    }

    public void moveCamel(int stepSize) {
        this.x += stepSize;
    }

    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Boolean getHasMoved() { return hasMoved; }
    public void setHasMoved() { this.hasMoved = true; }
    public void resetHasMoved() { this.hasMoved = false; }


    public void advanceCamel(Camel[] camels, int stepSize) {

        // if (camel.getX() == COLS - 1) {return; } // do not advance if already at end.

        // set hasMoved flag
        setHasMoved();

        // Get all camels to be moved
        List<Camel> carriedCamels = getCarriedCamels(camels);

        // y-shift
        int yShift = getFreeTile(this.x + stepSize, camels) - this.y;

        // Move the camels
        for (Camel c : carriedCamels) {
            c.moveCamel(stepSize);
            c.setY(c.getY() + yShift);
        }

        // printPositions();
    }

    public int camelRanking(Camel[] camels) {
        List<Camel> sorted = Arrays.asList(camels.clone()); // clone to avoid side effects

        sorted.sort(Comparator
                .comparingInt(Camel::getX).reversed()
                .thenComparingInt(Camel::getY)
        );

        return sorted.indexOf(this) + 1;
    }

    private int getFreeTile(int x, Camel[] camels) {
        int y = 4;
        for (Camel camel : camels) {
            if (camel.getX() == x) {
                y -= 1;
            }
        }
        return y;
    }

    private List<Camel>  getCarriedCamels(Camel[] camels) {

        // get the current and all its carried camels
        List<Camel> carriedCamels = new ArrayList<>();

        carriedCamels.add(this); // add current

        // if race has started
        if (x > 0) {
            // add all camels with *smaller* y-value.
            for (Camel c : camels) {
                if (c.getX() == x && c.getY() < y) {
                    carriedCamels.add(c);
                }
            }
        }

        return carriedCamels;
    }

    public void setLapProbability(float prob) { this.lapProbability = prob; }
    public void setRaceProbability(float prob) { this.raceProbability = prob; }
    public float getLapProbability() { return lapProbability; }
    public float getRaceProbability() { return raceProbability; }

    @Override
    public Camel clone() {
        try {
            Camel copy = (Camel) super.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
