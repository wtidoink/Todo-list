import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public class Habit {
    private String description;
    private boolean hasReminder;
    private LocalTime reminderTime;
    private Map<LocalDate, Boolean> dailyProgress;

    public Habit(String description, boolean hasReminder,LocalTime reminderTime, Map<LocalDate, Boolean> dailyProgress) {
        this.description = description;
        this.hasReminder = hasReminder;
        this.reminderTime = reminderTime;
        this.dailyProgress = dailyProgress;
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
 public LocalTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    public boolean isReminderTime() {
        if (hasReminder) {
            LocalTime now = LocalTime.now();
            return now.isAfter(reminderTime);
        }
        return false;
    }
}
