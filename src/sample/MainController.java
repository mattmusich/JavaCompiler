package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Clipboard;
import javafx.scene.text.Font;

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

        String codeGenLog="";
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

            //if no errors in parse, then continue
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
                //tree scopeTable = (tree) sendConvert.get(5);

                //checks all of the ids in a duplicate hash table and will see if they were used based on the keys
                Set<String> keys = hashChecks.keySet();
                for (String key : keys) {
                    if (hashChecks.get(key) == "") {
                        System.out.println("WARN:\n Declared Variable: " + key + " was unassigned in program");
                        taOutput.appendText("WARN:\n Declared Variable: " + key + " was unassigned in program\n");
                    } else {
                        System.out.println("All Variables are used");
                    }
                }

                //checks if there are any CstToAst errors, if not then prints ast and scope table
                if (astErrors == "") {
                    taOutput.appendText("\nAST\n" + ast.toString() + "\n" + "\n");
                    taOutput.appendText("SCOPE TABLE\n" + (String) sendConvert.get(1) + "\n" + "\n");


                    /*
                    TODO REMINDER This is where to start with any new usage for Project 3
                    //You can utilize the ast from the Tree named ast
                    //build the 3 tables
                    //parse the ast
                    //write the hex for each situation
                    //backfill the var locations
                    //print it
                    */

                    //Run and print out results of CODEGEN

                    ArrayList<Object> sent = new CodeGen().generate(ast);

                    String hex = (String) sent.get(0);
                    codeGenLog = (String) sent.get(1);
                    taOutput.appendText("\nThe Hex Output has been already copied\nto your clipboard for your convience.\n");
                    taOutput.appendText("\nHEX OUTPUT\n");
                    taOutput.appendText(hex);
                    taOutput.appendText("\n\n");
                    final Clipboard clipboard = Clipboard.getSystemClipboard();
                    final ClipboardContent content = new ClipboardContent();
                    content.putString(hex);
                    clipboard.setContent(content);

                } else {
                    taOutput.appendText("AST ERRORS:\n" + astErrors + "\n");
                }

                //this turns on/off verbose mode, which is the output of the two addLog() in parser and CstToAst
                if (isVerboseOn) {
                    taOutput.appendText("VERBOSE LOG: " + "\n" + parseLog + "\n" + astLog + "\n" + codeGenLog + "\n");
                }
            }

        }

        //Outputs all tokens made, no errors matter for this
        taTokens.appendText("Lexer Tokens: \n" + lexedString + "\n");

    }

    //test case for convenience
    @FXML
    private void onCase1ButtonPress(ActionEvent event){
        taInput.appendText("{intaa=1\n" +
                "print(a)\na = 5 + a\n print(a)\nstringb  b = \"hey alan\"\n" +
                "if(a == 6){\n" +
                "print(b)\n" +
                "}}$");
    }



    //toggles verbose mode on and off and displays the state in taOutput
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