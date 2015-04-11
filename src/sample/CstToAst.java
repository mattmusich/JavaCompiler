package sample;


public class CstToAst {
    public tree tempAst = new tree();

    public String result = "";

    public tree convert(tree cst){
        tree ast = new tree();

        ast = scan(cst);


        return ast;
    }

    private tree scan(tree cst){
        compress(cst.root, 0);
        return tempAst;
    }

    private void compress(treeNode node, int depth){

        for (int i = 0; i < depth; i++){

        }

        if (node.nodeChildren == null || node.nodeChildren.size() == 0){
            //this means we hit a leaf, which should only happen if we find no special nodes

        } else {
            //this means we hit a branch ast

            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            //@@@@@ AST PATTERN CONVERSION ZONE START @@@@@
            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

            //block
            if (node.nodeName.equals("block")) {
                System.out.println("AST Checking block");
                tempAst.addBranchNode("block","branch");
            }

            //varDecl
            if (node.nodeName.equals("varDecl")){
                System.out.println("AST Checking varDecl");
                tempAst.addBranchNode(node.nodeName,"branch");

                System.out.println("Decl type:"+ node.nodeChildren.get(0).nodeName);
                if (node.nodeChildren.get(0).nodeName.equals("int") || node.nodeChildren.get(0).nodeName.equals("string") || node.nodeChildren.get(0).nodeName.equals("boolean")){
                    tempAst.addBranchNode(node.nodeChildren.get(0).nodeName,"leaf");
                    System.out.println("AST added varDecl left <-");
                } else {} //ERROR should not happen
                if (node.nodeChildren.get(1).nodeChildren.get(0).nodeName.matches("[a-z]")){
                    tempAst.addBranchNode(node.nodeChildren.get(1).nodeChildren.get(0).nodeName,"leaf");
                    System.out.println("AST added varDecl right ->");
                } else {} //ERROR should not happen

                tempAst.endChildren();
            }

            //Assignment
            if (node.nodeName.equals("assignment")){
                System.out.println("AST Checking assignment");
                tempAst.addBranchNode(node.nodeName,"branch");

                System.out.println("assign type:"+ node.nodeChildren.get(0).nodeChildren.get(0).nodeName);
                if (node.nodeChildren.get(0).nodeChildren.get(0).nodeName.matches("[a-z]")){
                    tempAst.addBranchNode(node.nodeChildren.get(0).nodeChildren.get(0).nodeName,"leaf");
                    System.out.println("AST added assignment left <-");
                } else {} //ERROR should not happen

                if (!node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName.equals("$")){
                    tempAst.addBranchNode(node.nodeChildren.get(2).nodeChildren.get(0).nodeChildren.get(0).nodeName,"leaf");
                    System.out.println("AST added assignment right ->");
                } else {} //ERROR should not happen

                tempAst.endChildren();
            }

            //Print

            //While

            //If

            //== (compare ==) BooleanExpression

            //!= (compare !=) BooleanExpression



            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            //@@@@@ AST PATTERN CONVERSION ZONE END @@@@@
            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

            for (int i = 0; i < node.nodeChildren.size(); i++) {
                compress(node.nodeChildren.get(i), depth + 1);
            }


        }
    }

    private boolean isVarDecl(treeNode node){

        return true;
    }




}
