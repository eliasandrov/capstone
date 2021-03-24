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
       Product product1=new Product(1,"PRODUCT ONE", 100F);
        Product product2=new Product(2,"PRODUCT TWO", 200F);

        eshop.insertProduct(product1);
        eshop.insertProduct(product2);

        eshop.selectProduct();

        Customer customer1=new Customer(1,"CUSTOMER ONE", Customer.CustType.B2G);
        Order order1=new Order(1,customer1, Order.PayentType.CREDIT);

        //should be 100*10*(1-0.5-0.15)=350
        eshop.addNewOrderItem(order1,product1,10);
        logger.info("added new order amount= "+order1.getTotalAmountAfterDiscount());

    }

    public void addNewOrderItem(Order order, Product product, int quantity){
        /* Individuals get no discount
        ● Business users get a 20% discount
        ● Government users get a 50% discount
        ● 10% discount when the customer pays by wire transfer
        ● 15% discount when the customer uses a credit card
         */

        double discount =0;

        if (order.getCustomer().getCustType()== Customer.CustType.B2C){
            discount =0.00;
        }else if (order.getCustomer().getCustType()== Customer.CustType.B2B){
            discount =0.20;
         }else if (order.getCustomer().getCustType()== Customer.CustType.B2G) {
            discount = 0.50;
        }

        if (order.getPaymentType()== Order.PayentType.CASH){
            discount+=0.10;
        }else if (order.getPaymentType()== Order.PayentType.CREDIT){
            discount+=0.15;
        }

        //add the item to the order
        order.addOrderItem(new OrderItem(product,quantity));

        //update the order total with the discount
        order.setDiscount(discount);

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
