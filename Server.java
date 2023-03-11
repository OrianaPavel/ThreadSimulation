import javax.swing.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class Server implements Runnable{
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod,currentTime;
    private Task currentTask;
    private JLabel serverLabel;
    private String labelText;
    private int arrivalTime,serviceTime;
    public Server(int serverID,AtomicInteger currentTime){
        this.currentTime = currentTime;
        labelText = "No "+ serverID + ": ";
        serverLabel = new JLabel(labelText);
        waitingPeriod = new AtomicInteger();
        waitingPeriod.set(0);
        tasks = new ArrayBlockingQueue<>(100);
    }

    public void addTask(Task newTask) {
        try{
            waitingPeriod.getAndAdd(newTask.getServiceTime());
            labelText += "-"+newTask.getID()+"-";
            serverLabel.setText(labelText);
            tasks.put(newTask);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        waitingPeriod.set(0);
        tasks.clear();
        while(true) {
            try {
                if(currentTask == null) {
                    currentTask = tasks.take();
                    arrivalTime = currentTime.get();
                    serviceTime = currentTask.getServiceTime();
                }
                int remainingServiceTime = serviceTime - (currentTime.get() - arrivalTime);
                while(remainingServiceTime > 0) {
                    synchronized (currentTime) {
                        currentTime.wait();
                        //Thread.sleep(1000);
                        currentTask.setServiceTime(currentTask.getServiceTime()-1);
                        waitingPeriod.getAndDecrement();
                        remainingServiceTime = serviceTime - (currentTime.get() - arrivalTime);
                   }
                }
                labelText = labelText.replaceAll("-" + currentTask.getID() + "-", "");
                serverLabel.setText(labelText);
                currentTask = null;
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    public String getTasks(){
        if(currentTask != null && currentTask.getServiceTime() > 0)
            return currentTask + tasks.toString();
        return tasks.toString();
    }
    public int getSizeTasks(){
        return tasks.size();
    }

    public int getWaitingPeriod() {
        return waitingPeriod.get();
    }

    public JLabel getServerLabel() {
        return serverLabel;
    }
}
