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

        OrderService orderService = new OrderService();
        DatabaseService databaseService = new DatabaseService();

        //Database Initialize
        eshop.initiateConnectionPooling();

        databaseService.setLogger(logger);
        databaseService.setHikariDatasource(hikariDatasource);
        databaseService.loadSQLStatements();
        databaseService.createTables();

        //Add some products to database
       logger.info("Inserting products...");
       Product product1=new Product(1,"PRODUCT ONE", 100F);
        Product product2=new Product(2,"PRODUCT TWO", 200F);

        databaseService.insertProduct(product1);
        databaseService.insertProduct(product2);
        databaseService.selectProduct();

        //Add a new customer and save to database
        Customer customer1=new Customer(1,"CUSTOMER ONE", Customer.CustType.B2G);
        databaseService.insertCustomer(customer1);
        databaseService.selectCustomer();

        //Create a new order and save to database
        Order order1=new Order(1,customer1, Order.PayentType.CREDIT);//amountAfterDiscount=350
        orderService.addNewOrderItem(order1,product1,10);

        logger.info("added new order amountBeforeDiscount={}, discount={}, amountAfterDiscount={} "
                ,order1.getTotalAmountBeforeDiscount()
                ,order1.getDiscount()
                ,order1.getTotalAmountAfterDiscount()
        );

        databaseService.insertOrder(order1);
        databaseService.selectOrder();

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

    public void initiateConnectionPooling() {
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
