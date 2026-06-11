package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ResetDatabase {
    public static void main(String[] args) {
        String host = "localhost";
        String port = "3306";
        String user = "root";
        String pass = "Aaron@2007";

        // Read from config.properties if exists
        java.io.File propFile = new java.io.File("config.properties");
        if (propFile.exists()) {
            java.util.Properties props = new java.util.Properties();
            try (java.io.FileInputStream in = new java.io.FileInputStream(propFile)) {
                props.load(in);
                host = props.getProperty("db.host", host);
                port = props.getProperty("db.port", port);
                user = props.getProperty("db.user", user);
                pass = props.getProperty("db.pass", pass);
            } catch (Exception e) {
                System.out.println("Could not load config.properties, using defaults.");
            }
        }

        String serverUrl = "jdbc:mysql://" + host + ":" + port + "/";
        String dbName = "hospital_db";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connecting to MySQL server at " + serverUrl);
            try (Connection conn = DriverManager.getConnection(serverUrl, user, pass);
                 Statement stmt = conn.createStatement()) {
                
                System.out.println("Dropping database if exists...");
                stmt.executeUpdate("DROP DATABASE IF EXISTS " + dbName);
                
                System.out.println("Creating database " + dbName + "...");
                stmt.executeUpdate("CREATE DATABASE " + dbName);
                
                System.out.println("Database reset successful.");
            }

            // Now connect to the new database to run the tables creation script
            String dbUrl = serverUrl + dbName;
            System.out.println("Connecting to new database at " + dbUrl);
            try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
                 Statement stmt = conn.createStatement()) {

                System.out.println("Creating patients table...");
                stmt.executeUpdate("CREATE TABLE patients (\n" +
                        "    patient_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "    name VARCHAR(100) NOT NULL,\n" +
                        "    age INT NOT NULL,\n" +
                        "    gender VARCHAR(10) NOT NULL,\n" +
                        "    phone VARCHAR(20) NOT NULL,\n" +
                        "    disease VARCHAR(150) NOT NULL,\n" +
                        "    address VARCHAR(255) NOT NULL,\n" +
                        "    visited VARCHAR(10) DEFAULT 'NO'\n" +
                        ")");

                System.out.println("Creating doctors table...");
                stmt.executeUpdate("CREATE TABLE doctors (\n" +
                        "    doctor_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "    name VARCHAR(100) NOT NULL,\n" +
                        "    specialization VARCHAR(100) NOT NULL,\n" +
                        "    phone VARCHAR(20) NOT NULL,\n" +
                        "    room_no VARCHAR(10) NOT NULL\n" +
                        ")");

                System.out.println("Creating appointments table...");
                stmt.executeUpdate("CREATE TABLE appointments (\n" +
                        "    appointment_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "    patient_id INT,\n" +
                        "    doctor_id INT,\n" +
                        "    appointment_date DATE,\n" +
                        "    appointment_time TIME,\n" +
                        "    status VARCHAR(20) DEFAULT 'Pending',\n" +
                        "    FOREIGN KEY(patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,\n" +
                        "    FOREIGN KEY(doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE\n" +
                        ")");

                System.out.println("Creating users table...");
                stmt.executeUpdate("CREATE TABLE users (\n" +
                        "    user_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "    username VARCHAR(50) UNIQUE NOT NULL,\n" +
                        "    password VARCHAR(50) NOT NULL,\n" +
                        "    role VARCHAR(20) DEFAULT 'Staff'\n" +
                        ")");

                System.out.println("Creating billing table...");
                stmt.executeUpdate("CREATE TABLE billing (\n" +
                        "    bill_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "    patient_id INT NOT NULL,\n" +
                        "    doctor_id INT NOT NULL,\n" +
                        "    consultation_fee DOUBLE DEFAULT 0.0,\n" +
                        "    room_charges DOUBLE DEFAULT 0.0,\n" +
                        "    medicine_charges DOUBLE DEFAULT 0.0,\n" +
                        "    other_charges DOUBLE DEFAULT 0.0,\n" +
                        "    total_amount DOUBLE DEFAULT 0.0,\n" +
                        "    billing_date VARCHAR(20) NOT NULL,\n" +
                        "    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,\n" +
                        "    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE\n" +
                        ")");

                System.out.println("Inserting default user accounts...");
                stmt.executeUpdate("INSERT INTO users (username, password, role) VALUES \n" +
                        "('admin', 'admin123', 'Admin'),\n" +
                        "('staff', 'staff123', 'Staff')");

                System.out.println("All initial tables created successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
