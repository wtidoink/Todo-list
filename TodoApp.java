import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

class TodoApp{
    private TaskCRUD taskCrud;
     private JFrame frame;
     private JButton addtask,deleteButton;
     private JPanel mypan;
     private JList<Task> myl;
     private DefaultListModel<Task> mytl;
     private JScrollPane myscroll;
     private void deleteSelectedTask() {
      int selectedIndex = myl.getSelectedIndex();
      if (selectedIndex != -1) {
        Task selecttask=myl.getSelectedValue();
        if(taskCrud.deleteTask(selecttask))
          mytl.remove(selectedIndex);
      }
  }


   TodoApp(TaskCRUD taskCrud){

        this.taskCrud=taskCrud;

       frame= new JFrame("Todo-list");
       mypan= new JPanel(new FlowLayout(FlowLayout.CENTER));
       addtask=new JButton("add");
      deleteButton = new JButton("Delete");


       mytl= new DefaultListModel<>();
       //Adds element to jlist
       List<Task> tasks= taskCrud.getAllTasks();
        for (Task task : tasks) {
            mytl.addElement(task);
        }

       myl=new JList<>(mytl);
       myscroll=new JScrollPane(myl);
       myscroll.setPreferredSize(new Dimension(400,380));
      
      deleteButton.setBounds(250,400,80,50);
       addtask.setBounds(150,400,80,50);
       addtask.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // This code will run when the "Add Task" button is clicked
            String taskDescription = JOptionPane.showInputDialog(frame, "Enter Task Description");
            if (taskDescription != null && !taskDescription.isEmpty()) {
                Task newTask = new Task(1,taskDescription, false);
                taskCrud.addTask(newTask);
                mytl.addElement(newTask); // Add the task to the GUI list
            }
        }
    });


     deleteButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteSelectedTask();
        }
    });

  

       mypan.add(myscroll);
       frame.add(addtask);
       frame.add(deleteButton);
       frame.add(mypan);
       frame.setSize(500, 500);
       //frame.setLayout(null);
       frame.setVisible(true);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public static void main(String[] args) {
      TaskCRUD taskCrud=new TaskCRUD(DBConn.connect());
      new TodoApp(taskCrud);
        
    }



}