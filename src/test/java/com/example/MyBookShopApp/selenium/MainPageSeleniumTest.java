package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
}