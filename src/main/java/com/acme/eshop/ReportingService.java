package com.acme.eshop;

import org.slf4j.Logger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReportingService {
    Logger logger;
    DatabaseService databaseService;

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public ReportingService() {
    }

    //Total number and cost of purchases for a particular customer
    public void numberAndCostOfPurchasesForCustomer(int customerCode) {

        double amount = 0;
        double num = 0;

        try {
            String stmt = databaseService.getSqlCommands().getProperty("order.select.003");
            PreparedStatement statement = databaseService.getHikariDatasource().getConnection().prepareStatement(stmt);

            statement.setInt(1, customerCode);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                amount += resultSet.getDouble("AMOUNTAFTERDISCOUNT");
                num++;
            }

        } catch (SQLException throwables) {
            logger.error("Error occurred while retrieving data", throwables);
        }

        logger.info("Total number and cost of purchases for customer {} = {},{}",customerCode,num,amount);
    }

    //Total number and cost of purchases for a particular product
    public void numberAndCostOfPurchasesForProduct(int productCode){
        double amount = 0;
        double num = 0;

        try {
            String stmt = databaseService.getSqlCommands().getProperty("orderitem.select.003");
            PreparedStatement statement = databaseService.getHikariDatasource().getConnection().prepareStatement(stmt);

            statement.setInt(1, productCode);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                amount += resultSet.getDouble("TOTALCOST");
                num++;
            }

        } catch (SQLException throwables) {
            logger.error("Error occurred while retrieving data", throwables);
        }

        logger.info("Total number and cost of purchases for product {} = {},{}",productCode,num,amount);

    }

    //Total number and cost of purchases per customer
    public void totalNumberAndCostOfPurchasesPerCustomer(){

        logger.info("Total number and cost of purchases per customer");
        logger.info("-----------------------------------------------");

        //num amount etc
        class ReportInfo  {
            public  int custID;
            public double num;//num of orders
            public double amount;//cost of orders

            public ReportInfo(int custID, double num, double amount ){
                this.custID=custID;
                this.num=num;
                this.amount=amount;
            }

            @Override
            public String toString() {
                return
                        "custID=" + custID +
                        ", num=" + num +
                        ", amount=" + amount;
            }
        }

        Hashtable<Integer,ReportInfo>  customers  = new Hashtable();

        try {
            String stmt = databaseService.getSqlCommands().getProperty("order.select.002");
            PreparedStatement statement = databaseService.getHikariDatasource().getConnection().prepareStatement(stmt);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int custID = resultSet.getInt("CUSTOMERCODE");
                double amount = resultSet.getDouble("AMOUNTAFTERDISCOUNT");

                if (customers.containsKey(custID)){
                    ((ReportInfo)customers.get(custID)).amount += amount;
                    ((ReportInfo)customers.get(custID)).num++;
                }else
                {
                    customers.put(custID,new ReportInfo(custID,1,amount));
                }
            }

        } catch (SQLException throwables) {
            logger.error("Error occurred while retrieving data", throwables);
        }

        // create an Enumeration object to read elements
        Enumeration e = customers.elements();

        // print elements of hashtable using enumeration
        while (e.hasMoreElements()) {
            logger.info("Total number and cost of purchases for customer {}",e.nextElement().toString());

        }

    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }



}
