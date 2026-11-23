package ballondor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
/**
 * This class is to load every single csv row into a LabelEntry object, and then validate to check if
 * each row has the correct input and format in the CSV files, and reject rows that are bad.
 */
public class LabelLoader {

    //This method helps load the csv rows into LabelEntry objects.
    public static ArrayList<LabelEntry> load(Path csvPath) throws IOException {
        if (!Files.exists(csvPath)) {
            throw new IOException("Labels file not found: " + csvPath);
        }
        //Essentially changed format to the header being set as the first line in csv file, and skip
        //the header, while also trimming out space.
        CSVFormat format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).build();
        ArrayList<LabelEntry> entries = new ArrayList<>();
        ArrayList<String> seenPlayerYears = new ArrayList<>();
        //StandardCharsets.UTF_8 is for extra latin characters in names like Mbappé, with the é, so
        //that the reader is able to read and parse it.
        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
             CSVParser parser = format.parse(reader)) {
            for (CSVRecord record : parser) {
                LabelEntry entry = parseRecord(record);
                String playerYearKey = entry.awardYear() + "|" + entry.player();
                if (seenPlayerYears.contains(playerYearKey)) {
                    throw new IllegalArgumentException("Duplicate player in labels file: "
                            + entry.player() + " (" + entry.awardYear() + ")");
                }
                seenPlayerYears.add(playerYearKey);
                entries.add(entry);
            }
        }
        return entries;
    }
    //This method is used to read all 5 column from the csv file, and checks if they are valid,
    //and don't contain values that are impossible, such as a rank below 1.
    private static LabelEntry parseRecord(CSVRecord record) {
        try {
            int awardYear = Integer.parseInt(record.get("award_year"));
            int rank = Integer.parseInt(record.get("rank"));
            int points = Integer.parseInt(record.get("points"));

            if (rank < 1 || rank > 30) {
                throw new IllegalArgumentException("rank must be between 1 and " + 30);
            }
            if (points < 0) {
                throw new IllegalArgumentException("points cannot be negative");
            }
            return new LabelEntry(awardYear, rank, record.get("player"), record.get("club"), points);
        } catch (IllegalArgumentException e) {
            // NumberFormatException (from parseInt on blank or non-numeric cells) is a subclass
            // of IllegalArgumentException, so this also catches those. The row number makes
            // a typo in a 240-line file easy to find.
            throw new IllegalArgumentException(
                    "Bad row " + record.getRecordNumber() + " in labels file: " + e.getMessage(), e);
        }
    }
}