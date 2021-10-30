package agents;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import jade.lang.acl.UnreadableException;

public class coordinator extends Agent {

    private  int id =4;
    private  int n =0;
    private ArrayList<Integer> x = new ArrayList<>();

    protected void setup() {
        super.setup();
        System.out.println("start "+id);
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("coordinator");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        //Behaviours
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        addBehaviour(new coordinator.Receiver());

    }
    public ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> aggregate(ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> main, ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> sub) {
        ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> total = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>>();
        for (String k1: main.keySet()) {
            if (sub.containsKey(k1)) {
                ConcurrentHashMap<String,Double> temp = new ConcurrentHashMap<String,Double>();
                temp.put("val", (main.get(k1).get("val")+sub.get(k1).get("val"))/2);
                temp.put("diff", (main.get(k1).get("diff")+sub.get(k1).get("diff"))/2);
                total.put(k1, temp);
            }
            else {
                total.put(k1,main.get(k1));
            }
        }
        for (String k2: sub.keySet()) {
            if (!total.containsKey(k2)) {
                total.put(k2, sub.get(k2));
            }
        }
        return total;
    }
    private class Receiver extends CyclicBehaviour {

        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> table1 = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>>();
                ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> table0 = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>>();
                ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> table2 = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>>();
                table1 = aggregate(left_agent.table1.Q_vals, right_agent.table1.Q_vals);
                table1= aggregate(table1, middle_agent.table1.Q_vals);
                table0 = aggregate(left_agent.table0.Q_vals, right_agent.table0.Q_vals);
                table0= aggregate(table0 ,middle_agent.table0.Q_vals);
                table2= aggregate(left_agent.table2.Q_vals, right_agent.table2.Q_vals);
                table2= aggregate(table2 ,middle_agent.table2.Q_vals);
                left_agent.table1.Q_vals = table1;
                right_agent.table1.Q_vals = table1;
                middle_agent.table1.Q_vals = table1;
                left_agent.table0.Q_vals = table0;
                right_agent.table0.Q_vals = table0;
                middle_agent.table0.Q_vals = table0;
                left_agent.table2.Q_vals = table2;
                right_agent.table2.Q_vals = table2;
                middle_agent.table2.Q_vals = table2;
                System.out.println("update shod table ha");
                if (msg.getPerformative() == ACLMessage.INFORM) {


                }


            }
        }
    }


}


