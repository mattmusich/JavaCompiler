package sample;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class parser {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static tree cst = new tree();
    static String errorString = "";

    public static ArrayList<Object> initParse(ArrayList<token> sentTokens){

        cst.root = null;
        cst.current = null;
        //set tokens to the token output from the lexer
        ArrayList<token> tokens = sentTokens;

        Queue<token> tokenStack = new LinkedList<token>();

        //Stack creation
        if (!tokens.isEmpty()) {
            for (int x = 0; x < tokens.size(); x++) {
                tokenStack.add(tokens.get(x));
                System.out.println(tokens.get(x).getToken());
            }
        }

        //The String that will return the status of the parse
        errorString = "";

        //Scan the tokens 1 by one and checking the lookahead to see if it is in the grammar
        errorString = parse(tokenStack);

        ArrayList<Object> parseSend = new ArrayList<Object>();
        parseSend.add(errorString);
        parseSend.add(cst);

        return parseSend;
    }


    //This will check if the current token is the same as the "testCase" sent
    public static Queue<token> match(String testCase, Queue<token> tokenStack){


        token current = tokenStack.remove();
        System.out.println("current Type: "+ current.getTokenType());
        System.out.println("current Data: "+ current.getTokenData());

        if (current.getTokenType().equals(testCase)) {
            System.out.println(ANSI_GREEN + "CASE: " + testCase + " PASSED: " + current.getToken() + ANSI_RESET);
            cst.addBranchNode(current.getTokenData(),"leaf");//

        } else {
            System.out.println("Expecting: " + testCase + " got " + current.getTokenType());
            errorString += "\nError at token: " + current.getToken() + " #Expecting: " + testCase + " got " + current.getTokenType();
            //TODO add a way to escape if this error occurs.
            tokenStack.clear();
            tokenStack.add(new token("RBRACK","}"));
            tokenStack.add(new token("RBRACK","}"));  //TODO kinda iffy fix, need to test for issues with other cases.
        }


        System.out.println(testCase + ": Match finished");
        return tokenStack;
    }

    //Calls the first recursive piece -> parseProgram
    public static String parse(Queue<token> tokenStack){

        System.out.println("PARSER IS STARTING: \n \n");

        parseProgram(tokenStack);

        //checks for any remaining right brackets and anything else after the $
        if (!tokenStack.isEmpty()){

            token test = tokenStack.peek();
            System.out.println(test.getToken());

            if (test.getTokenType().equals("RBRACK")) {
                System.out.println("you done goofed there is a right bracket.");
                errorString += "\nRight Bracket is missing a paired Left Bracket. Please add a Right or remove a bracket to fix the scope\n";
            }
        }

        //output to console and the taOutput that sends to the main controller
        if (errorString.equals("")) {
            System.out.println("We made it through the parse, all is good in the world");
            return "We made it through the parse, all is good in the world\n\nEND PARSE";
        }
        else{
            return "\nERRORS:" + errorString;
        }

    }

    //Calls parseBlock if there is at least 2 tokens i.e. ({) (int)
    public static Queue<token> parseProgram(Queue<token> tokenStack){

        if (tokenStack.size() > 1){
            cst.addBranchNode("root","branch");//
            tokenStack =  parseBlock(tokenStack);
        }

        cst.endChildren();//
        return tokenStack;
    }

    //calls parseStatementList if there is bracket infront of it, then after the recursive call checks the second bracket
    public static Queue<token> parseBlock(Queue<token> tokenStack){

        cst.addBranchNode("block","branch");//

        tokenStack = match("LBRACK", tokenStack);

        tokenStack = parseStatementList(tokenStack);


        if (!tokenStack.isEmpty()) {
            tokenStack = match("RBRACK", tokenStack);


        } else {
            System.out.println("more left bracks than right bracks.  Stop program");
            errorString += "\nLeft Bracket is missing a paired Right Bracket. Please add or remove a Right bracket to fix the scope\n";
            tokenStack.clear();
            tokenStack.add(new token("RBRACK","}"));
        }

        cst.endChildren();//

        if (!tokenStack.isEmpty()) {
            token current = tokenStack.peek();
            //System.out.println("Final token" + current.getToken());

            if (current.getTokenType().equals("LBRACK")) {
                errorString += "\nLeft Brace out of scope please make sure all braces are within the main scope \n";
                errorString += "\nErrors have occured, please fix all reported errors before continuing.\n";
            }
        }

        return tokenStack;
    }

    //checks to make sure that the current token is not the closing brace, which would denote that the braces are empty, thus the recursive call will not go deeper
    public static Queue<token> parseStatementList(Queue<token> tokenStack){

        if (tokenStack.size() != 0) {
            token current = tokenStack.peek();
            System.out.println("StatementList " + current.getToken());

            cst.addBranchNode("statementList","branch");//
            if (!current.getTokenType().equals("RBRACK")) {
                tokenStack = parseStatement(tokenStack);
                tokenStack = parseStatementList(tokenStack); //TODO IF broken check the types on the current.equals

            } else {

            }
        }
        cst.endChildren();//
        return tokenStack;
    }

    public static Queue<token> parseStatement(Queue<token> tokenStack){

        token current = tokenStack.peek();
        System.out.println("Statement " + current.getToken());
        cst.addBranchNode("statement","branch");//
        if (!current.getTokenType().equals("QUOTE")) {
            if (current.getTokenData().equals("print")) {
                tokenStack = parsePrint(tokenStack);
            } else if (current.getTokenType().equals("ID")) { //THIS is a type not data
                tokenStack = parseAssignment(tokenStack);
            } else if (current.getTokenData().equals("int") || current.getTokenData().equals("string") || current.getTokenData().equals("boolean")) {
                tokenStack = parseVarDecl(tokenStack);
            } else if (current.getTokenData().equals("while")) {
                tokenStack = parseWhile(tokenStack);
            } else if (current.getTokenData().equals("if")) {
                tokenStack = parseIf(tokenStack);
            } else if (current.getTokenType().equals("LBRACK")) { //THIS is a type not data
                tokenStack = parseBlock(tokenStack);
            } else { //TODO this might cause some errors ??????
                if (!current.getTokenType().equals("RBRACK")) {
                    errorString += "\nError at token: " + current.getToken() + " #Expecting: print, ID, int, while, if or Left Bracket, but got " + current.getTokenType();
                } else {
                    errorString += "\nErrors have occured, please fix all reported errors before continuing.\n";
                }

            }
        } else {
            System.out.println("Quote PREVENTION");
            errorString += "\nError at token: " + current.getToken() + " #Cannot use a quoted phrase without an assignment or print statement";
            tokenStack.clear();
            tokenStack.add(new token("RBRACK","}"));
        }

        //TODO this change might be iffy in other cases.
        if (current.getTokenType().equals("DIGIT")) {
            errorString += "\nError at token: " + current.getToken() + " #Cannot assign a number with a value.";
            tokenStack.clear();
            tokenStack.add(new token("RBRACK","}"));
        }
        cst.endChildren();//
        return tokenStack;
    }

    public static Queue<token> parsePrint(Queue<token> tokenStack){
        cst.addBranchNode("print","branch");//
        tokenStack = match("KEYWORD", tokenStack);

        tokenStack = match("LPAREN", tokenStack);

        tokenStack = parseExpr(tokenStack);

        tokenStack = match("RPAREN", tokenStack);
        cst.endChildren();//
        return tokenStack;
    }

    public static Queue<token> parseAssignment(Queue<token> tokenStack){
        cst.addBranchNode("assignment","branch");//
        System.out.println("Assignment");

        tokenStack = parseID(tokenStack);

        tokenStack = match("EQUALS", tokenStack);

        tokenStack = parseExpr(tokenStack);
        cst.endChildren();//
        return tokenStack;
    }

    public static Queue<token> parseVarDecl(Queue<token> tokenStack){
        cst.addBranchNode("varDecl","branch");//
        tokenStack = match("KEYWORD", tokenStack);

        tokenStack = parseID(tokenStack);
        cst.endChildren();//

        return tokenStack;
    }


    public static Queue<token> parseExpr(Queue<token> tokenStack){
        cst.addBranchNode("Expr","branch");//

        token current = tokenStack.peek();
        System.out.println("Expr " + current.getToken());

        if (current.getTokenType().equals("DIGIT") || current.getTokenType().equals("PLUS")){ //|| current.getTokenType().equals("ID")
            tokenStack = parseIntExpr(tokenStack);
        } else if (current.getTokenType().equals("QUOTE")){
            tokenStack = parseStringExpr(tokenStack);
        }  else if (current.getTokenType().equals("LPAREN") || current.getTokenType().equals("KEYWORD") ){
            tokenStack = parseBooleanExpr(tokenStack);
        }  else if (current.getTokenType().equals("ID")){
            tokenStack = parseID(tokenStack);
        }  else {
            if (!current.getTokenType().equals("RBRACK")) {
                errorString += "\nError at token: " + current.getToken() + " #Expecting: Digit, Quotes, Left Parenthesis, boolean, or an ID, but got " + current.getTokenType();
            } else {
                errorString += "\nErrors have occured, please fix all reported errors before continuing.\n";
            }
        }
        cst.endChildren();//

        return tokenStack;
    }

    public static Queue<token> parseIntExpr(Queue<token> tokenStack){

        cst.addBranchNode("IntExpr","branch");//

        tokenStack = match("DIGIT",tokenStack);

        token current = tokenStack.peek();
        System.out.println("IntExpr " + current.getToken());

        if(current.getTokenType().equals("PLUS")){
            tokenStack.remove();
            tokenStack = parseExpr(tokenStack);
        }
        cst.endChildren();//

        return tokenStack;
    }

    public static Queue<token> parseStringExpr(Queue<token> tokenStack){
        cst.addBranchNode("stringExpr","branch");//
        tokenStack = match("QUOTE",tokenStack);

        tokenStack = parseCharacterList(tokenStack);

        tokenStack = match("QUOTE",tokenStack);
        cst.endChildren();//

        return tokenStack;
    }

    public static Queue<token> parseCharacterList(Queue<token> tokenStack){
        cst.addBranchNode("charList","branch");//
        token current = tokenStack.peek();
        System.out.println("CharacterList " + current.getToken());

        if(current.getTokenType().equals("CHAR")){ //TODO >????
            tokenStack = match("CHAR",tokenStack);
            tokenStack = parseCharacterList(tokenStack);
        }
        cst.endChildren();//

        return tokenStack;
    }

    public static Queue<token> parseBooleanExpr(Queue<token> tokenStack){
        cst.addBranchNode("BoolExpr","branch");//
        token current = tokenStack.peek();
        System.out.println("BooleanExpr " + current.getToken());

        if(current.getTokenType().equals("LPAREN")){
            tokenStack = match("LPAREN",tokenStack);
            System.out.println("left match done");
            tokenStack = parseExpr(tokenStack);
            System.out.println("expr1 done");
            tokenStack = parseBooleanOp(tokenStack);
            System.out.println("boolop done");
            tokenStack = parseExpr(tokenStack);
            System.out.println("expr2 done");
            tokenStack = match("RPAREN",tokenStack);
            System.out.println("right match done");
        } else if (current.getTokenData().equals("true") || current.getTokenData().equals("false")){
            tokenStack = match("KEYWORD", tokenStack);
        } else {
            System.out.println("not a paren or boolval");
            errorString += "\nError at token: " + current.getToken() + " #Expecting: Left Parenthesis or boolean value, but got " + current.getTokenType();
            tokenStack.clear();
            tokenStack.add(new token("RBRACK","}"));
        }
        cst.endChildren();//

        return tokenStack;
    }

    public static Queue<token> parseBooleanOp(Queue<token> tokenStack){

        token current = tokenStack.peek();
        System.out.println("BooleanOp " + current.getToken());

        if(current.getTokenType().equals("DUBEQUALS")){
            tokenStack = match("DUBEQUALS",tokenStack);
            cst.addBranchNode("dubEqualBool","branch");//
        } else if (current.getTokenType().equals("NOTEQUALS")){
            tokenStack = match("NOTEQUALS",tokenStack);
            cst.addBranchNode("notEqualBool","branch");//
        } else {
            errorString += "\nError at token: " + current.getToken() + " #Expecting: == or !=, but got " + current.getTokenType();
        }
        cst.endChildren();//

        return tokenStack;
    }

    public static Queue<token> parseID(Queue<token> tokenStack){
        cst.addBranchNode("ID","branch");//

        System.out.println("ID");
        tokenStack = match("ID",tokenStack);
        cst.endChildren();//

        return tokenStack;
    }

    public static Queue<token> parseWhile(Queue<token> tokenStack){
        cst.addBranchNode("While","branch");//

        tokenStack = match("KEYWORD",tokenStack);

        tokenStack = parseBooleanExpr(tokenStack);

        tokenStack = parseBlock(tokenStack);
        cst.endChildren();//

        return tokenStack;
    }

    public static Queue<token> parseIf(Queue<token> tokenStack){
        cst.addBranchNode("IF","branch");//

        tokenStack = match("KEYWORD",tokenStack);

        tokenStack = parseBooleanExpr(tokenStack);

        tokenStack = parseBlock(tokenStack);
        cst.endChildren();//

        return tokenStack;
    }

}
