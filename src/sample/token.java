package sample;


public class token {

    private String tokenType;
    private String tokenData;

    public token (String type, String data){
        setTokenType(type);
        setTokenData(data);
    }

    public void setTokenType(String x) {
        tokenType = x;
    }

    public void setTokenData(String x){
        tokenData = x;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getTokenData() {
        return tokenData;
    }

    public String getToken(){
        return "(" + getTokenType()  + "," + getTokenData() + ")";
    }
}
