package databasePopulation.dailyPrice;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

import static databasePopulation.dailyPrice.DailyPriceTasks.connect;

//public class addDailyPrice {
//    public static void main(String args[]) {
public class StockUpdate implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            Connection con = connect.open();
            Statement stm = con.createStatement();
            String getTickerQuery = "select id,ticker from company";
            ResultSet companyData = stm.executeQuery(getTickerQuery);
            ArrayList<tickerDataModel> tickersList = new ArrayList<>();
            while (companyData.next()) {
                tickersList.add(new tickerDataModel(companyData.getInt(1), companyData.getString(2)));
            }
            if (tickersList.size() > 0) {
                tickersList.forEach(data -> {
                    insertTickerData(data.tickerSymbol, data.id);
                });
            }
            System.out.println(tickersList.get(0).tickerSymbol);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    /**
     * Method to insert or update ticker data in daily_price table
     *
     * @param symbol ticker Symbol
     * @param id     id of ticker Symbol given in company table
     */
    static void insertTickerData(String symbol, int id) {
        try {
            Date date = new Date();
            SimpleDateFormat insertFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String stringDate = insertFormatter.format(date);

            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();

            Stock stock = YahooFinance.get(symbol, true);
            List<HistoricalQuote> history = stock.getHistory(from, to);

            if (history.size() > 0) {
                String insertQuery = "";
                if (checkDataIsPresent(id)) {
                    insertQuery = "UPDATE daily_price SET open_price=" + history.get(0).getOpen() +
                            ", price_date=\'" + convertDate(history.get(0).getDate()) +
                            "\', close_price=" + history.get(0).getClose() +
                            ", low_price=" + history.get(0).getClose() +
                            ", high_price=" + history.get(0).getClose() +
                            ", volume=" + history.get(0).getClose() +
                            ", last_updated=\'" + stringDate +
                            "\' where ticker_id=" + id;
                } else {
                    insertQuery = "INSERT INTO daily_price( " +
                            "ticker_id,price_date, " +
                            "open_price,close_price,low_price,high_price,volume) VALUES( "
                            + id +
                            ",'" + convertDate(history.get(0).getDate()) + "'" +
                            "," + history.get(0).getOpen() +
                            "," + history.get(0).getClose() +
                            "," + history.get(0).getLow() +
                            "," + history.get(0).getHigh() +
                            "," + history.get(0).getVolume() +
                            ") ";
                }
                Connection con = connect.open();
                Statement stm = con.createStatement();
                stm.executeUpdate(insertQuery);
                System.out.println("Record of " + history.get(0).getSymbol() + " - " + stringDate + " inserted");
            } else {
                System.out.println("No Record found for " + symbol);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to convert Calendar value to date
     *
     * @param cal Calender time
     * @return Date time
     */
    private static String convertDate(Calendar cal) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = format.format(cal.getTime());
        return formatDate;
    }

    /**
     * Method to check ticker symbol is  present or not
     *
     * @param id ticker_id got from company table
     * @return true if symbol is present else false
     */
    private static boolean checkDataIsPresent(int id) {
        boolean isDataPresent = false;
        try {
            Connection con = connect.open();
            Statement stm = con.createStatement();
            String checkQuery = "select id from daily_price where ticker_id=" + id;
            ResultSet data = stm.executeQuery(checkQuery);
            if (data.next()) {
                isDataPresent = true;
            } else {
                isDataPresent = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDataPresent;
    }
}

/**
 * Class used as interface to store data as tuple
 */
class tickerDataModel {
    int id;
    String tickerSymbol;

    public tickerDataModel(int id, String tickerSymbol) {
        super();
        this.id = id;
        this.tickerSymbol = tickerSymbol;
    }
}
