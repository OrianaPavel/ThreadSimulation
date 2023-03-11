import javax.swing.*;
import javax.swing.Timer;
import java.util.TimerTask;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable {

    private int timeLimit, maxProcessingTime, minProcessingTime, numberOfServers,minArrivalTime,maxArrivalTime;
    private int numberOfClients;
    private SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;
    private float averageServiceTime,averageWaitingTime;
    private int maxPeek;
    private Scheduler scheduler;
    private SimulationFrame frame;
    private List<Task> generatedTasks;
    private Thread t;
    private AtomicInteger currentTime;
    private FileWriter log ;
    private String clientsArrived;
    public SimulationManager(){ // initializam GUI si adaugam actionlistener pt inceperea simularii
        frame = new SimulationFrame();
        frame.getStart().addActionListener(new StartSimulation());
    }
    public void generateNRandomTasks(){ // generez n tasks random si le adaug in lista generatedTasks pt a fi procesate ulterior
        Random rand = new Random();
        for(int i = 0; i < numberOfClients; i++){
            int arrivalTime = rand.nextInt(minArrivalTime,maxArrivalTime);
            Task a = new Task(i,arrivalTime,rand.nextInt(minProcessingTime,maxProcessingTime));
            averageServiceTime += a.getServiceTime(); // adun toate serviceTime generate pt a calcula media
            generatedTasks.add(a);
        }
        averageServiceTime = averageServiceTime / (float)numberOfClients; // impart totalServiceTime la nr de clienti pt a afla media
        //de sortat dupa arrival time
        Collections.sort(generatedTasks); // sortez crescator task-urile generate in functie de arrivalTime
    }
    static SimulationManager gen;
    public static void main(String[] args) {
        gen = new SimulationManager();
    }

    @Override
    public void run() {
        float currentWaitingTime = 2;
        frame.setTitleSimulation("Current time: "+ currentTime.get() + "");
        while(currentTime.get() <= timeLimit && (generatedTasks.size() > 0 || currentWaitingTime > 0)){ // loop pentru a simula queues conditia de oprire este fie daca am trecut peste timeLimit sau nu mai avem clienti in asteptare
            currentWaitingTime = scheduler.WaitingTime();
            List<Task> taskToRemove = new ArrayList<>(); // selectez si salvez toti clientii care au ajuns la timpul currentTime
            clientsArrived = "";
            for(Task task: generatedTasks) {
                if(task.getArrivalTime() == currentTime.get()) {
                    frame.removeClientFromTextArea(task.getID()); // scot clientul din panoul din dreapta
                    clientsArrived += task.getID()+"-";  // adaug la string clientul curent pt a putea fi afisat in panoul central, animatia de transfer la coada
                    taskToRemove.add(task);
                }
                if(task.getArrivalTime() > currentTime.get())
                    break; // deoarece sunt sortati crescator dupa arrivalTime nu mai are sens sa verific si restul obiectelor
            }
            if(animation())
                break;
            for(Task task: taskToRemove){
                currentWaitingTime += task.getServiceTime();
                scheduler.dispatchTask(task); // adaug clientii in cozi
            }
            if(taskToRemove.size() > maxPeek)
                maxPeek = taskToRemove.size(); // verific daca momentul curent este maxPeek
            averageWaitingTime += currentWaitingTime / (float)numberOfServers;
            generatedTasks.removeAll(taskToRemove); // sterg toti clientii care au sosit la currentTime din lista de clienti generata la inceput
            try {
                log.write(generatedTasks + "\n");
                log.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            scheduler.writeCurrentState(log,currentTime.get());
            currentTime.getAndIncrement();
            try {
                Thread.sleep(1000);

                synchronized (currentTime) {
                    currentTime.notifyAll();
                }
                frame.setTitleSimulation("Current time: "+ currentTime.get() + "");
            } catch (InterruptedException e) {
                break;
            }
        }
        finishSimulation();
    }
    private void finishSimulation(){
        scheduler.stopThreads(); // opresc cozile
        averageWaitingTime /= numberOfClients;
        JOptionPane.showMessageDialog(null,"MaxPeek: " + maxPeek + "\nAverageServiceTime: " + averageServiceTime + "\nAverageWaitingTime "+ averageWaitingTime + "\n");
        try {
            log.write("MaxPeek: " + maxPeek + "\nAverageServiceTime: " + averageServiceTime + "\nAverageWaitingTime "+ averageWaitingTime + "\n");
            log.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean animation(){
        int x = 190, y = 200; // coordonatele de inceput pt animatie
        while (x > 20 ) {
            frame.moveClientToQueue(clientsArrived, x, y + 4 * clientsArrived.length()); // apelez functia de desenare pt noile coordonate
            try {
                Thread.sleep(10);
                x -= 3; // scad din x astfel incat sa apara o miscare liniara spre stanga
            } catch (InterruptedException e) {
                return true;
            }
        }
        frame.moveClientToQueue(null, x, y); //  apelez functia cu stringul null pentru a sterge stringul
        return false;
    }
    class StartSimulation implements ActionListener{ // in momentul in care utilizatorul apasa pe start atunci se
        private boolean getInput(){
            try {
                numberOfServers = frame.getNoOfServers(); // citesc input din interfata grafica
                numberOfClients = frame.getNoOfClients();
                timeLimit = frame.getTimeLimit();
                maxProcessingTime = frame.getMaxProcessingTime();
                minProcessingTime = frame.getMinProcessingTime();
                minArrivalTime = frame.getMinArrivalTime();
                maxArrivalTime = frame.getMaxArrivalTime();
                if (numberOfServers != 0 && numberOfClients != 0 && timeLimit > 0 && maxProcessingTime > minProcessingTime && maxArrivalTime > minArrivalTime)
                    return true;
                else
                    JOptionPane.showMessageDialog(null, "Date de intrare invalide");
            }
            catch(NumberFormatException e1){
                JOptionPane.showMessageDialog(null, "Date de intrare invalide");
            }
            return false;
        }
        public void actionPerformed(ActionEvent e) { // genereaza clientii si se porneste thread-ul
            if(t != null && t.isAlive()) { // opresc thread-ul simularii in cazul in care butonul de start a fost apasat inainte de finalizare
                t.interrupt();
                scheduler.stopThreads();
            }
            try {
                log = new FileWriter("log.txt");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            currentTime = new AtomicInteger();
            currentTime.set(0);
            averageServiceTime = 0; averageWaitingTime = 0; maxPeek = Integer.MIN_VALUE;
            boolean flagValidInput = getInput();
            /*numberOfServers = 5;
            numberOfClients = 15;
            timeLimit = 10;
            maxProcessingTime = 2;
            minProcessingTime = 1;
            minArrivalTime = 1;
            maxArrivalTime = 5;*/
            if(flagValidInput) {
                scheduler = new Scheduler(numberOfServers, numberOfClients, currentTime); // creez serverele
                scheduler.changeStrategy(selectionPolicy); // selectez strategia de distribuire a clientilor la cozi
                generatedTasks = new ArrayList<>();
                generateNRandomTasks(); // apelez functia de generare a clientilor
                frame.startSimulation(numberOfClients, scheduler.getServers()); // initializez frame-ul pt simulare
                t = new Thread(gen); // pornesc thread-ul
                t.start();
            }
        }
    }
}
