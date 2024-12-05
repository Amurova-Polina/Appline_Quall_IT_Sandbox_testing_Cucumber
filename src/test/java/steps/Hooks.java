package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;

public class Hooks {
    private static Process process;
    protected static WebDriver chromeDriver;
    protected static Statement statement;
    private static Connection connection;

    @Before(order = 1)
    public void setupStand() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "libs/qualit-sandbox.jar");
            process = processBuilder.start();

            // без этого у меня не успевает подняться приложение целиком :(
            Thread.sleep(10_000);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Before(order = 2)
    public void setupChromeDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @Before(order = 3)
    public void initChromeDriver() {
        chromeDriver = new ChromeDriver();
        chromeDriver.manage().window().maximize();
        chromeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        chromeDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        chromeDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(20));
    }

    @Before(order = 4)
    public void setDBConnection() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:h2:tcp://localhost:9092/mem:testdb",
                    "user",
                    "pass");
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @After(order = 1)
    public void tearDownChromeDriver() {
        chromeDriver.quit();
    }

    @After(order = 2)
    public void closeDBConnection() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException ignored) {
        }
    }

    @After(order = 3)
    public void closeStand() {
        process.destroyForcibly();
    }
}