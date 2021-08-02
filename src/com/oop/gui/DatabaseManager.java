package com.oop.gui;

import javax.swing.*;
import java.sql.*;

public class DatabaseManager {
    static final String DB_URL = "jdbc:sqlite:names.db";
    static Connection conn = null;
    static PreparedStatement prep = null;

    public static void connect(boolean withMessage) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection((DB_URL));
            if (withMessage) {
                System.out.println("Connection to SQLite Established");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Connection Failed");
        }
    }

    public static void disconnect() {
        try {
            if (prep != null) {
                prep.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            // don't do anything
        }
    }
    public static int getCurrentID() {
        try {
            prep = conn.prepareStatement(
                    "SELECT seq FROM sqlite_sequence"
            );
            ResultSet rs = prep.executeQuery();
            return rs.getInt(1)+1;
        } catch (SQLException e) {
            System.out.println("Cannot retrieve maximum ID");
            return 0;
        }
    }

    public static void addRecord(int id, String name, String number, String email) {
        // this is a counter
        int i = 0;
        try {
            prep = conn.prepareStatement(
                    "INSERT INTO address_book VALUES (?, ?, ?, ?)"
            );
            prep.setInt(1, id);
            prep.setString(2, name);
            prep.setString(3, number);
            prep.setString(4, email);
            i = prep.executeUpdate();
            System.out.printf("%d record added to table\n", i);
        } catch (SQLException e) {
            System.out.println("Failed to add record to database");
        }
    }

    public static void deleteRecord(int id) {
        int i = 0;
        // used for checking how many columns are used
        ResultSet rs;
        // used to contain the number of columns
        int rowNumber = 0;
        new JOptionPane();
        try {
            prep = conn.prepareStatement(
                    "SELECT * FROM address_book"
            );
            rs = prep.executeQuery();
            while(rs.next()) {
                rowNumber++;
            }
            if (id > rowNumber || id <= 0) {
                JOptionPane.showMessageDialog(null, "You have entered an ID with no name assigned yet.");
            } else {
                prep = conn.prepareStatement(
                        "DELETE FROM address_book WHERE contact_id = ?"
                );
                prep.setInt(1, id);
                i = prep.executeUpdate();
                System.out.printf("%d record deleted from table\n", i);
                JOptionPane.showMessageDialog(null, "Record deleted Successfully");
            }
        } catch (SQLException e) {
            System.out.println("Failed to delete record to database");
        }
    }

    public static void updateRecord(int id, String name, String number, String email) {
        int i = 0;
        try {
            prep = conn.prepareStatement(
                    "UPDATE address_book SET contact_name = ?, contact_number = ?, contact_email = ? WHERE contact_id = ?"
            );
            prep.setString(1, name);
            prep.setString(2, number);
            prep.setString(3, email);
            prep.setInt(4, id);
            i = prep.executeUpdate();
            System.out.printf("%d record updated in table\n", i);
        } catch (SQLException e) {
            System.out.println("Failed to update record");
        }
    }

    public static ResultSet showRecord() {
        try {
            prep = conn.prepareStatement("SELECT * FROM address_book");
            return prep.executeQuery();
        } catch (SQLException e) {
            new JOptionPane();
            JOptionPane.showMessageDialog(null, "Cannot retrieve records, Check connection");
            return null;
        }
    }

}
