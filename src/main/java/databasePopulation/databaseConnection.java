package databasePopulation;

import com.google.inject.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to make connection to SQL database
 */
public class databaseConnection {
    /**
     * Method to open the connection to SQL database using Google juices dependency injection
     */
    public Connection open() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        Injector injector = Guice.createInjector(new DBCredentialsModule());
        DBCredentials connectionData = injector.getInstance(DBCredentials.class);
        Map<String, String> details = connectionData.getCredentialDetails();
        Class.forName(details.get("driver")).newInstance();
        return DriverManager.getConnection(details.get("dbURL"), details.get("userName"), details.get("password"));
    }
}

/**
 * Class to get credential details for database connection
 */
class DBCredentials {
    private CredentialDetails details;

    /**
     * Method to inject dependency
     */
    @Inject
    public DBCredentials(CredentialDetails credentialDetails) {
        this.details = credentialDetails;
    }

    /**
     * Method to return credential details as Map
     */
    public Map<String, String> getCredentialDetails() {
        return details.getDetails();
    }
}

//Binding Module
class DBCredentialsModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CredentialDetails.class)
                .toProvider(CredentialProvider.class);
    }
}

//CredentialDetails interface
interface CredentialDetails {
    public Map<String, String> getDetails();
}

//CredentialDetails implementation
class CredentialDetailsImpl implements CredentialDetails {

    private String driver;
    private String dbURL;
    private String userName;
    private String password;

    @Inject
    public CredentialDetailsImpl(String driver, String dbURL, String userName, String password) {
        this.driver = driver;
        this.dbURL = dbURL;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public Map<String, String> getDetails() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("driver", this.driver);
        map.put("dbURL", this.dbURL);
        map.put("userName", this.userName);
        map.put("password", this.password);
        return map;
    }
}

class CredentialProvider implements Provider<CredentialDetails> {

    @Override
    public CredentialDetails get() {
        String driver = "com.mysql.cj.jdbc.Driver";
        String dbURL = "jdbc:mysql://localhost:3306/stock";
        String userName = "xxx";
        String password = "xxx";
        CredentialDetails details = new CredentialDetailsImpl(driver, dbURL, userName, password);
        return details;
    }

}
