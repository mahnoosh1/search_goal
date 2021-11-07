
import java.util.ArrayList;
import java.util.Arrays;
public class x {
    int x=-2;
    int y=0;
    private int x_goal = 1;
    private  int y_goal = 1;
    public static int move_step=5;
    private static int ent_left_mid_xx = 100;
    private static int ent_left_mid_yy =105;
    /// <summary>
/// Calculates angle in radians between two points and x-axis.
/// </summary>

    public static Boolean updatePosition(int x, int y,String action, int move_step) {
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


        return false;
    }
    public static double theta(int x0,int y0,int x1,int y1) {
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
    public static ArrayList<Integer> getNextPos(int x, int y,String action, Boolean hitBlock) {
        ArrayList<Integer> pos = new ArrayList<Integer>();
        int x_new = 0;
        int y_new = 0;
        if (hitBlock) {
            x_new= x;
            y_new=y;
        }
        else {
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
        }
        pos.add(0, x_new);
        pos.add(1, y_new);
        return  pos;
    }
    public static Double diffAngleDoubleModified(String action, int x, int y, int x_goal, int y_goal) {
        Double x1 =0.0;
        Double x2=0.0;
        Boolean hit= updatePosition(x,y,action,5);
        ArrayList<Integer> nextPos = getNextPos(x,y,action,hit);
        x1=theta(x_goal,y_goal,x,y);
        x2=theta(x_goal,y_goal,nextPos.get(0),nextPos.get(1));
        return x1-x2;
    }
    public static Double diffAngleDoubleModifiedx(String action, int x, int y, int x_goal, int y_goal) {
        Double x1 =0.0;
        Double x2=0.0;
        Boolean hit= updatePosition(x,y,action,move_step);
        ArrayList<Integer> nextPos = getNextPos(x,y,action,hit);
        x1=theta(ent_left_mid_xx,ent_left_mid_yy,x,y);
        x2=theta(ent_left_mid_xx,ent_left_mid_yy,nextPos.get(0),nextPos.get(1));
        return x1-x2;
    }

    public static void main(String args[]) {

//        String a0="276, 304, 184, 164, 269, 194, 114, 188, 182, 176, 230, 154, 170, 186, 246, 156, 192, 252, 142, 170, 206, 136, 212, 140, 170, 256, 202, 148, 204, 246, 126, 206, 142, 138, 150, 134, 254, 134, 232, 174, 152, 180, 98, 148, 146, 208, 146, 174, 276, 156, 140, 236, 106, 168, 144, 128, 194, 158, 150, 100, 134, 144, 246, 150, 182, 124, 156, 106, 130, 128, 128, 222, 180, 156, 132, 162, 172, 148, 224, 112, 174, 128, 120, 180, 120, 116, 152, 168, 132, 116, 132, 114, 176, 160, 144, 126, 102, 148, 174, 126, 162, 134, 98, 86, 94, 142, 102, 128, 154, 126, 106, 118, 90, 146, 134, 130, 122, 126, 110, 184, 144, 106, 130, 106, 150, 94, 116, 96, 152, 104, 140, 112, 94, 118, 116, 166, 78, 162, 148, 138, 136, 152, 116, 162, 122, 100, 156, 146, 102, 88, 102, 126, 92, 154, 140, 146, 94, 90, 154, 206, 90, 148, 106, 94, 163, 106, 122, 110, 298, 164, 104, 110, 168, 82, 163, 108, 88, 106, 102, 104, 98, 102, 80, 82, 116, 114, 90, 136, 106, 98, 124, 98, 108, 132, 116, 177, 122, 118, 84, 114, 106, 98, 258, 146, 92, 122, 176, 122, 88, 300, 170, 110, 112, 150, 142, 320, 120, 96, 90, 100, 86, 128, 172, 168, 114, 108, 186, 110, 92, 126, 100, 80, 98, 114, 238, 108, 116, 94, 116, 74, 126, 120, 244, 146, 212, 106, 84, 126, 82, 88, 116, 90, 124, 98, 156, 78, 108, 88, 84, 98, 94, 90, 164, 178, 146, 134, 132, 134, 82, 100, 90, 88, 92, 130, 112, 98, 82, 88, 90, 88, 88, 80, 151, 134, 108, 124, 116, 88, 100, 110, 96, 90, 114, 144, 108, 96, 110, 84, 98, 200, 116, 228, 124, 144, 124, 108, 150, 128, 178, 112, 94, 104, 112, 88, 138, 88, 74, 86, 114, 178, 124, 92, 102, 100, 126, 128, 155, 154, 88, 102, 149, 78, 104, 108, 84, 107, 108, 116, 76, 80, 134, 110, 106, 86, 112, 110, 98, 114, 110, 108, 96, 124, 98, 114, 98, 120, 182, 86, 100, 128, 124, 158, 118, 82, 104, 110, 100, 116, 104, 102, 112, 114, 170, 78, 102, 80, 104, 154, 146, 114, 138, 102, 92, 117, 86, 88, 98, 119, 90, 82, 144, 122, 86, 98, 92, 104, 112, 116, 110, 96, 86, 90, 142, 124, 127, 110, 160, 110, 158, 104, 121, 114, 86, 94, 118, 106, 159, 108, 186, 172, 116, 100, 108, 134, 120, 112, 98, 92, 144, 158, 102, 92, 152, 118, 112, 124, 122, 154, 148, 214, 182, 129, 155, 126, 92, 122, 86, 110, 82, 116, 98, 120, 134, 107, 112, 145, 114, 157, 128, 78, 102, 100, 155, 138, 112, 128, 100, 112, 112, 106, 98, 88, 119, 98, 148, 100, 108, 88, 108, 112, 170, 136, 104, 84, 152, 108, 134, 158, 112, 114, 100, 146, 124, 86, 160, 140, 80, 108, 148, 90";
//        String x0[]=a0.split(",");
//
//
//        for(int i=0;i<500;i++) {
//}
        int x_goal=150;
        int y_goal=295;
        int x=50;
        int y=145;
        Double x5=theta(150,295,140,150);
        Double diff_temp1 = diffAngleDoubleModifiedx("right",x,y,x_goal,y_goal);
        Double diff_temp2 = diffAngleDoubleModifiedx("down",x,y,x_goal,y_goal);
        Double diff_temp3 = diffAngleDoubleModifiedx("left",x,y,x_goal,y_goal);
        Double diff_temp4 = diffAngleDoubleModifiedx("up",x,y,x_goal,y_goal);
        System.out.println(diff_temp1+"  "+diff_temp2+"  "+diff_temp3+"  "+diff_temp4);
        int c=0;
        Double v= (double)c;
        System.out.println(v);
    }
}
