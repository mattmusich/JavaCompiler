package sample;


public class CstToAst {

    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";

    public tree tempAst = new tree();

    //defines the new ast and calls scan
    public tree convert(tree cst){
        tree ast = new tree();
        ast = scan(cst);
        return ast;
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

        } else {
            //this means we hit a branch ast
            switch (node.nodeName.charAt(0)) {
                case 'b': //block
                    if (node.nodeName.equals("block")) {
                        System.out.println("AST checking block");
                        tempAst.addBranchNode("block", "branch");
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

                        tempAst.endChildren();
                    }
                    break;
                case 'a'://Assignment
                    if (node.nodeName.equals("assignment")) {
                        System.out.println("AST Checking assignment");
                        tempAst.addBranchNode(node.nodeName, "branch");

                        System.out.println("assign type:" + node.nodeChildren.get(0).nodeChildren.get(0).nodeName);
                        if (node.nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("[a-z]")) {
                            tempAst.addBranchNode(node.nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            System.out.println("AST added assignment left <-");
                        } else {} //ERROR should not happen

                        if (!node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("$")) {
                            tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            System.out.println("AST added assignment right ->");
                        } else {} //ERROR should not happen

                        tempAst.endChildren();
                    }
                    break;
                case 'p'://Print
                    if (node.nodeName.equals("print")) {
                        System.out.println("AST Checking print");
                        tempAst.addBranchNode(node.nodeName, "branch");

                        System.out.println("assign type:" + node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName);
                        if (node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("[a-z]")) {
                            tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            System.out.println("AST added print left <-");
                        } else { } //ERROR should not happen

                        tempAst.endChildren();
                    }
                    break;
                case 'B':
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

                        if (!node.nodeChildren.get(4).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("$")) {
                            tempAst.addBranchNode(node.nodeChildren.get(4).nodeChildren.get(0).nodeChildren.get(0).nodeName, "leaf");
                            System.out.println("AST added dubEqualbool right ->");
                        } else {} //ERROR should not happen

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
                    for (int i = 0; i < node.nodeChildren.size(); i++) {
                        compress(node.nodeChildren.get(i));
                    }
                    break;

            }//end compress.case

        }//end compress.else

    }// end compress

}//EOF



