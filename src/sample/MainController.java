package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class MainController {
    @FXML
    private TextArea taInput;
    @FXML
    private TextArea taOutput;

    //Starts the lexing when pressed, and will run the parsing too
    @FXML
    private void onLexButtonPress(ActionEvent event) {
        // Button was clicked, do something...

        String baseString = taInput.getText().trim();

        ArrayList<token> lexerTokens = lexer.lexercise(baseString + "$");

        String lexedString = tokenToSting(lexerTokens);

        String parsedString = parser.initParse(lexerTokens);

        taOutput.appendText("Lexer Tokens: \n" + lexedString + "\n");

        taOutput.appendText("\nParse Status: \n" + parsedString + "\n");


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