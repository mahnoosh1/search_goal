package agents;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import java.util.concurrent.ThreadLocalRandom;

public class left_agent extends Agent {

    private  int id =1;
    private int decider = 0;
    private int step = 0;
    private int episode = 0;
    private int max_step = 2000;
    private int max_episodes = 40;
    private int x = 0;
    private int y =0;
    private int section = 0;
    private ArrayList<String> actions = new ArrayList<String>();
    private Q_table table = new Q_table();
    private int x_goal = 150;
    private  int y_goal = 270;
    private ArrayList<entrance> entrances = new ArrayList<entrance>();
    protected void setup() {
        super.setup();
        System.out.println("start "+id);
        this.initial();
        this.actions.add("up");this.actions.add("down");this.actions.add("left");this.actions.add("right");
        for(int i=0;i<10;i++) {
            entrance ent_t = new entrance(100, 100+i);
            entrance ent_tt = new entrance(200, 200+i);
            this.entrances.add(ent_t);
            this.entrances.add(ent_tt)

        }
        addBehaviour(new Receiver());
        addBehaviour(new Results(this,5000));

    }
    public void initial() {
        this.x = 50;
        this.y= 20;
    }
    public String randomAction() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 4);
        return this.actions.get(randomNum);
    }
    public String getAction(String state){
        String action = "";
        if (this.decider == 0) {
            action = this.randomAction();
        }
        else {
        action = this.table.getAction(state);
        }
        return action;
    }
    public void updatePosition(String action) {
        int x_new = 0;
        int y_new = 0;
        if (action == "up"){
            y_new = this.y+1;
            x_new = this.x;
        }
        if (action == "down"){
            y_new = this.y-1;
            x_new = this.x;
        }
        if (action == "left"){
            x_new = this.x-1;
            y_new = this.y;
        }
        if (action == "right"){
            x_new = this.x+1;
            y_new = this.y;
        }
        if ((x_new >= 0 && x_new<=300) && (y_new>=0 && y_new<=300)){
            this.y = y_new;
            this.x = x_new;
        }
    }
    public void defineSection() {
        if (this.x < 100) {
            this.section = 0;
        }
        if (this.x >= 100 && this.x < 200) {
            this.section = 1;
        }
        if (this.x >= 200) {
            this.section = 2;
        }
    }
    public Double calcReward() {
        if
    }
    private class Receiver extends CyclicBehaviour {

        public void action() {
//           // System.out.println("reciever "+id);
            ACLMessage msg = receive();
            if (msg != null) {
//                //System.out.println("mesage daryaft shod az "+id);
                //Receive coordinates from each pharmacy
                if (msg.getPerformative() == ACLMessage.INFORM) {


                }


            }
        }
    }

    private class Results extends TickerBehaviour {

        public Results(Agent a, long timeout)
        {
            super(a, timeout);
        }
        protected void onTick() {
            System.out.println("action "+id);
            System.out.println("send message to coordinator");
            try {
                // System.out.println("action agent "+id);
                // Contact pharmacies
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("coordinator");
                dfd.addServices(sd);

                DFAgentDescription[] result = DFService.search(this.myAgent, dfd);

                for (int i = 0; i < result.length; ++i) {
                    System.out.println(result[i].getName().getLocalName());
                    // Send message to all pharmacies request the coordinates
                    ACLMessage mensagem = new ACLMessage(ACLMessage.CFP);
                    ArrayList<Integer> a = new ArrayList<Integer>();
                    a.add(1);a.add(2);
                    mensagem.setContentObject(a);
                    AID receiver = new AID();
                    receiver.setLocalName(result[i].getName().getLocalName());
                    mensagem.addReceiver(receiver);
                    myAgent.send(mensagem);
                }
                Thread.sleep(500);
            } catch (FIPAException | IOException | InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

}


