package ru.avrsoft.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class ConnectionDB {
    static String userName = "postgres";
    static String password = "";
    static String connectionURL = "jdbc:postgresql://localhost:5432/testar";
    static String driver = "org.postgresql.Driver";

    private final DataSource dataSource;
    public static ConnectionDB INSTANCE = new ConnectionDB();


    public ConnectionDB() {

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(connectionURL);
        config.setUsername(userName);
        config.setPassword(password);
        dataSource = new HikariDataSource(config);
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
