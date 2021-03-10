package serverPackage;

import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DB_Schema {

    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());

    private static void playersTable() {
        try {
            Connection connection = DatabaseManager.getDBConnection();
            Statement statement = connection.createStatement();

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + "player"
                            + "("
                            + "id" + " INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                            + "user_name" + " VARCHAR(100) NOT NULL, "
                            + "email" + " VARCHAR(100) NOT NULL UNIQUE, "
                            + "password" + " VARCHAR(128) NOT NULL, "
                            + "score" + " INT DEFAULT 0 NOT NULL, "
                            + "status" + " INT DEFAULT 0 NOT NULL"
                            + ")"
            );
            statement.close();
            connection.close();

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    private static void gamesTable() {
        try {
            Connection connection = DatabaseManager.getDBConnection();
            Statement statement = connection.createStatement();

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + "game"
                            + "("
                            + "id" + " INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                            + "first_player" + " VARCHAR(100) NOT NULL, "
                            + "second_player" + " VARCHAR(100) NOT NULL, "
                            + "winner" + " VARCHAR(100) DEFAULT NULL, "
                            + "status" + " INT DEFAULT 0 NOT NULL, "
                            + "board" + " VARCHAR(500) DEFAULT '' NOT NULL, "
                            + "recorded_by_first_player" + " INT DEFAULT 0 NOT NULL, "
                            + "recorded_by_second_player" + " INT DEFAULT 0 NOT NULL "
                            + ")"
            );
            statement.close();
            connection.close();

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    public static void createSchema() {
        try {
            playersTable();
            gamesTable();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
