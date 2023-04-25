package ScheduleAPI;

import Models.Task;
import Models.TaskStatus;
import Models.User;
import Utils.DataBaseUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.h2.util.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.time.LocalDate;

public class TasksTests {
    private Gson gson = new Gson();
    private Task task = null;
    private RequestSpecification request =
            RestAssured.given().baseUri("http://localhost:8080/").contentType(ContentType.JSON);
    private Response response;

    @Given("que eu não tenho uma tarefa cadastrada")
    public void iDontHaveTaskRegistered(DataTable data) throws SQLException {
        task = DataBaseUtil.createTaskFromDataTable(data);
        Task taskFound = DataBaseUtil.readTask(task.getTitle());
        Assertions.assertNull(taskFound);
    }

    @Given("que eu já tenho uma tarefa cadastrada")
    public void iAlreadyHaveTaskRegistered(DataTable data) throws SQLException {
        final String[] title = {null};

        data.asMaps().forEach(it -> {
            title[0] = it.get("title");
        });

        System.out.println(">>> "+title[0]);

        task = DataBaseUtil.findLastTaskByPartOfTitle(title[0]);
        System.out.println(task.getTitle());
        Assertions.assertNotNull(task);
    }

    @When("eu registro a tarefa")
    public void registerTask(){
        String jsobBody = gson.toJson(task);
        response = request.body(jsobBody).when().post("api/tasks");
        response.prettyPrint();
    }

    @When("eu solicito para deletar a tarefa")
    public void iWishDeleteTask(){
        response = request.when().delete("api/tasks/"+String.valueOf(task.getId()));
        response.prettyPrint();
    }

    @When("eu atualizo a tarefa com um novo título")
    public void updateTaskWithNewTitle() throws SQLException {
        String newTitle = RandomStringUtils.randomAlphabetic(10);
        task.setTitle(newTitle);
        String jsobBody = gson.toJson(task);
        JsonElement jsonObject = gson.fromJson(jsobBody, JsonElement.class);
        jsonObject.getAsJsonObject().remove("closedAt");
        System.out.println(jsonObject);
        response = request.body(jsonObject).when().put("api/tasks/"+String.valueOf(task.getId()));
        response.prettyPrint();
    }

    @And("eu atualizo a tarefa com uma nova descrição")
    public void updateTaskWithNewDescription() throws SQLException {
        Assertions.assertNull(task.getDescription());
        String description = RandomStringUtils.randomAlphabetic(10);
        task.setDescription(description);
        String jsobBody = gson.toJson(task);
        Task newTask = DataBaseUtil.readTask(task.getTitle());
        response = request.body(jsobBody).when().put("api/tasks/"+String.valueOf(newTask.getId()));
        response.prettyPrint();
    }

    @And("eu atualizo a tarefa com um novo usuário de id {int}")
    public void updateTaskWithNewUser(Integer idUser) throws SQLException {
        Assertions.assertNull(task.getUser());
        User user = DataBaseUtil.findUserById(new Long(idUser));
        task.setUser(user);
        String jsobBody = gson.toJson(task);
        Task newTask = DataBaseUtil.readTask(task.getTitle());
        response = request.body(jsobBody).when().put("api/tasks/"+String.valueOf(newTask.getId()));
        response.prettyPrint();
    }

    @And("eu atualizo a tarefa para status {string}")
    public void updateTaskWithNewStatus(String newStatus) throws SQLException {
        Assertions.assertEquals("OPEN", String.valueOf(task.getStatus()));
        Assertions.assertNull(task.getClosedAt());
        task.setStatus(TaskStatus.valueOf(newStatus));
        String jsobBody = gson.toJson(task);
        Task newTask = DataBaseUtil.readTask(task.getTitle());
        response = request.body(jsobBody).when().put("api/tasks/"+String.valueOf(newTask.getId()));
        response.prettyPrint();
    }

    @Then("não é possível atualizar a tarefa")
    public void taskNotUpdated() throws SQLException {
       Task taskFound = DataBaseUtil.findTaskById(task.getId());
       Assertions.assertNotEquals(taskFound.getTitle(), task.getTitle());
    }

    @Then("não é possível deletar a tarefa")
    public void taskNotDeleted() throws SQLException {
        Task taskFound = DataBaseUtil.findTaskById(task.getId());
        Assertions.assertNotNull(taskFound);
    }

    @Then("eu encontro a tarefa cadastrada com status como OPEN")
    public void searchTaskWithStatusOpen() throws SQLException {
        Task taskFound = DataBaseUtil.readTask(task.getTitle());
        Assertions.assertEquals("OPEN", String.valueOf(taskFound.getStatus()));
    }

    @Then("eu encontro a tarefa atualizada com novo status")
    public void searchTaskWithNewStatus() throws SQLException {
        Task taskFound = DataBaseUtil.readTask(task.getTitle());
        Assertions.assertEquals("CLOSE", String.valueOf(taskFound.getStatus()));
    }

    @Then("eu encontro a tarefa atualizada com nova data de fechamento")
    public void searchTaskWithNewClosedAt() throws SQLException {
        Task taskFound = DataBaseUtil.readTask(task.getTitle());
        System.out.println(taskFound.getTitle());
        Assertions.assertEquals(LocalDate.now(), taskFound.getClosedAt());
    }

    @Then("eu encontro a tarefa atualizada com nova descrição")
    public void searchTaskWithNewDescription() throws SQLException {
        Task taskFound = DataBaseUtil.readTask(task.getTitle());
        Assertions.assertNotNull(taskFound.getDescription());
    }

    @When("eu encontro a tarefa atualizada com vinculo de usuário")
    public void searchTaskWithNewUser() throws SQLException {
        Task taskFound = DataBaseUtil.readTask(task.getTitle());
        Assertions.assertNotNull(taskFound.getUser());
    }

    @Then("eu não encontro a tarefa cadastrada")
    public void iDontFoundTask() throws SQLException {
        Task taskFound = DataBaseUtil.readTask(task.getTitle());
        Assertions.assertNull(taskFound);
    }

    @And("e a resposta deve ter o status code igual a {int}")
    public void statusEquals(Integer status) {
        response.then().statusCode(status);
    }
}
