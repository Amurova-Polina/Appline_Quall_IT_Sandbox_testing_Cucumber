package steps;

import helpers.Configuration;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class Hooks {
    protected static WebDriver webDriver;
    protected static Statement statement;
    private static Connection connection;

    @Before(order = 1)
    public void setupChromeDriver() {
        var driverType = Configuration.getProperty("driver.type");
        if (driverType.equals("local")) {
            WebDriverManager.chromedriver().setup();
            webDriver = new ChromeDriver();
        } else if (driverType.equals("remote")) {
            webDriver = initRemoteChromeDriver();
        } else throw new IllegalArgumentException("Неизвестный тип драйвера, " +
                "можно использовать только 'local' или 'remote'");
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        webDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(20));
    }

    @Before(order = 2)
    public void setDBConnection() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:h2:tcp://qualit.applineselenoid.fvds.ru:9092/mem:testdb",
                    "user",
                    "pass");
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @After(order = 1)
    public void tearDownChromeDriver() {
        webDriver.quit();
    }

    @After(order = 2)
    public void closeDBConnection() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException ignored) {
        }
    }

    private WebDriver initRemoteChromeDriver() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("browserName", Configuration.getProperty("browser.type"));
        selenoidOptions.put("browserVersion", Configuration.getProperty("browser.version"));
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("enableVideo", false);
        capabilities.setCapability("selenoid:options", selenoidOptions);
        try {
            return new RemoteWebDriver(
                    URI.create(Configuration.getProperty("selenoid.url")).toURL(),
                    capabilities
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}