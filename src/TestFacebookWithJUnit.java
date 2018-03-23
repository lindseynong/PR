
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import static org.junit.Assert.*;
public class TestFacebookWithJUnit {
static WebDriver driver;
FirefoxOptions a = new FirefoxOptions();
public abstract class MyTestBaseClass {}
@Before
public void setUp() throws Exception{
	

}
@After
public void tearDown() throws Exception{
	
}
	public
	@Test
	void test() {
		System.setProperty("webdriver.gecko.driver", "C:\\Users\\Computer\\Desktop\\geckodriver.exe");
		a.addPreference("browser.tabs.remote.autostart", false);
		driver=new FirefoxDriver(a);
		driver.get("http://facebook.com");
		driver.manage().window().maximize();
		driver.findElement(By.xpath(".//*[@id='email']")).sendKeys("iasjdiaosjd@gmail.com");
		driver.findElement(By.xpath(".//*[@id='pass']")).sendKeys("Asdasd123!");
		driver.findElement(By.xpath(".//*[@id='u_0_2']")).click();
		
		
		
	}

}
