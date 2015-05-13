package sample;

import java.util.ArrayList;

public class CodeGen {

    String logString = ""; //for verbose output, builds from addLog() which will sout and append to this string
    int scope = 0;
    int pos;
    int backPos = 255;
    String[] hexTable;
    ArrayList<TempRow> tempTable;
    ArrayList<JumpRow> jumpTable;
    int currentTemp = 0;
    int currentJump = 0;
    int addressCounter = 0;


    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_RESET = "\u001B[0m";


    //"Main for process"
    //send in a Tree(AST)
    public String generate(tree ast){
        String hexDump = "";
        hexTable = new String[256];
        scope = 0;
        pos = 0;
        /* Might need to make an object to store all of this stuff*/
        tempTable = new ArrayList<TempRow>();
        jumpTable = new ArrayList<JumpRow>();
        tree targetTree = ast;
        hexDump = preScan(targetTree);

        addLog(hexDump);
        return hexDump;
    }

    public String preScan(tree ast){
        //the final array that comes from scan
        String[] hexArray;
        //call to recursive scan to hexArray output
        addLog(ANSI_GREEN +"\nSTARTING CODE GEN\n" + ANSI_RESET);
        /*Temp Code Gen*/
        hexArray = scan(ast.root);

        /*JUMP CHECK*/

        /*BACK PATCH*/

        //converts array to the pairs of hex in a string
        StringBuilder builder = new StringBuilder();
        for (String string : hexArray) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(string);
        }
        String hexDump = "";
        hexDump = builder.toString();

