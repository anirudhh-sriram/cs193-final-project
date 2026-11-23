package ballondor;

/**
 *The LabelEntry class allows the LabelLoader to use it and store values from all five columns
 * of the csv file into one object.
 */
public class LabelEntry {
    private final int awardYear;
    private final int rank;
    private final String player;
    private final String club;
    private final int points;
    public LabelEntry(int awardYear, int rank, String player, String club, int points) {
        this.awardYear = awardYear;
        this.rank = rank;
        this.player = player;
        this.club = club;
        this.points = points;
    }
    public int awardYear() {
        return awardYear;
    }
    public int rank() {
        return rank;
    }
    public String player() {
        return player;
    }
    public String club() {
        return club;
    }
    public int points() {
        return points;
    }
}
