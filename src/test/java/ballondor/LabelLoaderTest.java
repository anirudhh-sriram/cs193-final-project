package ballondor;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class uses JUnit tests to make sure that the LabelLoader correctly read and parsed the csv
 * files, without any logical errors such as having fewer than 30 nominees,
 * or having more than 1 winner in one year.
 */
class LabelLoaderTest {
    //This variable holds the path of the csv file within this project, and this can be used
    //to then load all the LabelEntry objects using the LabelLoader class.
    private static final Path LABELS = Path.of("data", "ballon_dor_labels.csv");

    @Test
    void everyAwardYearHasThirtyNominees() throws IOException {
        ArrayList<LabelEntry> entries = LabelLoader.load(LABELS);
        // The nested for loop goes through every row in the CSV file, and outer loop and inner loop
        //check which entries have matching award years, in which case the count in incremented.
        for (int i = 0; i < entries.size(); i++) {
            int year = entries.get(i).awardYear();

            int count = 0;
            for (int j = 0; j < entries.size(); j++) {
                if (entries.get(j).awardYear() == year) {
                    count++;
                }
            }
            //Count should equal 30 because there should be exactly 30 nominees each year.
            assertEquals(30, count, "Wrong nominee count for " + year);
        }
    }

    @Test
    void everyAwardYearHasExactlyOneWinner() throws IOException {
        ArrayList<LabelEntry> entries = LabelLoader.load(LABELS);

        for (int i = 0; i < entries.size(); i++) {
            int year = entries.get(i).awardYear();

            int winners = 0;
            for (int j = 0; j < entries.size(); j++) {
                if (entries.get(j).awardYear() == year && entries.get(j).rank() == 1) {
                    winners++;
                }
            }
            //Checks to see if the number of players who had a rank equal to 1 was only one
            //for each year.
            assertEquals(1, winners, "Wrong winner count for " + year);
        }
    }

    @Test
    void pointsNeverIncreaseAsRankGetsWorse() throws IOException {
        ArrayList<LabelEntry> entries = LabelLoader.load(LABELS);

        for (int i = 0; i < entries.size(); i++) {
            for (int j = 0; j < entries.size(); j++) {
                LabelEntry better = entries.get(i);
                LabelEntry worse = entries.get(j);

                // Used >= instead of > because some players can have the same number of points.
                if (better.awardYear() == worse.awardYear() && better.rank() < worse.rank()) {
                    //Checks if any player with a higher rank has lower points
                    assertTrue(better.points() >= worse.points(),
                            "Points out of order in " + better.awardYear() + ": "
                                    + better.player() + " vs " + worse.player());
                }
            }
        }
    }
}