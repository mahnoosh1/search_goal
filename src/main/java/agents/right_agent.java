package agents;
import java.io.IOException;
import java.io.Serializable;
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

public class right_agent extends Agent {

    private  int id =1;

    protected void setup() {
        super.setup();
        System.out.println("start "+id);
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("x");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        //Behaviours
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        addBehaviour(new MakeContact());
        addBehaviour(new Receiver());
        addBehaviour(new Results(this,5000));


    }
    private class MakeContact extends OneShotBehaviour {
        public void action() {
            System.out.println("action "+id);

        }
    }


    private class Receiver extends CyclicBehaviour {

        public void action() {

            ACLMessage msg = receive();
            if (msg != null) {
                System.out.println("mesage daryaft shod az "+id);
                try {
                    Object a = msg.getContentObject();
                    System.out.println(a);
                } catch (UnreadableException e) {
                    e.printStackTrace();
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
                    a.add(3);a.add(4);
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


