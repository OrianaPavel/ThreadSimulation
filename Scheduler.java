import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {
    private List<Server> servers ;
    /*private int maxNoServers ;
    private int maxTasksPerServer ;*/
    private Strategy strategy;
    private List<Thread> threads;

    public Scheduler(int maxNoServers, int maxTasksPerServer, AtomicInteger currentTime){
       /* this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;*/
        servers = new ArrayList<>(maxNoServers);
        threads = new ArrayList<>(maxNoServers);
        for(int i = 0; i < maxNoServers; i++){
            Server server = new Server(i+1,currentTime);
            servers.add(server);
            Thread a = new Thread(server);
            threads.add(a);
            a.start();
        }
    }
    protected int WaitingTime(){
        int waitingTime = 0;
        for(Server server:servers)
            waitingTime += server.getWaitingPeriod();
        return waitingTime;
    }
    protected void stopThreads(){
        for(Thread thread: threads)
            thread.interrupt();
    }
    public void changeStrategy(SelectionPolicy policy){
        //policy = SelectionPolicy.SHORTEST_TIME;
        if(policy == SelectionPolicy.SHORTEST_QUEUE){
            strategy = new ConcreteStrategyQueue();
        }
        if(policy == SelectionPolicy.SHORTEST_TIME){
            strategy = new ConcreteStrategyTime();
        }
    }
    public void writeCurrentState(FileWriter log,int currentTime) {
        int id = 0;
        try {
            log.write("Time" + currentTime + "\n");
            log.flush();
            for(Server server:servers) {
                id++;
                log.write("Queue " + id + ": " + server.getTasks() + "\n");
                log.flush();
            }
        } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public void dispatchTask(Task t){
        strategy.addTask(servers,t);
    }

    public List<Server> getServers() {
        return servers;
    }


}
