package agents;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class Q_table {
    ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> Q_vals = new ConcurrentHashMap<String, ConcurrentHashMap<String,Double>> ();
    private double alpha = 0.3;
    private double gamma = 0.1;
    private ArrayList<String> actions = new ArrayList<String>();
    private int ent_left_mid_x = 100;
    private int ent_left_mid_y = 105;
    private int ent_right_mid_x = 200;
    private int ent_right_mid_y = 205;
    private int a = 20;
    public int move_step=5;
    private ArrayList<entrance> entrances_left = new ArrayList<entrance>();
    private ArrayList<entrance> entrances_right = new ArrayList<entrance>();
    public Q_table() {
        this.actions.add("up");this.actions.add("down");this.actions.add("left");this.actions.add("right");
        for(int i=0;i<10;i++) {
            entrance ent_t = new entrance(100, 100+i);
            entrance ent_tt = new entrance(200, 200+i);
            this.entrances_left.add(ent_t);
            this.entrances_right.add(ent_tt);

        }
    }
    public void update(String state,String next_state, Double reward, Double diff_angle) {
        if (this.Q_vals.containsKey(state)) {
            Double new_val = this.Q_vals.get(state).get("val") + alpha*(reward+gamma*MaxQvalueNext(next_state)- this.Q_vals.get(state).get("val"));
            ConcurrentHashMap<String,Double> val = new ConcurrentHashMap<String,Double>();
            val.put("val",new_val);val.put("diff",diff_angle);
            this.Q_vals.replace(state, val);
        }
        else {
            Double new_val = alpha*(reward+gamma*MaxQvalueNext(next_state));
            ConcurrentHashMap<String,Double> val = new ConcurrentHashMap<String,Double>();
            val.put("val",new_val);val.put("diff",diff_angle);
            this.Q_vals.put(state, val);
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

    public Double MaxQvalueNext(String next_state) {
        Double Qmax = -10000.0;
        for (String k : this.Q_vals.keySet()) {
            if (k.contains(next_state)){
                ConcurrentHashMap<String,Double> temp = this.Q_vals.get(k);
                if (temp.get("val") > Qmax) {
                    Qmax = temp.get("val") ;
                }
            }
            else {
                Qmax = 0.0;
            }
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
        Double diff = 0.0;
        Double x1 =0.0;
        Double x2=0.0;
        Boolean hit= this.updatePosition(x,y,action,this.move_step);
        ArrayList<Integer> nextPos = this.getNextPos(x,y,action,hit);
        x1 = this.findAngle(x_goal, y_goal, x,y,ent_middle_x, ent_middle_y);
        x2 = this.findAngle(x_goal, y_goal, nextPos.get(0),nextPos.get(1), ent_middle_x, ent_middle_y);
        return x1-x2;
    }

    public String mapAction(int x, int y, int x_goal, int y_goal, int section, Double diff_proper) {
        Double diff_close = 1000000000.0;
        String action = "";
         for (int i=0;i<this.actions.size();i++) {
             if(section == 0 || section ==1) {
                 Double diff_temp = this.diffAngleDouble(this.actions.get(i),x,y,x_goal,y_goal,ent_left_mid_x,ent_left_mid_y);
                 Double distance = Math.abs(diff_proper-diff_temp);
                 if (distance < diff_close) {
                     diff_close = distance;
                     action = this.actions.get(i);
                 }
             }
             if(section == 2) {
                 Double diff_temp = this.diffAngleDouble(this.actions.get(i),x,y,x_goal,y_goal,ent_right_mid_x,ent_right_mid_y);
                 Double distance = Math.abs(diff_proper-diff_temp);
                 if (distance< diff_close) {
                     diff_close = distance;
                     action = this.actions.get(i);
                 }
             }
        }
         return action;
    }
    public String getAction(String temp, int x, int y, int x_goal, int y_goal, int section) {
        Double Qmax = -100000000.0;
        String action = null;
        Double diff_proper = 0.0;
        for (String k : this.Q_vals.keySet()) {
            if (k.contains(temp)){
                ConcurrentHashMap<String,Double> tempx = this.Q_vals.get(k);
                if (tempx.get("val")> Qmax) {
                    Qmax = tempx.get("val");
                    diff_proper = tempx.get("diff");
                }
            }
        }
        action = this.mapAction(x,y,x_goal,y_goal,section,diff_proper);
        return action;
    }
}
