package Other;

public class DragRaceSaveTimesHandler {
    private String userName;
    private String carData;
    private String from0to100;
    private String from0to200;
    private String from100to200;

    public DragRaceSaveTimesHandler(){

    }

    public DragRaceSaveTimesHandler(String userName, String carData, String from0to100, String from0to200, String from100to200) {
        this.userName = userName;
        this.carData = carData;
        this.from0to100 = from0to100;
        this.from0to200 = from0to200;
        this.from100to200 = from100to200;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCarData() {
        return carData;
    }

    public void setCarData(String carData) {
        this.carData = carData;
    }

    public String getFrom0to100() {
        return from0to100;
    }

    public void setFrom0to100(String from0to100) {
        this.from0to100 = from0to100;
    }

    public String getFrom0to200() {
        return from0to200;
    }

    public void setFrom0to200(String from0to200) {
        this.from0to200 = from0to200;
    }

    public String getFrom100to200() {
        return from100to200;
    }

    public void setFrom100to200(String from100to200) {
        this.from100to200 = from100to200;
    }
}
