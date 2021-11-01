package agents;
import java.io.IOException;
import java.io.Serializable;
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

public class coordinator extends Agent implements Serializable {

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
                if (main.get(k1).get("val")>=sub.get(k1).get("val")) {
                    temp.put("val", main.get(k1).get("val"));
                    temp.put("diff", main.get(k1).get("diff"));
                }
                else {
                    temp.put("val", sub.get(k1).get("val"));
                    temp.put("diff", sub.get(k1).get("diff"));
                }
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
                ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> q1 = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>>();
                ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> q0 = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>>();
                ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> q2 = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>>();
                q1 = aggregate(left_agent.table.Q1_vals, right_agent.table.Q1_vals);
                q1= aggregate(q1, middle_agent.table.Q1_vals);
                q0 = aggregate(left_agent.table.Q0_vals, right_agent.table.Q0_vals);
                q0= aggregate(q0 ,middle_agent.table.Q0_vals);
                q2= aggregate(left_agent.table.Q2_vals, right_agent.table.Q2_vals);
                q2= aggregate(q2 ,middle_agent.table.Q2_vals);
                left_agent.table.Q1_vals = q1;
                right_agent.table.Q1_vals = q1;
                middle_agent.table.Q1_vals = q1;
                left_agent.table.Q0_vals = q0;
                right_agent.table.Q0_vals = q0;
                middle_agent.table.Q0_vals = q0;
                left_agent.table.Q2_vals = q2;
                right_agent.table.Q2_vals = q2;
                middle_agent.table.Q2_vals = q2;
                System.out.println("update shod table ha");
                if (msg.getPerformative() == ACLMessage.INFORM) {


                }


            }
        }
    }


}


