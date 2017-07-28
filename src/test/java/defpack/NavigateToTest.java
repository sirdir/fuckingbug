package defpack;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;

import static java.lang.Thread.sleep;

public class NavigateToTest {

    final String ning = "https://www.ning.com/";
    final String google = "https://www.google.com/";
    ChromeDriver driver;
    BrowserMobProxyServer proxyServer;

    @After
    public void stop(){
        if (driver != null) {
            driver.quit();
        }
        if (proxyServer !=null) {
            proxyServer.stop();
        }
    }

    /**
     * will fail
     */
    @Test
    public void testNing() throws InterruptedException, IOException {
        driver = new ChromeDriver();
        driver.get(ning);
        sleep(5000);
        driver.findElement(By.cssSelector("[data-target='login']")).click();
        sleep(5000);
        driver.findElement(By.cssSelector("#login_popup #email")).sendKeys("velosipendiigonwik@test.test");
        driver.findElement(By.cssSelector("#login_popup #password")).sendKeys("qweasd123");
        driver.findElement(By.cssSelector("[data-label='button_signin']")).click();
        sleep(10000);

        driver.get(google);
        sleep(10000);


        driver.manage().deleteAllCookies();
        driver.get(ning);
        sleep(10000);

        driver.manage().deleteAllCookies();
        driver.get(ning); // look like this is doing refresh rather than open

        Assert.assertEquals("refresh instead of navigation", "https://www.ning.com/", driver.getCurrentUrl());
    }

    /**
     * will pass
     */
    @Test
    public void testNingWithProxy() throws InterruptedException, IOException {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        proxyServer = new BrowserMobProxyServer();
        proxyServer.start(0);
        proxyServer.setHarCaptureTypes(CaptureType.getAllContentCaptureTypes());
        proxyServer.newHar("example.com");

        capabilities.setCapability(CapabilityType.PROXY, ClientUtil.createSeleniumProxy(proxyServer));

        driver = new ChromeDriver(capabilities);


        driver.get(ning);
        sleep(5000);
        driver.findElement(By.cssSelector("[data-target='login']")).click();
        sleep(5000);
        driver.findElement(By.cssSelector("#login_popup #email")).sendKeys("velosipendiigonwik@test.test");
        driver.findElement(By.cssSelector("#login_popup #password")).sendKeys("qweasd123");
        driver.findElement(By.cssSelector("[data-label='button_signin']")).click();
        sleep(10000);

        driver.get(google);
        sleep(10000);


        driver.manage().deleteAllCookies();
        driver.get(ning);
        sleep(10000);

        driver.manage().deleteAllCookies();
        driver.get(ning); // working fine

        Har har = proxyServer.getHar();
        String path = System.getProperty("user.home") +"/ning" + System.currentTimeMillis() + ".har";
        System.out.println(path);
        har.writeTo(new File(path));

        Assert.assertEquals("refresh instead of navigation", "https://www.ning.com/", driver.getCurrentUrl());
    }



}
