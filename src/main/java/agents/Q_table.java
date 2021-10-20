package agents;

import java.util.ArrayList;
import java.util.HashMap;


public class Q_table {
    HashMap<String, ArrayList<Double>> Q_vals = new HashMap<String, ArrayList<Double>>();
    private double alpha = 0.3;
    private double gamma = 0.1;
    public Q_table() {

    }
    public void update(String state,String next_state, Double reward, Double diff_angle) {
        if (this.Q_vals.containsKey(state)) {
            Double new_val = this.Q_vals.get(state).get(0) + alpha*(reward+gamma*MaxQvalueNext(next_state)- this.Q_vals.get(state).get(0));
            ArrayList<Double> val = new ArrayList<Double>();
            val.add(new_val);val.add(diff_angle);
            this.Q_vals.replace(state, val);
        }
    }

    public Double MaxQvalueNext(String next_state) {
        Double Qmax = -10000.0;
        for (String k : this.Q_vals.keySet()) {
            if (k.contains(next_state)){
                ArrayList<Double> temp = this.Q_vals.get(k);
                if (temp.get(0) > Qmax) {
                    Qmax = temp.get(0);
                }
            }
            else {
                Qmax = 0.0;
            }
        }
        return  Qmax;
    }
    public String getAction(String temp) {
        Double Qmax = -10000.0;
        String action = null;
        for (String k : this.Q_vals.keySet()) {
            if (k.contains(temp)){
                ArrayList<Double> tempx = this.Q_vals.get(k);
                if (tempx.get(0)> Qmax) {
                    Qmax = tempx.get(0);
                    String[] s = k.split("#");
                    action = s[3];
                }
            }
        }
        return action;
    }
}
