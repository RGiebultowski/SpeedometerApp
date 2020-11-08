package Other;

public class RaceTimesHandler {
    String[] raceModeTimes;
    String raceModeCar;
    String user;
    String track;
    String trackLength;

    public RaceTimesHandler(){

    }

    public RaceTimesHandler(String[] raceModeTimes, String raceModeCar, String user, String track, String trackLength) {
        this.raceModeTimes = raceModeTimes;
        this.raceModeCar = raceModeCar;
        this.user = user;
        this.track = track;
        this.trackLength = trackLength;
    }

    public String[] getRaceModeTimes() {
        return raceModeTimes;
    }

    public void setRaceModeTimes(String[] raceModeTimes) {
        this.raceModeTimes = raceModeTimes;
    }

    public String getRaceModeCar() {
        return raceModeCar;
    }

    public void setRaceModeCar(String raceModeCar) {
        this.raceModeCar = raceModeCar;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getTrackLength() {
        return trackLength;
    }

    public void setTrackLength(String trackLength) {
        this.trackLength = trackLength;
    }
}
