package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class MainController {
    @FXML
    private TextArea taInput;
    @FXML
    private TextArea taOutput;

    @FXML
    private void onLexButtonPress(ActionEvent event) {
        // Button was clicked, do something...

        String baseString = taInput.getText().trim();
        String lexedString = lexer.lexercise(baseString + "$");
        taOutput.appendText(lexedString + "\n");

        /*
        String noSpaces = "";
        int count = 0;

        for(int i=0; i < baseString.length(); i++){
            if (baseString.charAt(i) == ' '){
                count++;
            } else {
                noSpaces += baseString.charAt(i);
            }
        }


        token test1 = new token("","");
        token test2 = new token("Expr","=");

        test1.setTokenType("Type");
        test1.setTokenData("Data");

        taOutput.appendText(noSpaces + "\n" + count + "\n");
        taOutput.appendText(test1.getToken() + "\n");
        taOutput.appendText(test2.getToken() + "\n");
        */



    }
}