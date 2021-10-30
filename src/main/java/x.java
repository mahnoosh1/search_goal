import java.util.ArrayList;

public class x {
    int move_step=1;
    int x=-2;
    int y=0;
    private int x_goal = 1;
    private  int y_goal = 1;
    public static void main(String args[]) {
        ArrayList<String> x1=new ArrayList<String>();
        ArrayList<String> x2=new ArrayList<String>();
        x1.add("sss");x1.add("sss");
        x2.add("sss");x2.add("ss");
        boolean allEqual = x1.isEmpty() || x1.stream().allMatch(x1.get(0)::equals);
        boolean allEqual2 = x2.isEmpty() || x2.stream().allMatch(x2.get(0)::equals);
System.out.println(allEqual);
        System.out.println(allEqual2);
    }
}
