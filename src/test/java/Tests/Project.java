package Tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.github.javafaker.Faker;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Project {
    static WebDriver driver;

    void Login(String login, String password) {
        WebElement myAccount = driver.findElement(By.id("menu-item-125"));
        myAccount.click();
        WebElement loginInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        loginInput.sendKeys(login);
        passwordInput.sendKeys(password);
        WebElement submit = driver.findElement(By.cssSelector("button[name=\"login\"]"));
        submit.click();

    }

    private WebElement productOnSaleList(int i) {
        List<WebElement> products = driver.findElements(By.cssSelector("span[class=\"onsale\"]"));
        WebElement prod;
        prod = products.get(i);
        prod.click();
        return null;
    }

    private int productOnSaleStringToInt(int i) {
        List<WebElement> productPrice = driver.findElements(By.cssSelector("td.product-subtotal > span > bdi"));
        String prodPrice;
        prodPrice = productPrice.get(i).getText();
        String numberOnly = prodPrice.replaceAll("[^0-9]", "");
        int number = Integer.parseInt(numberOnly);
        return number;
    }

    @BeforeEach
    void prepareBrowser() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://34.171.101.114/");
    }

    @AfterEach
    void closeBrowser() {
        driver.manage().deleteAllCookies();
        driver.quit();
    }

    @Test
    void shouldVerifyYouCantLoginWithoutLoginInput() {
        Login("", "321456");
        Assertions.assertEquals("Błąd: Nazwa użytkownika jest wymagana.",
                driver.findElement(By.className("woocommerce-error")).getText());
    }

    @Test
    void shouldVerifyYouCantLoginWithoutPasswordInput() {
        Login("asdsbsf", "");
        Assertions.assertEquals("Błąd: pole hasła jest puste.",
                driver.findElement(By.className("woocommerce-error")).getText());
    }

    @Test
    void shouldVerifyRegisterWork() throws InterruptedException {
        WebElement register = driver.findElement(By.id("menu-item-146"));
        register.click();
        Faker faker = new Faker(new Locale("pl-PL"));
        String userName = faker.name().firstName() + Math.random();
        String userPassword = faker.name().firstName() + Math.random();
        String userEmail = faker.name().lastName() + Math.random() + "@o13.pl";
        WebElement userNameInput = driver.findElement(By.name("user_login"));
        WebElement userPasswordInput = driver.findElement(By.name("user_pass"));
        WebElement userPasswordConfirmInput = driver.findElement(By.name("user_confirm_password"));
        WebElement userEmailInput = driver.findElement(By.name("user_email"));
        userNameInput.sendKeys(userName);
        userEmailInput.sendKeys(userEmail);
        userPasswordInput.sendKeys(userPassword);
        Thread.sleep(300);
        userPasswordConfirmInput.sendKeys(userPassword);
        userPasswordInput.sendKeys("1");
        userPasswordConfirmInput.sendKeys("1");
        WebElement submit = driver.findElement(By.cssSelector("div.ur-button-container > button"));
        submit.click();
        Wait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ur-submit-message-node")));
        Assertions.assertEquals("User successfully registered.",
                driver.findElement(By.id("ur-submit-message-node")).getText());
    }

    @Test
    void shouldVerifyIsThereLogoAndSearchBar() {
        Assertions.assertTrue(driver.findElement(By.id("woocommerce-product-search-field-0")).isDisplayed());
        Assertions.assertTrue(driver.findElement(By.cssSelector("div.site-branding")).isDisplayed());
        WebElement myAccount = driver.findElement(By.id("menu-item-125"));
        myAccount.click();
        Assertions.assertTrue(driver.findElement(By.id("woocommerce-product-search-field-0")).isDisplayed());
        Assertions.assertTrue(driver.findElement(By.cssSelector("div.site-branding")).isDisplayed());

    }

    @Test
    void shouldGoToContactPage() {
        WebElement contact = driver.findElement(By.id("menu-item-132"));
        contact.click();
        Assertions.assertEquals("Kontakt", driver.findElement(By.cssSelector("header > h1")).getText());
        WebElement okruszki = driver.findElement(By.className("woocommerce-breadcrumb"));
        Assertions.assertTrue(okruszki.getText().contains("Kontakt"));
    }

    @Test
    void shouldVerifyGoingFromLoginPageToMainPaige() {
        WebElement myAccount = driver.findElement(By.id("menu-item-125"));
        myAccount.click();
        WebElement logo = driver.findElement(By.cssSelector("div.site-branding > div > a"));
        logo.click();
        Assertions.assertEquals("Sklep", driver.findElement(
                By.className("woocommerce-products-header")).getText());
    }

    @Test
    void shouldVerifyContactMessageIsSend() {
        WebElement contact = driver.findElement(By.id("menu-item-132"));
        contact.click();
        WebElement name = driver.findElement(By.cssSelector("input[name='your-name']"));
        name.sendKeys("TestName");
        WebElement email = driver.findElement(By.cssSelector("input[name='your-email']"));
        email.sendKeys("TestEmail@yahoo.com");
        WebElement submit = driver.findElement(By.cssSelector("input[type='submit']"));
        submit.click();
        Wait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[aria-hidden=\"true\"]")));
        Assertions.assertEquals("Twoja wiadomość została wysłana. Dziękujemy!",
                driver.findElement(By.cssSelector("div[aria-hidden=\"true\"]")).getText());
    }

    @Test
    void shouldVerifyTheCartBoxIsEmptyAndAddProduct() {
        WebElement shopBox = driver.findElement(By.id("site-header-cart"));
        shopBox.click();
        Assertions.assertEquals("Twój koszyk aktualnie jest pusty.",
                driver.findElement(By.cssSelector("p.cart-empty.woocommerce-info")).getText());
        WebElement toShop = driver.findElement(By.id("menu-item-124"));
        toShop.click();
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        List<WebElement> products = driver.findElements(By.cssSelector
                (".ajax_add_to_cart"));
        WebElement first = products.get(0);
        first.click();
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        WebElement viewCart = driver.findElement(By.cssSelector("a.added_to_cart.wc-forward"));
        viewCart.click();
        Assertions.assertTrue(driver.findElement(By.className("wc-proceed-to-checkout")).isDisplayed());
    }

    @Test
    void shouldAddProductToCartAndDeleteHim() {
        List<WebElement> products = driver.findElements(By.cssSelector
                (".ajax_add_to_cart"));
        WebElement first = products.get(0);
        first.click();
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        WebElement viewCart = driver.findElement(By.cssSelector("a.added_to_cart.wc-forward"));
        viewCart.click();
        Assertions.assertTrue(driver.findElement(By.className("wc-proceed-to-checkout")).isDisplayed());
        WebElement remove = driver.findElement(By.className("remove"));
        remove.click();
        Assertions.assertEquals("Twój koszyk aktualnie jest pusty.",
                driver.findElement(By.cssSelector("p.cart-empty.woocommerce-info")).getText());
        Assertions.assertTrue(driver.findElement(By.className("woocommerce-message")).isDisplayed());
    }

    @Test
    void shouldAddPromotedProductToCartAndVerifyPrice() {
        List<WebElement> products = driver.findElements(By.cssSelector("span[class=\"onsale\"]"));
        int sum = 0;
        for (int i = 0; i < products.size(); i++) {
            productOnSaleList(i);
            WebElement addToCart = driver.findElement(By.cssSelector("button[name=\"add-to-cart\"]"));
            addToCart.click();
            WebElement toShop = driver.findElement(By.id("menu-item-124"));
            toShop.click();
        }
        WebElement shopBox = driver.findElement(By.id("site-header-cart"));
        shopBox.click();
        List<WebElement> productPrice = driver.findElements(By.cssSelector("td.product-subtotal > span > bdi"));
        for (int i = 0; i < productPrice.size(); i++) {
            productOnSaleStringToInt(i);
            sum += productOnSaleStringToInt(i);
        }
        WebElement totalCartPrice = driver.findElement(By.cssSelector("tr.cart-subtotal > td > span > bdi"));
        String totalPrice = totalCartPrice.getText().replaceAll("[^0-9]", "");
        int totalPriceInteger = Integer.parseInt(totalPrice);
        Assertions.assertEquals(sum, totalPriceInteger);
    }

    @Test
    void shouldLogincorrectly() {
        Login("test2212", "zaq1@WSX");
        Assertions.assertTrue(driver.findElement(By.linkText("Wyloguj się")).isDisplayed());
    }

    @Test
    void shouldVerifyUsernameAndEmailAreInTheDataBase() throws InterruptedException {
        WebElement register = driver.findElement(By.id("menu-item-146"));
        register.click();
        Faker faker = new Faker(new Locale("pl-PL"));
        String userName = "test2212";
        String userPassword = faker.name().firstName() + Math.random();
        String userEmail = "test2212@o2.pl";
        WebElement userNameInput = driver.findElement(By.name("user_login"));
        WebElement userPasswordInput = driver.findElement(By.name("user_pass"));
        WebElement userPasswordConfirmInput = driver.findElement(By.name("user_confirm_password"));
        WebElement userEmailInput = driver.findElement(By.name("user_email"));
        userNameInput.sendKeys(userName);
        userEmailInput.sendKeys(userEmail);
        userPasswordInput.sendKeys(userPassword);
        Thread.sleep(300);
        userPasswordConfirmInput.sendKeys(userPassword);
        userPasswordInput.sendKeys("1");
        userPasswordConfirmInput.sendKeys("1");
        WebElement submit = driver.findElement(By.cssSelector("div.ur-button-container > button"));
        submit.click();
        Wait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ur-submit-message-node")));
        Assertions.assertEquals("Username already exists.\n" + "Email already exists.",
                driver.findElement(By.id("ur-submit-message-node")).getText());
    }

    @Test
    void shouldVerifySortByLowestPrice() {
        Select sort = new Select(driver.findElement(By.cssSelector("select[name=\"orderby\"]")));
        List<WebElement> sorts = sort.getOptions();
        sorts.get(4).click();
        List<WebElement> products = driver.findElements(By.cssSelector("span[class=\"price\"]"));
        for (int i = 0; i < products.size() - 1; i++) {
            String text = products.get(i).getText();
            String text2 = products.get(i + 1).getText();
            String segments[] = text.split("zł");
            String segments2[] = text2.split("zł");
            String document = segments[segments.length - 1];
            String document2 = segments2[segments2.length - 1];
            String numberOnly = document.replaceAll("[^0-9]", "");
            String numberOnly2 = document2.replaceAll("[^0-9]", "");
            int number = Integer.parseInt(numberOnly);
            int number2 = Integer.parseInt(numberOnly2);
            Assertions.assertTrue(number < number2);
        }
    }

    @Test
    void shouldVerifySortByHighestPrice() {
        Select sort = new Select(driver.findElement(By.cssSelector("select[name=\"orderby\"]")));
        List<WebElement> sorts = sort.getOptions();
        sorts.get(5).click();
        List<WebElement> products = driver.findElements(By.cssSelector("span[class=\"price\"]"));
        for (int i = 0; i < products.size() - 1; i++) {
            String text = products.get(i).getText();
            String text2 = products.get(i + 1).getText();
            String segments[] = text.split("zł");
            String segments2[] = text2.split("zł");
            String document = segments[segments.length - 1];
            String document2 = segments2[segments2.length - 1];
            String numberOnly = document.replaceAll("[^0-9]", "");
            String numberOnly2 = document2.replaceAll("[^0-9]", "");
            int number = Integer.parseInt(numberOnly);
            int number2 = Integer.parseInt(numberOnly2);
            Assertions.assertTrue(number > number2);
        }
    }

    @Test
    void shouldVerifyReturnProductPageHaveConfirmMessage() {
        WebElement returnProduct = driver.findElement(By.id("menu-item-145"));
        returnProduct.click();
        WebElement yourName = driver.findElement(By.cssSelector("input[name=\"your-name\"]"));
        yourName.sendKeys("TestTest");
        WebElement yourEmail = driver.findElement(By.cssSelector("input[name=\"your-email\"]"));
        yourEmail.sendKeys("testest@o13.pl");
        WebElement yourPhoneNumber = driver.findElement(By.cssSelector("input[name=\"tel-750\"]"));
        yourPhoneNumber.sendKeys("123123123");
        WebElement acceptance = driver.findElement(By.cssSelector("input[name=\"acceptance-574\"]"));
        acceptance.click();
        WebElement submit = driver.findElement(By.cssSelector("input[type=\"submit\"]"));
        submit.click();
        Wait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[aria-hidden=\"true\"]")));
        Assertions.assertEquals("Twoja wiadomość została wysłana. Dziękujemy!",
                driver.findElement(By.cssSelector("div[aria-hidden=\"true\"]")).getText());
    }

    @Test
    void shouldVerifyYouCanCheckYourShopHistory() throws InterruptedException {
        Login("test2212", "zaq1@WSX");
        WebElement toShop = driver.findElement(By.id("menu-item-124"));
        toShop.click();
        List<WebElement> products = driver.findElements(By.cssSelector
                (".ajax_add_to_cart"));
        WebElement first = products.get(0);
        first.click();
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        WebElement viewCart = driver.findElement(By.cssSelector("a.added_to_cart.wc-forward"));
        viewCart.click();
        WebElement procedeToPurche = driver.findElement(By.className("wc-proceed-to-checkout"));
        procedeToPurche.click();
        Thread.sleep(1000);
        WebElement order = driver.findElement(By.cssSelector("button[class=\"button alt wp-element-button\"]"));
        order.click();
        Wait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("entry-header")));
        String orderNumber = driver.findElement(By.cssSelector("li.woocommerce-order-overview__order.order")).getText();
        String segments[] = orderNumber.split(":");
        String document = segments[segments.length - 1];
        String numberOnly = document.replaceAll("[^0-9]", "");
        orderNumber = "#" + numberOnly;
        WebElement myAccount = driver.findElement(By.id("menu-item-125"));
        myAccount.click();
        WebElement myOrder = driver.findElement(By.cssSelector
                (".woocommerce-MyAccount-navigation-link--orders"));
        myOrder.click();
        List<WebElement> orderNumbers = driver.findElements(By.cssSelector("td[data-title=\"Zamówienie\"]"));
        Assertions.assertEquals(orderNumber, orderNumbers.get(0).getText());
    }

}
