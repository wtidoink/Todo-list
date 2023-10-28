import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;

class TodoApp{
     private JFrame frame;
     private JButton addtask;
     private JPanel mypan;
     private JList<Taskitem> myl;
     private JScrollPane myscroll;

   TodoApp(){

        

       frame= new JFrame("Todo-list");
       mypan= new JPanel(new FlowLayout(FlowLayout.CENTER));
       addtask=new JButton("add");
       String[] myt={
        "Cook food","Complete Java Project","Read a book","Solve Code force Problems","Do exercise"};
       /* DefaultListModel<String> taskListModel = new DefaultListModel<>();*/
       myl=new JList<Taskitem>();
        for(String task :myt)
                myl.addElement(new Taskitem(task,false));
       
       myl.setCellRenderer(new TaskCellRenderer());
       myscroll=new JScrollPane(myl);
       myscroll.setPreferredSize(new Dimension(400,380));
      

       addtask.setBounds(400,400,80,50);
       mypan.add(myscroll);
       frame.add(addtask);
       frame.add(mypan);
       frame.setSize(500, 500);
       //frame.setLayout(null);
       frame.setVisible(true);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

       private class TaskCellRenderer extends JLabel implements ListCellRenderer<String> {
        private Border padding = new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new LineBorder(Color.BLACK));

        public TaskCellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value);
            setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);
            setBorder(padding);
            return this;
        }
    }

    public static void main(String[] args) {
          new TodoApp();
        
    }



}