import java.time.LocalDate;
import java.util.Map;

public class Habit {
    private int habitId;
    private String description;
    private boolean hasReminder;
    private Map<LocalDate, Boolean> dailyProgress;

    public Habit(int habitId, String description, boolean hasReminder, Map<LocalDate, Boolean> dailyProgress) {
        this.habitId = habitId;
        this.description = description;
        this.hasReminder = hasReminder;
        this.dailyProgress = dailyProgress;
    }

    public int getHabitId() {
        return habitId;
    }

    public void setHabitId(int habitId) {
        this.habitId = habitId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean hasReminder() {
        return hasReminder;
    }

    public void setHasReminder(boolean hasReminder) {
        this.hasReminder = hasReminder;
    }

    public Map<LocalDate, Boolean> getDailyProgress() {
        return dailyProgress;
    }

    public void setDailyProgress(Map<LocalDate, Boolean> dailyProgress) {
        this.dailyProgress = dailyProgress;
    }

    @Override
    public String toString() {
        return "Habit{" +
                "habitId=" + habitId +
                ", description='" + description + '\'' +
                ", hasReminder=" + hasReminder +
                '}';
    }
}
