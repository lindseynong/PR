import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

public class TestFacebookRegistration {
    static WebDriver driver;

    public static void main(String[] ar) {
        long startTime = System.currentTimeMillis();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String randomEmail = "user_" + UUID.randomUUID().toString().substring(0, 6) + "@example.com";
        String genderToSelect = "Female"; // 🔁 Change this to "Male" or "Custom" if needed

        System.setProperty("webdriver.gecko.driver", "C:\\Users\\Computer\\Desktop\\geckodriver.exe");
        driver = new FirefoxDriver();
        driver.get("http://facebook.com");
        driver.manage().window().maximize();

        System.out.println("🚀 Test started at " + timestamp);
        System.out.println("📧 Using random email: " + randomEmail);

        try {
            // 🔍 Check if fields exist first
            if (driver.findElements(By.id("email")).isEmpty() || driver.findElements(By.id("pass")).isEmpty()) {
                throw new RuntimeException("❌ Email or password input field not found.");
            }

            driver.findElement(By.id("email")).sendKeys(randomEmail);
            driver.findElement(By.id("pass")).sendKeys("Asdasd123!");
            System.out.println("🖋️ Entered credentials.");

            // 📅 Birthdate
            new Select(driver.findElement(By.id("day"))).selectByVisibleText("15");
            new Select(driver.findElement(By.id("month"))).selectByVisibleText("Apr");
            new Select(driver.findElement(By.id("year"))).selectByVisibleText("1995");
            System.out.println("📅 Selected birth date.");

            // 👤 Dynamic gender selection
            try {
                String xpath = String.format("//label[text()='%s']/preceding-sibling::input", genderToSelect);
                driver.findElement(By.xpath(xpath)).click();
                System.out.println("👤 Selected gender: " + genderToSelect);
            } catch (Exception e) {
                System.out.println("⚠️ Could not select gender: " + genderToSelect + " — " + e.getMessage());
            }

            System.out.println("📄 Page title before submit: " + driver.getTitle());

            // Submit form
            driver.findElement(By.name("websubmit")).click();

            // 🖼️ Screenshot
            Thread.sleep(3000);
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            String screenshotPath = "C:\\Users\\Computer\\Desktop\\fb_test_" + timestamp + ".png";
            FileUtils.copyFile(src, new File(screenshotPath));
            System.out.println("📸 Screenshot saved at: " + screenshotPath);

            // 🧪 Error validation
            boolean isErrorDisplayed = driver.findElements(By.xpath("//*[contains(text(), 'required') or contains(text(), 'invalid')]")).size() > 0;
            System.out.println("📄 Page title after submit: " + driver.getTitle());

            if (isErrorDisplayed) {
                System.out.println("✅ Error message displayed as expected.");
            } else {
                System.out.println("❌ No error message — review validation logic.");
            }

        } catch (Exception e) {
            System.out.println("💥 Test failed due to: " + e.getMessage());
        } finally {
            driver.quit();
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("⏱️ Execution time: " + duration / 1000.0 + " seconds");
            System.out.println("✅ Test finished.\n");
        }
    }
}
