import java.util.concurrent.ConcurrentHashMap;

public class x {
    public static void main(String args[]) {
        ConcurrentHashMap<String, Double> Q_vals = new ConcurrentHashMap<String, Double> ();
        Q_vals.put("kkk",1.0);
        Q_vals.replace("kkk", 2.0);
        System.out.println(Q_vals.get("kkk"));
    }
}
