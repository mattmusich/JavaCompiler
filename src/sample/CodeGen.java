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
        hexArray[pos] = "00";
        pos++;
        /*JUMP CHECK*/
        for (JumpRow j : jumpTable)
            addLog(j.dumpText());
        patchJump(hexArray);

        /*BACK PATCH*/
        addLog(ANSI_GREEN +"\nSTARTING BACK PATCH\n" + ANSI_RESET);
        for(TempRow t : tempTable)
            addLog(t.dumpText());
        backPatch(hexArray);


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


        return hexDump;
    }

    //Send It All 3 tables and the root of the ast
    public String[] scan(treeNode head){
        //parse from depth in order and when you hit a KeyName call its function to parse
        //return a String[] that can be added on

        addLog(ANSI_CYAN+"scan.CurrentNode: "+head.nodeName+ANSI_RESET);

        //this should allow it to stop
        if (head.nodeChildren == null || head.nodeChildren.size() == 0) {
            addLog(ANSI_PURPLE+"scan.HitLeaf: " + head.nodeName+ANSI_RESET);

            return hexTable;
        } else {


            //some should endchildren, while others should go lower
            String nodeName = head.nodeName;
            Specials special = Specials.valueOf(nodeName.toUpperCase());
            switch (special) {
                case BLOCK:
                    scope++;
                    readBlock(head);
                    scope--;
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
                    //START JUMP
                    int startJump = pos;
                    readIf(head);

                    /*SUPER IMPORTANT*/
                    readBlock(head);

                    int jumpEnd = pos - startJump -1;
                    String jump = getLastJump();
                    insertLastJump(jump,jumpEnd);

                    break;
                default:
                    break;
            }

            /*the block call and loops will call the block scope, so all scope changes happen properly*/
//            for (int i = 0; i < head.nodeChildren.size(); i++) {
//                scan(head.nodeChildren.get(i));
//            }

        }



        return hexTable;
    }


    /*@@@@@@@@@@@@@@@ KEY FUNCTIONS @@@@@@@@@@@@@@@*/
    //Each KeyValue to read and process

    public void readBlock(treeNode head){
        for (int i = 0; i < head.nodeChildren.size(); i++) {
            scan(head.nodeChildren.get(i));
        }
    }


    public void readVarDecl(treeNode head){
        addLog("readVarDecl.");
        String type = head.nodeChildren.get(0).nodeName;
        String var = head.nodeChildren.get(1).nodeName;

        if(type.equals("int")) {
            loadAccConst("00", "int");
            storeAcc(var);
        }
        if(type.equals("string")) {
            addTempRow(var);


        }
        if(type.equals("boolean")) {

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

        if(isScopedVar(value)){  //TODO NEED TO FIX SCOPE ISSUE.  Look at notebook
            addLog("storeAcc.VarIsSame: " + value);
            hexTable[pos] = getScopedName(value);
            pos++;
        }

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

    // for already declared vars
    public boolean isScopedVar(String var){
        int tempScope = scope;
        addLog("isScopedVar.Var: " + var);
        while (tempScope >=0) {
            for (TempRow t : tempTable) {
                addLog("isScopedVar.Scope:" +tempScope);
                addLog("isScopedVar.table:" +t.dumpText());
                if (t.var.equals(var) && t.scope == tempScope) {
                    return true;
                }
            }
            tempScope = tempScope -1;
        }
        return false;
    }

    //for already declared
    public String getScopedName(String var){
        int tempScope = scope;
        addLog("getScopedName.Var: " + var);
        while (tempScope >=0) {
            for (TempRow t : tempTable) {
                if (t.var.equals(var) && t.scope == tempScope) {
                    return t.name;
                }
            }
            tempScope = tempScope -1;
        }
        return "ZZ";
    }

    public String getLastJump(){
        return jumpTable.get(jumpTable.size()-1).name;
    }

    public void insertLastJump(String jump, int distance){
        for(JumpRow j : jumpTable){
            if(j.name.equals(jump)){
                j.distance = distance;
            }
        }
    }


    public String[] patchJump(String[] hextable){
        for (JumpRow j : jumpTable){
            String loc = j.name;
            String val = intToHexString(j.distance);
            for(int i = 0; i <= 255;i++) {
                if (hextable[i] == loc){
                    hextable[i] = val;
                }
            }
        }
        return hextable;
    }

    public String[] backPatch(String[] hextable){

        for (TempRow t : tempTable){

            hextable[pos] = "00";
            String loc = intToHexString(pos);
            addLog("Location: "+loc);
            String tempVal = t.name;
            for(int i = 0; i <= 255;i++) {
                if (hextable[i] == tempVal){
                    hextable[i] = loc;
                    hextable[i+1] = "00";
                }

            }
            pos++;
        }

        return hextable;
    }

}
