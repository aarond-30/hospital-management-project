package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ViewPatient extends JFrame {

    JTable table;

    public ViewPatient() {

        setTitle("Patient Records");
        setSize(700, 400);

        String[] columns = {
                "ID", "Name", "Age",
                "Gender", "Phone",
                "Disease", "Address"
        };

        String[][] data = {
                {"1", "Aaron", "20", "Male",
                 "9876543210", "Fever", "Bangalore"}
        };

        table = new JTable(
                new DefaultTableModel(data, columns));

        JScrollPane pane = new JScrollPane(table);

        add(pane);

        setVisible(true);
    }
}
