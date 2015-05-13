package sample;

/**
 * Created by matt on 5/10/2015.
 */
public class JumpRow {

    int rowLoc; //for index use

    int scope; // Not sure if i need this, keeping it for now to be sure
    String name = ""; // ex T0XX
    int distance = 0; // bit distance of the hop

    public JumpRow(String name, int distance){
        this.name = name;
        this.distance = distance;
    }

    public void dumpText(){
        System.out.println("Name: "+name + " Distance:" + distance);
    }

}
