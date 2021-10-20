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
        addBehaviour(new coordinator.MakeContact());
        addBehaviour(new coordinator.Receiver());
        addBehaviour(new coordinator.Results(this,5000));

    }
    private class MakeContact extends OneShotBehaviour {
        public void action() {

        }
    }


    private class Receiver extends CyclicBehaviour {

        public void action() {

            ACLMessage msg = receive();
            if (msg != null) {
                System.out.println("mesage daryaft shod az "+msg.getSender().getName());
                n++;
                if (n==2) {
                    try {
                        ArrayList<Integer> m = (ArrayList<Integer>) msg.getContentObject();
                        for (int i=0;i<m.size();i++) {
                            x.add(m.get(i));
                        }

                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }
                    System.out.println("payam koli");
                    System.out.println(x);
                }
                if (n == 3) {
                    n = 1;
                    x = new ArrayList<Integer>();
                    ArrayList<Integer> m = null;
                    try {
                        m = (ArrayList<Integer>) msg.getContentObject();
                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }
                    for (int i=0;i<m.size();i++) {
                        x.add(m.get(i));
                    }
                }
                else {
                    try {
                        ArrayList<Integer> m = (ArrayList<Integer>) msg.getContentObject();
                        for (int i=0;i<m.size();i++) {
                            x.add(m.get(i));
                        }

                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }
                }

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
            //System.out.println("action "+id);
            System.out.println("send message to"+ 1);



        }
    }
}


