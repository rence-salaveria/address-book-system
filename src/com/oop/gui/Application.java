package com.oop.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Vector;

public class Application extends JFrame {
    private JPanel appPanel;
    private JPanel inputPanel;
    private JPanel buttonPanel;
    private JLabel appName;
    private JLabel nameInputLabel;
    private JLabel idInputLabel;
    private JLabel numberInputLabel;
    private JLabel emailInputLabel;
    private JLabel iconHolder;
    private JTextField nameInputField;
    private JTextField idInputField;
    private JTextField numberInputField;
    private JTextField emailInputField;
    private JButton addButton;
    private JButton showButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JTable resultTable;
    private int counter = 0;
    public Application() {
        super("Address Book");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(300, 270);
        this.setResizable(false);
        this.setLayout(new FlowLayout(FlowLayout.CENTER));

        ImageIcon logo = new ImageIcon("../assets/contact.png");

        addButton = new JButton();
        addButton.setText("Add Contact");
        addButton.setPreferredSize(new Dimension(120, 30));
        showButton = new JButton();
        showButton.setText("Show Contacts");
        showButton.setPreferredSize(new Dimension(120, 30));
        updateButton = new JButton();
        updateButton.setText("Modify Contact");
        updateButton.setPreferredSize(new Dimension(120, 30));
        deleteButton = new JButton();
        deleteButton.setText("Delete Contact");
        deleteButton.setPreferredSize(new Dimension(120, 30));

        nameInputField = new JTextField(12);

        idInputField = new JTextField(12);
        idInputField.setText(Integer.toString(DatabaseManager.getCurrentID()));
        idInputField.setEnabled(false);

        numberInputField = new JTextField(12);
        emailInputField = new JTextField(12);

        appName = new JLabel("Address Book System");
        appName.setFont(new Font("assets/Roboto-Light.ttf", Font.PLAIN, 18));
        appName.setVerticalAlignment(JLabel.CENTER);

        nameInputLabel = new JLabel("Full Name: ");

        idInputLabel = new JLabel("Contact ID: ");

        numberInputLabel = new JLabel("Phone Number: ");

        emailInputLabel = new JLabel("E-mail address: ");

        iconHolder = new JLabel();
        iconHolder.setIcon(logo);

        appPanel = new JPanel();
        appPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        appPanel.setPreferredSize(new Dimension(200, 30));
        appPanel.add(appName);
        appPanel.add(iconHolder);

        inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setPreferredSize(new Dimension(250, 100));
        inputPanel.add(idInputLabel);
        inputPanel.add(idInputField);
        inputPanel.add(nameInputLabel);
        inputPanel.add(nameInputField);
        inputPanel.add(emailInputLabel);
        inputPanel.add(emailInputField);
        inputPanel.add(numberInputLabel);
        inputPanel.add(numberInputField);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setPreferredSize(new Dimension(300, 200));
        buttonPanel.add(addButton);
        buttonPanel.add(showButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);

        addButton.addActionListener(actionEvent -> {
            DatabaseManager.connect(false);
            revertFields();
            DatabaseManager.disconnect();
            new JOptionPane();
            try {
                String name = nameInputField.getText();
                int id = Integer.parseInt(idInputField.getText());
                String number = numberInputField.getText();
                String email = emailInputField.getText();
                if (name.equals("") || number.equals("") || email.equals("")) {
                    throw new EmptyInformationException("Fill up the information fields");
                }
                DatabaseManager.connect(false);
                DatabaseManager.addRecord(id, name, number, email);
                JOptionPane.showMessageDialog(null, "Successfully added Record");
                DatabaseManager.disconnect();
            } catch (NullPointerException e) {
                // JOptionPane for dialog boxes and message dialogs
                JOptionPane.showMessageDialog(null, "Failed to add record. Check your inputs");
            } catch (EmptyInformationException ex) {
                JOptionPane.showMessageDialog(null, "Please fill up the information fields");
            }
            DatabaseManager.connect(false);
            revertFields();
            DatabaseManager.disconnect();
        });

        // 2. delete name button
        deleteButton.addActionListener(actionEvent -> {
            counter = 0;
            counter++;
            new JOptionPane();
            if (counter == 1) {
                appName.setText("Delete Contact by ID");
                additionalDelete();
            } else if (counter == 2) {
                try {
                    int id = Integer.parseInt(idInputField.getText());
                    DatabaseManager.connect(false);
                    DatabaseManager.deleteRecord(id);
                    DatabaseManager.disconnect();
                } catch (NumberFormatException | NullPointerException e) {
                    // JOptionPane for dialog boxes and message dialogs
                    JOptionPane.showMessageDialog(null, "Failed to delete record. Check your inputs");
                }
                counter = 0;
                DatabaseManager.connect(false);
                revertFields();
                DatabaseManager.disconnect();
            }
        });

        updateButton.addActionListener(actionEvent -> {
            counter = 0;
            counter++;
            new JOptionPane();
            if (counter == 1) {
                appName.setText("Update Record by ID");
                additionalUpdate();
            } else if (counter == 2) {
                try {
                    String name = nameInputField.getText();
                    int id = Integer.parseInt(idInputField.getText());
                    String number = numberInputField.getText();
                    String email = emailInputField.getText();
                    if (name.equals("") || number.equals("") || email.equals("")) {
                        throw new EmptyInformationException("Fill up the information fields");
                    }
                    DatabaseManager.connect(false);
                    DatabaseManager.updateRecord(id, name, number, email);
                    JOptionPane.showMessageDialog(null, "Successfully updated Record");
                    DatabaseManager.disconnect();
                } catch (NumberFormatException | NullPointerException e) {
                    // JOptionPane for dialog boxes and message dialogs
                    JOptionPane.showMessageDialog(null, "Failed to update record. Check your inputs");
                } catch (EmptyInformationException ex) {
                    JOptionPane.showMessageDialog(null, "Please fill up the information fields");
                }
                counter = 0;
                DatabaseManager.connect(false);
                revertFields();
                DatabaseManager.disconnect();
            }
        });

        showButton.addActionListener(actionEvent -> {
            try {
                DatabaseManager.connect(false);
                resultTable = new JTable(buildTableModel(Objects.requireNonNull(DatabaseManager.showRecord())));
                JFrame f = new JFrame();
                f.setSize(600, 300);
                f.add(resultTable);
                f.setVisible(true);
                DatabaseManager.disconnect();
            } catch (SQLException e) {
                System.out.println("Cannot show record");
            }
        });

        this.add(appPanel);
        this.add(inputPanel);
        this.add(buttonPanel);
        setLocationRelativeTo(null);
        this.setVisible(true);
    }

    // boilerplate code
    public static DefaultTableModel buildTableModel(ResultSet rs)
            throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        // names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }
        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }

    public static void main(String[] args) {
        DatabaseManager.connect(true);
        new Application();
        DatabaseManager.disconnect();
    }

    public void additionalUpdate() {
        idInputField.setEnabled(true);
        nameInputField.setEnabled(true);
        emailInputField.setEnabled(true);
        numberInputField.setEnabled(true);
    }

    public void additionalDelete() {
        idInputField.setEnabled(true);
        nameInputField.setEnabled(false);
        emailInputField.setEnabled(false);
        numberInputField.setEnabled(false);
    }

    public void revertFields() {
        idInputField.setEnabled(false);
        nameInputField.setEnabled(true);
        emailInputField.setEnabled(true);
        numberInputField.setEnabled(true);
        idInputField.setText("");
        nameInputField.setText("");
        emailInputField.setText("");
        numberInputField.setText("");
        idInputField.setText(Integer.toString(DatabaseManager.getCurrentID()));
    }
}