        for(TempRow t : tempTable)
            addLog(t.dumpText());
        return hexDump;
    }

    //Send It All 3 tables and the root of the ast
    public String[] scan(treeNode head){
        //parse from depth in order and when you hit a KeyName call its function to parse
        //return a String[] that can be added on

        addLog(ANSI_CYAN+"scan.CurrentNode: "+head.nodeName+ANSI_RESET);

        //this should allow it to stop
        if (head.nodeChildren == null || head.nodeChildren.size() == 0) {
            addLog("scan.HitLeaf: " + head.nodeName);

            return hexTable;
        } else {


            //some should endchildren, while others should go lower
            String nodeName = head.nodeName;
            Specials special = Specials.valueOf(nodeName.toUpperCase());
            switch (special) {
                case BLOCK:
                    scope++;
                    break;
                case VARDECL:
                    readVarDecl(head);
                    break;
                case ASSIGNMENT:
                    readAssignment(head);
                    break;
                case PRINT:
                    readPrint(head);
                    break;
                case COMPEQ:
                    //readCompEQ();
                    break;
                case COMPNOTEQ:
                    //readCompNotEQ();
                    break;
                case WHILE:
                    readWhile();
                    break;
                case IF:
                    readIf(head);
                    break;
            }

            for (int i = 0; i < head.nodeChildren.size(); i++) {
                scan(head.nodeChildren.get(i));
            }

        }

        hexTable[pos] = "00";

        return hexTable;
    }


    /*@@@@@@@@@@@@@@@ KEY FUNCTIONS @@@@@@@@@@@@@@@*/
    //Each KeyValue to read and process
    public void readVarDecl(treeNode head){
        addLog("readVarDecl.");
        String type = head.nodeChildren.get(0).nodeName;
        String var = head.nodeChildren.get(1).nodeName;

        if(type.equals("int")) {
            loadAccConst("00", "int");
            storeAcc(var);
        }

    }

    public void readAssignment(treeNode head){

        String var = head.nodeChildren.get(0).nodeName;
        String value = head.nodeChildren.get(1).nodeName;
        addLog("readAssignment: " + var + ":" +value);
        //check for bool, if not check for var
        if(value.equals("true")||value.equals("false")) {
            value = intToHexString(Integer.parseInt(value));
            loadAccConst(value, "bool");
            storeAcc(var);
        } else if (Character.toString(value.charAt(0)).matches("[a-z]")){
            loadAccMem(value, "var");
            storeAcc(var);
        }

        //check for int
        if(Character.toString(value.charAt(0)).matches("[0-9]")){
            value = intToHexString(Integer.parseInt(value));
            loadAccConst(value,"int");
            storeAcc(var);
        }

        //check for string
        if(value.charAt(0) == '"'){
            value = intToHexString(Integer.parseInt(value));
            loadAccConst(value,"string");
            storeAcc(var);
        }


    }

    public void readPrint(treeNode head){
        String value = head.nodeChildren.get(0).nodeName;

        if(value.equals("true")||value.equals("false")) {


        } else if (Character.toString(value.charAt(0)).matches("[a-z]")){
            loadYmem(value);
            loadXcon("01","var");
        }

        //check for int
        if(Character.toString(value.charAt(0)).matches("[0-9]")){
            value = intToHexString(Integer.parseInt(value));

        }

        //check for string
        if(value.charAt(0) == '"'){
            value = intToHexString(Integer.parseInt(value));


        }

    }

    public void readIf(treeNode head){
        // true ==   false !=
        if(head.nodeChildren.get(0).nodeName.equals("CompEQ")){
            treeNode current = head.nodeChildren.get(0);
            String left = current.nodeChildren.get(0).nodeName;
            String right = current.nodeChildren.get(1).nodeName;

            if(left.equals("true")||left.equals("false")) {


            } else if (Character.toString(left.charAt(0)).matches("[a-z]")){
                loadXmem(left);
                compare(right);
                branchN();
            }

            //check for int
            if(Character.toString(left.charAt(0)).matches("[0-9]")){

            }

            //check for string
            if(left.charAt(0) == '"'){


            }

        } else{
            treeNode current = head.nodeChildren.get(0);
            String left = current.nodeChildren.get(0).nodeName;
            String right = current.nodeChildren.get(1).nodeName;
            if(left.equals("true")||left.equals("false")) {


            } else if (Character.toString(left.charAt(0)).matches("[a-z]")){
                loadYmem(left);
                loadXcon("01","var");
            }

            //check for int
            if(Character.toString(left.charAt(0)).matches("[0-9]")){
                left = intToHexString(Integer.parseInt(left));

            }

            //check for string
            if(left.charAt(0) == '"'){
                left = intToHexString(Integer.parseInt(left));


            }
        }

    }

    public void readWhile(){

    }

    public void readCompEQ(){

    }

    public void readCompNotEQ(){

    }



    /*@@@@@@@@@@@@@OP CODE functions@@@@@@@@@@@@@@*/
    //called within each read____()
    public void loadAccConst(String loadVal, String type){
        if(type.equals("int")) {
            addLog("loadAccConst.int");
            hexTable[pos] = "A9";
            pos++;
            hexTable[pos] = loadVal;
            pos++;
        }



    }

    public void loadAccMem(String loadVal,String type){
        if(type.equals("var")) {
            addLog("loadAccConst.var");
            hexTable[pos] = "AD";
            pos++;

            if(isInSameTempScope(loadVal)){
                addLog("loadAccConst.VarIsSame: " + loadVal);
                hexTable[pos] = getTempName(loadVal);
            } else {
                hexTable[pos] = "T" + Integer.toString(currentTemp);
                addTempRow(loadVal);
                currentTemp++;
            }
            pos++;
            hexTable[pos] = "XX";
            pos++;
        }

    }

    public void storeAcc(String var){
        addLog("storeAcc.");
        hexTable[pos] = "8D";
        pos++;

        if(isInSameTempScope(var)){
            addLog("storeAcc.VarIsSame: " + var);
            hexTable[pos] = getTempName(var);
        } else {
            hexTable[pos] = "T" + Integer.toString(currentTemp);
            addTempRow(var);
            currentTemp++;
        }
        pos++;
        hexTable[pos] = "XX";
        pos++;
    }

    public void addWithCarry(){

    }

    public void loadXcon(String loadVal, String type){
        if(type.equals("var")) {
            addLog("loadAccConst.int");
            hexTable[pos] = "A2";
            pos++;
            hexTable[pos] = loadVal;
            pos++;
            hexTable[pos] = "FF";
            pos++;
        }
    }

    public void loadXmem(String value){
        addLog("loadXmem: "+ value);
        hexTable[pos] = "AE";
        pos++;
        if(isInSameTempScope(value)){
            addLog("storeAcc.VarIsSame: " + value);
            hexTable[pos] = getTempName(value);
        } else {
            hexTable[pos] = "T" + Integer.toString(currentTemp);
            addTempRow(value);
            currentTemp++;
        }
        pos++;
        hexTable[pos] = "XX";
        pos++;
    }

    public void loadYcon(){

    }

    public void loadYmem(String value){
        addLog("loadYmem: "+ value);
        hexTable[pos] = "AC";
        pos++;
        if(isInSameTempScope(value)){  //TODO NEED TO FIX SCOPE ISSUE.  Look at notebook
            addLog("storeAcc.VarIsSame: " + value);
            hexTable[pos] = getTempName(value);
        } else {
            hexTable[pos] = "T" + Integer.toString(currentTemp);   //TODO REFER TO NOTES ABOUT IMPOSSIBLE USEAGE HERE
            addTempRow(value);
            currentTemp++;
        }
        pos++;
        hexTable[pos] = "XX";
        pos++;
    }

    public void noOP(){

    }

    public void breakCode(){

    }

    public void compare(String value){
        addLog("compare: "+ value);
        hexTable[pos] = "EC";
        pos++;
        if(isInSameTempScope(value)){
            addLog("storeAcc.VarIsSame: " + value);
            hexTable[pos] = getTempName(value);
        } else {
            hexTable[pos] = "T" + Integer.toString(currentTemp);
            addTempRow(value);
            currentTemp++;
        }
        pos++;
        hexTable[pos] = "XX";
        pos++;
    }

    public void branchN(){
        addLog("branchN: ");
        hexTable[pos] = "D0";
        pos++;
        String val = "J" + currentJump;
        addJumpRow(val);
        hexTable[pos] = val;
        pos++;
    }

    public void increment(){

    }

    public void syscall(){

    }


    public void addTempRow(String var){
        addLog("addTempRow: " + var);
        tempTable.add(new TempRow(hexTable[pos],scope,var,addressCounter));
        addressCounter++;
    }

    public void addJumpRow(String var){
        addLog("addJumpRow" + var);
        jumpTable.add(new JumpRow(var,0));
        currentJump++;
    }

    public enum Specials {
        BLOCK,
        VARDECL,
        ASSIGNMENT,
        PRINT,
        COMPEQ,
        COMPNOTEQ,
        WHILE,
        IF
    }


    //small helper stuff
    public String intToHexString(int n) {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toHexString(n));
        if (sb.length() < 2) {
            sb.insert(0, '0'); // pad with leading zero if needed
        }
        String hex = sb.toString();
        return hex;
    }

    public void addLog(String data){
        logString += data+ "\n";
        System.out.println(data);
    }

    public boolean isInSameTempScope(String var){  //TODO NEED TO FIX SCOPE ISSUE.  Look at notebook
        addLog("isInSameTempScope.Scope: " + scope);
        addLog("isInSameTempScope.Var: " + var);
        for (TempRow t : tempTable){
            if (t.var.equals(var) && t.scope == scope){
                return true;
            }
        }
        return false;
    }

    public String getTempName(String var){
        for (TempRow t : tempTable){
            if (t.var.equals(var) && t.scope == scope) {
                return t.name;
            }
        }

        return "ZZ";
    }



}
