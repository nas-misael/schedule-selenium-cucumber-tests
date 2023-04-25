package ScheduleWeb;

import Models.User;
import Utils.DataBaseUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;


public class UserTest {
    private static WebDriver driver;
    private final String baseURL = "http://localhost:8080/app/users";
    private final String InputName = "input#name";
    private final String InputUserName = "input#username";
    private final String InputPassword = "input#password";

    @BeforeAll
    public static void setup(){
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(new ChromeOptions().addArguments("--remote-allow-origins=*"));
    }

    /* Na listagem deve ser possível ver o name, username e ter acesso a página de edição. */
    @Test
    public void createUserValidationTest(){
        driver.get(baseURL);

        driver.findElement(By.cssSelector("a.create")).click();

        new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(InputName)
        ));

        String name = RandomStringUtils.randomAlphabetic(15);
        String username = RandomStringUtils.randomAlphabetic(6);
        String password = RandomStringUtils.randomAlphanumeric(6);

        driver.findElement(By.cssSelector(InputName)).click();
        driver.findElement(By.cssSelector(InputName)).sendKeys(name);

        driver.findElement(By.cssSelector(InputUserName)).click();
        driver.findElement(By.cssSelector(InputUserName)).sendKeys(username);

        driver.findElement(By.cssSelector(InputPassword)).click();
        driver.findElement(By.cssSelector(InputPassword)).sendKeys(password);

        driver.findElement(By.cssSelector("button.btn-default")).click();

        String lineTextName = driver.findElement(By.cssSelector("tbody > tr:nth-last-of-type(1) > td:nth-of-type(1)")).getText();
        String lineTextUsername = driver.findElement(By.cssSelector("tbody > tr:nth-last-of-type(1) > td:nth-of-type(2)")).getText();

        Assertions.assertEquals(name, lineTextName);
        Assertions.assertEquals(username, lineTextUsername);

        boolean textEdit = driver.findElement(By.cssSelector("tbody > tr:nth-last-of-type(1) > td:nth-of-type(3)")).isEnabled();
        Assertions.assertTrue(textEdit);

    }

    /* Não deve ser possível cadastrar dois usuário com o mesmo username. */
    @Test
    public void validationUsernameDuplicateNotAllowedTest() throws SQLException {
        driver.get(baseURL);

        String validationTextDefault = "Username already in use";

        driver.findElement(By.cssSelector("a.create")).click();

        new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(InputName)
        ));

        String name = RandomStringUtils.randomAlphabetic(15);
        String username = DataBaseUtil.findUserById(1L).getUsername();
        String password = RandomStringUtils.randomAlphanumeric(6);

        driver.findElement(By.cssSelector(InputName)).click();
        driver.findElement(By.cssSelector(InputName)).sendKeys(name);

        driver.findElement(By.cssSelector(InputUserName)).click();
        driver.findElement(By.cssSelector(InputUserName)).sendKeys(username);

        driver.findElement(By.cssSelector(InputPassword)).click();
        driver.findElement(By.cssSelector(InputPassword)).sendKeys(password);

        driver.findElement(By.cssSelector("button.btn-default")).click();

        String validationTextInputUsername = driver.findElement(By.cssSelector("div.form-group > input#username ~ p")).getText();

        Assertions.assertEquals(validationTextDefault, validationTextInputUsername);
    }

    /* Não deve ser permitido alterar o username do usuário após o cadastro. */
    @Test
    public void usernameNeverChangeTest(){
        driver.get(baseURL);

        String username = driver.findElement(By.cssSelector("tbody > tr:nth-last-of-type(1) > td:nth-of-type(2)")).getText();
        driver.findElement(By.cssSelector("tbody > tr:nth-last-of-type(1) > td:nth-of-type(3) a")).click();

        String name = RandomStringUtils.randomAlphabetic(15);
        String password = RandomStringUtils.randomAlphanumeric(6);

        driver.findElement(By.cssSelector(InputName)).clear();
        driver.findElement(By.cssSelector(InputName)).sendKeys(name);

        driver.findElement(By.cssSelector(InputUserName)).click();
        String readOnlyAttribute = driver.findElement(By.cssSelector(InputUserName)).getAttribute("readonly");

        driver.findElement(By.cssSelector(InputPassword)).click();
        driver.findElement(By.cssSelector(InputPassword)).sendKeys(password);

        driver.findElement(By.cssSelector("button.btn-default")).click();

        Assertions.assertNotNull(readOnlyAttribute);
        Assertions.assertEquals(username, driver.findElement(By.cssSelector("tbody > tr:nth-last-of-type(1) > td:nth-of-type(2)")).getText());

    }

    /* O cadastro de usuário deve exigir name, username e password para que seja realizado. */
    @Test
    public void validationFieldsForCreateUserTest(){
        driver.get(baseURL);
        String validationTextDefault = "não deve estar em branco";

        driver.findElement(By.cssSelector("a.create")).click();

        new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(InputName)
        ));

        driver.findElement(By.cssSelector("button.btn-default")).click();

        String validationTextInputName = driver.findElement(By.cssSelector("div.form-group > input#name ~ p")).getText();
        String validationTextInputUsername = driver.findElement(By.cssSelector("div.form-group > input#username ~ p")).getText();
        String validationTextInputPassword = driver.findElement(By.cssSelector("div.form-group > input#password ~ p")).getText();

        String[] validationsTexts = {validationTextInputName, validationTextInputUsername, validationTextInputPassword};

        Arrays.stream(validationsTexts).forEach(text -> Assertions.assertEquals(validationTextDefault, text));
    }

    /* A listagem deve exibir todos os usuários já cadastrados, sem nenhum tipo de filtro. */
    @Test
    public void allUsersShouldBeDisplayedTest() throws SQLException {
        driver.get(baseURL);

        int lengthOfElements = driver.findElements(By.cssSelector("tbody > tr")).size();

        for(int i = 1; i <= lengthOfElements; i++){
            String name = driver.findElement(By.cssSelector("tbody > tr:nth-last-of-type(" + i +") > td:nth-of-type(1)")).getText();
            String username = driver.findElement(By.cssSelector("tbody > tr:nth-last-of-type(" + i +") > td:nth-of-type(2)")).getText();

            User user = DataBaseUtil.findUserByUsername(username);

            Assertions.assertEquals(user.getName(), name);
            Assertions.assertEquals(user.getUsername(), username);
        }
    }

    /* A senha antiga não deve ser carregada na edição do usuário, para evitar que seja exposta. */
    @Test
    public void oldPasswordShouldNotBeDisplayedTest(){
        driver.get(baseURL);

        driver.findElement(By.cssSelector("tbody > tr:nth-last-of-type(1) > td:nth-of-type(3) a")).click();

        String name = RandomStringUtils.randomAlphabetic(15);
        String password = RandomStringUtils.randomAlphanumeric(6);

        driver.findElement(By.cssSelector(InputName)).clear();
        driver.findElement(By.cssSelector(InputName)).sendKeys(name);

        driver.findElement(By.cssSelector(InputPassword)).click();
        String inputPasswordValue = driver.findElement(By.cssSelector(InputPassword)).getText();

        Assertions.assertTrue(inputPasswordValue.isEmpty());

        driver.findElement(By.cssSelector(InputPassword)).sendKeys(password);

        driver.findElement(By.cssSelector("button.btn-default")).click();

    }

    /* É permitido atualizar name e password do usuário. */
    @Test
    public void isAllowedToUpdateNameAndPasswordTest() throws SQLException {
        driver.get(baseURL);

        String username = driver.findElement(By.cssSelector("tbody > tr:nth-last-of-type(1) > td:nth-of-type(2)")).getText();
        driver.findElement(By.cssSelector("tbody > tr:nth-last-of-type(1) > td:nth-of-type(3) a")).click();

        String name = RandomStringUtils.randomAlphabetic(15);
        String password = RandomStringUtils.randomAlphanumeric(6);

        String readOnlyAttributeForInputName = driver.findElement(By.cssSelector(InputName)).getAttribute("readonly");
        driver.findElement(By.cssSelector(InputName)).clear();
        driver.findElement(By.cssSelector(InputName)).sendKeys(name);


        String readOnlyAttributeForInputPassword = driver.findElement(By.cssSelector(InputPassword)).getAttribute("readonly");
        driver.findElement(By.cssSelector(InputPassword)).click();
        driver.findElement(By.cssSelector(InputPassword)).sendKeys(password);

        driver.findElement(By.cssSelector("button.btn-default")).click();

        Assertions.assertNull(readOnlyAttributeForInputName);
        Assertions.assertNull(readOnlyAttributeForInputPassword);
        User user = DataBaseUtil.findUserByUsername(username);
        Assertions.assertEquals(name, user.getName());
        Assertions.assertEquals(password, user.getPassword());
    }

    /* Não deve ser possível apagar usuário já cadastrados. */
    @Test
    public void itsNotAllowedDeleteUsers(){
        driver.get(baseURL);

        Assertions.assertFalse(driver.getPageSource().contains("Delete"));

    }

}
