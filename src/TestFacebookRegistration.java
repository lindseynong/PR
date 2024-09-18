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
    static String testId;
    static String logPath;
    static String htmlReportPath;
    static String summaryPath;
    static boolean isWindows;
    static String baseDir;

    public static void main(String[] args) {
        timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        testId = UUID.randomUUID().toString().substring(0, 8);
        isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        baseDir = System.getProperty("user.home") + File.separator + "Desktop";

        ensureFolders();

        logPath = baseDir + "/fb_logs/fb_log_" + testId + "_" + timestamp + ".txt";
        htmlReportPath = baseDir + "/fb_reports/fb_report_" + testId + "_" + timestamp + ".html";
        summaryPath = baseDir + "/fb_reports/fb_test_summary.html";

        TestParams params = parseArgs(args);
        int exitCode = 0;
        String resultStatus = "PASS";
        long start = System.currentTimeMillis();

        setDriverPathByOS(params.browser);
        driver = params.browser.equals("chrome") ? new ChromeDriver() : new FirefoxDriver();
        driver.get("http://facebook.com");
        driver.manage().window().maximize();

        logHtml("<h2>🚀 Facebook Test [" + testId + "] - " + timestamp + "</h2>");
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

            String xpath = String.format("//label[text()='%s']/preceding-sibling::input", params.gender);
            driver.findElement(By.xpath(xpath)).click();
            log("👤 Selected gender.");

            driver.findElement(By.name("websubmit")).click();
            Thread.sleep(3000);
            takeScreenshot("submission");

            boolean isErrorDisplayed = driver.findElements(By.xpath("//*[contains(text(), 'required') or contains(text(), 'invalid')]")).size() > 0;

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
            double duration = (System.currentTimeMillis() - start) / 1000.0;
            log("⏱️ Duration: " + duration + "s");
            log("🏁 Status: " + resultStatus);
            updateSummary(params, resultStatus, duration);
            logHtml("<p><b>Log:</b> <a href='" + logPath + "'>" + testId + ".log</a></p>");
            if (isWindows) open(summaryPath);
            System.exit(exitCode);
        }
    }

    private static void takeScreenshot(String label) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            String path = baseDir + "/fb_screenshots/" + testId + "_" + label + "_" + timestamp + ".png";
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
            (isWindows ? baseDir + "\\chromedriver.exe" : "/usr/bin/chromedriver") :
            (isWindows ? baseDir + "\\geckodriver.exe" : "/usr/bin/geckodriver");

        System.setProperty(browser.equals("chrome") ? "webdriver.chrome.driver" : "webdriver.gecko.driver", driverPath);
        log("🖥️ Driver set for " + browser + ": " + driverPath);
    }

    private static void ensureFolders() {
        new File(baseDir + "/fb_logs").mkdirs();
        new File(baseDir + "/fb_screenshots").mkdirs();
        new File(baseDir + "/fb_reports").mkdirs();
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

    private static void open(String path) {
        try {
            Runtime.getRuntime().exec("explorer \"" + path + "\"");
        } catch (IOException ignored) {}
    }

    private static void updateSummary(TestParams p, String status, double duration) {
        File f = new File(summaryPath);
        boolean newFile = !f.exists();
        try (FileWriter fw = new FileWriter(f, true)) {
            if (newFile) {
                fw.write("<html><head><style>table{border-collapse:collapse}td,th{border:1px solid #ccc;padding:5px;}th{background:#eee}</style><title>FB Test Summary</title></head><body><h1>📊 Facebook Test History</h1><table>\n");
                fw.write("<tr><th>ID</th><th>Time</th><th>Email</th><th>Gender</th><th>Browser</th><th>Duration (s)</th><th>Status</th></tr>\n");
            }
            fw.write(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%.2f</td><td style='color:%s;'>%s</td></tr>\n",
                    testId, timestamp, p.email, p.gender, p.browser, duration,
                    status.equals("PASS") ? "green" : "red", status));
        } catch (IOException ignored) {}
    }

    private static TestParams parseArgs(String[] args) {
        String prefix = args.length > 0 ? args[0] : "user";
        String gender = args.length > 1 ? args[1] : "Male";
        String day = args.length > 2 ? args[2] : "10";
        String month = args.length > 3 ? args[3] : "Jan";
        String year = args.length > 4 ? args[4] : "1990";
        String browser = args.length > 5 ? args[5].toLowerCase() : "firefox";
        String email = prefix + "_" + UUID.randomUUID().toString().substring(0, 6) + "@example.com";
        return new TestParams(email, gender, day, month, year, browser);
    }

    static class TestParams {
        String email, gender, day, month, year, browser;
        TestParams(String email, String gender, String day, String month, String year, String browser) {
            this.email = email;
            this.gender = gender;
            this.day = day;
            this.month = month;
            this.year = year;
            this.browser = browser;
        }
    }
}
