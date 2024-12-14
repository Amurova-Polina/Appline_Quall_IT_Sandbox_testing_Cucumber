package steps;

import helpers.Configuration;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
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
    public void setupWebDriver() {
        var driverType = Configuration.getProperty("driver.type");
        var browserType = Configuration.getProperty("browser.type");
        if (browserType.equals("chrome") || browserType.equals("firefox"))
            webDriver = switch (driverType) {
                case "local" -> initLocalWebDriver(browserType);
                case "remote" -> initRemoteWebDriver(browserType, Configuration.getProperty("browser.version"));
                default -> throw new IllegalArgumentException("Указан Неизвестный тип драйвера, " +
                        "можно использовать только 'local' или 'remote'");
            };
        else throw new IllegalArgumentException("Указан неизвестный тип браузера, " +
                "можно использовать только 'chrome' или 'firefox'");
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

    private WebDriver initLocalWebDriver(String browserType) {
        if (browserType.equals("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            return new FirefoxDriver();
        } else {
            WebDriverManager.chromedriver().setup();
            return new ChromeDriver();
        }
    }

    private WebDriver initRemoteWebDriver(String browserType, String browserVersion) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        Map<String, Object> selenoidOptions = new HashMap<>();
        capabilities.setCapability("browserName", browserType);
        capabilities.setCapability("browserVersion", browserVersion);
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