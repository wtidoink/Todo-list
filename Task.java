public class Task {
    private int taskId;
    private String tasktext;
    private boolean ischeck;
    public Task(int taskId,String tasktext,boolean ischeck){
        this.taskId=taskId;
         this.tasktext=tasktext;
         this.ischeck=ischeck;
    }
    public int getTaskId() {
        return taskId;
    }

    public String getDescription() {
        return tasktext;
    }

    public boolean isCompleted() {
        return ischeck;
    }
    @Override
        public String toString() {
            return tasktext;
        }
}
