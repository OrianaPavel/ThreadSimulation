import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SimulationFrame extends JFrame {
    private final JLabel noOfServersLabel = new JLabel("Number of queues");
    private final JLabel noOfClientsLabel = new JLabel("Number of clients");
    private final JLabel timeLimitLabel = new JLabel("Simulation interval");
    private final JLabel maxProcessingTimeLb = new JLabel("Max processing time");
    private final JLabel minProcessingTimeLb = new JLabel("Min processing time");
    private final JLabel minArrivalTimeLb = new JLabel("Min arrival time");
    private final JLabel maxArrivalTimeLb = new JLabel("Max arrival time");

    private final JTextField noOfServers = new JTextField(8);
    private final JTextField noOfClients = new JTextField(8);
    private final JTextField timeLimit = new JTextField(8);
    private final JTextField maxProcessingTime= new JTextField(8);
    private final JTextField minProcessingTime = new JTextField(8);
    private final JTextField minArrivalTime = new JTextField(8);
    private final JTextField maxArrivalTime = new JTextField(8);
    protected JButton start = new JButton("Start");

    private JFrame frameSimulation;
    private Box center;
    private String clientsString;
    private JTextArea clientsTextArea;
    private DrawCanvas canvas;
    public SimulationFrame(){

        center = Box.createVerticalBox();
        frameSimulation = new JFrame();
        frameSimulation.setVisible(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setLayout(new GridLayout(1,1));
        this.setSize(400,400);
        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 1;
        gbc.gridx = 1;
        panel.add(noOfServersLabel,gbc); gbc.gridy ++; panel.add(noOfServers,gbc); gbc.gridy ++;
        panel.add(noOfClientsLabel,gbc); gbc.gridy ++; panel.add(noOfClients,gbc); gbc.gridy ++;
        panel.add(timeLimitLabel,gbc); gbc.gridy ++; panel.add(timeLimit,gbc); gbc.gridy ++;
        panel.add(minProcessingTimeLb,gbc); gbc.gridy ++; panel.add(minProcessingTime,gbc); gbc.gridy ++;
        panel.add(maxProcessingTimeLb,gbc); gbc.gridy ++; panel.add(maxProcessingTime,gbc); gbc.gridy ++;
        panel.add(minArrivalTimeLb,gbc); gbc.gridy ++; panel.add(minArrivalTime,gbc); gbc.gridy ++;
        panel.add(maxArrivalTimeLb,gbc); gbc.gridy ++; panel.add(maxArrivalTime,gbc); gbc.gridy ++;
        panel.add(start,gbc);

        this.add(panel);
        this.setVisible(true);
    }
    public void startSimulation(int numberOfClients,List<Server> servers){
        canvas = new DrawCanvas();
        canvas.setPreferredSize(new Dimension(200, 400));
        center.removeAll();
        frameSimulation.getContentPane().removeAll();
        frameSimulation.setLayout(new BorderLayout());
        frameSimulation.setSize(600,500);
        frameSimulation.setVisible(true);
        JScrollPane mainScroll = new JScrollPane(center);
        mainScroll.setMaximumSize(new Dimension(200,400));
        mainScroll.setPreferredSize(new Dimension(200,400));
        JScrollPane clientsArea = new JScrollPane();
        clientsArea.setMaximumSize(new Dimension(200,400));
        clientsArea.setPreferredSize(new Dimension(200,400));
        for(Server server: servers){
            center.add(server.getServerLabel());
        }
        clientsString = "";
        for(int i = 0; i < numberOfClients; i++)
            clientsString += "-"+i + "-";
        clientsTextArea = new JTextArea(clientsString);
        clientsTextArea.setLineWrap(true);
        clientsTextArea.setWrapStyleWord(true);
        clientsTextArea.setEditable(false);
        center.repaint();
        clientsArea.getViewport().add(clientsTextArea);
        clientsArea.repaint();
        frameSimulation.add(canvas,BorderLayout.CENTER);
        frameSimulation.add(clientsArea,BorderLayout.LINE_END);
        frameSimulation.add(mainScroll,BorderLayout.LINE_START);
        frameSimulation.revalidate();
    }
    protected void moveClientToQueue(String ID,int x,int y){
        canvas.ID = ID;
        canvas.x = x; canvas.y = y;
        canvas.repaint();
    }
    public void setTitleSimulation(String s) {
        frameSimulation.setTitle(s);
    }
    class DrawCanvas extends JPanel {
        protected String ID;
        protected int x,y;
        @Override
        public void paintComponent(Graphics g) {
            setBackground(Color.WHITE);
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            if(ID != null) {
                g2d.translate((float)x,(float)y);
                g2d.rotate(-Math.toRadians(90));
                g2d.drawString(ID,0,0);
                g2d.translate(-(float)x,-(float)y);
                g2d.rotate(Math.toRadians(90));
            }
            else
                g2d.drawString("",x,400);
        }
    }
    protected void removeClientFromTextArea(int ID){
        clientsString = clientsString.replaceAll("-"+ID + "-","");
        clientsTextArea.setText(clientsString);
        clientsTextArea.repaint();
    }
    public JButton getStart() {
        return start;
    }
    public int getNoOfServers() {
        return Integer.parseInt(noOfServers.getText());
    }
    public int getNoOfClients() {
        return Integer.parseInt(noOfClients.getText());
    }
    public int getTimeLimit() {
        return Integer.parseInt(timeLimit.getText());
    }
    public int getMaxProcessingTime() {
        return Integer.parseInt(maxProcessingTime.getText());
    }
    public int getMinProcessingTime() {
        return Integer.parseInt(minProcessingTime.getText());
    }
    public int getMinArrivalTime() {
        return Integer.parseInt(minArrivalTime.getText());
    }
    public int getMaxArrivalTime() {
        return Integer.parseInt(maxArrivalTime.getText());
    }
}
