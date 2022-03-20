package com.example.stockproject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static databasePopulation.company.CompanyTasks.*;
import static databasePopulation.dailyPrice.DailyPriceTasks.*;

@Path("/stock")
public class StockApplication {
    @GET
    @Produces("text/plain")
    @Path("/createTables")
    /**
     * successfull output of this method will be like following
     *
     * Created company table successfully!
     * Created daily_price table successfully!
     *
     */
    public String createTables() {
        return createCompanyTable()+
                System.lineSeparator()+
                createDailyPriceTable();
    }

    @GET
    @Produces("text/plain")
    @Path("/addCompany")
    /**
     * successfull output of this method will be like following
     *
     * Record of xxx inserted
     * Record of yyy inserted
     * Record of zzz inserted
     *
     */
    public String insertCompanyData() {
        return insertDataIntoCompany().toString();
    }

    @GET
    @Produces("text/plain")
    @Path("/getCompany")
    /**
     * output of this method will be like following
     *
     * [ {
     *   "name" : "xxx.",
     *   "ticker_symbol" : "^xxx"
     * }, {
     *   "name" : "yyy",
     *   "ticker_symbol" : "yyy"
     * } ]
     *
     */
    public String getCompanyTable() throws Exception {
        return getCompanyData();
    }

    @GET
        @Produces("text/plain")
        @Path("/updateStock")
        public String addPrice() {
        addDataIntoDailyPrice();
            return "Job scheduled to add daily price of company Stock";
        }

    @GET
    @Produces("text/plain")
    @Path("/getDailyPrice")
    /**
     * output of this method will be like following
     * [ {
     *   "id" : "123",
     *   "name" : "xxx",
     *   "ticker_symbol" : "^xxx",
     *   "priceDate" : "yyyy-mm-dd",
     *   "createdDate" : "yyyy-mm-dd hh:mm:ss",
     *   "lastUpdated" : "yyyy-mm-dd hh:mm:ss",
     *   "openPrice" : 99999.99999,
     *   "closePrice" : 99999.99999,
     *   "lowPrice" : 99999.99999,
     *   "HighPrice" : 99999.99999,
     *   "volume" : 9999999
     * }, {
     *   "id" : "456",
     *   "name" : "yyy.",
     *   "ticker_symbol" : "yyy",
     *   "priceDate" : "yyyy-mm-dd",
     *   "createdDate" : "yyyy-mm-dd hh:mm:ss",
     *   "lastUpdated" : "yyyy-mm-dd hh:mm:ss",
     *   "openPrice" : 99999.99999,
     *   "closePrice" : 99999.999991,
     *   "lowPrice" : 99999.99999,
     *   "HighPrice" : 99999.99999,
     *   "volume" : 2E+1
     * }]
     */
    public String getDailyPriceTable() throws Exception {
        return getDailyPriceData();
    }

}

