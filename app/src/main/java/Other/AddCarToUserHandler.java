package Other;

public class AddCarToUserHandler {
    String userName;
    String carBrand;
    String carModel;
    String carHP;

    public AddCarToUserHandler(){

    }

    public AddCarToUserHandler(String userName, String carBrand, String carModel, String carHP) {
        this.userName = userName;
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.carHP = carHP;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarHP() {
        return carHP;
    }

    public void setCarHP(String carHP) {
        this.carHP = carHP;
    }
}
