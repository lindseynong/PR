import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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
    static String htmlReportPath;
    static String summaryPath = "C:\\Users\\Computer\\Desktop\\fb_test_summary.html";
    static boolean isWindows;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        logPath = "C:\\Users\\Computer\\Desktop\\fb_test_log_" + timestamp + ".txt";
        htmlReportPath = "C:\\Users\\Computer\\Desktop\\fb_test_report_" + timestamp + ".html";

        TestParams params = parseArgs(args);
        int exitCode = 0;
        String resultStatus = "PASS";

        setDriverPathByOS(params.browser);
        driver = params.browser.equals("chrome") ? new ChromeDriver() : new FirefoxDriver();
        driver.get("http://facebook.com");
        driver.manage().window().maximize();

        logHtml("<h2>🚀 Facebook Test Run - " + timestamp + "</h2>");
        log("📧 Email: " + params.email);
        log("👤 Gender: " + params.gender);
        log("🎂 DOB: " + params.day + " " + params.month + " " + params.year);
        log("🌐 Browser: " + params.browser);

        try {
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
                log("⚠️ Gender selection failed: " + e.getMessage());
            }

            driver.findElement(By.name("websubmit")).click();
            Thread.sleep(3000);
            takeScreenshot("submission");

            boolean isErrorDisplayed = driver.findElements(By.xpath("//*[contains(text(), 'required') or contains(text(), 'invalid')]")).size() > 0;
            log("📄 Page title: " + driver.getTitle());

            if (!isErrorDisplayed) {
                log("❌ No error message found.");
                resultStatus = "FAIL";
                exitCode = 1;
            } else {
                log("✅ Error message detected.");
            }

        } catch (Exception e) {
            log("💥 Test failed: " + e.getMessage());
            takeScreenshot("error");
            resultStatus = "ERROR";
            exitCode = 2;
        } finally {
            driver.quit();
            double duration = (System.currentTimeMillis() - startTime) / 1000.0;
            log("⏱️ Duration: " + duration + "s");
            log("🏁 Status: " + resultStatus);

            updateSummary(params, resultStatus, duration);
            logHtml("<p><b>See:</b> <a href='" + logPath + "'>Text Log</a></p>");

            if (isWindows) {
                try {
                    Runtime.getRuntime().exec("explorer \"" + summaryPath + "\"");
                } catch (IOException ignored) {}
            }

            System.exit(exitCode);
        }
    }

    private static void takeScreenshot(String label) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            String path = "C:\\Users\\Computer\\Desktop\\fb_" + label + "_" + timestamp + ".png";
            FileUtils.copyFile(src, new File(path));
            log("📸 Screenshot saved: " + path);
            logHtml("<p>📸 Screenshot: <a href='" + path + "'>" + label + "</a></p>");
        } catch (IOException e) {
            log("⚠️ Screenshot error: " + e.getMessage());
        }
    }

    private static void setDriverPathByOS(String browser) {
        String os = System.getProperty("os.name").toLowerCase();
        String driverPath = browser.equals("chrome") ?
            (os.contains("win") ? "C:\\Users\\Computer\\Desktop\\chromedriver.exe" : "/usr/bin/chromedriver") :
            (os.contains("win") ? "C:\\Users\\Computer\\Desktop\\geckodriver.exe" : "/usr/bin/geckodriver");

        if (browser.equals("chrome")) {
            System.setProperty("webdriver.chrome.driver", driverPath);
        } else {
            System.setProperty("webdriver.gecko.driver", driverPath);
        }

        log("🖥️ OS: " + os + " | Driver path set: " + driverPath);
    }

    private static void log(String message) {
        System.out.println(message);
        try (FileWriter fw = new FileWriter(logPath, true)) {
            fw.write(message + "\n");
        } catch (IOException ignored) {}
    }

    private static void logHtml(String html) {
        try (FileWriter fw = new FileWriter(htmlReportPath, true)) {
            fw.write(html + "\n");
        } catch (IOException ignored) {}
    }

    private static void updateSummary(TestParams params, String status, double duration) {
        File summary = new File(summaryPath);
        boolean isNew = !summary.exists();
        try (FileWriter fw = new FileWriter(summary, true)) {
            if (isNew) {
                fw.write("<html><head><title>FB Test Summary</title></head><body><h1>📊 Facebook Test History</h1><table border='1'>\n");
                fw.write("<tr><th>Timestamp</th><th>Email</th><th>Gender</th><th>Browser</th><th>Duration (s)</th><th>Status</th></tr>\n");
            }
            fw.write(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%.2f</td><td style='color:%s;'>%s</td></tr>\n",
                    timestamp, params.email, params.gender, params.browser, duration,
                    status.equals("PASS") ? "green" : "red", status));
            if (isNew) fw.write("</table></body></html>");
        } catch (IOException ignored) {}
    }

    private static TestParams parseArgs(String[] args) {
        String prefix = args.length > 0 ?
