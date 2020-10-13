package Other;



public class AutoData{

    private String carBrand;
    private String carModel;
    private String carPowerHP;

    public AutoData(String carBrand, String carModel, String carPowerHP) {
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.carPowerHP = carPowerHP;
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

    public String getCarPowerHP() {
        return carPowerHP;
    }

    public void setCarPowerHP(String carPowerHP) {
        this.carPowerHP = carPowerHP;
    }
}
