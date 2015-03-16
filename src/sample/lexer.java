package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.*;
import java.util.regex.*;
import java.lang.Enum;

//does all the lex to token operations
public class lexer {

    @FXML
    private TextArea taOutput;

    // This will loop through input to determine tokens
    // Im stupid and didn't divide up the functions and got carried away.
    public ArrayList<Object> lexercise(String baseString){
//ArrayList<token>

        //Keyword arraylist building
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

        //Defined Strings
        String remainingText = baseString;
        String tokenText = "";
        String errorText = "";
        String tokenString = "";
        String builtString = "";

        for(int i=0; i < remainingText.length(); i++) {

            // STEP 1 Symbol checking is first
            if (Character.toString(baseString.charAt(i)).matches("[{|}|(|)|\"|=|!|+]")) {
                switch (baseString.charAt(i)) {
                    case '{': tokens.add(new token("LBRACK","{")); break;
                    case '}': tokens.add(new token("RBRACK","}")); break;
                    case '(': tokens.add(new token("LPAREN","(")); break;
                    case ')': tokens.add(new token("RPAREN",")")); break;
                    case '"': tokens.add(new token("QUOTE","\""));
                                System.out.println("Quote found");
                                //check to make sure the next char is not a "
                                if (Character.toString(baseString.charAt(i+1)).matches("\"")){
                                    tokens.add(new token("CHAR", ""));
                                    tokens.add(new token("QUOTE", "\""));
                                    System.out.println("Quote empty");
                                    i++;
                                } else {
                                    System.out.println("Quote else hit these quotes have stuff in them");
                                    //Loop through a forward set that adds to i and keep going till the char is "
                                    for (int adder = 1; adder <= baseString.length(); adder++ ){
                                        System.out.println("in quote loop");
                                        if(Character.toString(baseString.charAt(i + adder)).matches("\"")) {
                                            //tokens.add(new token("STRING", builtString));  STRINGS??
                                            tokens.add(new token("QUOTE","\""));
                                            i = i + adder;
                                            System.out.println("String made " + builtString);
                                            adder = baseString.length()+1;
                                        } else {
                                            System.out.println("Character added to quote: " + Character.toString(baseString.charAt(i + adder)) +"@");
                                            if(Character.toString(baseString.charAt(i + adder)).matches(" ")){
                                                tokens.add(new token("CHAR", " "));
                                            } else {
                                                if (!Character.toString(baseString.charAt(i + adder)).matches("[A-Z]")) {
                                                    if (!Character.toString(baseString.charAt(i + adder)).matches("[0-9]")) {
                                                        if (!Character.toString(baseString.charAt(i + adder)).matches("\\n")) {
                                                            tokens.add(new token("CHAR", Character.toString(baseString.charAt(i + adder))));
                                                        } else {
                                                            errorText += "ERROR: String Characters cannot be newline characters, please remove all newlines from the strings.\n\n";
                                                        }
                                                    } else {
                                                        errorText += "ERROR: String Characters cannot be Numbers, please remove all numbers from the string.\n\n";
                                                    }
                                                } else {
                                                    errorText += "ERROR: String Characters cannot be uppercase, please change all upper case letters to lower case within the string.\n\n";
                                                }
                                            }
                                            //builtString += Character.toString(baseString.charAt(i + adder)); STRINGS??
                                        }
                                    }
                                    builtString = "";
                                }

                              break;
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
                System.out.println("SYM TOKEN MADE: " + tokenText);

            // STEP 2 Digit checking
            } else if (Character.toString(baseString.charAt(i)).matches("[0-9]")) {
               //TODO CHECK DOUBLE DIGITS
                if (!Character.toString(baseString.charAt(i-1)).matches("[0-9]")) {
                    tokens.add(new token("DIGIT", Character.toString(baseString.charAt(i))));
                    System.out.println("DIGIT TOKEN MADE: " + tokenText);
                } else {
                    errorText += "ERROR: Numbers cannot have double digits or more, please make all instances single Digits.\n\n";
                }



            //STEP 3 Alpha checking
            } else if (Character.toString(baseString.charAt(i)).matches("[a-z]")) {

                //check to see if the char is the last of the string in it is make a new token for the "i" character
                if ( i+1 < baseString.length()) {
                    //if the next character is not a letter it makes a token for "i", otherwise it continues the keyword check
                    if (Character.toString(baseString.charAt(i+1)).matches("[a-z]")) {

                        //This loop is an incrementer for lookahead, check the systemlog for how it kinda works
                        for (int j = 1; j <= 6; j++) {
                           if (i + j < baseString.length()) {

                               //only adds the "i" position character on the first time the lookahead is active
                               if (j == 1 ){
                                   tokenText += Character.toString(baseString.charAt(i));
                               }

                               //adds each value at "j" of the lookahead
                               tokenText += Character.toString(baseString.charAt(i + j));
                               System.out.println("lookahead: " +tokenText);


                               //-checks if the tokenText is directly equal to the kewords list
                               //-if it is it resets the lookahead
                               //-if it is not it checks if it is the last lookahead increment (6) and creates a token for "i"
                               //    and resets the tokenText for the next lookahead loop at i++
                               if (keywords.contains(tokenText)) {
                                   tokens.add(new token("KEYWORD", tokenText));
                                   System.out.println("KW TOKEN MADE: " + tokenText);
                                   i += j;
                                   j = 8;
                                   tokenText = "";

                               } else {
                                    if(j == 6){
                                        System.out.println("ID TOKEN MADE: " + Character.toString(baseString.charAt(i)));
                                        tokenText="";
                                        tokens.add(new token("ID",Character.toString(baseString.charAt(i))));
                                    }

                                    //checks my current increment positions( really for debugging)
                                   //System.out.println("i: " + i);
                                   //System.out.println("j: " + j);
                               }
                           }
                       }
                    } else {
                        //if character after i is not a letter
                        //TODO ADD A DEFENSE AGAINST CAPS
                        if (!Character.toString(baseString.charAt(i)).matches("[A-Z]")){
                            tokens.add(new token("ID", Character.toString(baseString.charAt(i))));
                        } else {
                            errorText += "ERROR: Characters cannot be uppercase, please change all upper case letters to lower case.\n\n";
                        }

                    }
                } else {
                    //if no character after i
                    //TODO ADD A DEFENSE AGAINST CAPS
                    if (!Character.toString(baseString.charAt(i)).matches("[A-Z]")) {
                        tokens.add(new token("ID", Character.toString(baseString.charAt(i))));
                    } else {
                        errorText += "ERROR: Characters cannot be uppercase, please change all upper case letters to lower case.\n\n";
                    }


                }
                //if the $ is found it is the end of the file
            } else if (Character.toString(baseString.charAt(i)).matches("[A-Z]")) {
                errorText += "ERROR: Characters cannot be uppercase, please change all upper case letters to lower case.\n\n";
            } else if (Character.toString(baseString.charAt(i)).matches("$")){
                tokens.add(new token("EOF", "$"));
                i = remainingText.length();
            }

        }

        //output management, loops through all tokens in the Arraylist and sets them to tokenString
        if (!tokens.isEmpty()) {
            for (int x=0; x < tokens.size(); x++)
            tokenString += tokens.get(x).getToken();
        }


        //TODO check the null pointer
        System.out.println(errorText);
        ArrayList<Object> sends = new ArrayList<Object>();
        sends.add(0, tokens);
        sends.add(1, errorText);
        return sends;
    }


}
