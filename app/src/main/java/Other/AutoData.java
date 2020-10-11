package Other;


import java.io.Serializable;

public class AutoData implements Serializable {

    private String carBrand;
    private String carModel;
    private Integer carPowerHP;

    public AutoData(String carBrand, String carModel, Integer carPowerHP) {
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

    public Integer getCarPowerHP() {
        return carPowerHP;
    }

    public void setCarPowerHP(Integer carPowerHP) {
        this.carPowerHP = carPowerHP;
    }
}
