import org.junit.*;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
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
import java.util.UUID;

import org.apache.commons.io.FileUtils;

public class TestFacebookWithJUnit {
    static WebDriver driver;
    FirefoxOptions options = new FirefoxOptions();
    static String timestamp;

    // 🆕 Rule to take screenshot only when test fails
    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            takeScreenshot("FAIL_" + description.getMethodName());
        }

        @Override
        protected void succeeded(Description description) {
            System.out.println("✅ Test passed: " + description.getMethodName());
        }
    };

    @Before
    public void setUp() {
        timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        System.setProperty("webdriver.gecko.driver", "C:\\Users\\Computer\\Desktop\\geckodriver.exe");
        options.addPreference("browser.tabs.remote.autostart", false);
        driver = new FirefoxDriver(options);
        driver.manage().window().maximize();
        System.out.println("🔧 [" + timestamp + "] Setup complete.");
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        System.out.println("🔚 Teardown complete.\n");
    }

    // 🆕 Reusable screenshot method
    private void takeScreenshot(String label) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            String filePath = "C:\\Users\\Computer\\Desktop\\" + label + "_" + timestamp + ".png";
            FileUtils.copyFile(src, new File(filePath));
            System.out.println("📸 Screenshot saved: " + filePath);
        } catch (IOException e) {
            System.out.println("⚠️ Failed to capture screenshot: " + e.getMessage());
        }
    }

    @Test
    public void testFacebookSignupForm() {
        driver.get("http://facebook.com");

        // 🆕 Generate random email
        String randomEmail = "user_" + UUID.randomUUID().toString().substring(0, 5) + "@example.com";
        System.out.println("🧪 Using test email: " + randomEmail);

        driver.findElement(By.id("email")).sendKeys(randomEmail);
        driver.findElement(By.id("pass")).sendKeys("Asdasd123!");
        new Select(driver.findElement(By.id("day"))).selectByVisibleText("10");
        new Select(driver.findElement(By.id("month"))).selectByVisibleText("Jun");
        new Select(driver.findElement(By.id("year"))).selectByVisibleText("1990");

        try {
            WebElement genderRadio = driver.findElement(By.xpath("//label[text()='Male']/preceding-sibling::input"));
            genderRadio.click();
        } catch (Exception e) {
            System.out.println("⚠️ Could not select gender.");
        }

        driver.findElement(By.name("websubmit")).click();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        boolean errorAppeared = false;
        try {
            WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'Please choose a password') or contains(text(), 'invalid')]")
            ));
            errorAppeared = errorMsg.isDisplayed();
        } catch (TimeoutException e) {
            System.out.println("❌ No error message detected.");
        }

        assertTrue("Expected error message after submission.", errorAppeared);
    }
}
