package databasePopulation.dailyPrice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import databasePopulation.dailyPrice.ScheduleListener;
import databasePopulation.databaseConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

public class DailyPriceTasks {
    static databaseConnection connect = new databaseConnection();

    /**
     * Method to create a table called dailyPrice
     * which will have date of company's ticker symbol(foreign key),
     * price date, created date, last updated date,
     * open, close, low, high and volume price
     */
    public static String createDailyPriceTable() {
        String output = null;
        try {
            Connection con = connect.open();
            Statement stm = con.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS daily_price (id INTEGER(10) AUTO_INCREMENT, ticker_id INTEGER(10), price_date DATE ,created_date DATETIME, last_updated DATETIME, open_price decimal(11,6) , close_price decimal(11,6) , low_price decimal(11,6) ,high_price decimal(11,6), volume BIGINT(10),PRIMARY KEY ( id ), FOREIGN KEY (ticker_id) REFERENCES company(id))";
            stm.executeUpdate(query);
            DatabaseMetaData meta = con.getMetaData();
            ResultSet res = meta.getTables(null, null, "daily_price",
                    new String[] {"TABLE"});
            if(res.next()) {
               output= "Created daily_price table successfully!";
            }
            else{
                output= "Sorry, something went wrong!";
            }
            con.close();
            return output;
        } catch (Exception e) {
            System.out.println(e);
            return e.toString();
        }
    }

    /**
     * Method to add data into daily price table
     */
    public static void addDataIntoDailyPrice() {
        ScheduleListener addJob = new ScheduleListener();
        addJob.contextInitialized(null);
    }

    /**
     * Method to get display the data of daily price table in JSON format
     */
    public static String getDailyPriceData() throws Exception {
        try {
            Connection con = connect.open();
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("select d2.id, d1.name, d1.ticker, d2.price_date , d2.created_date, d2.last_updated, " +
                    " d2.open_price, d2.close_price,d2.low_price, d2.high_price, d2.volume  from company d1 join daily_price d2 on d1.id=d2.ticker_id");

            ObjectMapper mapper = new ObjectMapper();
            ArrayNode arrayNode = mapper.createArrayNode();

            while (rs.next()) {
                ObjectNode stock = mapper.createObjectNode();
                stock.put("id", rs.getString(1));
                stock.put("name", rs.getString(2));
                stock.put("ticker_symbol", rs.getString(3));
                stock.put("priceDate", rs.getString(4));
                stock.put("createdDate", rs.getString(5));
                stock.put("lastUpdated", rs.getString(6));
                stock.put("openPrice", rs.getDouble(7));
                stock.put("closePrice", rs.getDouble(8));
                stock.put("lowPrice", rs.getDouble(9));
                stock.put("HighPrice", rs.getDouble(10));
                stock.put("volume", rs.getBigDecimal(11));
                arrayNode.add(stock);
            }
            con.close();
            String jsonResponse = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
            return jsonResponse;
        } catch (Exception e) {
            return e.toString();
        }
    }
}

