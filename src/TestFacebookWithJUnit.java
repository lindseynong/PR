import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import static org.junit.Assert.*;

public class TestFacebookWithJUnit {
    static WebDriver driver;
    FirefoxOptions options = new FirefoxOptions();

    @Before
    public void setUp() throws Exception {
        System.setProperty("webdriver.gecko.driver", "C:\\Users\\Computer\\Desktop\\geckodriver.exe");
        options.addPreference("browser.tabs.remote.autostart", false);
        driver = new FirefoxDriver(options);
        driver.manage().window().maximize();
    }

    @After
    public void tearDown() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testFacebookLoginForm() {
        driver.get("http://facebook.com");

        driver.findElement(By.xpath(".//*[@id='email']")).sendKeys("iasjdiaosjd@gmail.com");
        driver.findElement(By.xpath(".//*[@id='pass']")).sendKeys("Asdasd123!");
        driver.findElement(By.xpath(".//*[@id='u_0_2']")).click();

        // 🆕 Assertion: Check if an expected element exists (example: an error message or stay on login page)
        boolean isLoginErrorDisplayed = driver.findElements(By.xpath("//*[contains(text(), 'incorrect')]")).size() > 0;

        // 🧪 Assertion placeholder
        assertTrue("Expected login error message is not displayed.", isLoginErrorDisplayed);
    }
}
