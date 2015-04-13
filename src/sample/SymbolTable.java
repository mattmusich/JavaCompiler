package sample;


import java.util.Hashtable;
import java.util.LinkedList;

public class SymbolTable {
    public int sLevel;
    public LinkedList<Hashtable> scope;

    //Hashtable is (String(ID), String(TYPE))
    public SymbolTable(int sLevel, LinkedList<Hashtable> scope){
        this.sLevel = sLevel;
        this.scope = scope;
    }

    public String toString(){
        String result = "";
        for (int i = 0; i <= sLevel; i++)
        result += scope.get(i).toString();
        return result;
    }
}
