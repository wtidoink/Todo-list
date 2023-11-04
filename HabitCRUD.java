import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HabitCRUD {
    private Connection conn;

    public HabitCRUD(Connection conn) {
        this.conn = conn;
    }

    public void addHabit(Habit habit) {
        String insertQuery = "INSERT INTO habits (description, has_reminder) VALUES (?, ?)";
        try (PreparedStatement st = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, habit.getDescription());
            st.setBoolean(2, habit.hasReminder());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
  public List<Habit> getAllHabits() {
        List<Habit> habits = new ArrayList<>();

        String selectHabitsQuery = "SELECT * FROM habits";
        String selectProgressQuery = "SELECT * FROM habit_progress";

        try (PreparedStatement stHabits = conn.prepareStatement(selectHabitsQuery);
             PreparedStatement stProgress = conn.prepareStatement(selectProgressQuery);
             ResultSet rsHabits = stHabits.executeQuery();
             ResultSet rsProgress = stProgress.executeQuery()) {

            Map<Integer, Habit> habitMap = new HashMap<>();
            Map<Integer, Map<LocalDate, Boolean>> progressMap = new HashMap<>();

            // Retrieve habit information
            while (rsHabits.next()) {
                int habitId = rsHabits.getInt("habit_id");
                String description = rsHabits.getString("description");
                boolean hasReminder = rsHabits.getBoolean("has_reminder");

                habitMap.put(habitId, new Habit(habitId, description, hasReminder, new HashMap<>()));
            }

            // Retrieve progress information
            while (rsProgress.next()) {
                int habitId = rsProgress.getInt("habit_id");
                LocalDate progressDate = rsProgress.getDate("progress_date").toLocalDate();
                boolean isCompleted = rsProgress.getBoolean("is_completed");

                if (!progressMap.containsKey(habitId)) {
                    progressMap.put(habitId, new HashMap<>());
                }

                progressMap.get(habitId).put(progressDate, isCompleted);
            }

            // Combine habit and progress information
            for (Map.Entry<Integer, Habit> entry : habitMap.entrySet()) {
                int habitId = entry.getKey();
                Habit habit = entry.getValue();

                if (progressMap.containsKey(habitId)) {
                    habit.setDailyProgress(progressMap.get(habitId));
                }

                habits.add(habit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return habits;
    }
    public boolean deleteHabitByDescription(String description) {
        String deleteQuery = "DELETE FROM habits WHERE description = ?";
        try (PreparedStatement st = conn.prepareStatement(deleteQuery)) {
            st.setString(1, description);
            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0; // Return true if at least one row was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    

    public boolean deleteHabit(Habit habit) {
        String deleteQuery = "DELETE FROM habits WHERE habit_id = ?";
        try (PreparedStatement st = conn.prepareStatement(deleteQuery)) {
            st.setInt(1, habit.getHabitId());
            st.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
