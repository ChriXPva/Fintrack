package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static String url = "jdbc:sqlite:fintrack.db";

    public static void setTestUrl(String testUrl) {
        url = testUrl;
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url);
        conn.setAutoCommit(false); // Suporte total a transações ACID
        return conn;
    }

    public static void inicializarBanco() {
        // DDL válida e simplificada para SQLite
        String sql = "CREATE TABLE IF NOT EXISTS transacoes ("
                + "  id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "  descricao VARCHAR(100) NOT NULL,"
                + "  valor DECIMAL(10,2) NOT NULL,"
                + "  tipo VARCHAR(10) NOT NULL,"
                + "  data DATE NOT NULL"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}