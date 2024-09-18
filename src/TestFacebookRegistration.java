import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

public class TestFacebookRegistration {
    static WebDriver driver;
    static String timestamp;
    static String logPath;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        logPath = "C:\\Users\\Computer\\Desktop\\fb_test_log_" + timestamp.substring(0, 8) + ".txt";

        // ✅ Parse arguments
        TestParams params = parseArgs(args);

        setDriverPathByOS();
        driver = new FirefoxDriver();
        driver.get("http://facebook.com");
        driver.manage().window().maximize();

        log("🚀 Test started at " + timestamp);
        log("📧 Using email: " + params.email);
        log("👤 Gender: " + params.gender);
        log("🎂 Birthday: " + params.day + " " + params.month + " " + params.year);

        try {
            if (driver.findElements(By.id("email")).isEmpty() || driver.findElements(By.id("pass")).isEmpty()) {
                throw new RuntimeException("❌ Email or password field not found.");
            }

            driver.findElement(By.id("email")).sendKeys(params.email);
            driver.findElement(By.id("pass")).sendKeys("Asdasd123!");
            log("🖋️ Entered credentials.");

            new Select(driver.findElement(By.id("day"))).selectByVisibleText(params.day);
            new Select(driver.findElement(By.id("month"))).selectByVisibleText(params.month);
            new Select(driver.findElement(By.id("year"))).selectByVisibleText(params.year);
            log("📅 Selected birth date.");

            try {
                String xpath = String.format("//label[text()='%s']/preceding-sibling::input", params.gender);
                driver.findElement(By.xpath(xpath)).click();
                log("👤 Selected gender: " + params.gender);
            } catch (Exception e) {
                log("⚠️ Could not select gender: " + e.getMessage());
            }

            log("📄 Page title before submit: " + driver.getTitle());
            driver.findElement(By.name("websubmit")).click();

            Thread.sleep(3000);
            takeScreenshot("submission");

            boolean isErrorDisplayed = driver.findElements(By.xpath("//*[contains(text(), 'required') or contains(text(), 'invalid')]")).size() > 0;
            log("📄 Page title after submit: " + driver.getTitle());

            if (isErrorDisplayed) {
                log("✅ Error message displayed.");
            } else {
                log("❌ No error message found.");
            }

        } catch (Exception e) {
            log("💥 Test failed: " + e.getMessage());
            takeScreenshot("error");
        } finally {
            driver.quit();
            long duration = System.currentTimeMillis() - startTime;
            log("⏱️ Total execution time: " + (duration / 1000.0) + " seconds");
            log("🏁 Test finished.");
            log("═══════════════════════════════════════════\n");
        }
    }

    // ✅ Modular screenshot
    private static void takeScreenshot(String label) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            String filePath = "C:\\Users\\Computer\\Desktop\\fb_" + label + "_" + timestamp + ".png";
            FileUtils.copyFile(src, new File(filePath));
            log("📸 Screenshot saved: " + filePath);
        } catch (IOException e) {
            log("⚠️ Screenshot error: " + e.getMessage());
        }
    }

    // ✅ Detect OS and set gecko driver path accordingly
    private static void setDriverPathByOS() {
        String os = System.getProperty("os.name").toLowerCase();
        String driverPath;
        if (os.contains("win")) {
            driverPath = "C:\\Users\\Computer\\Desktop\\geckodriver.exe";
        } else if (os.contains("mac")) {
            driverPath = "/usr/local/bin/geckodriver";
        } else {
            driverPath = "/usr/bin/geckodriver";
        }
        System.setProperty("webdriver.gecko.driver", driverPath);
        log("🖥️ OS detected: " + os + ", driver path set to: " + driverPath);
    }

    // ✅ Logging to both console and file
    private static void log(String message) {
        System.out.println(message);
        try (FileWriter writer = new FileWriter(logPath, true)) {
            writer.write(message + "\n");
        } catch (IOException e) {
            System.out.println("⚠️ Failed to write to log file: " + e.getMessage());
        }
    }

    // ✅ Parse optional args or fallback to defaults
    private static TestParams parseArgs(String[] args) {
        String prefix = args.length > 0 ? args[0] : "user";
        String gender = args.length > 1 ? args[1] : "Male";
        String day = args.length > 2 ? args[2] : "10";
        String month = args.length > 3 ? args[3] : "Jan";
        String year = args.length > 4 ? args[4] : "1990";

        String email = prefix + "_" + UUID.randomUUID().toString().substring(0, 6) + "@example.com";

        return new TestParams(email, gender, day, month, year);
    }

    // ✅ Helper data class
    static class TestParams {
        String email, gender, day, month, year;

        TestParams(String email, String gender, String day, String month, String year) {
            this.email = email;
            this.gender = gender;
            this.day = day;
            this.month = month;
            this.year = year;
        }
    }
}
