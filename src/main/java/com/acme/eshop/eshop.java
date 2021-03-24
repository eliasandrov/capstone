package com.acme.eshop;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;
import static java.lang.System.exit;

public class eshop {

    private static final Logger logger = LoggerFactory.getLogger(eshop.class);
    //private static final String DB_URL = "jdbc:h2:~/sample";
    private static final String DB_URL = "jdbc:h2:mem:db1";
    private static final String DB_USERNAME = "sa";
    private static final String DB_PASSWORD = "";
    private static final Properties sqlCommands = new Properties();
    private static HikariDataSource hikariDatasource;

    private Server h2Server, webServer;

    public static void main(String[] args) {
        eshop eshop = new eshop();

       logger.info("Initializing ...");

        // Start H2 database server
        eshop.startH2Server();

        //initiateConnectionPooling
        eshop.initiateConnectionPooling();

        //Load SQL
        eshop.loadSQLStatements();

        //Create tables
        eshop.createTables();

       //Add products to database
       logger.info("Inserting products...");
        eshop.insertProduct(new Product(1,"PRODUCT ONE", 100F));
        eshop.insertProduct(new Product(2,"PRODUCT TWO", 200F));

        eshop.selectProduct();
    }

    private void selectProduct (){
        try {
            logger.info("Selecting products...");
            Statement statement = hikariDatasource.getConnection().createStatement();
            String stmt=sqlCommands.getProperty("product.select.002");
            logger.info(stmt);
            ResultSet resultSet = statement.executeQuery(stmt);

            while (resultSet.next()) {
                //@formatter:off
                logger.info("code:{}, description:{}, price:{}.",
                        resultSet.getInt("CODE"),
                        resultSet.getString("DESCRIPTION"),
                        resultSet.getFloat("PRICE")
                       );
                //@formatter:on
            }
        } catch (SQLException throwables) {
            logger.error("Error occurred while retrieving data", throwables);
        }
    }

    private void insertProduct (Product p) {
        try (PreparedStatement preparedStatement = hikariDatasource.getConnection().prepareStatement(

                sqlCommands.getProperty("product.insert.001"))) {

            preparedStatement.clearParameters();

            preparedStatement.setInt(1, p.getCode());
            preparedStatement.setString(2, p.getDescription());
            preparedStatement.setFloat(3, p.getPrice());

            preparedStatement.addBatch();

            int[] affectedRows = preparedStatement.executeBatch();

            logger.debug("Rows inserted {}.", Arrays.stream(affectedRows).sum());

        } catch (SQLException throwables) {
            logger.error("Error occurred while batch inserting data.", throwables);
        }
    }

    private void loadSQLStatements() {
        try (InputStream inputStream = eshop.class.getClassLoader().getResourceAsStream("sql.properties")) {
            if (inputStream == null) {
                logger.error("Unable to load SQL commands.");
                exit(-1);
            }
            sqlCommands.load(inputStream);
        } catch (IOException e) {
            logger.error("Error while loading SQL commands.", e);
        }
    }

    private boolean createTables() {
        try (Statement statement = hikariDatasource.getConnection().createStatement()) {

            String stmt=sqlCommands.getProperty("product.create.001");
            logger.info(stmt);
            statement.execute(stmt);

            logger.info("Tables created.");

            return true;
        } catch (SQLException throwables) {
            logger.warn("Cannot create tables: " + throwables.getMessage());
            return false;
        }
    }

    private void startH2Server() {
        try {
            h2Server = Server.createTcpServer("-tcpAllowOthers", "-tcpDaemon");
            h2Server.start();
            webServer = Server.createWebServer("-webAllowOthers", "-webDaemon");
            webServer.start();
            logger.info("H2 Database server is now accepting connections.");
        } catch (SQLException throwables) {
            logger.error("Unable to start H2 database server.", throwables);
            exit(-1);
        }
        logger.info("H2 server has started with status '{}'.", h2Server.getStatus());
    }

    private void initiateConnectionPooling() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USERNAME);
        config.setPassword(DB_PASSWORD);

        config.setConnectionTimeout(10000);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(1800000);
        config.setMinimumIdle(1);
        config.setMaxLifetime(5);
        config.setAutoCommit(true);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtsCacheSize", "500");
        hikariDatasource = new HikariDataSource(config);
    }



}
