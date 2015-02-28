package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class MainController {
    @FXML
    private TextArea taInput;
    @FXML
    private TextArea taOutput;
    @FXML
    private TextArea taTokens;


    //Starts the lexing when pressed, and will run the parsing too
    @FXML
    private void onLexButtonPress(ActionEvent event) {
        // Button was clicked, do something...

        String baseString = taInput.getText().trim();

        ArrayList<token> lexerTokens = lexer.lexercise(baseString + "$");

        String lexedString = tokenToSting(lexerTokens);

        String parsedString = parser.initParse(lexerTokens);

        taTokens.appendText("Lexer Tokens: \n" + lexedString + "\n");

        taOutput.appendText("Parse Status: \n" + parsedString + "\n");

    }

    @FXML
    private void onCase1ButtonPress(ActionEvent event){
        taInput.appendText("{intaa=1\n" +
                "print(a)stringb  b = \"Hey Alan\"\n" +
                "if(b == \"Hey Alan\"){\n" +
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