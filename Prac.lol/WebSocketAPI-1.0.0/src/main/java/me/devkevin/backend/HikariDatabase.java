package me.devkevin.backend;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A quick and easy way to setup a connection for Hikari. Once the instance is created it is suggested to run {@link
 * HikariDatabase#init(Connection)} and pass the {@link HikariDatabase#getConnection()} to test the connection and to
 * run anything like table generation.
 *
 * @since 9/26/2017
 */
public abstract class HikariDatabase {
    @Getter private final HikariDataSource hikari;

    public HikariDatabase(String host, int port, String database, String username, String password) throws Exception {
        this.hikari = new HikariDataSource();
        this.hikari.setMaximumPoolSize(10);
        this.hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        this.hikari.addDataSourceProperty("serverName", host);
        this.hikari.addDataSourceProperty("port", port);
        this.hikari.addDataSourceProperty("databaseName", database);
        this.hikari.addDataSourceProperty("user", username);
        this.hikari.addDataSourceProperty("password", password);
    }

    public HikariDatabase(String host, String database, String username, String password) throws Exception {
        this(host, 3306, database, username, password);
    }

    public abstract void init(Connection connection) throws Exception;

    public Connection getConnection() throws SQLException {
        return this.hikari.getConnection();
    }

    public boolean cleanup(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.close();
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean cleanup(PreparedStatement preparedStatement, ResultSet resultSet) {
        return this.cleanup(null, preparedStatement, resultSet);
    }

    public boolean cleanup(PreparedStatement preparedStatement) {
        return this.cleanup(null, preparedStatement, null);
    }
}
