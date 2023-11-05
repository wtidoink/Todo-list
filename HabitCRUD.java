import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HabitCRUD {
    private Connection conn;

    public HabitCRUD(Connection conn) {
        this.conn = conn;
    }

    public boolean addHabit(Habit habit) {
        String insertQuery = "INSERT INTO habits (description, has_reminder, remind_time) VALUES (?, ?, ?)";
        try (PreparedStatement st = conn.prepareStatement(insertQuery)) {
            LocalTime localTime = habit.getReminderTime(); 
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            st.setString(1, habit.getDescription());
            st.setInt(2, habit.hasReminder() ? 1 : 0);
            if(localTime!=null)
                st.setString(3, localTime.format(formatter));
            else 
                st.setString(3, "00:00:00");
            st.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Habit> getAllHabits() {
        List<Habit> habits = new ArrayList<>();
        String selectQuery = "SELECT * FROM habits";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(selectQuery)) {
            while (rs.next()) {
                String description = rs.getString("description");
                boolean hasReminder = rs.getInt("has_reminder") == 1;
                String remindTime = rs.getString("remind_time");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime localTime = LocalTime.parse(remindTime, formatter);
                Map<LocalDate, Boolean> dailyProgress = getHabitProgress(description);
                habits.add(new Habit(description, hasReminder, localTime, dailyProgress));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return habits;
    }
    public Map<LocalDate, Boolean> getHabitProgress(String description) {
    Map<LocalDate, Boolean> habitProgress = new HashMap<>();
    String selectQuery = "SELECT progress_date, is_completed FROM habit_progress WHERE habit_description =? ";
    
    try (PreparedStatement st = conn.prepareStatement(selectQuery)) {
        st.setString(1, description);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            LocalDate progressDate = rs.getDate("progress_date").toLocalDate();
            boolean isCompleted = rs.getInt("is_completed") == 1;
            habitProgress.put(progressDate, isCompleted);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return habitProgress;
}
public void updateHabit(String description, boolean hasReminder, LocalTime remindTime) {
    String updateQuery = "UPDATE habits SET has_reminder = ?, remind_time = ? WHERE description = ?";
    
    try (PreparedStatement st = conn.prepareStatement(updateQuery)) {
        st.setBoolean(1, hasReminder);
        st.setObject(2, Time.valueOf(remindTime));
        st.setString(3, description);
        st.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}
public boolean updateHabitProgress(String description, LocalDate progressDate, boolean isCompleted) {
    String updateQuery = "UPDATE habit_progress SET is_completed = ? WHERE habit_description = ? AND progress_date = ?";
    String insertQuery = "INSERT INTO habit_progress (habit_description, progress_date, is_completed) VALUES (?, ?, ?)";
    
    try (PreparedStatement stUpdate = conn.prepareStatement(updateQuery);
         PreparedStatement stInsert = conn.prepareStatement(insertQuery)) {
        stUpdate.setBoolean(1, isCompleted);
        stUpdate.setString(2, description);
        stUpdate.setDate(3, Date.valueOf(progressDate));
        int rowsUpdated = stUpdate.executeUpdate();
        
        if (rowsUpdated == 0) {
            stInsert.setString(1, description);
            stInsert.setDate(2, Date.valueOf(progressDate));
            stInsert.setBoolean(3, isCompleted);
            stInsert.executeUpdate();
        }
        
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


public boolean deleteHabit(String description) {
    String deleteQuery = "DELETE FROM habits WHERE description = ?";
    
    try (PreparedStatement st = conn.prepareStatement(deleteQuery)) {
        st.setString(1, description);
        int rowsDeleted = st.executeUpdate();
        if (rowsDeleted > 0) {
            String deleteProgressQuery = "DELETE FROM habit_progress WHERE habit_description =? ";
            try (PreparedStatement stProgress = conn.prepareStatement(deleteProgressQuery)) {
                stProgress.setString(1, description);
                stProgress.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return rowsDeleted > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public List<String> getAllHabitDescriptions() {
    List<String> descriptions = new ArrayList<>();

    String selectQuery = "SELECT description FROM habits";

    try (PreparedStatement st = conn.prepareStatement(selectQuery);
         ResultSet rs = st.executeQuery()) {

        while (rs.next()) {
            String description = rs.getString("description");
            descriptions.add(description);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return descriptions;
}
public void createRowsForHabitDescriptions() {
    LocalDate today = LocalDate.now();
    List<String> habitDescriptions = getAllHabitDescriptions(); // Implement a method to get all habit descriptions.

    for (String description : habitDescriptions) {
        if (!habitProgressExists(description, today)) {
            insertHabitProgress(description, today, false);
        }
    }
}

public boolean habitProgressExists(String description, LocalDate date) {
    String selectQuery = "SELECT COUNT(*) FROM habit_progress WHERE habit_description = ? AND progress_date = ?";
    try (PreparedStatement st = conn.prepareStatement(selectQuery)) {
        st.setString(1, description);
        st.setDate(2, Date.valueOf(date));
        ResultSet rs = st.executeQuery();
        rs.next();
        int count = rs.getInt(1);
        return count > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public void insertHabitProgress(String description, LocalDate date, boolean isCompleted) {
    String insertQuery = "INSERT INTO habit_progress (habit_description, progress_date, is_completed) VALUES (?, ?, ?)";
    try (PreparedStatement st = conn.prepareStatement(insertQuery)) {
        st.setString(1, description);
        st.setDate(2, Date.valueOf(date));
        st.setBoolean(3, isCompleted);
        st.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
public double getCompletionPercentageForHabit(String habitDescription) {
    String selectQuery = "SELECT COUNT(*) FROM habit_progress WHERE habit_description = ? AND is_completed = 1";

    try (PreparedStatement st = conn.prepareStatement(selectQuery)) {
        st.setString(1, habitDescription);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            int completedCount = rs.getInt(1);
            rs.close();

            int totalEntries = getTotalEntriesForHabit(habitDescription);
            if (totalEntries > 0) {
                return (double) completedCount / totalEntries * 100.0;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return 0.0; // Default completion percentage.
}

public int getTotalEntriesForHabit(String habitDescription) {
    String selectQuery = "SELECT COUNT(*) FROM habit_progress WHERE habit_description = ?";

    try (PreparedStatement st = conn.prepareStatement(selectQuery)) {
        st.setString(1, habitDescription);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return rs.getInt(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return 0; // Default total entries.
}



}