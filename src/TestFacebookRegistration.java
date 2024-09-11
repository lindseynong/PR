import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class TestFacebookRegistration {
    static WebDriver driver;

    public static void main(String[] ar) {
        System.setProperty("webdriver.gecko.driver", "C:\\Users\\Computer\\Desktop\\geckodriver.exe");
        driver = new FirefoxDriver();
        driver.get("http://facebook.com");
        driver.manage().window().maximize();

        // Fill login fields
        driver.findElement(By.id("email")).sendKeys("iasjdiaosjd@gmail.com");
        driver.findElement(By.id("pass")).sendKeys("Asdasd123!");
        System.out.println("🖋️ Entered email and password");

        // 🆕 Select full birthdate (day, month, year)
        new Select(driver.findElement(By.id("day"))).selectByVisibleText("15");
        new Select(driver.findElement(By.id("month"))).selectByVisibleText("Apr");
        new Select(driver.findElement(By.id("year"))).selectByVisibleText("1995");
        System.out.println("📅 Selected birth date: Apr 15, 1995");

        // 🆕 Select gender (Male)
        try {
            driver.findElement(By.xpath("//label[text()='Male']/preceding-sibling::input")).click();
            System.out.println("👤 Selected gender: Male");
        } catch (Exception e) {
            System.out.println("⚠️ Gender selection failed: " + e.getMessage());
        }

        // Submit form
        driver.findElement(By.name("websubmit")).click();

        // Screenshot
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(src, new File("C:\\Users\\Computer\\Desktop\\fb_test_screenshot.png"));
            System.out.println("📸 Screenshot taken successfully.");
        } catch (IOException e) {
            System.out.println("⚠️ Screenshot capture failed: " + e.getMessage());
        }

        // Error message validation
        try {
            Thread.sleep(3000);
            boolean isErrorDisplayed = driver.findElements(By.xpath("//*[contains(text(), 'required')]")).size() > 0;
            if (isErrorDisplayed) {
                System.out.println("✅ Error message displayed as expected.");
            } else {
                System.out.println("❌ No error message found — unexpected behavior.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Validation check failed: " + e.getMessage());
        }

        driver.quit();
    }
}
