import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
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
import java.util.*;

import org.apache.commons.io.FileUtils;

@RunWith(Parameterized.class)
public class TestFacebookWithJUnit {

    private final String email;
    private final String password;
    private final String gender;
    private final String birthDay;
    private final String birthMonth;
    private final String birthYear;
    private final int index;

    static WebDriver driver;
    FirefoxOptions options = new FirefoxOptions();
    static String timestamp;

    // ✅ Constructor: now includes birthdate
    public TestFacebookWithJUnit(String email, String password, String gender, String birthDay, String birthMonth, String birthYear, int index) {
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.birthDay = birthDay;
        this.birthMonth = birthMonth;
        this.birthYear = birthYear;
        this.index = index;
    }

    // ✅ Test data now includes birthdate
    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][] {
            {"user1@example.com", "Test123!", "Male", "15", "Apr", "1992", 1},
            {"user2@example.com", "Invalid!", "Female", "23", "Sep", "1985", 2},
            {"user3@example.com", "AsdfQwe123", "Custom", "8", "Jan", "2000", 3}
        });
    }

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            takeScreenshot("FAIL_" + description.getMethodName() + "_" + index);
        }

        @Override
        protected void succeeded(Description description) {
            System.out.println("✅ Test passed: " + description.getMethodName() + " [" + index + "]");
        }
    };

    @Before
    public void setUp() {
        timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        System.setProperty("webdriver.gecko.driver", "C:\\Users\\Computer\\Desktop\\geckodriver.exe");
        options.addPreference("browser.tabs.remote.autostart", false);
        driver = new FirefoxDriver(options);
        driver.manage().window().maximize();
        System.out.println("🚀 [" + timestamp + "] Starting test #" + index + " with:");
        System.out.println("    📧 " + email);
        System.out.println("    👤 " + gender + ", 🎂 " + birthDay + " " + birthMonth + " " + birthYear);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        System.out.println("🔚 Test #" + index + " complete.\n");
    }

    private void takeScreenshot(String label) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            String filePath = "C:\\Users\\Computer\\Desktop\\" + label + "_" + timestamp + ".png";
            FileUtils.copyFile(src, new File(filePath));
            System.out.println("📸 Screenshot saved: " + filePath);
        } catch (IOException e) {
            System.out.println("⚠️ Screenshot failed: " + e.getMessage());
        }
    }

    @Test
    public void testFacebookSignupFormWithMultipleData() {
        driver.get("http://facebook.com");

        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("pass")).sendKeys(password);

        // ✅ Set dynamic birthdate
        new Select(driver.findElement(By.id("day"))).selectByVisibleText(birthDay);
        new Select(driver.findElement(By.id("month"))).selectByVisibleText(birthMonth);
        new Select(driver.findElement(By.id("year"))).selectByVisibleText(birthYear);

        try {
            String xpath = String.format("//label[text()='%s']/preceding-sibling::input", gender);
            WebElement genderRadio = driver.findElement(By.xpath(xpath));
            genderRadio.click();
            System.out.println("👤 Selected gender: " + gender);
        } catch (Exception e) {
            System.out.println("⚠️ Gender selection failed for: " + gender);
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
            System.out.println("❌ Error message not found for data set #" + index);
        }

        assertTrue("Expected error message was not displayed for test #" + index, errorAppeared);
    }
}
