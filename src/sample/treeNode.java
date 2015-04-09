package sample;


import java.util.ArrayList;

public class treeNode {

    public String nodeName;
    public treeNode nodeParent;
    public ArrayList<treeNode> nodeChildren;

    public treeNode (String nodeName){
        this.nodeName = nodeName;
        this.nodeParent = null;
        this.nodeChildren = new ArrayList<treeNode>();
    }

}
