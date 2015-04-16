package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class MainController {
    @FXML
    private TextArea taInput;
    @FXML
    public TextArea taOutput;
    @FXML
    private TextArea taTokens;
    public boolean isVerboseOn;


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
            //define the arraylist to send more than 1 thing back
            ArrayList<Object> parseSend = parser.initParse(lexerTokens);

            //print the errors if any are sent back
            String parsedString = parseSend.get(0).toString();
            taOutput.appendText("Parse Status: \n" + parsedString + "\n" );

            if (parsedString.equals("")) {
                taOutput.appendText("Parse Complete, No errors\n\n\n"  );

                //print cst from parse
                tree cst = new tree();

                cst = (tree) parseSend.get(1);
                taOutput.appendText("CST\n" + cst.toString() + "\n");

                String parseLog = (String) parseSend.get(2);

                //convert the cst to ast and print
                CstToAst test = new CstToAst();
                tree ast = new tree();
                ast.addBranchNode("root", "branch");//
                ArrayList<Object> sendConvert = test.convert(cst);
                ast = (tree) sendConvert.get(0);
                //see if errors
                String astErrors = (String) sendConvert.get(2);
                String astLog = (String) sendConvert.get(3);
                Hashtable hashChecks = (Hashtable) sendConvert.get(4);

                Set<String> keys = hashChecks.keySet();
                for (String key : keys) {
                    if (hashChecks.get(key) == "") {
                        System.out.println("WARN: Declared Variable: " + key + " was unused in program");
                        taOutput.appendText("WARN: Declared Variable: " + key + " was unused in program\n");
                    } else {
                        System.out.println("All Variables are used");
                    }
                }

                if (astErrors == "") {
                    taOutput.appendText("\nAST\n" + ast.toString() + "\n" + "\n");
                    taOutput.appendText("SCOPE TABLE\n" + (String) sendConvert.get(1) + "\n" + "\n");
                } else {
                    taOutput.appendText("AST ERRORS:\n" + astErrors + "\n");
                }


                if (isVerboseOn) {
                    taOutput.appendText("VERBOSE LOG: " + "\n" + parseLog + "\n" + astLog + "\n");
                }
            }

        }



        taTokens.appendText("Lexer Tokens: \n" + lexedString + "\n");


    }




    @FXML
    private void onCase1ButtonPress(ActionEvent event){
        taInput.appendText("{intaa=1\n" +
                "print(a)stringb  b = \"hey alan\"\n" +
                "if(b == \"hey alan\"){\n" +
                "print(b)\n" +
                "}}$");
    }


    @FXML
    private void onVerboseButtonToggle(ActionEvent event) {
        isVerboseOn = !isVerboseOn;
        if(isVerboseOn == true){
            taOutput.appendText("VERBOSE MODE ON");
        } else {
            taOutput.appendText("VERBOSE MODE OFF");
        }

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