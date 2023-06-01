package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MainPageSeleniumTest {

    private static ChromeDriver driver;
    
    @BeforeAll
    static void setup() {
        System.setProperty("webdriver.chrome.driver", "d:/chromedriver/chromedriver.exe");
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
    }

    @AfterAll
    static void tearDown() {
        driver.quit();
    }

    @Test
    public void mainPageAccessTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause();
        assertTrue(driver.getPageSource().contains("BOOKSHOP"));
    }

    @Test
    public void mainPageSearchTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .setUpMaximizeWindow()
                .setUpSearchToken("Blueberry")
                .pause()
                .submit()
                .pause();
        assertTrue(driver.getPageSource().contains("Blueberry"));
    }

    @Test
    public void linksAccessTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .setUpMaximizeWindow();
        driver.findElement(By.cssSelector("a[href='/genres']")).click();
        assertTrue(driver.getPageSource().contains("Fiction"));
        mainPage.pause();
        driver.findElement(By.cssSelector("a[href='/books/recent']")).click();
        assertTrue(driver.getPageSource().contains("The Testimony"));
        mainPage.pause();
        driver.findElement(By.cssSelector("a[href='/books/popular']")).click();
        assertTrue(driver.getPageSource().contains("Vital"));
        mainPage.pause();
        driver.findElement(By.cssSelector("a[href='/authors']")).click();
        assertTrue(driver.getPageSource().contains("Arlott Anni"));
        mainPage.pause();
    }

    @Test
    public void loginAndLogoutTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .setUpMaximizeWindow();
        driver.findElement(By.cssSelector("a[href='/signin']")).click();
        driver.findElement(By.xpath("/html/body/div/div[2]/main/form/div/div[1]/div[2]/div/div[2]/label/input")).click();
        mainPage.pause();
        driver.findElement(By.id("mail")).sendKeys("user@mail.ru");
        driver.findElement(By.id("sendauth")).click();
        mainPage.pause();
        driver.findElement(By.id("mailcode")).sendKeys("111111");
        driver.findElement(By.id("toComeInMail")).click();
        mainPage.pause();
        assertTrue(driver.getPageSource().contains("User One"));
        mainPage.pause();
        driver.findElement(By.cssSelector("a[href='/logout']")).click();
        assertTrue(driver.getPageSource().contains("Вход"));
    }
}