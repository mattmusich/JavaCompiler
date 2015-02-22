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
            if (Character.toString(baseString.charAt(i)).matches("[a-z]") && Character.toString(baseString.charAt(i+1)).matches("[a-z]")) {
                switch ((baseString.charAt(i))) {
                    case 'b': if (Character.toString(baseString.charAt(i+1)).matches("o")){
                                if (Character.toString(baseString.charAt(i+1)).matches("o")){
                                    if (Character.toString(baseString.charAt(i+1)).matches("l")){
                                        if (Character.toString(baseString.charAt(i+1)).matches("e")){
                                            if (Character.toString(baseString.charAt(i+1)).matches("a")){
                                                if (Character.toString(baseString.charAt(i+1)).matches("n")){
                                                    tokens.add(new token("type","boolean"));
                                                 }
                                            }
                                        }
                                    }
                                }
                              } else { errorText += "Error at position: " + String.valueOf(i) + " Nonvaild Character";}
                            break;
                    case 'f':
                            break;
                    case 'i':
                            break;
                    case 'p':
                            break;
                    case 's':
                            break;
                    case 't':
                            break;
                    case 'w':
                            break;
                    default:
                            break;

                }

            } else if(Character.toString(baseString.charAt(i)).matches("[a-z]")) {

            } else if (Character.toString(baseString.charAt(i)).matches("[0-9]")) {

            } else if (Character.toString(baseString.charAt(i)).matches("[{|}|(|)|\"|=|==|!=]")) {

            }

        }




/*
        for(int i=0; i < remainingText.length(); i++) {
            if (Character.toString(baseString.charAt(i)).matches("b")) {
                Pattern p = Pattern.compile("boolean");
                Matcher m = p.matcher(remainingText);
                if (m.find()) {
                    tokens.add(new token("type", "boolean"));
                } else {

                }
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
