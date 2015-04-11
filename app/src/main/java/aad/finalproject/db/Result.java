package aad.finalproject.db;

/**
 * Created by Daniel on 3/28/2015.
 */
public class Result {

    long id;
    long resultsRaceId;
    long resultsBoatId;
    String raceName;
    String raceDate;
    double raceDistance;
    String boatName;
    String boatSailNum;
    String boatClass;
    int boatPHRF;
    String resultsDuration;
    String resultsAdjDuration;
    double resultsPenalty;
    String resultsNote;
    int resultsPlace;
    int resultsVisible = 1;
    String resultsCreateDate = DBAdapter.getDateTime();


    public long getResultsId() {
        return id;
    }

    public void setResultsId(long id) {
        this.id = id;
    }

    public long getResultsRaceId() {
        return resultsRaceId;
    }

    public void setResultsRaceId(long resultsRaceId) {
        this.resultsRaceId = resultsRaceId;
    }

    public long getResultsBoatId() {
        return resultsBoatId;
    }

    public void setResultsBoatId(long resultsBoatId) {
        this.resultsBoatId = resultsBoatId;
    }

    public String getRaceName() {
        return raceName;
    }

    public void setRaceName(String raceName) {
        this.raceName = raceName;
    }

    public String getRaceDate() {
        return raceDate;
    }

    public void setRaceDate(String raceDate) {
        this.raceDate = raceDate;
    }

    public double getRaceDistance() {
        return raceDistance;
    }

    public void setRaceDistance(double raceDistance) {
        this.raceDistance = raceDistance;
    }

    public String getBoatName() {
        return boatName;
    }

    public void setBoatName(String boatName) {
        this.boatName = boatName;
    }

    public String getBoatSailNum() {
        return boatSailNum;
    }

    public void setBoatSailNum(String boatSailNum) {
        this.boatSailNum = boatSailNum;
    }

    public String getBoatClass() {
        return boatClass;
    }

    public void setBoatClass(String boatClass) {
        this.boatClass = boatClass;
    }

    public int getBoatPHRF() {
        return boatPHRF;
    }

    public void setBoatPHRF(int boatPHRF) {
        this.boatPHRF = boatPHRF;
    }

    public String getResultsDuration() {
        return resultsDuration;
    }

    public void setResultsDuration(String resultsDuration) {
        this.resultsDuration = resultsDuration;
    }

    public String getResultsAdjDuration() {
        return resultsAdjDuration;
    }

    public void setResultsAdjDuration(String resultsAdjDuration) {
        this.resultsAdjDuration = resultsAdjDuration;
    }

    public double getResultsPenalty() {
        return resultsPenalty;
    }

    public void setResultsPenalty(double resultsPenalty) {
        this.resultsPenalty = resultsPenalty;
    }

    public String getResultsNote() {
        return resultsNote;
    }

    public void setResultsNote(String resultsNote) {
        this.resultsNote = resultsNote;
    }

    public int getResultsPlace() {
        return resultsPlace;
    }

    public void setResultsPlace(int resultsPlace) {
        this.resultsPlace = resultsPlace;
    }

    public int getResultsVisible() {
        return resultsVisible;
    }

    public void setResultsVisible(int resultsVisible) {
        this.resultsVisible = resultsVisible;
    }

    public String getResultsCreateDate() {
        return resultsCreateDate;
    }

    public void setResultsCreateDate(String resultsCreateDate) {
        this.resultsCreateDate = resultsCreateDate;
    }




}
