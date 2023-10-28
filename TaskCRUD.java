import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class TaskCRUD {
    private Connection conn;


    public TaskCRUD(Connection conn) {
        this.conn = conn;
     }

    public void addTask(Task task) {
        String insertQuery = "INSERT INTO tasks (task_description, is_completed) VALUES (?, ?)";
        try (PreparedStatement st = conn.prepareStatement(insertQuery)) {
            st.setString(1, task.getDescription());
            st.setBoolean(2, task.isCompleted());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String selectQuery = "SELECT task_id, task_description, is_completed FROM tasks";
        try (PreparedStatement st = conn.prepareStatement(selectQuery);
             ResultSet resultSet = st.executeQuery()) {
            while (resultSet.next()) {
                int taskId = resultSet.getInt("task_id");
                String description = resultSet.getString("task_description");
                boolean isCompleted = resultSet.getBoolean("is_completed");
                Task task = new Task(taskId, description, isCompleted);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void updateTask(Task task) {
        String updateQuery = "UPDATE tasks SET task_description = ?, is_completed = ? WHERE task_id = ?";
        try (PreparedStatement st = conn.prepareStatement(updateQuery)) {
            st.setString(1, task.getDescription());
            st.setBoolean(2, task.isCompleted());
            st.setInt(3, task.getTaskId());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteTask(Task task) {
        String deleteQuery = "DELETE FROM tasks WHERE task_id = ?";
        try (PreparedStatement st = conn.prepareStatement(deleteQuery)) {
            st.setInt(1, task.getTaskId());
            st.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

