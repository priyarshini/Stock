package databasePopulation.company;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import databasePopulation.databaseConnection;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CompanyTasks {
    static databaseConnection connect = new databaseConnection();

    /**
     * Method to create a table called company
     * which will have name and ticker symbol of each companies
     */
    public static String createCompanyTable() {
        String output=null;
        try {
            Connection con = connect.open();
            Statement stm = con.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS company (id INTEGER(10) AUTO_INCREMENT, name VARCHAR(100), ticker VARCHAR(20), created_date DATETIME, last_updated DATETIME, PRIMARY KEY ( id ))";
            stm.executeUpdate(query);
            DatabaseMetaData meta = con.getMetaData();
            ResultSet res = meta.getTables(null, null, "company",
                    new String[] {"TABLE"});
            if(res.next()) {
                output= "Created company table successfully!";
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
     * Method to insert data into company table using objectMapper
     */
    public static StringBuilder insertDataIntoCompany() {
        StringBuilder output = new StringBuilder();
        try {
            String jsonCompaniesValue = "[{ \"name\" : \"Nasdaq\", \"ticker\" : \"^IXIC\" }," +
                    " { \"name\" : \"DraftKings Inc.\", \"ticker\" : \"DKNG\" }," +
                    " { \"name\" : \"S&P 500\", \"ticker\" : \"^GSPC\" }," +
                    " { \"name\" : \"Dow 30\", \"ticker\" : \"^DJI\" }]";
            ObjectMapper objectMapper = new ObjectMapper();
            List<CompanyDataModel> listCompanies = objectMapper.readValue(jsonCompaniesValue, new TypeReference<List<CompanyDataModel>>() {
            });
            System.out.println(listCompanies);
            Connection con = connect.open();
            Statement stm = con.createStatement();
            listCompanies.forEach(x -> {
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String strDate = formatter.format(date);

                String insertQuery = "INSERT INTO  company(name,ticker,created_date,last_updated ) VALUES(\'" +
                        x.getName() +
                        "\',\'" + x.getTicker() +
                        "\',\'" + strDate +
                        "\',\'" + strDate +
                        "\') ";
                try {
                    stm.executeUpdate(insertQuery);
                    output.append(System.lineSeparator() + "Record of " + x.getName() + " inserted");
                    System.out.println("Record of " + x.getName() + " inserted");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return output;
    }

    /**
     * Method to return JSON of company table
     */
    public static String getCompanyData() throws Exception {
        String jsonResponse = null;
        try {
            Connection con = connect.open();
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("select * from company");

            ObjectMapper mapper = new ObjectMapper();
            ArrayNode arrayNode = mapper.createArrayNode();

            while (rs.next()) {
                ObjectNode stock = mapper.createObjectNode();
                stock.put("name", rs.getString(2));
                stock.put("ticker_symbol", rs.getString(3));
                arrayNode.add(stock);
            }

            jsonResponse = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return jsonResponse;
    }
}

class CompanyDataModel{
    private String name;
    private String ticker;

    public CompanyDataModel() {
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setTicker(String tickerSymbol) {
        this.ticker = tickerSymbol;
    }
    public String getTicker() {
        return ticker;
    }

    @Override
    public String toString() {
        return "Company Data{" +
                " Name='"+name+
                ", Ticker='" + ticker+ '\'' +
                '}';
    }
}
