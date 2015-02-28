package sample;


public class token {

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
