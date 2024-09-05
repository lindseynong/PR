import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

public class TestFacebookWithJUnit {
    static WebDriver driver;
    FirefoxOptions options = new FirefoxOptions();
    static String timestamp;

    @Before
    public void setUp() throws Exception {
        timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        System.setProperty("webdriver.gecko.driver", "C:\\Users\\Computer\\Desktop\\geckodriver.exe");
        options.addPreference("browser.tabs.remote.autostart", false);
        driver = new FirefoxDriver(options);
        driver.manage().window().maximize();

        System.out.println("🔧 [" + timestamp + "] Test started.");
    }

    @After
    public void tearDown() throws Exception {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(src, new File("C:\\Users\\Computer\\Desktop\\junit_fb_test_" + timestamp + ".png"));
            System.out.println("📸 Screenshot saved: junit_fb_test_" + timestamp + ".png");
        } catch (IOException e) {
            System.out.println("⚠️ Screenshot failed: " + e.getMessage());
        }

        if (driver != null) {
            driver.quit();
        }

        System.out.println("✅ [" + timestamp + "] Test finished.\n");
    }

    @Test
    public void testFacebookSignupForm() {
        driver.get("http://facebook.com");

        // Fill out fields
        driver.findElement(By.id("email")).sendKeys("iasjdiaosjd@gmail.com");
        driver.findElement(By.id("pass")).sendKeys("Asdasd123!");
        new Select(driver.findElement(By.id("day"))).selectByVisibleText("10");
        new Select(driver.findElement(By.id("month"))).selectByVisibleText("Jun");
        new Select(driver.findElement(By.id("year"))).selectByVisibleText("1990");

        try {
            WebElement genderRadio = driver.findElement(By.xpath("//label[text()='Male']/preceding-sibling::input"));
            genderRadio.click();
        } catch (Exception e) {
            System.out.println("⚠️ Gender selection failed.");
        }

        driver.findElement(By.name("websubmit")).click();

        // 🆕 Use explicit wait
        WebDriverWait wait = new WebDriverWait(driver, 10);
        boolean errorAppeared = false;
        try {
            WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'Please choose a password') or contains(text(), 'invalid')]")
            ));
            errorAppeared = errorMsg.isDisplayed();
        } catch (TimeoutException e) {
            System.out.println("❌ Error message not found within timeout.");
        }

        // Assertion
        assertTrue("Expected error message not displayed after invalid signup.", errorAppeared);
    }
}
