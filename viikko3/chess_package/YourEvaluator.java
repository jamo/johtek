
import java.util.Random;

public class YourEvaluator extends Evaluator {

    public YourEvaluator() {
        
    }
    static double[][] blackBoostTable = {{0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.15D, 0.2D, 0.2D, 0.15D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.2D, 0.3D, 0.3D, 0.2D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.15D, 0.1D, 0.1D, 0.15D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}};
    static double[][] whiteBoostTable = {{0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.15D, 0.1D, 0.1D, 0.15D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.2D, 0.3D, 0.3D, 0.2D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.15D, 0.2D, 0.2D, 0.15D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}};

    public double eval(Position paramPosition) {

        Evaluator e = null;
        if (this == Main.oe) {
            e = Main.ye;
        } else if (this == Main.ye) {
            e = Main.oe;
        }
        double time_alku = System.currentTimeMillis();
        double result = e.eval(paramPosition);
        double time_loppu = System.currentTimeMillis();
        double aikaa = time_loppu - time_alku;
        //pelattu turhan pitkään, tyydytään vastustajan arvioon
        if (aikaa > 8*1000) {//sec
            return result+new Random().nextInt(10)-10;
        }
        Evaluator copy = new lol();
        double copyy = copy.eval(paramPosition);
        //System.out.println("vastustaja: "+result);
        //System.out.println("copy:" + copyy);
        return result+copyy*0.5;

    }

    @Override
    public String toString() {
        return "Jamo_my eval";
    }
}

class MyNoobEvaluator extends Evaluator {

    @Override
    public double eval(Position p) {
        return 0d;
    }

    @Override
    public String toString() {
        return "lol";
    }
}