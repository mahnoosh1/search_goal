package agents;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

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

    private  int id =2;
    private int decider = 0;
    private int step = 0;
    private int episode = 0;
    private int max_step = 2000;
    private int max_episodes = 500;
    private int trial = 0;
    private int max_trial = 40;
    private int x = 0;
    private int y =0;
    private int section = 0;
    private ArrayList<String> actions = new ArrayList<String>();
    public static Q_table table1 = new Q_table();
    public static  Q_table table0_2 = new Q_table();
    private int x_goal = 150;
    private  int y_goal = 270;
    private int move_step = 5;
    private ArrayList<entrance> entrances_left = new ArrayList<entrance>();
    private ArrayList<entrance> entrances_right = new ArrayList<entrance>();
    private int ent_left_mid_x = 100;
    private int ent_left_mid_y = 105;
    private int ent_right_mid_x = 200;
    private int ent_right_mid_y = 205;
    private int d=10;
    private int a=3;
    PrintWriter writer = null;
    public ArrayList<Integer> avg_step = new ArrayList<Integer>();

    protected void setup() {
        super.setup();
        try {
            writer = new PrintWriter("Agent"+id+"info"+".txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.initial();
        this.actions.add("up");this.actions.add("down");this.actions.add("left");this.actions.add("right");
        for(int i=0;i<10;i++) {
            entrance ent_t = new entrance(100, 100+i);
            entrance ent_tt = new entrance(200, 200+i);
            this.entrances_left.add(ent_t);
            this.entrances_right.add(ent_tt);

        }
        addBehaviour(new right_agent.Receiver());
        addBehaviour(new right_agent.Results(this,5000));

    }
    public void initial() {
        this.x = 250;
        this.y= 20;
    }
    public String randomAction() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 4);
        return this.actions.get(randomNum);
    }
    public Boolean updatePosition(String action) {
        int x_new = 0;
        int y_new = 0;
        Boolean hitBlock = false;
        if (action == "up"){
            y_new = this.y+this.move_step;
            x_new = this.x;
        }
        if (action == "down"){
            y_new = this.y-this.move_step;
            x_new = this.x;
        }
        if (action == "left"){
            x_new = this.x-this.move_step;
            y_new = this.y;
        }
        if (action == "right"){
            x_new = this.x+this.move_step;
            y_new = this.y;
        }
        if ((x_new >= 0 && x_new<300) && (y_new>=0 && y_new<300)){
            hitBlock = !isPath(x_new,y_new);
            if (!hitBlock) {
                this.x = x_new;
                this.y=y_new;
            }
        }
        else {
            hitBlock=true;
        }

        return hitBlock;
    }
    public Boolean isPath(int x, int y) {
        Boolean is = true;
        if (x==100) {
            Boolean find=false;
            for (int i=0;i<entrances_left.size();i++) {
                if (y==entrances_left.get(i).getY()){
                    find=true;
                    break;
                }
            }
            if (find==true) {
                is=true;
            }
            else {
                is=false;
            }
        }
        if (x==200) {
            Boolean find=false;
            for (int i=0;i<entrances_right.size();i++) {
                if (y==entrances_right.get(i).getY()){
                    find=true;
                    break;
                }
            }
            if (find==true) {
                is=true;
            }
            else {
                is=false;
            }
        }
        return is;
    }
    public Boolean inBound(String action) {
        int x_new = 0;
        int y_new = 0;
        Boolean in = true;
        if (action == "up"){
            y_new = this.y+this.move_step;
            x_new = this.x;
        }
        if (action == "down"){
            y_new = this.y-this.move_step;
            x_new = this.x;
        }
        if (action == "left"){
            x_new = this.x-this.move_step;
            y_new = this.y;
        }
        if (action == "right"){
            x_new = this.x+this.move_step;
            y_new = this.y;
        }
        if ((x_new >= 0 && x_new<300) && (y_new>=0 && y_new<300)){
            in = true;
        }
        else {
            in = false;
        }
        return in;
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
    public Double dist(int p0x, int p0y, int p1x, int p1y) {
        return Math.sqrt((p0x-p1x)*(p0x-p1x) + (p0y-p1y)*(p0y-p1y));
    }
    public double findAngle(double p0x,double p0y,double p1x,double p1y,double p2x,double p2y) {
        double a = Math.pow(p1x-p0x,2) + Math.pow(p1y-p0y,2),
                b = Math.pow(p1x-p2x,2) + Math.pow(p1y-p2y,2),
                c = Math.pow(p2x-p0x,2) + Math.pow(p2y - p0y, 2);
        return 57.2958*Math.acos( (a+b-c) / Math.sqrt(4*a*b) );
    }
    public Double calcReward(Boolean hitBlock) {
        Double r = 0.0;
        if (hitBlock) {
            r=-100.0;
        } else {
            if(this.section == 0){
                r = this.dist(this.x, this.y, this.ent_left_mid_x, this.ent_left_mid_y);
            }
            if(this.section == 1){
                r = this.dist(this.x, this.y, this.x_goal, this.y_goal);
            }
            if(this.section == 2){
                r = this.dist(this.x, this.y, this.ent_right_mid_x, this.ent_right_mid_y);
            }
            if (r==0) {
                r= 100.0;
            }
            else
                r=1/r;
        }
        return r;
    }
    public String calcState(String action, Boolean hitBlock) {
        String s = "";
        Integer diff = this.diffAngleState(action, hitBlock);
        if (this.section == 0) {
            Double x1 = this.dist(this.x, this.y, this.ent_left_mid_x, this.ent_left_mid_y);
            Double x2 = this.dist(this.x, this.y, this.x_goal, this.y_goal);
            Double x3 = this.findAngle(this.x_goal, this.y_goal, this.x, this.y, this.ent_left_mid_x, this.ent_left_mid_y);
            s = Math.round(x1/this.d)+"#"+Math.round(x2/this.d)+"#"+Math.round(x3/this.a)+"#"+diff;
        }
        if (this.section == 1) {
            Double x1 = this.dist(this.x, this.y, this.ent_left_mid_x, this.ent_left_mid_y);
            Double x2 = this.dist(this.x, this.y, this.x_goal, this.y_goal);
            Double x3 = this.findAngle(this.x_goal, this.y_goal, this.x, this.y, this.ent_left_mid_x, this.ent_left_mid_y);
            s = Math.round(x1/this.d)+"#"+Math.round(x2/this.d)+"#"+Math.round(x3/this.a)+"#"+diff;
        }
        if (this.section == 2) {
            Double x1 = this.dist(this.x, this.y, this.ent_right_mid_x, this.ent_right_mid_y);
            Double x2 = this.dist(this.x, this.y, this.x_goal, this.y_goal);
            Double x3 = this.findAngle(this.x_goal, this.y_goal, this.x, this.y, this.ent_right_mid_x, this.ent_right_mid_y);
            s = Math.round(x1/this.d)+"#"+Math.round(x2/this.d)+"#"+Math.round(x3/this.a)+"#"+diff;
        }
        return s;
    }
    public String calcStateHalf() {
        String s = "";
        if (this.section == 0) {
            Double x1 = this.dist(this.x, this.y, this.ent_left_mid_x, this.ent_left_mid_y);
            Double x2 = this.dist(this.x, this.y, this.x_goal, this.y_goal);
            Double x3 = this.findAngle(this.x_goal, this.y_goal, this.x, this.y, this.ent_left_mid_x, this.ent_left_mid_y);
            s = Math.round(x1/this.d)+"#"+Math.round(x2/this.d)+"#"+Math.round(x3/this.a);
        }
        if (this.section == 1) {
            Double x1 = this.dist(this.x, this.y, this.ent_left_mid_x, this.ent_left_mid_y);
            Double x2 = this.dist(this.x, this.y, this.x_goal, this.y_goal);
            Double x3 = this.findAngle(this.x_goal, this.y_goal, this.x, this.y, this.ent_left_mid_x, this.ent_left_mid_y);
            s = Math.round(x1/this.d)+"#"+Math.round(x2/this.d)+"#"+Math.round(x3/this.a);
        }
        if (this.section == 2) {
            Double x1 = this.dist(this.x, this.y, this.ent_right_mid_x, this.ent_right_mid_y);
            Double x2 = this.dist(this.x, this.y, this.x_goal, this.y_goal);
            Double x3 = this.findAngle(this.x_goal, this.y_goal, this.x, this.y, this.ent_right_mid_x, this.ent_right_mid_y);
            s = Math.round(x1/this.d)+"#"+Math.round(x2/this.d)+"#"+Math.round(x3/this.a);
        }
        return s;
    }
    public String nextPosState(String action, Boolean hitBlock) {
        String nextstate = "";
        ArrayList<Integer> nextpos = new ArrayList<Integer>();
        nextpos = this.getNextPos(action, hitBlock);
        if (nextpos.get(0)<100) {
            Double x1 = dist(nextpos.get(0), nextpos.get(1),x_goal,y_goal);
            Double x2 = dist(nextpos.get(0), nextpos.get(1),ent_left_mid_x,ent_left_mid_y);
            Double x3= findAngle(x_goal,y_goal, nextpos.get(0), nextpos.get(1), ent_left_mid_x,ent_left_mid_y);
            nextstate=Math.round(x1/this.d)+"#"+Math.round(x2/this.d)+"#"+Math.round(x3/this.a);
        }
        if (nextpos.get(0)>=100 && nextpos.get(0)<200) {
            Double x1 = dist(nextpos.get(0), nextpos.get(1),x_goal,y_goal);
            Double x2 = dist(nextpos.get(0), nextpos.get(1),ent_left_mid_x,ent_left_mid_y);
            Double x3= findAngle(x_goal,y_goal, nextpos.get(0), nextpos.get(1), ent_left_mid_x,ent_left_mid_y);
            nextstate=Math.round(x1/this.d)+"#"+Math.round(x2/this.d)+"#"+Math.round(x3/this.a);
        }
        if (nextpos.get(0)>=200) {
            Double x1 = dist(nextpos.get(0), nextpos.get(1),x_goal,y_goal);
            Double x2 = dist(nextpos.get(0), nextpos.get(1),ent_right_mid_x,ent_right_mid_y);
            Double x3= findAngle(x_goal,y_goal, nextpos.get(0), nextpos.get(1), ent_right_mid_x,ent_right_mid_y);
            nextstate=Math.round(x1/this.d)+"#"+Math.round(x2/this.d)+"#"+Math.round(x3/this.a);
        }
        return nextstate;

    }
    public ArrayList<Integer> getNextPos(String action, Boolean hitBlock) {
        ArrayList<Integer> pos = new ArrayList<Integer>();
        int x_new = 0;
        int y_new = 0;
        if (hitBlock) {
            x_new= this.x;
            y_new=this.y;
        }
        else {
            if (action == "up"){
                y_new = this.y+this.move_step;
                x_new = this.x;
            }
            if (action == "down"){
                y_new = this.y-this.move_step;
                x_new = this.x;
            }
            if (action == "left"){
                x_new = this.x-this.move_step;
                y_new = this.y;
            }
            if (action == "right"){
                x_new = this.x+this.move_step;
                y_new = this.y;
            }
        }
        pos.add(0, x_new);
        pos.add(1, y_new);
        return  pos;
    }
    public Double diffAngleDouble(String action, Boolean hitBlock) {
        Double diff = 0.0;
        Double x1 =0.0;
        Double x2=0.0;
        ArrayList<Integer> nextPos = this.getNextPos(action, hitBlock);
        if (this.section == 0) {
            x1 = this.findAngle(this.x_goal, this.y_goal, this.x,this.y, this.ent_left_mid_x, this.ent_left_mid_y);
            x2 = this.findAngle(this.x_goal, this.y_goal, nextPos.get(0),nextPos.get(1), this.ent_left_mid_x, this.ent_left_mid_y);
        }
        if (this.section == 1) {
            x1 = this.findAngle(this.x_goal, this.y_goal, this.x,this.y, this.ent_left_mid_x, this.ent_left_mid_y);
            x2 = this.findAngle(this.x_goal, this.y_goal, nextPos.get(0),nextPos.get(1), this.ent_left_mid_x, this.ent_left_mid_y);
        }
        if (this.section == 2) {
            x1 = this.findAngle(this.x_goal, this.y_goal, this.x,this.y, this.ent_right_mid_x, this.ent_right_mid_y);
            x2 = this.findAngle(this.x_goal, this.y_goal, nextPos.get(0),nextPos.get(1), this.ent_right_mid_x, this.ent_right_mid_y);
        }
        return x1-x2;
    }
    public Integer diffAngleState(String action, Boolean hitBlock) {
        Double diff = 0.0;
        Double x1 =0.0;
        Double x2=0.0;
        ArrayList<Integer> nextPos = this.getNextPos(action, hitBlock);
        if (this.section == 0) {
            x1 = this.findAngle(this.x_goal, this.y_goal, this.x,this.y, this.ent_left_mid_x, this.ent_left_mid_y);
            x2 = this.findAngle(this.x_goal, this.y_goal, nextPos.get(0),nextPos.get(1), this.ent_left_mid_x, this.ent_left_mid_y);
        }
        if (this.section == 1) {
            x1 = this.findAngle(this.x_goal, this.y_goal, this.x,this.y, this.ent_left_mid_x, this.ent_left_mid_y);
            x2 = this.findAngle(this.x_goal, this.y_goal, nextPos.get(0),nextPos.get(1), this.ent_left_mid_x, this.ent_left_mid_y);
        }
        if (this.section == 2) {
            x1 = this.findAngle(this.x_goal, this.y_goal, this.x,this.y, this.ent_right_mid_x, this.ent_right_mid_y);
            x2 = this.findAngle(this.x_goal, this.y_goal, nextPos.get(0),nextPos.get(1), this.ent_right_mid_x, this.ent_right_mid_y);
        }
        return Math.toIntExact(Math.round((x1 - x2) / this.a));
    }

    public int getDecider(int episode, int trial) {
        if (trial>=0 && trial<=5) {
            if (episode >=0 && episode <=10) {
                decider = 0;
            }
            if (episode >10 && episode <=20) {
                decider = 1;
            }
            if (episode >20 && episode <=30) {
                decider = 2;
            }
            if (episode >30 && episode <=40) {
                decider = 3;
            }
            if (episode >40 && episode <=50) {
                decider = 4;
            }
            if (episode >50 && episode <=150) {
                decider = 5;
            }
            if (episode >150 && episode <=250) {
                decider = 6;
            }
            if (episode >250 && episode <=350) {
                decider = 7;
            }
            if (episode >350 && episode <=500) {
                decider = 8;
            }
        } else {
            if (episode >=0 && episode<=50) {
                decider=6;
            }
            if (episode>50 && episode<=250) {
                decider=7;
            }
            if (episode>250) {
                decider=8;
            }
        }
        return decider;
    }
    private ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> All_Q_vals(String state) {
        ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> Q = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>>();
        if (this.section == 0 || this.section == 2) {
            for (String k : table0_2.Q_vals.keySet()) {
                if (k.contains(state)){
                    ConcurrentHashMap<String,Double> temp = new ConcurrentHashMap<String,Double>();
                    temp = table0_2.Q_vals.get(k);
                    Q.put(k, temp);
                }
            }
        }
        if (this.section == 1) {
            for (String k : table1.Q_vals.keySet()) {
                if (k.contains(state)){
                    ConcurrentHashMap<String,Double> temp = new ConcurrentHashMap<String,Double>();
                    temp = table1.Q_vals.get(k);
                    Q.put(k, temp);
                }
            }
        }
        return Q;
    }
    private String explorExplot(int decider, String state) {
        String action = "";
        ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> temp = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>>();
        temp = this.All_Q_vals(state);
        Random r1 = new Random();
        if (temp.size()>0) {
            if (decider == 0) {
                switch (r1.nextInt(10)) {
                    case 0:
                    case 2:
                    case 1:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        action = this.randomAction();
                        break;
                    case 9:
                        if (this.section == 0){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table1.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
                        }

                        break;
                }
            }
            if (decider == 1) {
                switch (r1.nextInt(10)) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        action = this.randomAction();
                        break;
                    case 8:
                    case 9:
                        if (this.section == 0){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table1.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
                        }

                        break;
                }
            }
            if (decider == 2) {
                switch (r1.nextInt(10)) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        action = this.randomAction();
                        break;
                    case 7:
                    case 8:
                    case 9:
                        if (this.section == 0){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table1.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
                        }

                        break;
                }
            }
            if (decider == 3) {
                switch (r1.nextInt(10)) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        action = this.randomAction();
                        break;
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        if (this.section == 0){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table1.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
                        }

                        break;
                }
            }
            if (decider == 4) {
                switch (r1.nextInt(10)) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        action = this.randomAction();
                        break;
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        if (this.section == 0){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table1.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
                        }

                        break;
                }
            }
            if (decider == 5) {
                switch (r1.nextInt(10)) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        action = this.randomAction();
                        break;
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        if (this.section == 0){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table1.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
                        }

                        break;
                }
            }
            if (decider == 6) {
                switch (r1.nextInt(10)) {
                    case 0:
                    case 1:
                    case 2:
                        action = this.randomAction();
                        break;
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        if (this.section == 0){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table1.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
                        }

                        break;
                }
            }
            if (decider == 7) {
                switch (r1.nextInt(10)) {
                    case 0:
                    case 1:
                        action = this.randomAction();
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        if (this.section == 0){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table1.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
                        }

                        break;
                }
            }
            if (decider == 8) {
                switch (r1.nextInt(10)) {
                    case 0:
                        action = this.randomAction();
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        if (this.section == 0){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table1.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table0_2.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
                        }

                        break;
                }
            }

        }
        else {
            action = this.randomAction();
        }
        return  action;
    }
    private Boolean hit_goal() {
        Boolean hit = false;
        if (this.x == this.x_goal && this.y == this.y_goal) {
            hit = true;
        }
        return hit;
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

            if (trial<max_trial) {
                if (episode<max_episodes) {
                    int step_goal = 2000;
                    initial();
                    for(int i=0;i<max_step;i++) {
                        System.out.println("Agent "+id+" step "+i+" episode "+episode+" trial "+trial);
                        getDecider(episode, trial);
                        defineSection();
                        String stateHalf = calcStateHalf();
                        String action = explorExplot(decider, stateHalf);
                        if (inBound(action)) {
                            Boolean hitBlock = updatePosition(action);
                            String state = calcState(action, hitBlock);
                            Double reward = calcReward(hitBlock);
                            String nextpos= nextPosState(action, hitBlock);
                            Double diff = diffAngleDouble(action,hitBlock);

                            if (section == 0 || section==2){
                                table0_2.update(state,nextpos,reward,diff);
                            }
                            if (section==1) {
                                table1.update(state,nextpos,reward,diff);
                            }

                        } else {
                            String state = calcState(action,true);
                            Double reward = -100.0;
                            String nextpos= calcState(action, true);
                            Double diff = 0.0;
                            if (section == 0 || section==2){
                                table0_2.update(state,nextpos,reward, (diff));
                            }
                            if (section==1) {
                                table1.update(state,nextpos,reward, (diff));
                            }
                        }
                        ///////////////////////////
                        try {
                            DFAgentDescription dfd = new DFAgentDescription();
                            ServiceDescription sd = new ServiceDescription();
                            sd.setType("coordinator");
                            dfd.addServices(sd);
                            DFAgentDescription[] result = DFService.search(this.myAgent, dfd);
                            ACLMessage mensagem = new ACLMessage(ACLMessage.CFP);
                            if(section == 0 || section == 2) {
                                mensagem.setContentObject( table0_2.Q_vals);
                            }
                            if (section==1) {
                                mensagem.setContentObject(table1.Q_vals);
                                mensagem.setContent("table1");
                            }

                            AID receiver = new AID();
                            receiver.setLocalName("coordinator");
                            mensagem.addReceiver(receiver);
                            myAgent.send(mensagem);
                            Thread.sleep(1000);
                        } catch (FIPAException | InterruptedException | IOException e) {
                            e.printStackTrace();
                        }
                        //////////////////////////
                        if (hit_goal()) {
                            writer.println("Agent "+id+" hit the goal in episode "+episode );
                            writer.flush();
                            step_goal = i;
                            break;
                        }
                    }
                    episode++;
                    avg_step.add(step_goal);
                }
                if (episode == max_episodes) {
                    trial++;
                    writer.println("Agent "+id +"  trial "+ trial+" shoro shod");
                    writer.flush();
                    episode = 0;
                    step =0;
                    initial();
                    PrintWriter writer1 = null;
                    try {
                        writer1 = new PrintWriter("Agent"+id+"trial"+(trial-1)+".txt", "UTF-8");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    writer1.println(avg_step);
                    writer1.println("size: "+avg_step.size());
                    writer1.close();
                    avg_step = new ArrayList<Integer>();
                }

            }
            if (trial==max_trial) {
                writer.close();
            }
        }
    }

}


