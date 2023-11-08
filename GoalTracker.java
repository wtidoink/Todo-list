import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;

class GoalTracker {
    private TaskCRUD taskCrud;
    private HabitCRUD habitCrud;
    private JFrame frame;
    private JButton addTaskButton, deleteTaskButton, addHabitButton, deleteHabitButton;
    private JPanel taskPanel, habitPanel;
    private JScrollPane taskScroll, habitScroll;
    private JTabbedPane tabbedPane;
    private DefaultTableModel taskTableModel,habitTableModel;
    private JTable taskTable,habitTable;
   private List<Habit> habits;



    GoalTracker(TaskCRUD taskCrud, HabitCRUD habitCrud) {
        this.taskCrud = taskCrud;
        this.habitCrud = habitCrud;

        frame = new JFrame("Goal Tracker");
        taskPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        habitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addTaskButton = new JButton("Add Task");
        deleteTaskButton = new JButton("Delete Task");
        addHabitButton = new JButton("Add Habit");
        
        deleteHabitButton = new JButton("Delete Habit");

            

        taskTableModel = new DefaultTableModel();
        habitTableModel =new DefaultTableModel();
taskTableModel.addColumn("Description");
taskTableModel.addColumn("Progress");


taskTable = new JTable(taskTableModel);
taskTable.getColumnModel().getColumn(1).setMinWidth(60);
taskTable.getColumnModel().getColumn(1).setMaxWidth(60);

 
taskTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected((Boolean) value);
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);
        checkBox.setPreferredSize(new Dimension(60,20));
        return checkBox;
    }
});

taskTable.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        int column = taskTable.columnAtPoint(e.getPoint());
        if (column == 1) { 
            int row = taskTable.rowAtPoint(e.getPoint());
            boolean completed = (Boolean) taskTable.getValueAt(row, 1);
            taskTable.setValueAt(!completed, row, 1);
            String taskDescription = (String) taskTable.getValueAt(row, 0);
            taskCrud.updateTaskCompletionStatus(taskDescription, !completed);
        }
    }
});



habitTableModel = new DefaultTableModel();
habitTableModel.addColumn("Description");
habitTableModel.addColumn("Percentage");
habitTableModel.addColumn("Progress");

