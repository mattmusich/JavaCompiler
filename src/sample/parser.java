package sample;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class parser {

    //init
    String errorString = "";
    

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

        //The print just for checking
        String tokenString = convertTokensToString(tokens);

        //Scan the tokens 1 by one and checking the lookahead to see if it is in the grammar
        parse(tokenStack);

        return tokenString;
    }

    //This will check if the current token is the same as the "testCase" sent
    public static Queue<token> match(String testCase, Queue<token> tokenStack){

        token current = tokenStack.remove();
        System.out.println("current Type: "+ current.getTokenType());
        System.out.println("current Data: "+ current.getTokenData());

        if (current.getTokenType().equals(testCase)) {
            if (current.getTokenType().equals("LPAREN") || current.getTokenType().equals("LBRACK") || current.getTokenType().equals("QUOTE")) {
                System.out.println("matched made it in");
                tokenStack.add(current);
            } else {}
        } else {
            //error out not wht expected
            System.out.println("Expecting: " + testCase + " got " + current.getTokenType());

            //System.exit(1);
        }


        System.out.println(testCase + ": Match finished");
        return tokenStack;
    }

    //Calls the first recursive piece -> parseProgram
    public static void parse(Queue<token> tokenStack){

        System.out.println("PARSER IS STARTING: \n \n");

        parseProgram(tokenStack);
        System.out.println("We made it through the parse, all is good in the world");

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

        token current = tokenStack.peek();

        if (!current.getTokenType().equals("RBRACK")) {
            tokenStack = parseStatement(tokenStack);
            //tokenStack = parseStatementList(tokenStack);
        }

        return tokenStack;
    }


    public static Queue<token> parseStatement(Queue<token> tokenStack){

        token current = tokenStack.peek();

        if (current.getTokenData().equals("print")){
            tokenStack = parsePrint(tokenStack);
        } else if (current.getTokenData().equals("ID")){
            tokenStack = parseAssignment(tokenStack);
        } else if (current.getTokenData().equals("TYPE")){
            tokenStack = parseVarDecl(tokenStack);
        } else if (current.getTokenData().equals("while")){
            tokenStack = parseWhile(tokenStack);
        } else if (current.getTokenData().equals("if")){
            tokenStack = parseIf(tokenStack);
        } else if (current.getTokenData().equals("LBRACK")){
            tokenStack = parseBlock(tokenStack);
        } else {
            //not one of these
        }


        return tokenStack;
    }

    public static Queue<token> parsePrint(Queue<token> tokenStack){

        tokenStack = match("print", tokenStack);

        tokenStack = match("(", tokenStack);

        tokenStack = parseExpr(tokenStack);

        tokenStack = match(")", tokenStack);

        return tokenStack;
    }

    public static Queue<token> parseAssignment(Queue<token> tokenStack){

        tokenStack = parseID(tokenStack);

        tokenStack = match("DUBEQUALS", tokenStack);

        tokenStack = parseExpr(tokenStack);

        return tokenStack;
    }

    public static Queue<token> parseVarDecl(Queue<token> tokenStack){

        tokenStack = match("TYPE", tokenStack);

        tokenStack = parseID(tokenStack);

        return tokenStack;
    }


    public static Queue<token> parseExpr(Queue<token> tokenStack){

        token current = tokenStack.peek();

        if (current.getTokenType().equals("Digit")){
            tokenStack = parseIntExpr(tokenStack);
        } else if (current.getTokenType().equals("QUOTE")){
            tokenStack = parseStringExpr(tokenStack);
        }  else if (current.getTokenType().equals("LPAREN") || current.getTokenType().equals("BOOLEAN") ){
            tokenStack = parseBooleanExpr(tokenStack);
        }  else if (current.getTokenType().equals("ID")){
            tokenStack = parseID(tokenStack);
        }  else {
            //error out
        }

        return tokenStack;
    }

    public static Queue<token> parseIntExpr(Queue<token> tokenStack){

        tokenStack = match("DIGIT",tokenStack);

        token current = tokenStack.peek();

        if(current.getTokenType().equals("PLUS")){
            tokenStack = parseExpr(tokenStack);
        }

        return tokenStack;
    }

    public static Queue<token> parseStringExpr(Queue<token> tokenStack){

        tokenStack = match("QUOTE",tokenStack);

        tokenStack = parseCharacterList(tokenStack);

        tokenStack = match("Quote",tokenStack);

        return tokenStack;
    }

    public static Queue<token> parseCharacterList(Queue<token> tokenStack){

        token current = tokenStack.peek();

        if(current.getTokenType().equals("ID")){
            tokenStack = match("ID",tokenStack);
            tokenStack = parseCharacterList(tokenStack);
        }

        return tokenStack;
    }

    public static Queue<token> parseBooleanExpr(Queue<token> tokenStack){

        token current = tokenStack.peek();

        if(current.getTokenType().equals("LPAREN")){
            tokenStack = match("LPAREN",tokenStack);
            tokenStack = parseExpr(tokenStack);
            tokenStack = parseBooleanOp(tokenStack);
            tokenStack = parseExpr(tokenStack);
            tokenStack = match("RPAREN",tokenStack);
        } else if (current.getTokenData().equals("true") || current.getTokenData().equals("false")){
            tokenStack = match("BOOLEAN", tokenStack);
        } else {
            //toss an error
        }

        return tokenStack;
    }

    public static Queue<token> parseBooleanOp(Queue<token> tokenStack){

        token current = tokenStack.peek();

        if(current.getTokenType().equals("EQUALS") || current.getTokenType().equals("NOTEQUALS")){
            tokenStack = match("EQUALS",tokenStack);
        } else {
            //toss an error
        }

        return tokenStack;
    }

    public static Queue<token> parseID(Queue<token> tokenStack){

        tokenStack = match("ID",tokenStack);

        return tokenStack;
    }

    public static Queue<token> parseWhile(Queue<token> tokenStack){

        tokenStack = match("while",tokenStack);

        tokenStack = parseBooleanExpr(tokenStack);

        tokenStack = parseBlock(tokenStack);

        return tokenStack;
    }

    public static Queue<token> parseIf(Queue<token> tokenStack){

        tokenStack = match("if",tokenStack);

        tokenStack = parseBooleanExpr(tokenStack);

        tokenStack = parseBlock(tokenStack);

        return tokenStack;
    }



    public static String convertTokensToString(ArrayList<token> sentTokens){

        String tokenString = "";
        ArrayList<token> tokens = sentTokens;

        if (!tokens.isEmpty()) {
            for (int x=0; x < tokens.size(); x++)
                tokenString += tokens.get(x).getToken();
        }
        return tokenString;
    }



}
