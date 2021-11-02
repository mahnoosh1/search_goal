import agents.test;

public class x {
    int move_step=1;
    int x=-2;
    int y=0;
    private int x_goal = 1;
    private  int y_goal = 1;


    /// <summary>
/// Calculates angle in radians between two points and x-axis.
/// </summary>
    private static double Angle(int x0,int y0,int x1,int y1)
    {
        double Rad2Deg = 180.0 / Math.PI;
        double d=Math.atan2(y1-y0, x1 - x0) * Rad2Deg;
        if (d<0) {
            d=360+d;
        }
        return d;
    }

    public static void main(String args[]) {
      System.out.println(Angle(0,0,1,-1));
    }
}
