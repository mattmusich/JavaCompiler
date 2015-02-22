package sample;


import java.util.*;
import java.util.regex.*;
import java.lang.Enum;


public class lexer {



    // This will loop through input to determine tokens
    public static String lexercise(String baseString){

        ArrayList<token> tokens = new ArrayList<token>();

        // MY Strings
        String pastText = "";
        String remainingText = baseString;
        String currentText = "";
        String tokenText = "";
        String errorText = "Errors: ";
        String tokenString = "";
        int forward = 1;

        for(int i=0; i < remainingText.length(); i++) {
            if (Character.toString(baseString.charAt(i)).matches("[{|}|(|)|\"|=|!|+]")) {
                switch (baseString.charAt(i)) {
                    case '{': tokens.add(new token("LBRACK","{")); break;
                    case '}': tokens.add(new token("RBRACK","}")); break;
                    case '(': tokens.add(new token("LPAREN","(")); break;
                    case ')': tokens.add(new token("RPAREN",")")); break;
                    case '"': tokens.add(new token("QUOTE","")); break;
                    case '+': tokens.add(new token("PLUS","+")); break;
                    case '=':
                        //if the index is less than the length of the string then check, if not then its just an =
                            if ( i+1 < baseString.length()) {
                                if (Character.toString(baseString.charAt(i+1)).matches("=")) {
                                    tokens.add(new token("DUBEQUALS", "=="));
                                    i++;
                                } else {
                                    tokens.add(new token("EQUALS", "="));
                                }
                            } else {
                                tokens.add(new token("EQUALS", "="));
                            }
                                break;
                    case '!':
                    // /if the index is less than the length of the string then check, if not then its an Error
                                if ( i+1 < baseString.length()) {
                                    if (Character.toString(baseString.charAt(i+1)).matches("=")) {
                                        tokens.add(new token("NOTEQUALS", "!="));
                                        i++;
                                    } else {}
                                }
                                break;

                }

            } else if (Character.toString(baseString.charAt(i)).matches("[0-9]")) {

            } else if (Character.toString(baseString.charAt(i)).matches("[a-z]")) {

            }
        }



/*
        for(int i=0; i < remainingText.length(); i++) {
            if (Character.toString(baseString.charAt(i)).matches("b")) {
                Pattern p = Pattern.compile("boolean");
                Matcher m = p.matcher(remainingText);
                if (m.find()) {
                    tokens.add(new token("type", "boolean"));
                } else {}
            }
        }
*/





        //output management
        if (!tokens.isEmpty()) {
            for (int x=0; x < tokens.size(); x++)
            tokenString = tokens.get(x).getToken();
        }

        String lexedString = errorText + "\n" + tokenString;
        return lexedString;
    }


}
