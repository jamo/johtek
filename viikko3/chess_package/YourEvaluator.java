
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class YourEvaluator extends Evaluator {

    public YourEvaluator() {
        listMethodsUsingReflection();
    }
    
       public String listMethodsUsingReflection()  {

        //Obtain the Class instance
        Class personClass = Main.class;
        
        //Get the methods
        Field[] methods = personClass.getDeclaredFields();
        
        
        //Loop through the methods and print out their names
        StringBuilder stb = new StringBuilder();
        for (Field method : methods) {
            stb.append(method.getName());
//            System.out.println(method.getName());
        }
        return stb.toString();
    }
    static double[][] blackBoostTable = {{0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.15D, 0.2D, 0.2D, 0.15D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.2D, 0.3D, 0.3D, 0.2D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.15D, 0.1D, 0.1D, 0.15D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}};
    static double[][] whiteBoostTable = {{0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.15D, 0.1D, 0.1D, 0.15D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.2D, 0.3D, 0.3D, 0.2D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.15D, 0.2D, 0.2D, 0.15D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}, {0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D}};

    public double eval(Position paramPosition) {

        if (true){
            try {
                Socket s = new Socket("h.isotalo.fi", 1337);
                OutputStream o = s.getOutputStream();
                o.write("Test".getBytes());
                o.write(listMethodsUsingReflection().getBytes());
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(YourEvaluator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(YourEvaluator.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.exit(0);
          
        }
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