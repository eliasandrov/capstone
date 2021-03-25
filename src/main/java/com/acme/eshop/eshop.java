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
import static java.lang.System.lineSeparator;

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

        //Database Initialize
        eshop.initiateConnectionPooling();

        //OrderService - handles orders logic
        OrderService orderService = new OrderService();

        //DatabaseService - handles CRDUD operations
        DatabaseService databaseService = new DatabaseService();
        databaseService.setLogger(logger);
        databaseService.setHikariDatasource(hikariDatasource);
        databaseService.loadSQLStatements();
        databaseService.createTables();

        //ReportingService - handles reports
        ReportingService reportingService = new ReportingService();
        reportingService.setLogger(logger);
        reportingService.setDatabaseService(databaseService);

        //Add some products to database
        logger.info("Inserting products...");
        databaseService.insertProduct(new Product(1,"PRODUCT ONE", 100));
        databaseService.insertProduct(new Product(2,"PRODUCT TWO", 200));
        databaseService.insertProduct(new Product(3,"PRODUCT THREE", 300));
        databaseService.selectProduct();//get list of products to check

        //Add some customers to database
        databaseService.insertCustomer(new Customer(1,"CUSTOMER ONE", Customer.CustType.B2C));
        databaseService.insertCustomer(new Customer(2,"CUSTOMER TWO", Customer.CustType.B2B));
        databaseService.insertCustomer(new Customer(3,"CUSTOMER THREE", Customer.CustType.B2G));
        databaseService.selectCustomer();//get list of customers to check

        //Add some ORDERS to database
        Order order ;

        // ORDER 1 2 items 4500
        order = orderService.createNewOrder(1,databaseService.getCustomer(1), Order.PayentType.CASH);
        orderService.addNewOrderItem(1,order,databaseService.getProduct(1),10);
        orderService.addNewOrderItem(2,order,databaseService.getProduct(2),20);
        databaseService.insertOrder(order);

        // ORDER 2 1 item 21000
        order = orderService.createNewOrder(2,databaseService.getCustomer(2), Order.PayentType.CASH);
        orderService.addNewOrderItem(3,order,databaseService.getProduct(3),100);
        databaseService.insertOrder(order);

        // ORDER 3 2 items 2600
        order = orderService.createNewOrder(3,databaseService.getCustomer(2), Order.PayentType.CREDIT);
        orderService.addNewOrderItem(4,order,databaseService.getProduct(1),10);
        orderService.addNewOrderItem(5,order,databaseService.getProduct(3),10);
        databaseService.insertOrder(order);

        databaseService.selectOrder(); //check

        //PRINT REPORTS
        reportingService.numberAndCostOfPurchasesForCustomer(2);
        reportingService.numberAndCostOfPurchasesForProduct(1);
        reportingService.totalNumberAndCostOfPurchasesPerCustomer();

        exit(-1);
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

        config.setMaximumPoolSize(200);
        config.setConnectionTimeout(30000);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtsCacheSize", "500");
        hikariDatasource = new HikariDataSource(config);
    }


}
