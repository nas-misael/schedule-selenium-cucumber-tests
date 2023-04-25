package Utils;

import Models.Task;
import Models.TaskStatus;
import Models.User;
import io.cucumber.datatable.DataTable;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataBaseUtil {

    public static User findUserByUsername(String username) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM USER_ WHERE username = '" + username + "'");
             ResultSet result = statement.executeQuery()
        ) {
            User user = null;
            if (result.next()) {
                user = readUser(result);
            }
            return user;
        }
    }

    public static User findUserById(Long id) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM USER_ WHERE id = " + id);
             ResultSet result = statement.executeQuery()
        ) {
            User user = null;
            if (result.next()) {
                user = readUser(result);
            }
            return user;
        }
    }

    private static User readUser(ResultSet result) throws SQLException {
        User user = new User();
        user.setId(result.getLong("id"));
        user.setName(result.getString("name"));
        user.setUsername(result.getString("username"));
        user.setPassword(result.getString("password"));
        return user;
    }

    public static List<Task> findTaskByUser(Long userId) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM TASK WHERE user_id = " + userId);
             ResultSet result = statement.executeQuery()
        ) {
            List<Task> tasks = new ArrayList<>();
            while (result.next()) {
                tasks.add(readTask(result.getString("TITLE")));
            }
            return tasks;
        }
    }

    public static Task findTaskById(Long id) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM TASK WHERE id = " + id);
             ResultSet result = statement.executeQuery()
        ) {
            Task task = null;
            if (result.next()) {
                task = readTask(result.getString("TITLE"));
            }
            return task;
        }
    }

    public static Task findLastTaskByPartOfTitle(String title) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM task WHERE title LIKE '%" + title  + "%' ");
             ResultSet result = statement.executeQuery()
        ) {
            Task task = null;
            while(result.next()) {
                if(result.isLast()) {
                    task = readTask(result.getString("TITLE"));
                }
            }

            System.out.println(task.getTitle());

            return task;
        }
    }

    public static Task readTask(String title) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM TASK WHERE title = '" + title + "'");
             ResultSet result = statement.executeQuery()
        ) {
            Task task = null;
            if(result.next()){
                task = new Task();
                String stringDate = String.valueOf(result.getObject("closed_at"));
                if(!stringDate.equals("null")){
                    LocalDate date = LocalDate.parse(stringDate);
                    task.setId(result.getLong("id"));
                    task.setTitle(result.getString("title"));
                    task.setDescription(result.getString("description"));
                    task.setStatus(TaskStatus.valueOf(result.getString("status")));
                    task.setUser(findUserById(result.getLong("user_id")));
                    task.setClosedAt(date);
                } else {
                    task.setId(result.getLong("id"));
                    task.setTitle(result.getString("title"));
                    task.setDescription(result.getString("description"));
                    task.setStatus(TaskStatus.valueOf(result.getString("status")));
                    task.setUser(findUserById(result.getLong("user_id")));
                }

            }
            return task;
        }
    }

    public static void insertTask(Task task) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO TASK(title, description, status, user_id, closed_at) VALUES(?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, task.getDescription());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getStatus().name());
            statement.setLong(4, task.getUser().getId());
            statement.setObject(5, task.getClosedAt());
            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            if (result.next()) {
                Long id = result.getLong(1);
                task.setId(id);
            }
        }
    }

    public static void updateTask(Task task) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE TASK SET title = ?, description = ?, status = ?, user_id = ?, closed_at = ? WHERE id = ?");
        ) {
            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getStatus().name());
            statement.setLong(4, task.getUser().getId());
            statement.setObject(5, task.getClosedAt());
            statement.setLong(6, task.getId());
            statement.executeUpdate();
        }
    }

    public static Task createTaskFromDataTable(DataTable data) {
        Task task = new Task();
        data.asMaps().forEach(it -> {
            String title = it.get("title");
            if(title != null){
                title += RandomStringUtils.randomAlphabetic(10);
            }

            String description = it.get("description");

            String userId = it.get("userId");
            User user = null;
            if(userId != null){
                try {
                    user = DataBaseUtil.findUserById(new Long(userId));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            String status = it.get("status");
            if(status == null){
                status = String.valueOf(TaskStatus.OPEN);
            }

            task.setTitle(title);
            task.setDescription(description);
            task.setUser(user);
            task.setStatus(TaskStatus.valueOf(status));
        });
        return task;
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager
                .getConnection("jdbc:h2:tcp://localhost:9092/nio:~/schedule", "sa", "password");
    }
}
