package com.oop.gui;

import com.opencsv.*;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CSVOperator {
    // used for exporting the database values in a CSV file
    public static void exportToCSV() {
        new JOptionPane();
        try {
            FileUtils.deleteQuietly(new File("csv/exported.csv"));
            File file = new File("csv/exported.csv");
            FileWriter output = new FileWriter(file);
            CSVWriter writer = new CSVWriter(
                    output,
                    ' ',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END
            );
            List<String[]> data = new ArrayList<>();
            ResultSet rs = DatabaseManager.tableSet();
            while (Objects.requireNonNull(rs).next()) {
                data.add(new String[]{Integer.toString(rs.getInt(1)), rs.getString(2), rs.getString(3), rs.getString(4)});
            }
            data.toArray(new String[0][0]);
            writer.writeAll(data);
            writer.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to write export data to a CSV file. Try again.");
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(null, "Failed to fetch data. Check your database connection then try again");
        }

    }

    // used to import a CSV file to the database (will overwrite values currently in db)
    public static void importFromCSV() {
        new JOptionPane();
        try {
            CSVReader reader = new CSVReader(new FileReader("csv/contacts.csv"));
            String[] nextLine;
            String[] tokenData;
            while ((nextLine = reader.readNext()) != null) {
                for (String line : nextLine) {
                    tokenData = line.split(" ");
                    DatabaseManager.connect(false);
                    DatabaseManager.addRecord(Integer.parseInt(tokenData[0]), tokenData[1], tokenData[2], tokenData[3]);
                    DatabaseManager.disconnect();
                }
            }
            reader.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "File may not be existing. Check the CSV folder of the application");
        } catch (NumberFormatException ne) {
            JOptionPane.showMessageDialog(null, "CSV Format invalid. Check the file if it has any errors in the data");
        }

    }

}
