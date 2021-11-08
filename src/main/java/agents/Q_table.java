package agents;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;


public class Q_table {
    ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> Q0_vals = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> ();
    ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> Q1_vals = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> ();
    ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> Q2_vals = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> ();
    private double alpha = 0.1;
    private double gamma = 0.8;
    private ArrayList<String> actions = new ArrayList<String>();
    private int ent_left_mid_xx = 100;
    private int ent_left_mid_yy = 105;
    private int ent_right_mid_xx = 200;
    private int ent_right_mid_yy = 205;
//    private int ent_ou_x_right=200;
//    private int ent_out_y_right=301;
//    private int ent_ou_x_left=100;
//    private int ent_out_y_left=301;
    public int move_step=5;
    PrintWriter writerposition=null;
    private ArrayList<entrance> entrances_left = new ArrayList<entrance>();
    private ArrayList<entrance> entrances_right = new ArrayList<entrance>();
    private int x_goal = 150;
    private  int y_goal = 270;
    public Q_table() {
        Q0_vals = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> ();
        Q1_vals = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> ();
        Q2_vals = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> ();
        try {
            writerposition = new PrintWriter("Agent middle"+".txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        this.actions.add("up");this.actions.add("down");this.actions.add("left");this.actions.add("right");
        for(int i=0;i<10;i++) {
            entrance ent_t = new entrance(100, 100+i);
            entrance ent_tt = new entrance(200, 200+i);
            this.entrances_left.add(ent_t);
            this.entrances_right.add(ent_tt);

        }
    }
    public void update(String state,String next_state, Double reward, Double diff_angle,int section, int new_section,double sec_table) {

        String string_diff = String.format("%.2f", diff_angle);
        Double diff_convert= Double.parseDouble(string_diff);
        if (section==0) {
            if (this.Q0_vals.containsKey(state)) {
                Double new_val = this.Q0_vals.get(state).get("val") + alpha*(reward+gamma*MaxQvalueNext(next_state,new_section)- this.Q0_vals.get(state).get("val"));
                ConcurrentHashMap<String,Double> val = new ConcurrentHashMap<String,Double>();
                val.put("val",new_val);val.put("diff",diff_convert);val.put("sect",sec_table);
                this.Q0_vals.replace(state, val);
            }
            else {
                Double new_val = alpha*(reward+gamma*MaxQvalueNext(next_state,new_section));
                ConcurrentHashMap<String,Double> val = new ConcurrentHashMap<String,Double>();
                val.put("val",new_val);val.put("diff",diff_convert);val.put("sect",  sec_table);
                this.Q0_vals.put(state, val);
            }
        }
        if (section==1) {
            if (this.Q1_vals.containsKey(state)) {
                Double new_val = this.Q1_vals.get(state).get("val") + alpha*(reward+gamma*MaxQvalueNext(next_state,new_section)- this.Q1_vals.get(state).get("val"));
                ConcurrentHashMap<String,Double> val = new ConcurrentHashMap<String,Double>();
                val.put("val",new_val+100);val.put("diff",diff_convert);val.put("sect",  sec_table);
                this.Q1_vals.replace(state, val);
            }
            else {
                Double new_val = alpha*(reward+gamma*MaxQvalueNext(next_state,new_section));
                ConcurrentHashMap<String,Double> val = new ConcurrentHashMap<String,Double>();
                val.put("val",new_val+100);val.put("diff",diff_convert);val.put("sect",  sec_table);
                this.Q1_vals.put(state, val);
            }
        }
        if (section==2) {
            if (this.Q2_vals.containsKey(state)) {
                Double new_val = this.Q2_vals.get(state).get("val") + alpha*(reward+gamma*MaxQvalueNext(next_state,new_section)- this.Q2_vals.get(state).get("val"));
                ConcurrentHashMap<String,Double> val = new ConcurrentHashMap<String,Double>();
                val.put("val",new_val);val.put("diff",diff_convert);val.put("sect", (double) sec_table);
                this.Q2_vals.replace(state, val);
            }
            else {
                Double new_val = alpha*(reward+gamma*MaxQvalueNext(next_state,new_section));
                ConcurrentHashMap<String,Double> val = new ConcurrentHashMap<String,Double>();
                val.put("val",new_val);val.put("diff",diff_convert);val.put("sect", (double) sec_table);
                this.Q2_vals.put(state, val);
            }
        }
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
    public Double MaxQvalueNext(String next_state, int new_section) {
        Double Qmax = -10000.0;
        Boolean find=false;
        if (new_section==0) {
            for (String k : this.Q0_vals.keySet()) {
                if (k.contains(next_state)){
                    find=true;
                    ConcurrentHashMap<String,Double> temp = this.Q0_vals.get(k);
                    if (temp.get("val") > Qmax) {
                        Qmax = temp.get("val") ;
                    }
                }
            }
        }
        if (new_section==1) {
            for (String k : this.Q1_vals.keySet()) {
                if (k.contains(next_state)){
                    find=true;
                    ConcurrentHashMap<String,Double> temp = this.Q1_vals.get(k);
                    if (temp.get("val") > Qmax) {
                        Qmax = temp.get("val") ;
                    }
                }
            }
        }
        if (new_section==2) {
            for (String k : this.Q2_vals.keySet()) {
                if (k.contains(next_state)){
                    find=true;
                    ConcurrentHashMap<String,Double> temp = this.Q2_vals.get(k);
                    if (temp.get("val") > Qmax) {
                        Qmax = temp.get("val") ;
                    }
                }
            }
        }
        if(find==false) {
            Qmax=0.0;
        }
        return  Qmax;
    }
    public Boolean updatePosition(int x, int y,String action, int move_step) {
        int x_new = 0;
        int y_new = 0;
        Boolean hitBlock = false;
        if (action == "up"){
            y_new = y+move_step;
            x_new = x;
        }
        if (action == "down"){
            y_new = y-move_step;
            x_new = x;
        }
        if (action == "left"){
            x_new = x-move_step;
            y_new = y;
        }
        if (action == "right"){
            x_new = x+move_step;
            y_new = y;
        }
        if ((x_new >= 0 && x_new<300) && (y_new>=0 && y_new<300)){
            hitBlock = !isPath(x_new,y_new);
        }
        else {
            hitBlock=true;
        }

        return hitBlock;
    }
    public double theta(int x0,int y0,int x1,int y1) {
        double Rad2Deg = 180.0 / Math.PI;
        double d=Math.atan2(y1-y0, x1 - x0) * Rad2Deg;
        if (d<0) {
            d=360+d;
        }
        return d;
    }

    public double findAngle(double p0x,double p0y,double p1x,double p1y,double p2x,double p2y) {
        double a = Math.pow(p1x-p0x,2) + Math.pow(p1y-p0y,2),
                b = Math.pow(p1x-p2x,2) + Math.pow(p1y-p2y,2),
                c = Math.pow(p2x-p0x,2) + Math.pow(p2y - p0y, 2);
        return 57.2958*Math.acos( (a+b-c) / Math.sqrt(4*a*b) );
    }
    public ArrayList<Integer> getNextPos(int x, int y,String action, Boolean hitBlock) {
        ArrayList<Integer> pos = new ArrayList<Integer>();
        int x_new = 0;
        int y_new = 0;
        if (hitBlock) {
            x_new= x;
            y_new=y;
        }
        else {
            if (action == "up"){
                y_new = y+this.move_step;
                x_new = x;
            }
            if (action == "down"){
                y_new = y-this.move_step;
                x_new = x;
            }
            if (action == "left"){
                x_new = x-this.move_step;
                y_new = y;
            }
            if (action == "right"){
                x_new = x+this.move_step;
                y_new = y;
            }
        }
        pos.add(0, x_new);
        pos.add(1, y_new);
        return  pos;
    }
    public Double diffAngleDouble(String action, int x, int y, int x_goal, int y_goal,int ent_middle_x,int ent_middle_y) {
        Double x1 =0.0;
        Double x2=0.0;
        Boolean hit= this.updatePosition(x,y,action,this.move_step);
        ArrayList<Integer> nextPos = this.getNextPos(x,y,action,hit);
        x1 = this.findAngle(x_goal, y_goal, x,y,ent_middle_x, ent_middle_y);
        x2 = this.findAngle(x_goal, y_goal, nextPos.get(0),nextPos.get(1), ent_middle_x, ent_middle_y);
        return x1-x2;
    }
    public Double diffAngleDoubleModified(String action, int x, int y, int x_goal, int y_goal) {
        Double x1 =0.0;
        Double x2=0.0;
        Boolean hit= this.updatePosition(x,y,action,this.move_step);
        ArrayList<Integer> nextPos = this.getNextPos(x,y,action,hit);
        x1=theta(x_goal,y_goal,x,y);
        x2=theta(x_goal,y_goal,nextPos.get(0),nextPos.get(1));
        return x1-x2;
    }
    public Double diffAngleDoubleModifiedx(String action, int x, int y, int x_goal, int y_goal) {
        Double x1 =0.0;
        Double x2=0.0;
        Boolean hit= this.updatePosition(x,y,action,this.move_step);
        ArrayList<Integer> nextPos = this.getNextPos(x,y,action,hit);
        x1=theta(ent_left_mid_xx,ent_left_mid_yy,x,y);
        x2=theta(ent_left_mid_xx,ent_left_mid_yy,nextPos.get(0),nextPos.get(1));
        return x1-x2;
    }
    public String randomAction() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 4);
        return this.actions.get(randomNum);
    }
    public Double calcReward(int x, int y, int x_prev, int y_prev, Boolean hitBlock,int section) {
        Double r = 0.0;
        if (hitBlock) {
            r = -1000.0;
        } else {
            if (section == 0) {
                if (this.dist(x, y, ent_left_mid_xx, this.ent_left_mid_yy) < this.dist(x_prev, y_prev, ent_left_mid_xx, this.ent_left_mid_yy)) {
                    r = 500.0;
                } else {
                    r = -500.0;
                }
            }
            if (section == 1) {
                if (this.dist(x, y,this.x_goal , this.y_goal) < this.dist(x_prev, y_prev, this.x_goal, this.y_goal)) {
                    r = 500.0;
                } else {
                    r = -500.0;
                }
            }
            if (section == 2) {
                if (this.dist(x, y, this.ent_right_mid_xx, this.ent_right_mid_yy) < this.dist(x_prev, y_prev, this.ent_right_mid_xx, this.ent_right_mid_yy)) {
                    r = 500.0;
                } else {
                    r = -500.0;
                }
            }

        }
        return r;
    }

    public String mapAction(int x, int y, int x_goal, int y_goal, int section, Double diff_proper,String temp,int id) {
        Double diff_close = 1000000000.0;
        String action = "";
        ArrayList<String> actionList=new ArrayList<>();
        if(section==1) {
            if(id==0 && left_agent.episode>100) {
                for(String k: Q1_vals.keySet()){
                    if (k.contains(temp)){
                        writerposition.println(Q1_vals.get(k));
                    }
                }
                writerposition.println("Agent "+id+" x: "+x+" y:"+y);
                writerposition.flush();
            }
            if(id==1 && middle_agent.episode>100) {
                for(String k: Q1_vals.keySet()){
                    if (k.contains(temp)){
                        writerposition.println(Q1_vals.get(k));
                    }
                }
                writerposition.println("Agent "+id+" x: "+x+" y:"+y);
                writerposition.flush();
            }
            if(id==2 && right_agent.episode>100) {
                for(String k: Q1_vals.keySet()){
                    if (k.contains(temp)){
                        writerposition.println(Q1_vals.get(k));
                    }
                }
                writerposition.println("Agent "+id+" x: "+x+" y:"+y);
                writerposition.flush();
            }
        }

        for (int i=0;i<this.actions.size();i++) {
            if(section == 0) {
                Double diff_temp = this.diffAngleDoubleModifiedx(this.actions.get(i),x,y,x_goal,y_goal);
                Double distance = Math.abs(diff_proper-diff_temp);
                if (diff_temp == diff_proper) {
                    diff_close = distance;
                    actionList.add(this.actions.get(i));
                }
                else {
                    if (distance <= diff_close) {
                        diff_close = distance;
                        action=(this.actions.get(i));
                    }
                }
            }
            if(section == 1) {
                Double diff_temp = this.diffAngleDoubleModified(this.actions.get(i),x,y,x_goal,y_goal);
                Double distance = Math.abs(diff_proper-diff_temp);
                if(id==0 && left_agent.episode>100){
                    writerposition.println("Agent "+id+" "+actions.get(i)+"  "+diff_temp+"  "+diff_proper);
                    writerposition.println("//////////////////////////");
                    writerposition.flush();
                }
                if(id==1 && middle_agent.episode>100){
                    writerposition.println("Agent "+id+" "+actions.get(i)+"  "+diff_temp+"  "+diff_proper);
                    writerposition.println("//////////////////////////");
                    writerposition.flush();
                }
                if(id==2 && right_agent.episode>100){
                    writerposition.println("Agent "+id+" "+actions.get(i)+"  "+diff_temp+"  "+diff_proper);
                    writerposition.println("//////////////////////////");
                    writerposition.flush();
                }
                if (diff_temp == diff_proper) {
                    diff_close = distance;
                    actionList.add(this.actions.get(i));

                }
                else {
                    if (distance < diff_close) {
                        diff_close = distance;
                        action=(this.actions.get(i));

                    }
                }
            }
            if(section == 2) {
                Double diff_temp = this.diffAngleDouble(this.actions.get(i),x,y,x_goal,y_goal,ent_right_mid_xx,ent_right_mid_yy);
                Double distance = Math.abs(diff_proper-diff_temp);
                if (diff_temp == diff_proper) {
                    diff_close = distance;
                    action=(this.actions.get(i));
                    break;
                }
                else {
                    if (distance < diff_close) {
                        diff_close = distance;
                        action=(this.actions.get(i));
                    }
                }
            }
        }
        if (actionList.size()==1) {
            action=actionList.get(0);
        }
        if(actionList.size()==2) {
            if(section==0) {
                Boolean hit1= this.updatePosition(x,y,actionList.get(0),this.move_step);
                ArrayList<Integer> nextPos1 = this.getNextPos(x,y,actionList.get(0),hit1);
                Double reward1=this.calcReward(nextPos1.get(0),nextPos1.get(1),x,y,hit1,0);
                Boolean hit2= this.updatePosition(x,y,actionList.get(1),this.move_step);
                ArrayList<Integer> nextPos2 = this.getNextPos(x,y,actionList.get(1),hit2);
                Double reward2=this.calcReward(nextPos2.get(0),nextPos2.get(1),x,y,hit2,0);
                if (reward1<=reward2) {
                    action=actionList.get(0);
                }
                else{
                    action=actionList.get(1);
                }
            }
            if(section==1) {
                Boolean hit1= this.updatePosition(x,y,actionList.get(0),this.move_step);
                ArrayList<Integer> nextPos1 = this.getNextPos(x,y,actionList.get(0),hit1);
                Double reward1=this.calcReward(nextPos1.get(0),nextPos1.get(1),x,y,hit1,1);
                Boolean hit2= this.updatePosition(x,y,actionList.get(1),this.move_step);
                ArrayList<Integer> nextPos2 = this.getNextPos(x,y,actionList.get(1),hit2);
                Double reward2=this.calcReward(nextPos2.get(0),nextPos2.get(1),x,y,hit2,1);
                if (reward1<=reward2) {
                    action=actionList.get(0);
                }
                else{
                    action=actionList.get(1);
                }
            }

        }
        if(section==1) {
            if(id==0 && left_agent.episode>100) {
                writerposition.println("Agent "+id+" Action map "+action);
                writerposition.flush();
            }
            if(id==1 && middle_agent.episode>100) {
                writerposition.println("Agent "+id+" Action map "+action);
                writerposition.flush();
            }
            if(id==2 && right_agent.episode>100) {
                writerposition.println("Agent "+id+" Action map "+action);
                writerposition.flush();
            }
        }
        return action;
    }
    public Double dist(int p0x, int p0y, int p1x, int p1y) {
        return Math.sqrt((p0x-p1x)*(p0x-p1x) + (p0y-p1y)*(p0y-p1y));
    }
    public String getAction(String temp, int x, int y, int x_goal, int y_goal, int section,int id) {
        Double Qmax = 0.0;
        String action = null;
        Double diff_proper = -100.0;
        double sect_proper=2.0;
        if (section==0) {
            for (String k : this.Q0_vals.keySet()) {
                if (k.contains(temp)){
                    ConcurrentHashMap<String,Double> tempx = this.Q0_vals.get(k);

                    if (tempx.get("val")> Qmax) {
                        Qmax = tempx.get("val");
                        diff_proper = tempx.get("diff");
                        sect_proper=tempx.get("sect");
                        if (diff_proper.isNaN()) {
                            break;
                        }
                    }
                }
            }
        }
        if (section==1) {
            for (String k : this.Q1_vals.keySet()) {
                if (k.contains(temp)){
                    ConcurrentHashMap<String,Double> tempx = this.Q1_vals.get(k);
                    if (tempx.get("val")> Qmax ) {
                        Qmax = tempx.get("val");
                        diff_proper = tempx.get("diff");
                        sect_proper=tempx.get("sect");
                        if (diff_proper.isNaN()) {
                            break;
                        }
                    }
                }
            }
        }
        if (section==2) {
            for (String k : this.Q2_vals.keySet()) {
                if (k.contains(temp)){
                    ConcurrentHashMap<String,Double> tempx = this.Q2_vals.get(k);
                    if (tempx.get("val")> Qmax) {
                        Qmax = tempx.get("val");
                        diff_proper = tempx.get("diff");
                        if (diff_proper.isNaN()) {
                            break;
                        }
                    }
                }
            }
        }
        if (diff_proper.isNaN()) {
            if (section==2) {
                action="left";
            }
            if (section==0) {
                action="right";
            }
        }
        else {
            if (diff_proper!=-100.0 && diff_proper!=0.0) {
                if (section==0) {

                    if (sect_proper==0.0 && y<=105) {
                        diff_proper=-1*diff_proper;

                    }
                    if (sect_proper==1.0 && y>105) {
                        diff_proper=-1*diff_proper;

                    }
                }
                if (section==1) {
                    if (sect_proper==0.0 && x<=150) {
                        diff_proper=-1*diff_proper;

                    }
                    if (sect_proper==1.0 && x>150) {
                        diff_proper=-1*diff_proper;

                    }
                }
                action = this.mapAction(x,y,x_goal,y_goal,section,diff_proper,temp,id);

            }
            else {
                action= randomAction();
            }
        }
        return action;
    }
}
