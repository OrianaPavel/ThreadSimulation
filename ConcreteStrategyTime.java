import java.util.List;

public class ConcreteStrategyTime implements Strategy{
    @Override
    public void addTask(List<Server> servers, Task t) {
        int minTime = Integer.MAX_VALUE, ok = 0;
        Server sel = null;
        for(Server server: servers){
            int size = server.getSizeTasks(), waiting = server.getWaitingPeriod();
            if(size == 0 && waiting == 0){
                server.addTask(t);
                ok = 1;
                break;
            }
            if(waiting < minTime){
                minTime = waiting;
                sel = server;
            }
        }
        if( ok == 0 && sel != null)
            sel.addTask(t);
    }
}
