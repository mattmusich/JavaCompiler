package sample;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.*;
import java.lang.Enum;


public class lexer {

    public static enum TokenType


    public static String lexercise(String baseString){

        String newInput = baseString;





        for(int i=0; i < baseString.length(); i++){
            if (Character.toString(baseString.charAt(i)).matches("[a-z]") ) {
                if (Character.toString(baseString.charAt(i)).matches("b"))
                    tempLetters += Character.toString(baseString.charAt(i));



            } else {

            }
        }


        String lexedString = "";

        return lexedString;
    }


}