habitTable = new JTable(habitTableModel);
habitTable.getColumnModel().getColumn(2).setCellRenderer(new ProgressCellRenderer());
habitTable.getColumnModel().getColumn(2).setMinWidth(60);
habitTable.getColumnModel().getColumn(2).setMaxWidth(60);
habitTable.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        int column = habitTable.columnAtPoint(e.getPoint());
        if (column == 2) { 
            int row = habitTable.rowAtPoint(e.getPoint());
            boolean completed = (Boolean) habitTable.getValueAt(row, 2);
            habitTable.setValueAt(!completed, row, 2);
            String taskDescription = (String) habitTable.getValueAt(row, 0);
            habitCrud.updateHabitProgress(taskDescription, LocalDate.now(), !completed);
            System.out.println(taskDescription);
        }
    }
});


     
        taskScroll = new JScrollPane(taskTable);
        taskScroll.setPreferredSize(new Dimension(400, 300));



        habitScroll = new JScrollPane(habitTable);
        habitScroll.setPreferredSize(new Dimension(400, 300));

        tabbedPane = new JTabbedPane();
        
        addTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String taskDescription = JOptionPane.showInputDialog(frame, "Enter Task Description");
                if (taskDescription != null && !taskDescription.isEmpty()) {
                    Task newTask = new Task(1,taskDescription, false);
                    taskCrud.addTask(newTask);
                    taskTableModel.addRow(new Object[]{newTask.getDescription(), false});
                }
            }
        });

        addHabitButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        String habitDescription = JOptionPane.showInputDialog(frame, "Enter Habit Description");
        if (habitDescription != null && !habitDescription.isEmpty()) {
            int option = JOptionPane.showConfirmDialog(frame, "Does it have a reminder?", "Reminder", JOptionPane.YES_NO_OPTION);
            boolean hasReminder = (option == JOptionPane.YES_OPTION);
            LocalTime reminderTime = null;

            if (hasReminder) {
                String timeInput = JOptionPane.showInputDialog(frame, "Enter Reminder Time (HH:mm:ss):");
                if (timeInput != null && !timeInput.isEmpty()) {
                    try {
                        reminderTime = LocalTime.parse(timeInput);
                    } catch (DateTimeParseException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid time format. Please use HH:mm:ss format.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
          
            Habit newHabit = new Habit(habitDescription, hasReminder, reminderTime,new HashMap<>());
            habitCrud.addHabit(newHabit);
            habitCrud.createRowsForHabitDescriptions();
            habitTableModel.addRow(new Object[]{newHabit.getDescription(),habitCrud.getCompletionPercentageForHabit(newHabit.getDescription()), false});
        }
    }
});

        

        deleteTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedTask();
            }
        });

        deleteHabitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedHabit();
            }
        });
       
       
        List<Task> tasks = taskCrud.getAllTasks();
    for (Task task : tasks) {
        taskTableModel.addRow(new Object[]{task.getDescription(), task.getCheck()});
    }

     populateHabitTable();
        taskPanel.add(addTaskButton);
        taskPanel.add(deleteTaskButton);
        taskPanel.add(taskScroll);


        habitPanel.add(addHabitButton);
        habitPanel.add(deleteHabitButton);
        habitPanel.add(habitScroll);

        tabbedPane.addTab("Tasks", taskPanel);
        tabbedPane.addTab("Habits", habitPanel);

        frame.add(tabbedPane);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
     private void deleteSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow != -1) {
            String taskDescription = (String) taskTable.getValueAt(selectedRow, 0);
            taskTableModel.removeRow(selectedRow);
    
            if (taskCrud.deleteTaskByDescription(taskDescription)) {
            } else {
            }
        }
    }
    
    private void deleteSelectedHabit() {
        int selectedRow = habitTable.getSelectedRow();
        if (selectedRow != -1) {
            String description = (String) habitTable.getValueAt(selectedRow, 0); 

                if (habitCrud.deleteHabit(description)) {
                    DefaultTableModel model = (DefaultTableModel) habitTable.getModel();
                    model.removeRow(selectedRow);
                }
        }
    }
    
    private void populateHabitTable() {
        habits = habitCrud.getAllHabits();
        for (Habit habit : habits) {
            habitTableModel.addRow(new Object[]{
                habit.getDescription(),
                habitCrud.getCompletionPercentageForHabit(habit.getDescription()),
                getProgressForToday(habit)
            });
        }
    }
    private Boolean getProgressForToday(Habit habit) {
        LocalDate today = LocalDate.now();
        Boolean progress = habit.getDailyProgress().get(today);
        return progress != null ? progress : false;
    }

    class ProgressCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setSelected((Boolean) value);
            return checkBox;
        }
    }
    public void checkReminders() {
        LocalTime currentTime = LocalTime.now();
    
        for (Habit habit : habits) {
            if (habit.hasReminder()) {
                LocalTime reminderTime = habit.getReminderTime();
    
                if (currentTime.equals(reminderTime)) {
                    showReminderDialog(habit.getDescription());
                }
            }
        }
    }
    public void showReminderDialog(String habitDescription) {
        JOptionPane.showMessageDialog(null, "Reminder: It's time to work on your habit - " + habitDescription, "Habit Reminder", JOptionPane.INFORMATION_MESSAGE);
    }



    
    
    public static void main(String[] args) {
        TaskCRUD taskCrud = new TaskCRUD(DBConn.connect());
        HabitCRUD habitCrud = new HabitCRUD(DBConn.connect());
        habitCrud.createRowsForHabitDescriptions();
        GoalTracker goaltracker=new GoalTracker(taskCrud, habitCrud);
        Timer reminderTimer = new Timer(6, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goaltracker.checkReminders();
            }
        });
        reminderTimer.start();
    }
}
