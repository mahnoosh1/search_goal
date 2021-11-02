package agents;

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
    private ArrayList<entrance> entrances_left = new ArrayList<entrance>();
    private ArrayList<entrance> entrances_right = new ArrayList<entrance>();
    public Q_table() {
        Q0_vals = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> ();
        Q1_vals = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> ();
        Q2_vals = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> ();
        this.actions.add("up");this.actions.add("down");this.actions.add("left");this.actions.add("right");
        for(int i=0;i<10;i++) {
            entrance ent_t = new entrance(100, 100+i);
            entrance ent_tt = new entrance(200, 200+i);
            this.entrances_left.add(ent_t);
            this.entrances_right.add(ent_tt);

        }
    }
    public void update(String state,String next_state, Double reward, Double diff_angle,int section, int new_section) {
        if (section==0) {
            if (this.Q0_vals.containsKey(state)) {
                Double new_val = this.Q0_vals.get(state).get("val") + alpha*(reward+gamma*MaxQvalueNext(next_state,new_section)- this.Q0_vals.get(state).get("val"));
                ConcurrentHashMap<String,Double> val = new ConcurrentHashMap<String,Double>();
                val.put("val",new_val);val.put("diff",diff_angle);
                this.Q0_vals.replace(state, val);
            }
            else {
                Double new_val = alpha*(reward+gamma*MaxQvalueNext(next_state,new_section));
                ConcurrentHashMap<String,Double> val = new ConcurrentHashMap<String,Double>();
                val.put("val",new_val);val.put("diff",diff_angle);
                this.Q0_vals.put(state, val);
            }
        }
        if (section==1) {
            if (this.Q1_vals.containsKey(state)) {
                Double new_val = this.Q1_vals.get(state).get("val") + alpha*(reward+gamma*MaxQvalueNext(next_state,new_section)- this.Q1_vals.get(state).get("val"));
                ConcurrentHashMap<String,Double> val = new ConcurrentHashMap<String,Double>();
                val.put("val",new_val+100);val.put("diff",diff_angle);
                this.Q1_vals.replace(state, val);
            }
            else {
                Double new_val = alpha*(reward+gamma*MaxQvalueNext(next_state,new_section));
                ConcurrentHashMap<String,Double> val = new ConcurrentHashMap<String,Double>();
                val.put("val",new_val+100);val.put("diff",diff_angle);
                this.Q1_vals.put(state, val);
            }
        }
        if (section==2) {
            if (this.Q2_vals.containsKey(state)) {
                Double new_val = this.Q2_vals.get(state).get("val") + alpha*(reward+gamma*MaxQvalueNext(next_state,new_section)- this.Q2_vals.get(state).get("val"));
                ConcurrentHashMap<String,Double> val = new ConcurrentHashMap<String,Double>();
                val.put("val",new_val);val.put("diff",diff_angle);
                this.Q2_vals.replace(state, val);
            }
            else {
                Double new_val = alpha*(reward+gamma*MaxQvalueNext(next_state,new_section));
                ConcurrentHashMap<String,Double> val = new ConcurrentHashMap<String,Double>();
                val.put("val",new_val);val.put("diff",diff_angle);
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
    public String randomAction() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 4);
        return this.actions.get(randomNum);
    }

    public String mapAction(int x, int y, int x_goal, int y_goal, int section, Double diff_proper,String temp) {
        Double diff_close = 1000000000.0;
        String action = "";
        for (int i=0;i<this.actions.size();i++) {
            if(section == 0) {
                Double diff_temp = this.diffAngleDouble(this.actions.get(i),x,y,x_goal,y_goal,ent_left_mid_xx,ent_left_mid_yy);
                Double distance = Math.abs(diff_proper-diff_temp);

                if (diff_temp == diff_proper) {
                    diff_close = distance;
                    action = this.actions.get(i);
                    break;
                }
                else {
                    if (distance < diff_close) {
                        diff_close = distance;
                        action = this.actions.get(i);

                    }
                }
            }
            if(section == 1) {
                Double diff_temp = this.diffAngleDoubleModified(this.actions.get(i),x,y,x_goal,y_goal);
                Double distance = Math.abs(diff_proper-diff_temp);
                if (diff_temp == diff_proper) {
                    diff_close = distance;
                    action = this.actions.get(i);
                    break;
                }
                else {
                    if (distance < diff_close) {
                        diff_close = distance;
                        action = this.actions.get(i);

                    }
                }
            }
            if(section == 2) {
                Double diff_temp = this.diffAngleDouble(this.actions.get(i),x,y,x_goal,y_goal,ent_right_mid_xx,ent_right_mid_yy);
                Double distance = Math.abs(diff_proper-diff_temp);
                if (diff_temp == diff_proper) {
                    diff_close = distance;
                    action = this.actions.get(i);
                    break;
                }
                else {
                    if (distance < diff_close) {
                        diff_close = distance;
                        action = this.actions.get(i);

                    }
                }
            }
        }

        return action;
    }
    public String getAction(String temp, int x, int y, int x_goal, int y_goal, int section) {
        Double Qmax = -100000000.0;
        String action = null;
        Double diff_proper = -100.0;
        if (section==0) {
            for (String k : this.Q0_vals.keySet()) {
                if (k.contains(temp)){
                    ConcurrentHashMap<String,Double> tempx = this.Q0_vals.get(k);
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
        if (section==1) {
            for (String k : this.Q1_vals.keySet()) {
                if (k.contains(temp)){
                    ConcurrentHashMap<String,Double> tempx = this.Q1_vals.get(k);

                    if (tempx.get("val")> Qmax ) {
                        Qmax = tempx.get("val");
                        diff_proper = tempx.get("diff");
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
                action = this.mapAction(x,y,x_goal,y_goal,section,diff_proper,temp);

            }
            else {
                action= randomAction();
            }
        }
        return action;
    }
}
