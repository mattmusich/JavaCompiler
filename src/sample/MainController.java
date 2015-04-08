package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import java.util.ArrayList;

public class MainController {
    @FXML
    private TextArea taInput;
    @FXML
    public TextArea taOutput;
    @FXML
    private TextArea taTokens;


    //Starts the lexing when pressed, and will run the parsing too
    @FXML
    private void onLexButtonPress(ActionEvent event) {
        // Button was clicked, do something...

        taOutput.clear();
        taTokens.clear();

        String baseString = taInput.getText().trim();
        baseString = baseString.replaceAll("[\\u201C\\u201D\\u201E\\u201F\\u2033\\u2036]", "\"");

        //the error wording is horrid
        if (!baseString.endsWith("$")){
            baseString += "$";
            taOutput.appendText("WARNING: Forgot End of Program \"$\" character, or code is included past the End of Line character.\n It has been added at the end of your input.\n All text after has been ignored by the Parser\n\n");
        }

        lexer currentLexer = new lexer();

        //set as object array to send 2 things back...
        ArrayList<Object> lexerReply = currentLexer.lexercise(baseString);

        //pull out the tokens out of the array lexerReply
        ArrayList<token> lexerTokens = (ArrayList<token>) lexerReply.get(0);

        //pull out the string from the array lexerReply and print it.
        String lexErrors = (String) lexerReply.get(1);
        if (!lexErrors.equals("")) {
            taOutput.appendText(lexErrors + "\nThe Parse will not continue, please fix all errors before continuing.\nEND ERRORS\n\n");
        }

        //does this before parsing cause why not.
        String lexedString = tokenToSting(lexerTokens);

        System.out.println(".." + lexErrors + "..");
        if (lexErrors.equals("")) {
            String parsedString = parser.initParse(lexerTokens);
            taOutput.appendText("Parse Status: \n" + parsedString + "\n" );
        }

        taTokens.appendText("Lexer Tokens: \n" + lexedString + "\n");

        //TODO All of this VV
        //we need to make the CST at this point

        //Add a call to copy of parse that makes the tree

        //@return a tree

        //build hash scope of tree?

        //print the sent tree in the taOutput area (When this is all done, make nice pretty gui one

        //from here check patterns on the cst?





    }

    @FXML
    private void onCase1ButtonPress(ActionEvent event){
        taInput.appendText("{intaa=1\n" +
                "print(a)stringb  b = \"hey alan\"\n" +
                "if(b == \"hey alan\"){\n" +
                "print(b)\n" +
                "}}");
    }

    //meant for output of the tokens that get send
    public String tokenToSting(ArrayList<token> sentTokens) {

        String tokenString = "";
        ArrayList<token> tokens = sentTokens;

        if (!tokens.isEmpty()) {
            for (int x=0; x < tokens.size(); x++)
                tokenString += tokens.get(x).getToken() + "\n";
        }
        return tokenString;
    }


}