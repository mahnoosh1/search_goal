package agents;
import java.io.*;
import java.util.*;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class test {

    private  int id =0;
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
    public static Q_table table = new Q_table();
    private int x_goal = 150;
    private  int y_goal = 270;
    private int move_step = 5;
    private ArrayList<entrance> entrances_left = new ArrayList<entrance>();
    private ArrayList<entrance> entrances_right = new ArrayList<entrance>();
    private int ent_left_mid_x = 100;
    private int ent_left_mid_y = 105;
    private int ent_right_mid_x = 200;
    private int ent_right_mid_y = 205;
    private int ent_ou_x_right=200;
    private int ent_out_y_right=301;
    private int ent_ou_x_left=100;
    private int ent_out_y_left=301;
    private int d=10;
    private int a=5;
    public Boolean rand=true;
    PrintWriter writer = null;
    public ArrayList<Integer> avg_step = new ArrayList<Integer>();
    public ArrayList<Double> rewards= new ArrayList<Double>();
    public Boolean randloop=false;
    public int randloopcounter=0;
    public String actionloop="";
    public test() {
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

    }
    public void initial() {
        this.x = 170;
        this.y= 105;
    }
    public String randomAction() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 4);
        return this.actions.get(randomNum);
    }
    public Boolean updatePosition(String action, Boolean update) {
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
                if (update) {
                    this.x = x_new;
                    this.y=y_new;
                }
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
        else if (x==200) {
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
    public int defineSection() {
        int section=0;
        if (this.x < 100) {
            section = 0;
        }
        if (this.x >= 100 && this.x <= 200) {
            section = 1;
        }
        if (this.x > 200) {
            section = 2;
        }
        return section;
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
    public Double calcReward(int x, int y,Boolean hitBlock) {
        Double r = 0.0;
        if (hitBlock) {
            r=-100.0;
        } else {
            if(this.section == 0){
                r = this.dist(x, y, this.ent_left_mid_x, this.ent_left_mid_y);
            }
            if(this.section == 1){
                r = this.dist(x, y, this.x_goal, this.y_goal);
            }
            if(this.section == 2){
                r = this.dist(x, y, this.ent_right_mid_x, this.ent_right_mid_y);
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
            if (this.x>=150) {
                Double x1 = this.dist(this.x, this.y, this.ent_ou_x_right, this.ent_out_y_right);
                Double x2 = this.dist(this.x, this.y, this.x_goal, this.y_goal);
                Double x3 = this.findAngle(this.x_goal, this.y_goal, this.x, this.y, this.ent_ou_x_right, this.ent_out_y_right);
                s = Math.round(x1/this.d)+"#"+Math.round(x2/this.d)+"#"+Math.round(x3/this.a)+"#"+diff;
            }
            else {
                Double x1 = this.dist(this.x, this.y, this.ent_ou_x_left, this.ent_out_y_left);
                Double x2 = this.dist(this.x, this.y, this.x_goal, this.y_goal);
                Double x3 = this.findAngle(this.x_goal, this.y_goal, this.x, this.y, this.ent_ou_x_left, this.ent_out_y_left);
                s = Math.round(x1/this.d)+"#"+Math.round(x2/this.d)+"#"+Math.round(x3/this.a)+"#"+diff;
            }
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
            if (this.x>=150) {
                Double x1 = this.dist(this.x, this.y, this.ent_ou_x_right, this.ent_out_y_right);
                Double x2 = this.dist(this.x, this.y, this.x_goal, this.y_goal);
                Double x3 = this.findAngle(this.x_goal, this.y_goal, this.x, this.y, this.ent_ou_x_right, this.ent_out_y_right);
                s = Math.round(x1/this.d)+"#"+Math.round(x2/this.d)+"#"+Math.round(x3/this.a);
            }
            else {
                Double x1 = this.dist(this.x, this.y, this.ent_ou_x_left, this.ent_out_y_left);
                Double x2 = this.dist(this.x, this.y, this.x_goal, this.y_goal);
                Double x3 = this.findAngle(this.x_goal, this.y_goal, this.x, this.y, this.ent_ou_x_left, this.ent_out_y_left);
                s = Math.round(x1/this.d)+"#"+Math.round(x2/this.d)+"#"+Math.round(x3/this.a);
            }
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
            Double x1 = dist(nextpos.get(0), nextpos.get(1),ent_left_mid_x,ent_left_mid_y);
            Double x2 = dist(nextpos.get(0), nextpos.get(1),x_goal,y_goal);
            Double x3= findAngle(x_goal,y_goal, nextpos.get(0), nextpos.get(1), ent_left_mid_x,ent_left_mid_y);
            nextstate=Math.round(x1/this.d)+"#"+Math.round(x2/this.d)+"#"+Math.round(x3/this.a);
        }
        if (nextpos.get(0)>=100 && nextpos.get(0)<=200) {
            if (nextpos.get(0)>=150) {
                Double x1 = dist(nextpos.get(0), nextpos.get(1),ent_ou_x_right,ent_out_y_right);
                Double x2 = dist(nextpos.get(0), nextpos.get(1),x_goal,y_goal);
                Double x3= findAngle(x_goal,y_goal, nextpos.get(0), nextpos.get(1), ent_ou_x_right,ent_out_y_right);
                nextstate=Math.round(x1/this.d)+"#"+Math.round(x2/this.d)+"#"+Math.round(x3/this.a);
            }
            else {
                Double x1 = dist(nextpos.get(0), nextpos.get(1),ent_ou_x_left,ent_out_y_left);
                Double x2 = dist(nextpos.get(0), nextpos.get(1),x_goal,y_goal);
                Double x3= findAngle(x_goal,y_goal, nextpos.get(0), nextpos.get(1), ent_ou_x_left,ent_out_y_left);
                nextstate=Math.round(x1/this.d)+"#"+Math.round(x2/this.d)+"#"+Math.round(x3/this.a);
            }
        }
        if (nextpos.get(0)>200) {
            Double x1 = dist(nextpos.get(0), nextpos.get(1),ent_right_mid_x,ent_right_mid_y);
            Double x2 = dist(nextpos.get(0), nextpos.get(1),x_goal,y_goal);
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
        System.out.println(hitBlock);
        if (this.section == 0) {
            x1 = this.findAngle(this.x_goal, this.y_goal, this.x,this.y, this.ent_left_mid_x, this.ent_left_mid_y);
            x2 = this.findAngle(this.x_goal, this.y_goal, nextPos.get(0),nextPos.get(1), this.ent_left_mid_x, this.ent_left_mid_y);
        }
        if (this.section == 1) {
            if (this.x>=150) {
                x1 = this.findAngle(this.x_goal, this.y_goal, this.x,this.y, this.ent_ou_x_right, this.ent_out_y_right);
            }
            if (this.x<150) {
                x1 = this.findAngle(this.x_goal, this.y_goal, this.x,this.y, this.ent_ou_x_left, this.ent_out_y_left);
            }
            if (nextPos.get(0)>=150){
                x2 = this.findAngle(this.x_goal, this.y_goal, nextPos.get(0),nextPos.get(1), this.ent_ou_x_right, this.ent_out_y_right);

            }
            if (nextPos.get(0)<150) {
                x2 = this.findAngle(this.x_goal, this.y_goal, nextPos.get(0),nextPos.get(1), this.ent_ou_x_left, this.ent_out_y_left);

            }
        }
        if (this.section == 2) {
            x1 = this.findAngle(this.x_goal, this.y_goal, this.x,this.y, this.ent_right_mid_x, this.ent_right_mid_y);
            x2 = this.findAngle(this.x_goal, this.y_goal, nextPos.get(0),nextPos.get(1), this.ent_right_mid_x, this.ent_right_mid_y);
        }
        return x1-x2;
    }
    public Integer diffAngleState(String action, Boolean hitBlock) {
        Double x1 =0.0;
        Double x2=0.0;
        ArrayList<Integer> nextPos = this.getNextPos(action, hitBlock);
        if (this.section == 0) {
            x1 = this.findAngle(this.x_goal, this.y_goal, this.x,this.y, this.ent_left_mid_x, this.ent_left_mid_y);
            x2 = this.findAngle(this.x_goal, this.y_goal, nextPos.get(0),nextPos.get(1), this.ent_left_mid_x, this.ent_left_mid_y);
        }
        if (this.section == 1) {
            if (this.x>=150) {
                x1 = this.findAngle(this.x_goal, this.y_goal, this.x,this.y, this.ent_ou_x_right, this.ent_out_y_right);
            }
            if (this.x<150) {
                x1 = this.findAngle(this.x_goal, this.y_goal, this.x,this.y, this.ent_ou_x_left, this.ent_out_y_left);
            }
            if (nextPos.get(0)>=150){
                x2 = this.findAngle(this.x_goal, this.y_goal, nextPos.get(0),nextPos.get(1), this.ent_ou_x_right, this.ent_out_y_right);

            }
            if (nextPos.get(0)<150) {
                x2 = this.findAngle(this.x_goal, this.y_goal, nextPos.get(0),nextPos.get(1), this.ent_ou_x_left, this.ent_out_y_left);

            }
        }
        if (this.section == 2) {
            x1 = this.findAngle(this.x_goal, this.y_goal, this.x,this.y, this.ent_right_mid_x, this.ent_right_mid_y);
            x2 = this.findAngle(this.x_goal, this.y_goal, nextPos.get(0),nextPos.get(1), this.ent_right_mid_x, this.ent_right_mid_y);
        }
        return Math.toIntExact(Math.round((x1 - x2) / this.a));
    }

    public int getDecider(int episode, int trial) {
        if (episode >=0 && episode <=30) {
            decider = 0;
        }
        if (episode >30 && episode <=60) {
            decider = 1;
        }
        if (episode >60 && episode <=90) {
            decider = 2;
        }
        if (episode >90 && episode <=120) {
            decider = 3;
        }
        if (episode >120 && episode <=150) {
            decider = 4;
        }
        if (episode >150 && episode <=200) {
            decider = 5;
        }
        if (episode >200 && episode <=300) {
            decider = 6;
        }
        if (episode >300 && episode <=400) {
            decider = 7;
        }
        if (episode >400 && episode <=500) {
            decider = 8;
        }
        return decider;
    }
    private ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> All_Q_vals(String state) {
        ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> Q = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>>();
        if (this.section == 0) {
            for (String k : table.Q0_vals.keySet()) {
                if (k.contains(state)){
                    ConcurrentHashMap<String,Double> temp = new ConcurrentHashMap<String,Double>();
                    temp = table.Q0_vals.get(k);
                    Q.put(k, temp);
                }
            }
        }
        if (this.section == 1) {
            for (String k : table.Q1_vals.keySet()) {
                if (k.contains(state)){
                    ConcurrentHashMap<String,Double> temp = new ConcurrentHashMap<String,Double>();
                    temp = table.Q1_vals.get(k);
                    Q.put(k, temp);
                }
            }
        }
        if (this.section == 2) {
            for (String k : table.Q2_vals.keySet()) {
                if (k.contains(state)){
                    ConcurrentHashMap<String,Double> temp = new ConcurrentHashMap<String,Double>();
                    temp = table.Q2_vals.get(k);
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
                        this.rand=true;
                        break;
                    case 9:
                        this.rand=false;
                        if (this.section == 0){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
                        }
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
                        this.rand=true;
                        break;
                    case 8:
                    case 9:
                        this.rand=false;
                        if (this.section == 0){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
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
                        this.rand=true;
                        break;
                    case 7:
                    case 8:
                    case 9:
                        this.rand=false;
                        if (this.section == 0){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
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
                        this.rand=true;
                        break;
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        this.rand=false;
                        if (this.section == 0){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
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
                        this.rand=true;
                        break;
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        this.rand=false;
                        if (this.section == 0){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
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
                        this.rand=true;
                        break;
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        this.rand=false;
                        if (this.section == 0){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
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
                        this.rand=true;
                        break;
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        this.rand=false;
                        if (this.section == 0){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
                        }

                        break;
                }
            }
            if (decider == 7) {
                switch (r1.nextInt(10)) {
                    case 0:
                    case 1:
                        action = this.randomAction();
                        this.rand=true;
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        this.rand=false;
                        if (this.section == 0){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
                        }

                        break;
                }
            }
            if (decider == 8) {
                switch (r1.nextInt(10)) {
                    case 0:
                        action = this.randomAction();
                        this.rand=true;
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
                        this.rand=false;
                        if (this.section == 0){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 0);
                        }
                        if (this.section==1){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 1);
                        }
                        if (this.section==2){
                            action = table.getAction(state,this.x, this.y, this.x_goal, this. y_goal, 2);
                        }

                        break;
                }
            }

        }
        else {
            this.rand = true;
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
    public String validate(String action, String action1, int episode) {
        String ax="";
        Boolean hitBlock = updatePosition(action, false);
        ArrayList<Integer> pos = getNextPos(action,hitBlock);
        Double reward = calcReward(pos.get(0), pos.get(1),hitBlock);
        //////////////////
        Boolean hitBlock1 = updatePosition(action1, false);
        ArrayList<Integer> pos1 = getNextPos(action1,hitBlock1);
        Double reward1 = calcReward(pos1.get(0), pos1.get(1),hitBlock1);
        if (reward1>reward && episode>=30) {
            ax=action1;//action
        }
        else {
            ax=action;
        }
        return ax;
    }

    public String randomActionExcept(String action) {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 4);
        String act=this.actions.get(randomNum);
        while(act==action) {
            randomNum = ThreadLocalRandom.current().nextInt(0, 4);
            act=this.actions.get(randomNum);
        }
        return act;
    }
    public void onTick() {
        if (trial<1) {
            if (episode<1) {
                int step_goal = 2000;
                initial();
                for(int i=0;i<1;i++) {
                    getDecider(episode, trial);
                    section=defineSection();
                    String stateHalf = calcStateHalf();
                    String action="left";
                    String action1="";
                    for(int j=0;j<this.actions.size();j++){
                        Double d= this.diffAngleDouble(actions.get(j),false);
                        String s = String.format("%.2f", d);
                        System.out.println(actions.get(j)+"   "+s);
                    }
                    this.x=130;
                    for(int j=0;j<this.actions.size();j++){
                        Double d= this.diffAngleDouble(actions.get(j),false);
                        String s = String.format("%.2f", d);
                        System.out.println(actions.get(j)+"   "+s);
                    }
                    ConcurrentHashMap<String, ConcurrentHashMap<String,Double>>  temp = All_Q_vals(stateHalf);
                    if (rand==true && temp.size()>0) {
                        if (section == 0){
                            action1 = table.getAction(stateHalf,x, y, x_goal, y_goal, 0);
                        }
                        if (section==1){
                            action1 = table.getAction(stateHalf,x, y, x_goal, y_goal, 1);
                        }
                        if (section==2){
                            action1 = table.getAction(stateHalf,x, y, x_goal,  y_goal, 2);
                        }
                        action=validate(action,action1,episode);
                        System.out.println(action1+"  action11111111111");
                    }
                    if (randloop && randloopcounter<3) {
                        if (randloopcounter==0) {
                            action=randomActionExcept(actionloop);
                        }
                        else {
                            action=randomAction();
                        }
                        randloopcounter++;
                    }
                    if (randloop && randloopcounter>=3) {
                        writer.println("out loop");
                        writer.flush();
                        randloop=false;
                    }
                    System.out.println("Agent "+id+" step "+i+" episode "+episode+" trial "+trial+"x: "+x+"y "+y+" act "+action);

                    if (inBound(action)) {
                        Boolean hitBlock = updatePosition(action,false);
                        String state = calcState(action, hitBlock);
                        int section_new=defineSection();
                        Double reward=0.0;
                        if (section==0 && section_new==1) {
                            reward=50.0;
                        }
                        else if (section==2 && section_new==1) {
                            reward=50.0;
                        }
                        else if (section==1 && section_new==0) {
                            reward=-100.0;
                        }
                        else if (section==1 && section_new==2) {
                            reward=-100.0;
                        }
                        else {
                            reward = calcReward(x,y,hitBlock);

                        }
                        //////////////////////////
                        if (rewards.size()<5) {
                            rewards.add(reward);
                        }
                        else {
                            boolean allEqual = rewards.isEmpty() || rewards.stream().allMatch(rewards.get(0)::equals);
                            if (allEqual) {
                                randloop=true;
                                randloopcounter=0;
                                actionloop=action;
                                writer.println("in loop");
                                writer.flush();
                            }
                            rewards=new ArrayList<Double>();
                        }
                        //////////////////////////
                        String nextpos= nextPosState(action, hitBlock);
                        Double diff = diffAngleDouble(action,hitBlock);
                        if (section == 0){
                            table.update(state,nextpos,reward,diff,section,section_new);
                        }
                        if (section==1) {
                            table.update(state,nextpos,reward,diff,section,section_new);
                        }
                        if (section==2) {
                            table.update(state,nextpos,reward,diff,section,section_new);
                        }

                    } else {
                        String state = calcState(action,true);
                        Double reward = -100.0;
                        String nextpos= calcState(action, true);
                        Double diff = 0.0;
                        if (section == 0){
                            table.update(state,nextpos,reward, (diff),section,section);
                        }
                        if (section==1) {
                            table.update(state,nextpos,reward, (diff),section,section);
                        }
                        if (section == 2){
                            table.update(state,nextpos,reward, (diff),section,section);
                        }
                    }

                    if (hit_goal()) {
                        writer.println("Agent "+id+" hit the goal in episode "+episode +" in step "+i);
                        writer.println("Agent "+id+" size table "+table.Q0_vals.size()+" "+table.Q1_vals.size()+" "+table.Q2_vals.size());
                        writer.flush();
                        step_goal = i;
                        initial();
                        break;
                    }
                    updatePosition(action,true);
                    System.out.println("x: "+x+" y:"+y);
                }
                System.out.println(table.Q2_vals);
                episode++;
                avg_step.add(step_goal);
            }
        }
        if (trial==max_trial) {
            writer.close();
        }
    }


}


