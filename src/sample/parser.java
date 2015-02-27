package sample;

import java.util.ArrayList;
import java.util.Stack;

public class parser {

    //init
    String errorString = "";

    public static String initParse(ArrayList<token> sentTokens){

        //set tokens to the token output from the lexer
        ArrayList<token> tokens = sentTokens;

        Stack<token> tokenStack= new Stack<token>();

        if (!tokens.isEmpty()) {
            for (int x=0; x < tokens.size(); x++)
                tokenStack.add(tokens.get(1));
        }

        //The print just for checking
        String tokenString = convertTokensToString(tokens);

        //Scan the tokens 1 by one and checking the lookahead to see if it is in the grammar
        parse(tokenStack);

        return tokenString;
    }

    //This will check if the current token is the same as the "testCase" sent
    public static Stack<token> match(String testCase, Stack<token> tokenStack){

        token current = tokenStack.pop();
        System.out.println("Match Type: "+ current.getTokenType());
        System.out.println("Match Data: "+ current.getTokenType());

        if (current.getTokenType().equals(testCase)) {
            if (current.getTokenType().equals("LPAREN") || current.getTokenType().equals("LBRACK") || current.getTokenType().equals("QUOTE")) {
                System.out.println(tokenStack.toString() + "matched made it in");
                tokenStack.push(current);
            } else {}
        } else {
            //error out not wht expected
            System.out.println("Expecting: " + testCase + " got" + current.getTokenType());
            System.out.println("TOKEN STACK " + tokenStack.toString());
        }

        System.out.println("Match finished");
        return tokenStack;
    }

    //Calls the first recursive piece -> parseProgram
    public static void parse(Stack<token> tokenStack){

        System.out.println("PARSER IS STARTING: \n \n");

        parseProgram(tokenStack);
        System.out.println("We made it through the parse, all is good in the world");

    }

    //Calls parseBlock if there is at least 2 tokens i.e. ({) (int)
    public static Stack<token> parseProgram(Stack<token> tokenStack){

        if (tokenStack.size() > 1){
          tokenStack =  parseBlock(tokenStack);
        }

        return tokenStack;
    }

    //calls parseStatementList if there is bracket infront of it, then after the recursive call checks the second bracket
    public static Stack<token> parseBlock(Stack<token> tokenStack){

        tokenStack = match("LBRACK", tokenStack);

        tokenStack = parseStatementList(tokenStack);

        tokenStack = match("RBRACK", tokenStack);

        return tokenStack;
    }

    //checks to make sure that the current token is not the closing brace, which would denote that the braces are empty, thus the recursive call will not go deeper
    public static Stack<token> parseStatementList(Stack<token> tokenStack){

        token current = tokenStack.peek();

        if (!current.getTokenType().equals("RBRACK")) {
            tokenStack = parseStatement(tokenStack);
            //tokenStack = parseStatementList(tokenStack);
        }

        return tokenStack;
    }


    public static Stack<token> parseStatement(Stack<token> tokenStack){

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

    public static Stack<token> parsePrint(Stack<token> tokenStack){

        tokenStack = match("print", tokenStack);

        tokenStack = match("(", tokenStack);

        tokenStack = parseExpr(tokenStack);

        tokenStack = match(")", tokenStack);

        return tokenStack;
    }

    public static Stack<token> parseAssignment(Stack<token> tokenStack){

        tokenStack = parseID(tokenStack);

        tokenStack = match("DUBEQUALS", tokenStack);

        tokenStack = parseExpr(tokenStack);

        return tokenStack;
    }

    public static Stack<token> parseVarDecl(Stack<token> tokenStack){

        tokenStack = match("TYPE", tokenStack);

        tokenStack = parseID(tokenStack);

        return tokenStack;
    }


    public static Stack<token> parseExpr(Stack<token> tokenStack){

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

    public static Stack<token> parseIntExpr(Stack<token> tokenStack){

        tokenStack = match("DIGIT",tokenStack);

        token current = tokenStack.peek();

        if(current.getTokenType().equals("PLUS")){
            tokenStack = parseExpr(tokenStack);
        }

        return tokenStack;
    }

    public static Stack<token> parseStringExpr(Stack<token> tokenStack){

        tokenStack = match("QUOTE",tokenStack);

        tokenStack = parseCharacterList(tokenStack);

        tokenStack = match("Quote",tokenStack);

        return tokenStack;
    }

    public static Stack<token> parseCharacterList(Stack<token> tokenStack){

        token current = tokenStack.peek();

        if(current.getTokenType().equals("ID")){
            tokenStack = match("ID",tokenStack);
            tokenStack = parseCharacterList(tokenStack);
        }

        return tokenStack;
    }

    public static Stack<token> parseBooleanExpr(Stack<token> tokenStack){

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

    public static Stack<token> parseBooleanOp(Stack<token> tokenStack){

        token current = tokenStack.peek();

        if(current.getTokenType().equals("EQUALS") || current.getTokenType().equals("NOTEQUALS")){
            tokenStack = match("EQUALS",tokenStack);
        } else {
            //toss an error
        }

        return tokenStack;
    }

    public static Stack<token> parseID(Stack<token> tokenStack){

        tokenStack = match("ID",tokenStack);

        return tokenStack;
    }

    public static Stack<token> parseWhile(Stack<token> tokenStack){

        tokenStack = match("while",tokenStack);

        tokenStack = parseBooleanExpr(tokenStack);

        tokenStack = parseBlock(tokenStack);

        return tokenStack;
    }

    public static Stack<token> parseIf(Stack<token> tokenStack){

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
