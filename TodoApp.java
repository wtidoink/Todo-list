import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

class TodoApp{
    private TaskCRUD taskCrud;
     private JFrame frame;
     private JButton addtask;
     private JPanel mypan;
     private JList<Task> myl;
     private DefaultListModel<Task> mytl;
     private JScrollPane myscroll;


   TodoApp(TaskCRUD taskCrud){

        this.taskCrud=taskCrud;

       frame= new JFrame("Todo-list");
       mypan= new JPanel(new FlowLayout(FlowLayout.CENTER));
       addtask=new JButton("add");


       mytl= new DefaultListModel<>();
       List<Task> tasks= taskCrud.getAllTasks();
        for (Task task : tasks) {
            mytl.addElement(task);
        }
//System.out.println(tasks);

       myl=new JList<>(mytl);
       myscroll=new JScrollPane(myl);
       myscroll.setPreferredSize(new Dimension(400,380));
      

       addtask.setBounds(400,400,80,50);
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
       mypan.add(myscroll);
       frame.add(addtask);
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