package sample;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

public class CstToAst {

    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    public int x = 0;
    public tree tempAst = new tree();
    public tree hashTree = new tree();
    //public SymbolTable astHash = new SymbolTable(-1, new LinkedList<Hashtable>());

    //defines the new ast and calls scan
    public ArrayList<Object> convert(tree cst){
        tree ast = new tree();
        ast = scan(cst);
        System.out.println(hashTree.scopeString());
        ArrayList<Object> send = new ArrayList<Object>();
        send.add(ast);
        send.add(hashTree.scopeString());
        return send;
    }

    //scan initializes the recursive function compress with the first child of the root node(block)
    private tree scan(tree cst){
        compress(cst.root.nodeChildren.get(0));
        return tempAst;
    }

    //recursive decent of the cst and builds the ast
    private void compress(treeNode node) {

        System.out.println(ANSI_CYAN + "COMPRESS: Current Node: " + node.nodeName + ANSI_RESET);

        if (node.nodeChildren == null || node.nodeChildren.size() == 0) {
            //this means we hit a leaf, which should only happen if we find no special nodes
            System.out.println("LEAFED " + node.nodeName);
            if (node.nodeName.equals("}")){
                tempAst.endChildren();
                hashTree.endChildren();
            }

        } else {
            //this means we hit a branch ast
            switch (node.nodeName.charAt(0)) {
                case 'b': //block
                    if (node.nodeName.equals("block")) {
                        System.out.println("AST checking block");
                        tempAst.addBranchNode("block", "branch");

                        //Hashtable is String(ID) String(TYPE)
                        hashTree.addBranchNode("Scope"+ Integer.toString(x) ,"branch");
                        x += x + 1;

                        for (int i = 0; i < node.nodeChildren.size(); i++) {
                            compress(node.nodeChildren.get(i));
                        }

                    }
                    break;
                case 'v'://varDecl
                    if (node.nodeName.equals("varDecl")) {
                        System.out.println("AST Checking varDecl");
                        tempAst.addBranchNode(node.nodeName, "branch");

                        System.out.println("Decl type:" + node.nodeChildren.get(0).nodeName);
                        if (node.nodeChildren.get(0).nodeName.equals("int") || node.nodeChildren.get(0).nodeName.equals("string") || node.nodeChildren.get(0).nodeName.equals("boolean")) {
                            tempAst.addBranchNode(node.nodeChildren.get(0).nodeName, "leaf");
                            System.out.println("AST added varDecl left <-");
                        } else {} //ERROR should not happen
                        if (node.nodeChildren.get(1).nodeChildren.get(0).nodeName.matches("[a-z]")) {
                            tempAst.addBranchNode(node.nodeChildren.get(1).nodeChildren.get(0).nodeName, "leaf");
                            System.out.println("AST added varDecl right ->");
                        } else {} //ERROR should not happen

                        if  (node.nodeChildren.get(0).nodeName.equals("int")){ //add an int to the Hash tree
                            if(!hashTree.current.table.containsKey(node.nodeChildren.get(1).nodeChildren.get(0).nodeName)) {
                                hashTree.current.table.put(node.nodeChildren.get(1).nodeChildren.get(0).nodeName, node.nodeChildren.get(0).nodeName);
                                System.out.println(ANSI_YELLOW + "HASH.Scope.IntAdded: " + hashTree.toString() + ANSI_RESET);
                            } else {
                                System.out.println(ANSI_PURPLE + "HASH.Scope.Int.ERROR: ID: " + node.nodeChildren.get(1).nodeChildren.get(0).nodeName+ " Is already initialized in the scope" + ANSI_RESET);
                            }
                        }
                        if  (node.nodeChildren.get(0).nodeName.equals("string")){ //add an String to the Hash tree
                            if(!hashTree.current.table.containsKey(node.nodeChildren.get(1).nodeChildren.get(0).nodeName)) {
                                hashTree.current.table.put(node.nodeChildren.get(1).nodeChildren.get(0).nodeName,node.nodeChildren.get(0).nodeName);
                                System.out.println(ANSI_YELLOW +"HASH.Scope.StringAdded: "+ hashTree.toString() + ANSI_RESET);
                            } else {
                                System.out.println(ANSI_PURPLE + "HASH.Scope.String.ERROR: ID: " + node.nodeChildren.get(1).nodeChildren.get(0).nodeName+ " Is already initialized in the scope" + ANSI_RESET);
                            }

                        }
                        if  (node.nodeChildren.get(0).nodeName.equals("boolean")) { //add an Boolean to the Hash tree
                            if (!hashTree.current.table.containsKey(node.nodeChildren.get(1).nodeChildren.get(0).nodeName)) {
                                hashTree.current.table.put(node.nodeChildren.get(1).nodeChildren.get(0).nodeName, node.nodeChildren.get(0).nodeName);
                                System.out.println(ANSI_YELLOW + "HASH.Scope.BooleanAdded: " + hashTree.toString() + ANSI_RESET);
                            } else {
                                System.out.println(ANSI_PURPLE + "HASH.Scope.Boolean.ERROR: ID: " + node.nodeChildren.get(1).nodeChildren.get(0).nodeName + " Is already initialized in the scope" + ANSI_RESET);
                            }
                        }
                        tempAst.endChildren();
                    }
                    break;
                case 'a'://Assignment
                    if (node.nodeName.equals("assignment")) {
                        System.out.println("AST Checking assignment");
                        tempAst.addBranchNode(node.nodeName, "branch");

                        System.out.println("assign type:" + node.nodeChildren.get(0).nodeChildren.get(0).nodeName);
                        //id
                        if (node.nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("[a-z]")) {
                            tempAst.addBranchNode(node.nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            System.out.println("AST added assignment left <-");
                        } else {} //ERROR should not happen

                        //string
                        if (node.nodeChildren.get(2).nodeChildren.get(0).nodeName.equals("stringExpr")){
                            if (!node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("$")) {

                                treeNode stringNode = node.nodeChildren.get(2).nodeChildren.get(0);
                                String build = "";
                                System.out.println("ASDF"+stringNode.nodeName);

                                while (stringNode.nodeChildren.size() >= 2){
                                    System.out.println("string build: "+ stringNode.nodeChildren.get(0).nodeName);
                                    build += stringNode.nodeChildren.get(0).nodeName;
                                    stringNode = stringNode.nodeChildren.get(1);
                                }

                                build += "\"";
                                tempAst.addBranchNode(build, "leaf");
                                checkAssign(hashTree.current);
                                System.out.println("AST added string assignment right ->");
                            } else {
                            } //ERROR should not happen
                        }

                        //int
                        if (node.nodeChildren.get(2).nodeChildren.get(0).nodeName.equals("IntExpr")) {

                            if (!node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("$")) {
                                tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");

                                if (node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.size() == 2 ) {
                                    if (node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(1).nodeName.equals("Expr")) {
                                        tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(1).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                                    }
                                }
                                checkAssign(hashTree.current);
                                System.out.println("AST added int assignment right ->");
                            } else {
                            } //ERROR should not happen
                        }

                        //bool
                        if (node.nodeChildren.get(2).nodeChildren.get(0).nodeName.equals("BoolExpr")) {
                            if (!node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("$")) {
                                tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                                System.out.println("AST added bool assignment right ->");
                                checkAssign(hashTree.current);

                            } else {
                            } //ERROR should not happen
                        }

                        //see if the assign is there

                        tempAst.endChildren();
                    }
                    break;
                case 'p'://Print
                    if (node.nodeName.equals("print")) {
                        System.out.println("AST Checking print");
                        tempAst.addBranchNode(node.nodeName, "branch");



                        System.out.println("print type:" + node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName);
                        if (node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("[a-z]")) {
                            tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            System.out.println("AST added print left <-");
                        } else { } //ERROR should not happen

                        if (node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("\"")) {
                            treeNode stringNode = node.nodeChildren.get(2).nodeChildren.get(0);
                            String build = "";
                            System.out.println("ASDF"+stringNode.nodeName);

                            while (stringNode.nodeChildren.size() >= 2){
                                System.out.println("string build: "+ stringNode.nodeChildren.get(0).nodeName);
                                build += stringNode.nodeChildren.get(0).nodeName;
                                stringNode = stringNode.nodeChildren.get(1);
                            }

                            build += "\"";
                            tempAst.addBranchNode(build, "leaf");
                            System.out.println("AST added string assignment right ->");

                        }
                            tempAst.endChildren();
                    }
                    break;
                case 'B': //DUB EQUALS  NOT EQUALS
                    if (node.nodeName.equals("BoolExpr")) {
                        System.out.println("AST Checking BoolExpr");

                        if(node.nodeChildren.get(3).nodeName == "dubEqualBool") {
                            tempAst.addBranchNode("Comp ==", "branch");
                        } else{
                            tempAst.addBranchNode("Comp !=", "branch");
                        }

                        if (node.nodeChildren.get(1).nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("[a-z]")) {
                            tempAst.addBranchNode(node.nodeChildren.get(1).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            System.out.println("AST added dubEqualbool left <-");
                        } else {} //ERROR should not happen

                        if (node.nodeChildren.get(1).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("\"")) {
                            treeNode stringNode = node.nodeChildren.get(1).nodeChildren.get(0);
                            String build = "";
                            System.out.println("ASDF"+stringNode.nodeName);

                            while (stringNode.nodeChildren.size() >= 2){
                                System.out.println("string build: "+ stringNode.nodeChildren.get(0).nodeName);
                                build += stringNode.nodeChildren.get(0).nodeName;
                                stringNode = stringNode.nodeChildren.get(1);
                            }

                            build += "\"";
                            tempAst.addBranchNode(build, "leaf");
                            System.out.println("AST added string assignment <-");

                        }

                        if (node.nodeChildren.get(4).nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("[a-z]")) {
                            tempAst.addBranchNode(node.nodeChildren.get(4).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            System.out.println("AST added dubEqualbool right ->");
                        } else {} //ERROR should not happen

                        if (node.nodeChildren.get(4).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("\"")) {
                            treeNode stringNode = node.nodeChildren.get(1).nodeChildren.get(0);
                            String build = "";
                            System.out.println("ASDF"+stringNode.nodeName);

                            while (stringNode.nodeChildren.size() >= 2){
                                System.out.println("string build: "+ stringNode.nodeChildren.get(0).nodeName);
                                build += stringNode.nodeChildren.get(0).nodeName;
                                stringNode = stringNode.nodeChildren.get(1);
                            }

                            build += "\"";
                            tempAst.addBranchNode(build, "leaf");
                            System.out.println("AST added string assignment <-");

                        }

                        tempAst.endChildren();
                    }
                    break;
                case 'W'://While
                    if (node.nodeName.equals("While")) {
                        System.out.println("AST Checking while");
                        tempAst.addBranchNode(node.nodeName, "branch");
                        compress(node.nodeChildren.get(1));
                        compress(node.nodeChildren.get(2));
                    }
                    break;
                case 'I'://If
                    if (node.nodeName.equals("IF")) {
                        System.out.println("AST Checking if");
                        tempAst.addBranchNode(node.nodeName, "branch");
                        compress(node.nodeChildren.get(1));
                        compress(node.nodeChildren.get(2));
                    }
                    break;
                default:

                    System.out.println("DEFAULT " + node.nodeName);

                    for (int i = 0; i < node.nodeChildren.size(); i++) {
                        compress(node.nodeChildren.get(i));
                    }
                    break;


            }//end compress.case

        }//end compress.else

    }// end compress

    public void checkAssign(treeNode pointer){
        System.out.println("checkAssign called");
        System.out.println("In scope: " + pointer.nodeName + " looking for: " +tempAst.current.nodeChildren.get(0).nodeName);
        if (pointer.table.containsKey(tempAst.current.nodeChildren.get(0).nodeName)){

            String decType = pointer.table.get(tempAst.current.nodeChildren.get(0).nodeName).toString();

            if (decType.equals("int")){
                if (tempAst.current.nodeChildren.get(1).nodeName.matches("[0-9]")){
                    System.out.println("INT assign GOOD");
                    return;
                } else {} //error

            } else if (decType.equals("string")){
                if (tempAst.current.nodeChildren.get(1).nodeName.charAt(0) == '\"'){
                    System.out.println("STRING assign GOOD");
                    return;
                } else {} //error

            } else if (decType.equals("boolean")) {
                if (tempAst.current.nodeChildren.get(1).nodeName.matches("true") || tempAst.current.nodeChildren.get(1).nodeName.matches("false")) {
                    System.out.println("BOOLEAN assign GOOD");
                    return;
                } else {} //error

            }

        } else {
            if (pointer.nodeParent == null){
                System.out.println("Id was never made");
                return; //This is an error
            } else {
                pointer = pointer.nodeParent;
                checkAssign(pointer);
            }
        }

    }


}//EOF



