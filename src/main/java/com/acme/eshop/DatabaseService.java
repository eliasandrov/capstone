package com.acme.eshop;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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

public class DatabaseService {

    private Logger logger;
    private static final Properties sqlCommands = new Properties();
    private static HikariDataSource hikariDatasource;

    public DatabaseService() {
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public static HikariDataSource getHikariDatasource() {
        return hikariDatasource;
    }

    public void setHikariDatasource(HikariDataSource hikariDatasource) {
        DatabaseService.hikariDatasource = hikariDatasource;
    }

    public void selectOrder (){
        try {
            logger.info("Selecting Orders...");
            Statement statement = hikariDatasource.getConnection().createStatement();
            String stmt=sqlCommands.getProperty("order.select.002");
            logger.info(stmt);
            ResultSet resultSet = statement.executeQuery(stmt);

            while (resultSet.next()) {
                //@formatter:off
                logger.info("ORDERID:{}, CUSTOMERCODE:{}, PAYMENTTYPE:{}, AMOUNTBEFOREDISCOUNT:{}, DISCOUNT:{}, AMOUNTAFTERDISCOUNT:{}.",
                        resultSet.getInt("ORDERID"),
                        resultSet.getString("CUSTOMERCODE"),
                        resultSet.getString("PAYMENTTYPE"),
                        resultSet.getFloat("AMOUNTBEFOREDISCOUNT"),
                        resultSet.getFloat("DISCOUNT"),
                        resultSet.getFloat("AMOUNTAFTERDISCOUNT")
                );
                //@formatter:on
            }

            logger.info("Selecting Order Items...");
            stmt=sqlCommands.getProperty("orderitem.select.002");
            logger.info(stmt);
            resultSet = statement.executeQuery(stmt);

            //SELECT ID,ORDERID,PRODUCTCODE,TOTALCOST FROM CUSTORDERITEM

            while (resultSet.next()) {
                //@formatter:off
                logger.info("ID:{}, ORDERID:{}, PRODUCTCODE:{}, TOTALCOST:{}",
                        resultSet.getInt("ID"),
                        resultSet.getInt("ORDERID"),
                        resultSet.getInt("PRODUCTCODE"),
                        resultSet.getDouble("TOTALCOST")
                );
                //@formatter:on
            }

        } catch (SQLException throwables) {
            logger.error("Error occurred while retrieving data", throwables);
        }
    }

    public void selectProduct (){
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

    public void selectCustomer (){
        try {
            logger.info("Selecting customers...");
            Statement statement = hikariDatasource.getConnection().createStatement();
            String stmt=sqlCommands.getProperty("customer.select.002");
            logger.info(stmt);
            ResultSet resultSet = statement.executeQuery(stmt);

            while (resultSet.next()) {
                //@formatter:off
                logger.info("code:{}, name:{}, custtype:{}.",
                        resultSet.getInt("CODE"),
                        resultSet.getString("NAME"),
                        resultSet.getString("CUSTTYPE")
                );
                //@formatter:on
            }
        } catch (SQLException throwables) {
            logger.error("Error occurred while retrieving data", throwables);
        }
    }

    public void insertOrder(Order o) {
        String stmt;
        PreparedStatement preparedStatement;

        try {

            //INSERT ORDER
            stmt=sqlCommands.getProperty("order.insert.001");
            logger.info(stmt);
            preparedStatement = hikariDatasource.getConnection().prepareStatement(stmt);

            preparedStatement.clearParameters();

            preparedStatement.setInt(1, o.getOrderId());
            preparedStatement.setInt(2, o.getCustomer().getCode());
            preparedStatement.setString(3, o.getPaymentType().toString());
            preparedStatement.setDouble(4, o.getTotalAmountBeforeDiscount());
            preparedStatement.setDouble(5, o.getDiscount());
            preparedStatement.setDouble(6, o.getTotalAmountAfterDiscount());

            preparedStatement.addBatch();

            int[] affectedRows = preparedStatement.executeBatch();

            logger.debug("Rows inserted {}.", Arrays.stream(affectedRows).sum());

            //INSERT ORDER ITEMS
            stmt=sqlCommands.getProperty("orderitem.insert.001");
            preparedStatement = hikariDatasource.getConnection().prepareStatement(stmt);
            for (OrderItem oi : o.getOrderItems()){
                preparedStatement.clearParameters();

                preparedStatement.setInt(1, oi.getId());
                preparedStatement.setInt(2, o.getOrderId());
                preparedStatement.setInt(3, oi.getProduct().getCode());
                preparedStatement.setDouble(4, oi.getTotalCost());

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

        } catch (SQLException throwables) {
            logger.error("Error occurred while batch inserting data.", throwables);
        }
    }

    public void insertProduct (Product p) {
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

    public void insertCustomer (Customer c) {
        try (PreparedStatement preparedStatement = hikariDatasource.getConnection().prepareStatement(

                sqlCommands.getProperty("customer.insert.001"))) {

            preparedStatement.clearParameters();

            preparedStatement.setInt(1, c.getCode());
            preparedStatement.setString(2, c.getName());
            preparedStatement.setString(3, c.getCustType().toString());

            preparedStatement.addBatch();

            int[] affectedRows = preparedStatement.executeBatch();

            logger.debug("Rows inserted {}.", Arrays.stream(affectedRows).sum());

        } catch (SQLException throwables) {
            logger.error("Error occurred while batch inserting data.", throwables);
        }
    }

    public void loadSQLStatements() {
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

    public boolean createTables() {
        try (Statement statement = hikariDatasource.getConnection().createStatement()) {

            String stmt=sqlCommands.getProperty("product.create.001");
            logger.info(stmt);
            statement.execute(stmt);

            stmt=sqlCommands.getProperty("customer.create.001");
            logger.info(stmt);
            statement.execute(stmt);

            stmt=sqlCommands.getProperty("order.create.001");
            logger.info(stmt);
            statement.execute(stmt);

            stmt=sqlCommands.getProperty("orderitem.create.001");
            logger.info(stmt);
            statement.execute(stmt);


            logger.info("Tables created.");

            return true;
        } catch (SQLException throwables) {
            logger.warn("Cannot create tables: " + throwables.getMessage());
            return false;
        }
    }



}
