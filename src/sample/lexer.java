package sample;


import java.util.*;
import java.util.regex.*;
import java.lang.Enum;


public class lexer {



    // This will loop through input to determine tokens
    public static String lexercise(String baseString){

        ArrayList<token> tokens = new ArrayList<token>();
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("boolean");
        keywords.add("false");
        keywords.add("if");
        keywords.add("int");
        keywords.add("print");
        keywords.add("string");
        keywords.add("true");
        keywords.add("while");


        // MY Strings
        String pastText = "";
        String remainingText = baseString;
        String currentText = "";
        String tokenText = "";
        String errorText = "Errors: ";
        String tokenString = "";
        int indexPlace = 0;

        for(int i=0; i < remainingText.length(); i++) {
            //Symbol checking is all done
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
                //To EZ
                    tokens.add(new token("DIGIT",Character.toString(baseString.charAt(i))));

            } else if (Character.toString(baseString.charAt(i)).matches("[a-z]")) {
                //adds the first char to the tokenText
                tokenText += Character.toString(baseString.charAt(i));

                if ( i+1 < baseString.length()) {
                    if (Character.toString(baseString.charAt(i+1)).matches("[a-z]")) {

                        //design some kind of loop to check all of the possibilities
                            tokenText += Character.toString(baseString.charAt(i+1));
                            if (keywords.contains(tokenText)){
                                tokens.add(new token("KEYWORD", tokenText ));

                            }

                    } else {
                        tokens.add(new token("ID", Character.toString(baseString.charAt(i))));
                    }
                } else {
                    tokens.add(new token("ID", Character.toString(baseString.charAt(i))));
                }




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
            tokenString += tokens.get(x).getToken();
        }

        String lexedString = errorText + "\n" + tokenString;
        return lexedString;
    }


}
