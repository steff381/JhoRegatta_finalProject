package aad.finalproject.db;

/*
This class handles data related to the results data table.
 */
public class Result {

    //IDs
    long id;
    long resultsRaceId;
    long resultsBoatId;
    long resultsSeriesId;

    // results
    String resultsClassStartTime;
    String resultsBoatFinishTime;
    String resultsDuration;
    String resultsAdjDuration;
    int resultsPenalty;
    String resultsNote;
    int resultsPlace;
    double resultsPoints;
    int resultsVisible;
    int resultsNotFinished;
    int resultsManualEntry;
    int resultsOrderFinished;
    // boat
    String boatName;
    String boatSailNum;
    String boatClass;
    int boatPHRF;
    //race
    double raceDistance;
    String raceName; // no
    String raceDate; // no
    //series
    String seriesName;

    String resultsCreateDate = DBAdapter.getDateTime();

    public String getResultsClassStartTime() {
        return resultsClassStartTime;
    }

    public void setResultsClassStartTime(String resultsClassStartTime) {
        this.resultsClassStartTime = resultsClassStartTime;
    }

    public String getResultsBoatFinishTime() {
        return resultsBoatFinishTime;
    }

    public void setResultsBoatFinishTime(String resultsBoatFinishTime) {
        this.resultsBoatFinishTime = resultsBoatFinishTime;
    }

    public int getResultsManualEntry() {
        return resultsManualEntry;
    }

    public void setResultsManualEntry(int resultsManualEntry) {
        this.resultsManualEntry = resultsManualEntry;
    }

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

    public int getResultsPenalty() {
        return resultsPenalty;
    }

    public void setResultsPenalty(int resultsPenalty) {
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

    public int getResultsNotFinished() {
        return resultsNotFinished;
    }

    public void setResultsNotFinished(int resultsNotFinished) {
        this.resultsNotFinished = resultsNotFinished;
    }

    public int getResultsOrderFinished() {
        return resultsOrderFinished;
    }

    public void setResultsOrderFinished(int resultsOrderFinished) {
        this.resultsOrderFinished = resultsOrderFinished;
    }

    public long getResultsSeriesId() {
        return resultsSeriesId;
    }

    public void setResultsSeriesId(long resultsSeriesId) {
        this.resultsSeriesId = resultsSeriesId;
    }

    public double getResultsPoints() {
        return resultsPoints;
    }

    public void setResultsPoints(double resultsPoints) {
        this.resultsPoints = resultsPoints;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }
}
