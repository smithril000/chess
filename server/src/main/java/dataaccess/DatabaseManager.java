package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws ResponseException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new ResponseException(400, "failed to create database");
        }
    }

    static public void dbHelper(String statement) throws ResponseException {
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new ResponseException(400, "failed to execute statement");
        }
    }

    static public void createDatabaseHelper() throws ResponseException {
        databaseName = "chess";
        try{
            createDatabase();
        }catch(ResponseException ex){
            throw new ResponseException(400, "error with database");
        }
        //now we want to describe our tables / create them

    }

    public static void createUserDate(UserData user) throws ResponseException {
        //now add to my db
        String sql = "INSERT INTO userData (username, email, password) VALUES (?, ?, ?)";
        System.out.println(sql);

        try (var conn = getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, user.email());
            preparedStatement.setString(3, user.password());

            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println("Rows inserted: " + rowsInserted);

        } catch (SQLException ex) {
            throw new ResponseException(400, "failed to execute statement" + ex.getMessage());
        }
    }




    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public static void pushAuthData(AuthData auth) throws ResponseException{
        //now add to my db
        String sql = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
        System.out.println(sql);

        try (var conn = getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, auth.authToken());
            preparedStatement.setString(2, auth.username());

            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println("Rows inserted: " + rowsInserted);

        } catch (SQLException ex) {
            throw new ResponseException(400, "failed to execute statement" + ex.getMessage());
        }
    }

    public static void clear() throws ResponseException{
        try (var conn = getConnection()){
            var statement1 = "TRUNCATE TABLE users";
            var statement2 = "TRUNCATE TABLE authData";
            var statement3 = "TRUNCATE TABLE games";
            try (var ps = conn.prepareStatement(statement1)) {

                ps.executeUpdate();
            }
            try (var ps = conn.prepareStatement(statement2)) {

                ps.executeUpdate();
            }
            try (var ps = conn.prepareStatement(statement3)) {

                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, "Error in database" + ex.getMessage());
        }
    }




    static Connection getConnection() throws ResponseException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new ResponseException(400, "failed to get connection"+ ex.getMessage());
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }
}
