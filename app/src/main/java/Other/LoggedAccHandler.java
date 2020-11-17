package Other;

public class LoggedAccHandler {
    static String loggedUserName;

    public LoggedAccHandler(){

    }

    public LoggedAccHandler(String loggedUserName){
        this.loggedUserName = loggedUserName;
    }

    public String getLoggedUserName() {
        return loggedUserName;
    }

    public void setLoggedUserName(String loggedUserName) {
        this.loggedUserName = loggedUserName;
    }
}
