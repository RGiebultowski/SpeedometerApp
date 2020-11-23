package Other;

public class RaceTimesHandler {
    String date;
    String bestTime;
    String raceModeCar;
    String user;
    String track;
    String trackLength;
    String lapCounter;

    public RaceTimesHandler(){

    }

    public RaceTimesHandler(String bestTime, String raceModeCar, String user, String track, String trackLength, String date, String lapCounter) {
        this.bestTime = bestTime;
        this.raceModeCar = raceModeCar;
        this.user = user;
        this.track = track;
        this.trackLength = trackLength;
        this.date = date;
        this.lapCounter = lapCounter;
    }

    public String getLapCounter() {
        return lapCounter;
    }

    public void setLapCounter(String lapCounter) {
        this.lapCounter = lapCounter;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBestTime() {
        return bestTime;
    }

    public void setBestTime(String bestTime) {
        this.bestTime = bestTime;
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
