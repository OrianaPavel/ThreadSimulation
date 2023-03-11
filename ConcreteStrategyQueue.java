import java.util.List;

public class ConcreteStrategyQueue implements Strategy{
    @Override
    public void addTask(List<Server> servers, Task t) {
        int minQueue = Integer.MAX_VALUE, ok = 0;
        Server sel = null;
        for(Server server: servers){
            int size = server.getSizeTasks();
            if(size < minQueue){
                minQueue = size;
                sel = server;
            }
        }
        sel.addTask(t);
    }
}

