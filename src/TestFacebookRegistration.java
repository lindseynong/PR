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

        driver.findElement(By.xpath(".//*[@id='email']")).sendKeys("iasjdiaosjd@gmail.com");
        driver.findElement(By.xpath(".//*[@id='pass']")).sendKeys("Asdasd123!");

        // 🆕 Select birthdate (Day, Month, Year)
        Select day = new Select(driver.findElement(By.id("day")));
        day.selectByVisibleText("15");

        Select month = new Select(driver.findElement(By.id("month")));
        month.selectByVisibleText("Apr");

        Select year = new Select(driver.findElement(By.id("year")));
        year.selectByVisibleText("1995");

        driver.findElement(By.xpath(".//*[@id='u_0_2']")).click();

        // Screenshot after submission
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(src, new File("C:\\Users\\Computer\\Desktop\\fb_test_screenshot.png"));
            System.out.println("📸 Screenshot taken successfully.");
        } catch (IOException e) {
            System.out.println("⚠️ Screenshot capture failed: " + e.getMessage());
        }

        // Error validation
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
