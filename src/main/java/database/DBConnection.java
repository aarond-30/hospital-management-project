package database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gui.DBConfigDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);
    private static HikariDataSource dataSource;

    static {
        initializePool();
    }

    private static void initializePool() {
        Properties props = new Properties();
        java.io.File propFile = new java.io.File("config.properties");

        if (!propFile.exists()) {
            // Test default credentials. If they work, save it and proceed silently!
            if (testDirectConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "Aaron@2007")) {
                props.setProperty("db.host", "localhost");
                props.setProperty("db.port", "3306");
                props.setProperty("db.name", "hospital_db");
                props.setProperty("db.user", "root");
                props.setProperty("db.pass", "Aaron@2007");
                try (java.io.FileOutputStream out = new java.io.FileOutputStream(propFile)) {
                    props.store(out, "Auto-saved default DB Config");
                } catch (Exception ignored) {}
            } else {
                showConfigDialog();
            }
        }

        // Try reading configuration properties
        try (java.io.FileInputStream in = new java.io.FileInputStream("config.properties")) {
            props.load(in);
        } catch (Exception ex) {
            showConfigDialog();
            try (java.io.FileInputStream in = new java.io.FileInputStream("config.properties")) {
                props.load(in);
            } catch (Exception e) {
                throw new RuntimeException("Could not load DB properties.", e);
            }
        }

        String host = props.getProperty("db.host", "localhost");
        String port = props.getProperty("db.port", "3306");
        String dbName = props.getProperty("db.name", "hospital_db");
        String user = props.getProperty("db.user", "root");
        String pass = props.getProperty("db.pass", "Aaron@2007");
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName;

        // Verify connection with loaded settings. If it fails, prompt again.
        if (!testDirectConnection(jdbcUrl, user, pass)) {
            showConfigDialog();
            try (java.io.FileInputStream in = new java.io.FileInputStream("config.properties")) {
                props.load(in);
            } catch (Exception e) {
                throw new RuntimeException("Could not load DB properties.", e);
            }
            host = props.getProperty("db.host", "localhost");
            port = props.getProperty("db.port", "3306");
            dbName = props.getProperty("db.name", "hospital_db");
            user = props.getProperty("db.user", "root");
            pass = props.getProperty("db.pass", "Aaron@2007");
            jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
        }

        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(user);
            config.setPassword(pass);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // Connection Pool settings
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(30000);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
            logger.info("HikariCP Connection Pool initialized successfully.");

        } catch (Exception e) {
            logger.error("Failed to initialize HikariCP connection pool: ", e);
            showConfigDialog();
            initializePool(); // Retry pool creation
        }
    }

    private static boolean testDirectConnection(String url, String user, String pass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (java.sql.Connection conn = java.sql.DriverManager.getConnection(url, user, pass)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static void showConfigDialog() {
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception ignored) {}

        DBConfigDialog dialog = new DBConfigDialog(null);
        dialog.setVisible(true);
        if (!dialog.isConfigured()) {
            throw new RuntimeException("Database configuration cancelled by user.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            logger.info("HikariCP Connection Pool shut down successfully.");
        }
    }
}