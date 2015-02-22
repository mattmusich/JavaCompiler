package sample;


public class token {
// EW JUST EW WHY DID I DO THIS
//    // Declare all of the REGEX typing with the enums
//    public static enum TokenType {
//
//        //keywords
//        BOOL("boolean"),
//        FALSE("false"),
//        IF("if"),
//        INT("int"),
//        PRINT("print"),
//        STRING("string"),
//        TRUE("true"),
//        WHILE("while"),
//
//        //variable
//        VAR("[a-z]"),
//
//        //Numbers
//        NUMBER("[0-9]"),
//
//        //Operations
//        LBRACK("{"),
//        RBRACK("}"),
//        LPAREN("("),
//        RPAREN(")"),
//        EQUALS("="),
//        DUBEQUALS("=="),
//        NOTEQUALS("!="),
//        PLUS("+"),
//
//        WHITESPACE("[ \t\f\r\n]+"),
//        SYMBOL("[{|}|(|)");
//
//
//        public final String pattern;
//
//        private TokenType(String pattern) {
//            this.pattern = pattern;
//        }
//
//    };

    public String type;
    public String data;

    //CONSTRUCTOR
    public token (String type, String data){
        this.type = type;
        this.data = data;
    }


    //not needed for now
    public void setTokenType(String x) {
        type = x;
    }

    public void setTokenData(String x){
        data = x;
    }




    public String getTokenType() {
        return type;
    }

    public String getTokenData() {
        return data;
    }


    public String getToken(){
        return "(" + getTokenType()  + "," + getTokenData() + ")";
    }
}
