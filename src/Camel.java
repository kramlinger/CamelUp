public class Camel {

    private String name;
    private int x, y;
    private int[] lapCards; // to be filled with PlayerIDs
    private Boolean hasMoved = false; // has not moved at beginning.


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




}
