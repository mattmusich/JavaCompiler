package sample;


import java.util.ArrayList;
import java.util.Hashtable;

public class treeNode {

    public String nodeName;
    public treeNode nodeParent;
    public ArrayList<treeNode> nodeChildren;
    public Hashtable table;

    public treeNode (String nodeName){
        this.nodeName = nodeName;
        this.nodeParent = null;
        this.nodeChildren = new ArrayList<treeNode>();
        this.table = new Hashtable<String,String>();

    }

}
