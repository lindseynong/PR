import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
public class TestFacebookRegistration {
static WebDriver driver;
public static void main(String []ar) {
	System.setProperty("webdriver.gecko.driver", "C:\\Users\\Computer\\Desktop\\geckodriver.exe");
	driver=new FirefoxDriver();
	driver.get("http://facebook.com");
	driver.manage().window().maximize();
	driver.findElement(By.xpath(".//*[@id='email']")).sendKeys("iasjdiaosjd@gmail.com");
	driver.findElement(By.xpath(".//*[@id='pass']")).sendKeys("Asdasd123!");
	Select sel = new Select(driver.findElement(By.xpath(".//*[@id='month']")));
	sel.selectByIndex(3);
	driver.findElement(By.xpath(".//*[@id='u_0_2']")).click();
}

}
