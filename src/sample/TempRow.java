package sample;

/**
 * Created by matt on 5/10/2015.
 */
public class TempRow {

    int rowLoc; //for index use

    String name = ""; // ex T0XX
    int scope;
    String var = "";
    int address = 0; //Bit +0 / a bit is +1 based off the last / a string is -(amt of char)
    String type = "";


    public TempRow(String name,int scope, String var,int address,String type){
        this.name = name;
        this.scope = scope;
        this.var = var;
        this.address = address;
        this.type = type;
    }

    public String dumpText(){
        return "Name: "+name + " Scope:" + scope + " Var:" + var + " Address:" + address + " Type:" + type;
    }

}
