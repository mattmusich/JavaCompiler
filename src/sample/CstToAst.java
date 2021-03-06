package sample;


import java.util.ArrayList;
import java.util.Hashtable;

public class CstToAst {

    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    public int x = 0;
    public tree tempAst = new tree();
    public tree hashTree = new tree();
    public String errors = "";
    public String logString = "";
    public Hashtable unused = new Hashtable<String,String>();

    //defines the new ast and calls scan
    //the arraylist allows sending back multiple pieces of data
    public ArrayList<Object> convert(tree cst){
        tree ast = new tree();
        ast = scan(cst);
        System.out.println(hashTree.scopeString());
        System.out.println(unused.toString());
        ArrayList<Object> send = new ArrayList<Object>();
        send.add(ast);
        send.add(hashTree.scopeString());
        send.add(errors);
        send.add(logString);
        send.add(unused);
        send.add(hashTree);
        return send;
    }

    //scan initializes the recursive function compress with the first child of the root node(block)
    private tree scan(tree cst){
        compress(cst.root.nodeChildren.get(0));
        return tempAst;
    }

    //keeps track of all logging in the analysis for verbose mode, along with throwing it to console
    public void addLog(String data){
        logString += data+ "\n";
        System.out.println(data);
    }

    //recursive decent of the cst and builds the ast
    private void compress(treeNode node) {

        System.out.println(ANSI_CYAN + "COMPRESS: Current Node: " + node.nodeName + ANSI_RESET);
        addLog("COMPRESS: Current Node: " + node.nodeName);

        //If this is true we hit a leaf, which should only happen if we find no special nodes or reach the EOF
        if (node.nodeChildren == null || node.nodeChildren.size() == 0) {
            addLog("LEAFED " + node.nodeName);
            if (node.nodeName.equals("}")){
                if (tempAst.root == null){
                } else {
                    tempAst.endChildren();
                }
                hashTree.endChildren();
            }

        } else {
            //this means we hit a branch ast

            //This switch case will check for each type of special node, Block, varDecl, assignment, print, boolExpr, while, if
            //Since Java is a special child, switch case has trouble supporting string comparisons
            //This is why I need to use the first char instead(which is stupid as hell in my opinion)
            switch (node.nodeName.charAt(0)) {
                case 'b': //block
                    if (node.nodeName.equals("block")) {
                        addLog("AST checking block");
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
                        addLog("AST Checking varDecl");
                        tempAst.addBranchNode(node.nodeName, "branch");

                        addLog("Decl type:" + node.nodeChildren.get(0).nodeName);

                        //looks to see if the right branch of varDecl is one of our types
                        if (node.nodeChildren.get(0).nodeName.equals("int") || node.nodeChildren.get(0).nodeName.equals("string") || node.nodeChildren.get(0).nodeName.equals("boolean")) {
                            tempAst.addBranchNode(node.nodeChildren.get(0).nodeName, "leaf");
                            addLog("AST added varDecl left <-");
                        } else {} //ERROR should not happen

                        //check right branch to see if ID
                        if (node.nodeChildren.get(1).nodeChildren.get(0).nodeName.matches("[a-z]")) {
                            tempAst.addBranchNode(node.nodeChildren.get(1).nodeChildren.get(0).nodeName, "leaf");
                            addLog("AST added varDecl right ->");
                        } else {} //ERROR should not happen

                        //check right branch to see if int
                        if  (node.nodeChildren.get(0).nodeName.equals("int")){ //add an int to the Hash tree
                            if(!hashTree.current.table.containsKey(node.nodeChildren.get(1).nodeChildren.get(0).nodeName)) {
                                hashTree.current.table.put(node.nodeChildren.get(1).nodeChildren.get(0).nodeName, node.nodeChildren.get(0).nodeName);
                                System.out.println(ANSI_YELLOW + "HASH.Scope.IntAdded: " + hashTree.toString() + ANSI_RESET);
                                addLog("HASH.Scope.IntAdded: " + hashTree.toString());
                                unused.put(node.nodeChildren.get(1).nodeChildren.get(0).nodeName,"");
                            } else {
                                System.out.println(ANSI_PURPLE + "HASH.Scope.Int.ERROR: ID: " + node.nodeChildren.get(1).nodeChildren.get(0).nodeName+ " Is already initialized in the scope" + ANSI_RESET);
                                errors += "HASH.Scope.Int.ERROR: ID: " + node.nodeChildren.get(1).nodeChildren.get(0).nodeName+ " is already initialized in the scope\n";
                            }
                        }

                        //check right branch to see if string
                        if  (node.nodeChildren.get(0).nodeName.equals("string")){ //add an String to the Hash tree
                            if(!hashTree.current.table.containsKey(node.nodeChildren.get(1).nodeChildren.get(0).nodeName)) {
                                hashTree.current.table.put(node.nodeChildren.get(1).nodeChildren.get(0).nodeName,node.nodeChildren.get(0).nodeName);
                                System.out.println(ANSI_YELLOW +"HASH.Scope.StringAdded: "+ hashTree.toString() + ANSI_RESET);
                                addLog("HASH.Scope.StringAdded: " + hashTree.toString());
                                unused.put(node.nodeChildren.get(1).nodeChildren.get(0).nodeName,"");
                            } else {
                                System.out.println(ANSI_PURPLE + "HASH.Scope.String.ERROR: ID: " + node.nodeChildren.get(1).nodeChildren.get(0).nodeName+ " Is already initialized in the scope" + ANSI_RESET);
                                errors += "HASH.Scope.String.ERROR: ID: " + node.nodeChildren.get(1).nodeChildren.get(0).nodeName+ " is already initialized in the scope\n";
                            }
                        }
                        //check right branch to see if boolean
                        if  (node.nodeChildren.get(0).nodeName.equals("boolean")) { //add an Boolean to the Hash tree
                            if (!hashTree.current.table.containsKey(node.nodeChildren.get(1).nodeChildren.get(0).nodeName)) {
                                hashTree.current.table.put(node.nodeChildren.get(1).nodeChildren.get(0).nodeName, node.nodeChildren.get(0).nodeName);
                                System.out.println(ANSI_YELLOW + "HASH.Scope.BooleanAdded: " + hashTree.toString() + ANSI_RESET);
                                addLog("HASH.Scope.BooleanAdded: " + hashTree.toString());
                                unused.put(node.nodeChildren.get(1).nodeChildren.get(0).nodeName,"");
                            } else {
                                System.out.println(ANSI_PURPLE + "HASH.Scope.Boolean.ERROR: ID: " + node.nodeChildren.get(1).nodeChildren.get(0).nodeName + " Is already initialized in the scope" + ANSI_RESET);
                                errors += "HASH.Scope.Boolean.ERROR: ID: " + node.nodeChildren.get(1).nodeChildren.get(0).nodeName + " is already initialized in the scope\n";
                            }
                        }
                        tempAst.endChildren();
                    }
                    break;
                case 'a'://Assignment
                    if (node.nodeName.equals("assignment")) {
                        addLog("AST Checking assignment");
                        tempAst.addBranchNode(node.nodeName, "branch");
                        addLog("assign type:" + node.nodeChildren.get(0).nodeChildren.get(0).nodeName);

                        //id Left branch
                        if (node.nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("[a-z]")) {
                            tempAst.addBranchNode(node.nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            addLog("AST added assignment left <-");
                        } else {} //ERROR should not happen

                        //ID right branch
                        if (node.nodeChildren.get(2).nodeChildren.get(0).nodeName.equals("ID")) {
                            if (!node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("$")) {
                                //tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");

                                String rightId = node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName;
                                addLog(rightId);
                                String leftId = node.nodeChildren.get(0).nodeChildren.get(0).nodeName;

                                addLog(checkType(rightId, hashTree.current));
                                addLog(checkType(leftId, hashTree.current));
                                if ((checkType(rightId, hashTree.current)).equals(checkType(leftId,hashTree.current))){
                                    addLog("Id's MATCH");
                                    tempAst.addBranchNode(rightId,"leaf"); //TODO
                                    tempAst.endChildren();
                                } else {
                                    System.out.println("NO MATCH ASS");
                                    errors += "Id's don't match: Type: "+checkType(rightId, hashTree.current) + " doesn't match " + checkType(leftId, hashTree.current) + "\n";
                                }


                                addLog("AST added int assignment right ->");
                            } else {
                            } //ERROR should not happen
                        }

                        //string right branch
                        if (node.nodeChildren.get(2).nodeChildren.get(0).nodeName.equals("stringExpr")){
                            if (!node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("$")) {

                                addLog("exprNode start: " + node.nodeChildren.get(2).nodeChildren.get(0).nodeName);
                                treeNode exprNode = node.nodeChildren.get(2).nodeChildren.get(0);
                                //exprNode loops through the StringExpr nodes in the tree
                                while(exprNode.nodeChildren.size() >= 4) {
                                    treeNode stringNode = exprNode;
                                    String build = "";
                                    addLog("ASDF" + stringNode.nodeName);

                                    //string node allows a loop through charList children (the letters in a string)
                                    while (stringNode.nodeChildren.size() >= 2) {
                                        addLog("string build: " + stringNode.nodeChildren.get(0).nodeName);
                                        build += stringNode.nodeChildren.get(0).nodeName;
                                        stringNode = stringNode.nodeChildren.get(1);
                                    }

                                    build += "\"";
                                    tempAst.addBranchNode(build, "leaf");
                                    checkAssign(hashTree.current);
                                    addLog("AST added string assignment right ->");

                                    addLog("expr Node at end of loop "+exprNode.nodeName);
                                    exprNode = exprNode.nodeChildren.get(3).nodeChildren.get(0);

                                }

                                //when the whiles are done, there is one stringExpr that doesnt fit the loop case.
                                // this accounts for it
                                if(exprNode.nodeChildren.size() == 3){
                                    treeNode stringNode = exprNode;

                                    String build = "";
                                    addLog("ASDF" + stringNode.nodeName);

                                    while (stringNode.nodeChildren.size() >= 2) {
                                        addLog("string build: " + stringNode.nodeChildren.get(0).nodeName);
                                        build += stringNode.nodeChildren.get(0).nodeName;
                                        stringNode = stringNode.nodeChildren.get(1);
                                    }

                                    build += "\"";
                                    tempAst.addBranchNode(build, "leaf");
                                    checkAssign(hashTree.current);
                                    addLog("AST added string assignment right ->");
                                }




                            } else {
                            } //ERROR should not happen
                        }

                        //int right branch
                        if (node.nodeChildren.get(2).nodeChildren.get(0).nodeName.equals("IntExpr")) {

                            if (!node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("$")) {
                                //tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");


                                treeNode intNode = node.nodeChildren.get(2).nodeChildren.get(0);
                                ArrayList<String> intBuild = new ArrayList<String>();

                                while (intNode.nodeChildren.size() >= 2){
                                        addLog("int build: " + intNode.nodeChildren.get(0).nodeName);
                                        intBuild.add(intNode.nodeChildren.get(0).nodeName);
                                        intNode = intNode.nodeChildren.get(1).nodeChildren.get(0);
                                }


                                if (intNode.nodeChildren.size() == 0){
                                    System.out.println("Type miss-match in int");
                                    errors += "Type miss-match in int";
                                } else {
                                    addLog("int build Final: " + intNode.nodeChildren.get(0).nodeName);
                                    intBuild.add(intNode.nodeChildren.get(0).nodeName);
                                }

                                for (int i = 0; i < intBuild.size(); i++){
                                    addLog("addIntBuild: " + intBuild.get(i));
                                    if(intBuild.get(i).equals("true") || intBuild.get(i).equals("false")){
                                        System.out.println("Type miss-match in int: "+ intBuild.get(i) );
                                        errors += "Type miss-match in int" + intBuild.get(i);
                                    }
                                    tempAst.addBranchNode(intBuild.get(i),"branch");
                                }
                                for (int i = 0; i < intBuild.size(); i++){
                                    tempAst.endChildren();
                                }



                                checkAssign(hashTree.current);
                                addLog("AST added int assignment right ->");
                            } else {
                            } //ERROR should not happen
                        }

                        //bool right branch
                        if (node.nodeChildren.get(2).nodeChildren.get(0).nodeName.equals("BoolExpr")) {
                            if (!node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("$")) {
                                //tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");

                                if(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName == "("){
                                    //tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeName, "branch");
                                    treeNode boolExprish = node.nodeChildren.get(2).nodeChildren.get(0);
                                    if(boolExprish.nodeChildren.get(3).nodeName == "dubEqualBool"){
                                        tempAst.addBranchNode("Comp ==", "branch");
                                    } else {
                                        tempAst.addBranchNode("Comp !=", "branch");
                                    }


                                    String boolLeftType = "";
                                    String boolRightType = "";

                                    if (node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(1).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("\"")) {
                                        treeNode stringNode = node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(1).nodeChildren.get(0);
                                        String build = "";
                                        addLog("BoolStringBuild" + stringNode.nodeName);

                                        while (stringNode.nodeChildren.size() >= 2){
                                            addLog("string build: " + stringNode.nodeChildren.get(0).nodeName);
                                            build += stringNode.nodeChildren.get(0).nodeName;
                                            stringNode = stringNode.nodeChildren.get(1);
                                        }

                                        build += "\"";
                                        tempAst.addBranchNode(build, "leaf");
                                        boolLeftType = getType(build);
                                        addLog("AST added string assignment left <-");
                                        //checkPrint(hashTree.current);

                                    } else {
                                        tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(1).nodeChildren.get(0).nodeChildren.get(0).nodeName,"leaf");
                                        boolLeftType = getType(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(1).nodeChildren.get(0).nodeChildren.get(0).nodeName);
                                    }

                                    if (node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(4).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("\"")) {
                                        treeNode stringNode = node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(4).nodeChildren.get(0);
                                        String build = "";
                                        addLog("BoolStringBuild" + stringNode.nodeName);

                                        while (stringNode.nodeChildren.size() >= 2){
                                            addLog("string build: " + stringNode.nodeChildren.get(0).nodeName);
                                            build += stringNode.nodeChildren.get(0).nodeName;
                                            stringNode = stringNode.nodeChildren.get(1);
                                        }

                                        build += "\"";
                                        tempAst.addBranchNode(build, "leaf");
                                        boolRightType = getType(build);
                                        addLog("AST added string assignment right ->");
                                        //checkPrint(hashTree.current);

                                    } else {
                                        tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(4).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                                        boolRightType = getType(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(4).nodeChildren.get(0).nodeChildren.get(0).nodeName);
                                    }

                                    if(boolLeftType == boolRightType){
                                        addLog("The boolean expression matches types! "+boolLeftType + " and " + boolRightType);
                                    } else {
                                        System.out.println("The boolean expression doesn't match " +boolLeftType + " and " + boolRightType);
                                        errors += "The boolean expression doesn't match " +boolLeftType + " and " + boolRightType + "\n";
                                    }

                                    tempAst.endChildren();
                                    } else {
                                    tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                                }

                                }

                                addLog("AST added bool assignment right ->");
                                checkAssign(hashTree.current);

                        } else {
                        } //ERROR should not happen
                    }

                    //This looks important, but forget why assignment needs it
                        if (tempAst.root == null){
                        } else {
                            tempAst.endChildren();
                        }

                    break;
                case 'p'://Print
                    if (node.nodeName.equals("print")) {
                        addLog("AST Checking print");
                        tempAst.addBranchNode(node.nodeName, "branch");
                        addLog("print type:" + node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName);

                        //printing an id
                        if (node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("[a-z]")) {
                            tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            addLog("AST added print left <-");
                            checkPrint(hashTree.current);
                        } else { } //ERROR should not happen

                        //printing a string
                        if (node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("\"")) {
                            treeNode exprNode = node.nodeChildren.get(2).nodeChildren.get(0);

                            //same process as the assignment version
                            while (exprNode.nodeChildren.size() >= 4) {
                                treeNode stringNode = exprNode;
                                String build = "";
                                addLog("ASDF" + stringNode.nodeName);

                                while (stringNode.nodeChildren.size() >= 2) {
                                    addLog("string build: " + stringNode.nodeChildren.get(0).nodeName);
                                    build += stringNode.nodeChildren.get(0).nodeName;
                                    stringNode = stringNode.nodeChildren.get(1);
                                }

                                build += "\"";
                                tempAst.addBranchNode(build, "leaf");
                                addLog("AST added string assignment right ->");
                                //checkAssign(hashTree.current);
                                exprNode = exprNode.nodeChildren.get(3).nodeChildren.get(0);
                            }

                            //same process as the assignment version
                            if (exprNode.nodeChildren.size() == 3) {
                                treeNode stringNode = exprNode;
                                String build = "";
                                addLog("ASDF" + stringNode.nodeName);

                                while (stringNode.nodeChildren.size() >= 2) {
                                    addLog("string build: " + stringNode.nodeChildren.get(0).nodeName);
                                    build += stringNode.nodeChildren.get(0).nodeName;
                                    stringNode = stringNode.nodeChildren.get(1);
                                }

                                build += "\"";
                                tempAst.addBranchNode(build, "leaf");
                                addLog("AST added string assignment right ->");
                                //checkAssign(hashTree.current);
                            }

                        }

                        //printing an int
                        if (node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("[0-9]")) {
                            tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            addLog("AST added print left <-");
                            //checkPrint(hashTree.current);

                        } else { } //ERROR should not happen

                        //printing true or false
                        if (node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("true") || node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("false")) {
                            tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            addLog("AST added print left <-");
                            //checkPrint(hashTree.current);

                        } else { } //ERROR should not happen

                            tempAst.endChildren();
                    }
                    break;
                case 'B': //DUB EQUALS  NOT EQUALS
                    if (node.nodeName.equals("BoolExpr")) {
                        addLog("AST Checking BoolExpr");

                        addLog(node.nodeChildren.get(0).nodeName);

                        //determines the type of comparison
                        if(node.nodeChildren.get(3).nodeName == "dubEqualBool") {
                            tempAst.addBranchNode("CompEQ", "branch");
                        } else{
                            tempAst.addBranchNode("CompNotEQ", "branch");
                        }

                        //left is id
                        if (node.nodeChildren.get(1).nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("[a-z]")) {
                            tempAst.addBranchNode(node.nodeChildren.get(1).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            addLog("AST added dubEqualbool left <-");
                        } else {} //ERROR should not happen

                        //left is int
                        if (node.nodeChildren.get(1).nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("[0-9]")) {
                            tempAst.addBranchNode(node.nodeChildren.get(1).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            addLog("AST added dubEqualbool left <-");
                        } else {} //ERROR should not happen

                        //left is boolean
                        if (node.nodeChildren.get(1).nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("true") || node.nodeChildren.get(1).nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("false") ) {
                            tempAst.addBranchNode(node.nodeChildren.get(1).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            addLog("AST added dubEqualbool left <-");
                        } else {} //ERROR should not happen

                        //left is string
                        if (node.nodeChildren.get(1).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("\"")) {
                            treeNode stringNode = node.nodeChildren.get(1).nodeChildren.get(0);
                            String build = "";
                            addLog("ASDF LEFT" + stringNode.nodeName);

                            while (stringNode.nodeChildren.size() >= 2){
                                addLog("string build: " + stringNode.nodeChildren.get(0).nodeName);
                                build += stringNode.nodeChildren.get(0).nodeName;
                                stringNode = stringNode.nodeChildren.get(1);
                            }

                            build += "\"";
                            tempAst.addBranchNode(build, "leaf");
                            addLog("AST added string assignment <-");

                        }

                        //right is id
                        if (node.nodeChildren.get(4).nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("[a-z]")) {
                            tempAst.addBranchNode(node.nodeChildren.get(4).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            addLog("AST added dubEqualbool right ->");
                        } else {} //ERROR should not happen

                        //right is int
                        if (node.nodeChildren.get(4).nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("[0-9]")) {
                            tempAst.addBranchNode(node.nodeChildren.get(4).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            addLog("AST added dubEqualbool right ->");
                        } else {} //ERROR should not happen

                        //right is boolean
                        if (node.nodeChildren.get(4).nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("true") || node.nodeChildren.get(4).nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("false") ) {
                            tempAst.addBranchNode(node.nodeChildren.get(4).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            addLog("AST added dubEqualbool right ->");
                        } else {} //ERROR should not happen

                        //right is string
                        if (node.nodeChildren.get(4).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("\"")) {
                            treeNode stringNode = node.nodeChildren.get(4).nodeChildren.get(0);
                            String build = "";
                            addLog("ASDF RIGHT" + stringNode.nodeName);

                            while (stringNode.nodeChildren.size() >= 2){
                                addLog("string build: " + stringNode.nodeChildren.get(0).nodeName);
                                build += stringNode.nodeChildren.get(0).nodeName;
                                stringNode = stringNode.nodeChildren.get(1);
                            }

                            build += "\"";
                            tempAst.addBranchNode(build, "leaf");
                            addLog("AST added string assignment ->");

                        }

                        addLog(tempAst.current.nodeName);

                        //Compares the two sides that were added, to see if the types are the same, if this check fails it will create an error
                        String type1 = checkType(tempAst.current.nodeChildren.get(0).nodeName, hashTree.current);
                        String type2 = checkType(tempAst.current.nodeChildren.get(1).nodeName, hashTree.current);
                        addLog("WOAH"+type1+type2+"\n");
                        if ((checkType(tempAst.current.nodeChildren.get(0).nodeName, hashTree.current)).equals(checkType(tempAst.current.nodeChildren.get(1).nodeName,hashTree.current))){
                            addLog("COMPARE MATCHES");
                        } else {
                            System.out.println("NO MATCH");
                            errors += "\nType: "+checkType(tempAst.current.nodeChildren.get(0).nodeName, hashTree.current) + " doesn't match " + checkType(tempAst.current.nodeChildren.get(1).nodeName, hashTree.current) + "\n";
                        }

                            tempAst.endChildren();
                    }
                    break;
                case 'W'://While
                    if (node.nodeName.equals("While")) {
                        addLog("AST Checking while");
                        tempAst.addBranchNode(node.nodeName, "branch");
                        //recursively checks the children of the cst
                        compress(node.nodeChildren.get(1));
                        compress(node.nodeChildren.get(2));
                    }
                    break;
                case 'I'://If
                    if (node.nodeName.equals("IF")) {
                        addLog("AST Checking if");
                        tempAst.addBranchNode(node.nodeName, "branch");
                        //recursively checks the children of the cst
                        compress(node.nodeChildren.get(1));
                        compress(node.nodeChildren.get(2));
                    }
                    break;
                default:
                    //This is for anything that is not a leaf or a symbol node
                    addLog("DEFAULT " + node.nodeName);
                    for (int i = 0; i < node.nodeChildren.size(); i++) {
                        compress(node.nodeChildren.get(i));
                    }
                    break;


            }//end compress.case

        }//end compress.else

    }// end compress


    //send in a string get its type back
    public String getType(String data){
        if(data.matches("[0-9]")){
            return "int";
        }
        if(data.equals("true") || data.equals("false")){
            return "boolean";
        }
        if(data.matches("[a-z]")){
            return "ID";
        }
        if(data.charAt(0) == '\"'){
            return "string";
        }
        return null;
    }


    //compares current tempAst node to the Hash table and passes or fails the comparison
    public void checkAssign(treeNode pointer){
        addLog("checkAssign called");
        addLog(tempAst.current.nodeName);
        if (pointer.table.containsKey(tempAst.current.nodeChildren.get(0).nodeName)){

            String decType = pointer.table.get(tempAst.current.nodeChildren.get(0).nodeName).toString();
            String varInQuestion = tempAst.current.nodeChildren.get(0).nodeName;
            //TODO


            if (decType.equals("int")){
                if (tempAst.current.nodeChildren.get(1).nodeName.matches("[0-9]")){
                    addLog("INT assign GOOD");
                    unused.put(varInQuestion, "true");
                } else {
                    System.out.println("Assignment didn't match INT from ID specified:" + tempAst.current.nodeChildren.get(1).nodeName);
                    errors += "Assignment didn't match INT from ID specified:" + tempAst.current.nodeChildren.get(1).nodeName + "\n";
                } //error
            } else if (decType.equals("string")){
                if (tempAst.current.nodeChildren.get(1).nodeName.charAt(0) == '\"'){
                    addLog("STRING assign GOOD");
                    unused.put(varInQuestion,"true");
                } else {
                    System.out.println("Assignment didn't match STRING from ID specified:" + tempAst.current.nodeChildren.get(1).nodeName);
                    errors += "Assignment didn't match STRING from ID specified:" + tempAst.current.nodeChildren.get(1).nodeName + "\n";
                } //error

            } else if (decType.equals("boolean")) {
                addLog("CHECKING Boolean: " + tempAst.current.nodeChildren.get(1).nodeName);
                if (tempAst.current.nodeChildren.get(1).nodeName.matches("true") || tempAst.current.nodeChildren.get(1).nodeName.matches("false")) {  //TODO ADD MY CASES HERE
                    addLog("BOOLEAN assign GOOD");
                    unused.put(varInQuestion,"true");
                } else if (tempAst.current.nodeChildren.get(1).nodeName.equals("Comp ==") || tempAst.current.nodeChildren.get(1).nodeName.equals("Comp !=")){

                } else {
                    System.out.println("Assignment didn't match BOOLEAN from ID specified" + tempAst.current.nodeChildren.get(1).nodeName);
                    errors += "Assignment didn't match BOOLEAN from ID specified:" + tempAst.current.nodeChildren.get(1).nodeName + "\n";
                } //error
            }

        } else {
            //checks if the from declaired scope of hashtree and up to the root, if not in any then error
            if (pointer.nodeParent == null){
                System.out.println("Id: " + tempAst.current.nodeChildren.get(0).nodeName +" was never made");
                errors += "Id: " + tempAst.current.nodeChildren.get(0).nodeName +" was never made"+ "\n";
            } else {
                pointer = pointer.nodeParent;
                checkAssign(pointer);
            }
        }

    }

    //checks id's in a print call
    public void checkPrint(treeNode pointer){
        addLog("checkPrint called");
        addLog("In scope: " + pointer.nodeName + " looking for: " + tempAst.current.nodeChildren.get(0).nodeName);
        if (pointer.table.containsKey(tempAst.current.nodeChildren.get(0).nodeName)){
            addLog("Print Check GOOD");
        } else {
            if (pointer.nodeParent == null){
                System.out.println("Id: " + tempAst.current.nodeChildren.get(0).nodeName +" was never made Cannot print");
                errors += "Id: " + tempAst.current.nodeChildren.get(0).nodeName + " was never made. Cannot print\n";
            } else {
                pointer = pointer.nodeParent;
                checkPrint(pointer);
            }
        }

    }

    //checks if a test case is sme type based on a the hash table
    public String checkType(String testcase, treeNode pointer){
        addLog("In scope: " + pointer.nodeName + " looking for type: " + testcase);

        if (testcase.matches("[a-z]")){
            if(pointer.table.containsKey(testcase)){
                addLog("CHECKTYPE:"+testcase+":"+pointer.table.get(testcase).toString());
                return pointer.table.get(testcase).toString();
            } else {
                if (pointer.nodeParent == null){
                    System.out.println("Id: " + testcase +" was never made Cannot type");
                    errors += "Id: " + testcase + " was never made. Cannot type\n";
                } else {
                    pointer = pointer.nodeParent;
                    String t = checkType(testcase, pointer); //TODO IS THIS SUPPOSE TO BE checkType
                    return t;
                }
            }
        } else if (testcase.matches("[0-9]")){
            addLog("checktype.int");
            return "int";
        } else if (testcase.charAt(0) == '\"'){
            return "string";
        } else if (testcase.matches("true") || testcase.matches("false")) {
            return "boolean";
        }

        return "NOT A TYPE.ERROR. " + testcase + " <- is not a type\n";

    }

}//EOF



