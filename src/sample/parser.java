package sample;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class parser {

    //TODO Defend against 2 didigts
    //TODO def against just chars
    //TODO def against num = num
    //TODO def against a single id


    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    static String errorString = "";
    public static String initParse(ArrayList<token> sentTokens){

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

        return errorString;
    }


    //This will check if the current token is the same as the "testCase" sent
    public static Queue<token> match(String testCase, Queue<token> tokenStack){

        token current = tokenStack.remove();
        System.out.println("current Type: "+ current.getTokenType());
        System.out.println("current Data: "+ current.getTokenData());

        if (current.getTokenType().equals(testCase)) {
            System.out.println(ANSI_GREEN + "CASE: " + testCase + " PASSED: " + current.getToken() + ANSI_RESET);

        } else {
            System.out.println("Expecting: " + testCase + " got " + current.getTokenType());
            errorString += "\nError at token: " + current.getToken() + " #Expecting: " + testCase + " got " + current.getTokenType();

        }


        System.out.println(testCase + ": Match finished");
        return tokenStack;
    }

    //Calls the first recursive piece -> parseProgram
    public static String parse(Queue<token> tokenStack){

        System.out.println("PARSER IS STARTING: \n \n");

        parseProgram(tokenStack);

        //output to console and the taOutput that sends to the main controller
        if (errorString.equals("")) {
            System.out.println("We made it through the parse, all is good in the world");
            return "We made it through the parse, all is good in the world\n";
        }
        else{
            return "\nERRORS:" + errorString;
        }

    }

    //Calls parseBlock if there is at least 2 tokens i.e. ({) (int)
    public static Queue<token> parseProgram(Queue<token> tokenStack){

        if (tokenStack.size() > 1){
            tokenStack =  parseBlock(tokenStack);
        }

        return tokenStack;
    }

    //calls parseStatementList if there is bracket infront of it, then after the recursive call checks the second bracket
    public static Queue<token> parseBlock(Queue<token> tokenStack){

        tokenStack = match("LBRACK", tokenStack);

        tokenStack = parseStatementList(tokenStack);

        tokenStack = match("RBRACK", tokenStack);

        return tokenStack;
    }

    //checks to make sure that the current token is not the closing brace, which would denote that the braces are empty, thus the recursive call will not go deeper
    public static Queue<token> parseStatementList(Queue<token> tokenStack){

        if (tokenStack.size() != 0) {
            token current = tokenStack.peek();
            System.out.println("StatementList " + current.getToken());

            if (!current.getTokenType().equals("RBRACK")) {
                tokenStack = parseStatement(tokenStack);
                tokenStack = parseStatementList(tokenStack); //TODO IF broken check the types on the current.equals
            } else {

                //escape the situation when the tokens run out????
            }
        }

        return tokenStack;
    }

    public static Queue<token> parseStatement(Queue<token> tokenStack){

        token current = tokenStack.peek();
        System.out.println("Statement " + current.getToken());

        if (!current.getTokenType().equals("QUOTE")) {
            if (current.getTokenData().equals("print")) {
                tokenStack = parsePrint(tokenStack);
            } else if (current.getTokenType().equals("ID")) { //THIS is a type not data
                tokenStack = parseAssignment(tokenStack);
            } else if (current.getTokenData().equals("int") || current.getTokenData().equals("string") || current.getTokenData().equals("boolean")) { //TODO or string or bool??
                tokenStack = parseVarDecl(tokenStack);
            } else if (current.getTokenData().equals("while")) {
                tokenStack = parseWhile(tokenStack);
            } else if (current.getTokenData().equals("if")) {
                tokenStack = parseIf(tokenStack);
            } else if (current.getTokenType().equals("LBRACK")) { //THIS is a type not data
                tokenStack = parseBlock(tokenStack);
            } else {
                errorString += "\nError at token: " + current.getToken() + " #Expecting: print, ID, int, while, if or Left Bracket, but got " + current.getTokenType();
            }
        } else {
            System.out.println("Quote PREVENTION");
            errorString += "\nError at token: " + current.getToken() + " #Cannot use a quoted phrase without an assignment or print statement";
            tokenStack.clear();
            tokenStack.add(new token("RBRACK","}"));
        }

        return tokenStack;
    }

    public static Queue<token> parsePrint(Queue<token> tokenStack){

        tokenStack = match("KEYWORD", tokenStack);

        tokenStack = match("LPAREN", tokenStack);

        tokenStack = parseExpr(tokenStack);

        tokenStack = match("RPAREN", tokenStack);

        return tokenStack;
    }

    public static Queue<token> parseAssignment(Queue<token> tokenStack){

        System.out.println("Assignment");

        tokenStack = parseID(tokenStack);

        tokenStack = match("EQUALS", tokenStack);

        tokenStack = parseExpr(tokenStack);

        return tokenStack;
    }

    public static Queue<token> parseVarDecl(Queue<token> tokenStack){

        tokenStack = match("KEYWORD", tokenStack);

        tokenStack = parseID(tokenStack);

        return tokenStack;
    }


    public static Queue<token> parseExpr(Queue<token> tokenStack){

        token current = tokenStack.peek();
        System.out.println("Expr " + current.getToken());

        if (current.getTokenType().equals("DIGIT") || current.getTokenType().equals("PLUS")){
            tokenStack = parseIntExpr(tokenStack);
        } else if (current.getTokenType().equals("QUOTE")){
            tokenStack = parseStringExpr(tokenStack);
        }  else if (current.getTokenType().equals("LPAREN") || current.getTokenType().equals("KEYWORD") ){
            tokenStack = parseBooleanExpr(tokenStack);
        }  else if (current.getTokenType().equals("ID")){
            tokenStack = parseID(tokenStack);
        }  else {
            errorString += "\nError at token: " + current.getToken() + " #Expecting: Digit, Quotes, Left Parenthesis, boolean, or an ID, but got " + current.getTokenType();

        }

        return tokenStack;
    }

    public static Queue<token> parseIntExpr(Queue<token> tokenStack){

        tokenStack = match("DIGIT",tokenStack);

        token current = tokenStack.peek();

        if(current.getTokenType().equals("PLUS")){
            tokenStack.remove();
            tokenStack = parseExpr(tokenStack);
        }

        return tokenStack;
    }

    public static Queue<token> parseStringExpr(Queue<token> tokenStack){

        tokenStack = match("QUOTE",tokenStack);

        tokenStack = parseCharacterList(tokenStack);

        tokenStack = match("QUOTE",tokenStack);

        return tokenStack;
    }

    public static Queue<token> parseCharacterList(Queue<token> tokenStack){

        token current = tokenStack.peek();
        System.out.println("CharacterList " + current.getToken());

        if(current.getTokenType().equals("CHAR")){ //TODO >????
            tokenStack = match("CHAR",tokenStack);
            tokenStack = parseCharacterList(tokenStack);
        }

        return tokenStack;
    }

    public static Queue<token> parseBooleanExpr(Queue<token> tokenStack){

        token current = tokenStack.peek();
        System.out.println("BooleanExpr " + current.getToken());

        if(current.getTokenType().equals("LPAREN")){
            tokenStack = match("LPAREN",tokenStack);
            tokenStack = parseExpr(tokenStack);
            tokenStack = parseBooleanOp(tokenStack);
            tokenStack = parseExpr(tokenStack);
            tokenStack = match("RPAREN",tokenStack);
        } else if (current.getTokenData().equals("true") || current.getTokenData().equals("false")){
            tokenStack = match("KEYWORD", tokenStack);
        } else {
            errorString += "\nError at token: " + current.getToken() + " #Expecting: Left Parenthesis or boolean value, but got " + current.getTokenType();
        }

        return tokenStack;
    }

    public static Queue<token> parseBooleanOp(Queue<token> tokenStack){

        token current = tokenStack.peek();
        System.out.println("BooleanOp " + current.getToken());

        if(current.getTokenType().equals("DUBEQUALS")){
            tokenStack = match("DUBEQUALS",tokenStack);
        } else if (current.getTokenType().equals("NOTEQUALS")){
            tokenStack = match("NOTEQUALS",tokenStack);
        } else {
            errorString += "\nError at token: " + current.getToken() + " #Expecting: == or !=, but got " + current.getTokenType();
        }

        return tokenStack;
    }

    public static Queue<token> parseID(Queue<token> tokenStack){

        System.out.println("ID");
        tokenStack = match("ID",tokenStack);

        return tokenStack;
    }

    public static Queue<token> parseWhile(Queue<token> tokenStack){

        tokenStack = match("KEYWORD",tokenStack);

        tokenStack = parseBooleanExpr(tokenStack);

        tokenStack = parseBlock(tokenStack);

        return tokenStack;
    }

    public static Queue<token> parseIf(Queue<token> tokenStack){

        tokenStack = match("KEYWORD",tokenStack);

        tokenStack = parseBooleanExpr(tokenStack);

        tokenStack = parseBlock(tokenStack);

        return tokenStack;
    }

}
