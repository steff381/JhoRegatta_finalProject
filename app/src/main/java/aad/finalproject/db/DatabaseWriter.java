package aad.finalproject.db;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import aad.finalproject.jhoregatta.GlobalContent;
import aad.finalproject.jhoregatta.MainActivity;

public class DatabaseWriter extends MainActivity {
    private static final String LOGTAG = "Database Writer ";
    // sql elements for selecting boats
    private static String where;

    // make a publicly accessible directory path
    public static File exportDir = Environment.getExternalStoragePublicDirectory(Environment
            .DIRECTORY_DOWNLOADS);

    public static String fixedLengthString(String string, int length) {
        return String.format("%1$" + length + "s", string);
    }


    public static boolean exportDatabase(String fileName, ResultDataSource resultDataSource,
                                         boolean getAllRaces) {

        if (!getAllRaces) {
            where = DBAdapter.KEY_RACE_ID + " = " + GlobalContent.getRaceRowID()
                    + " AND " + DBAdapter.KEY_RESULTS_VISIBLE + " = 1";
        } else {
            where = DBAdapter.KEY_RESULTS_VISIBLE + " = 1";
        }
        Log.i(LOGTAG, "Opening DB writer method");
        Log.i(LOGTAG, "Export directory: " + exportDir);
        Log.i(LOGTAG, "Where Variable = " + where);
        Log.i(LOGTAG, "Where = " +DBAdapter.KEY_RACE_ID + " = " + GlobalContent.getRaceRowID()
                + " AND " + DBAdapter.KEY_RESULTS_VISIBLE + " = 1");


        /**First of all we check if the external storage of the device is available for writing.
         * Remember that the external storage is not necessarily the sd card. Very often it is
         * the device storage.
         */
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.i(LOGTAG, "No media mounted");
            return false;
        } else {
            //We use the Download directory for saving our .csv file.

            Log.i(LOGTAG, "Media is mounted");
            if (!exportDir.exists()) {
                Log.i(LOGTAG, "Export directory doesn't exist");
                exportDir.mkdirs();
            }


            File file; //file instance
            String c = ","; //short way to make a comma
            PrintWriter printWriter = null; //create a null printwriter

            try {

                Log.i(LOGTAG, "Trying to write file ");
                //write the file using the given file location and file name
                file = new File(exportDir, fileName);
                file.createNewFile();
                printWriter = new PrintWriter(new FileWriter(file));

                /**This is our database connector class that reads the data from the database.
                 * The code of this class is omitted for brevity.
                 */

                /**Let's read the first table of the database.
                 * getFirstTable() is a method in our DBCOurDatabaseConnector class which retrieves a Cursor
                 * containing all records of the table (all fields).
                 * The code of this class is omitted for brevity.
                 */

                //create the order by clause for the sql statement
                String orderBy = DBAdapter.KEY_RACE_ID + ", " + DBAdapter.KEY_BOAT_CLASS + ", "
                        + DBAdapter.KEY_RESULTS_ADJ_DURATION;

                List<Result> results = resultDataSource.getAllResults(where, orderBy, null);

                for (Result r : results) {
                    Log.i(LOGTAG, "Results line = Race Name: " + r.raceName + " race ID " +
                            r.resultsRaceId + " boat name: " + r.boatName);
                }

                //Write the name of the table and the name of the columns (comma separated values) in the .csv file.
                String RESULTS_FIELDS_CSV_HEADER = DBAdapter.KEY_RACE_NAME + "," +
                        DBAdapter.KEY_RACE_DATE + "," +
                        DBAdapter.KEY_RACE_DISTANCE + "," +

                        DBAdapter.KEY_BOAT_NAME + "," +
                        DBAdapter.KEY_BOAT_SAIL_NUM + "," +
                        DBAdapter.KEY_BOAT_CLASS + "," +
                        DBAdapter.KEY_BOAT_PHRF + "," +
                        DBAdapter.KEY_RESULTS_MANUAL_ENTRY + "," +

                        DBAdapter.KEY_RESULTS_CLASS_START + "," +
                        DBAdapter.KEY_RESULTS_FINISH_TIME + "," +
                        DBAdapter.KEY_RESULTS_DURATION + "," +
                        DBAdapter.KEY_RESULTS_ADJ_DURATION + "," +
                        DBAdapter.KEY_RESULTS_PENALTY + "," +
                        DBAdapter.KEY_RESULTS_NOTE + "," +
                        DBAdapter.KEY_RESULTS_PLACE + "," +
                        DBAdapter.KEY_RESULTS_NOT_FINISHED + "," +
                        DBAdapter.KEY_CREATED_AT;


                printWriter.println(RESULTS_FIELDS_CSV_HEADER);

                for (Result r : results) {

                    //results
                    String ClassStartTime =  r.getResultsClassStartTime();
                    String BoatFinishTime =  r.getResultsBoatFinishTime();
                    String ResultsDuration = r.getResultsDuration();
                    String ResultsAdjDuration =  r.getResultsAdjDuration();
                    double ResultsPenalty = ((double)r.getResultsPenalty() / 100);
                    String ResultsNote =  r.getResultsNote();
                    Integer ResultsPlace =  r.getResultsPlace();
                    Integer ResultsNotFinished =  r.getResultsNotFinished();
                    Integer ResultsManualEntry =  r.getResultsManualEntry();
                    //boats
                    String BoatName =  r.getBoatName();
                    String BoatSailNum = r.getBoatSailNum();
                    String BoatClass = r.getBoatClass();
                    Integer BoatPHRF = r.getBoatPHRF();
                    //races
                    Double RaceDistance = r.getRaceDistance();
                    String RaceName = r.getRaceName();
                    String RaceDate = r.getRaceDate();

                    String Created = r.getResultsCreateDate();

                    /**Create the line to write in the .csv file.
                     * We need a String where values are comma separated.
                     * The field date (Long) is formatted in a readable text. The amount field
                     * is converted into String.
                     */
                    String record =

                                    RaceName + c +              // Column A
                                    RaceDate + c +              // Column B
                                    RaceDistance + c +          // Column C

                                    BoatName + c +              // Column D
                                    BoatSailNum + c +           // Column E
                                    BoatClass + c +             // Column F
                                    BoatPHRF + c +              // Column G
                                    ResultsManualEntry + c +    // Column H

                                    ClassStartTime + c +        // Column I
                                    BoatFinishTime + c +        // Column J
                                    ResultsDuration + c +       // Column K
                                    ResultsAdjDuration + c +    // Column L
                                    ResultsPenalty + c +        // Column M
                                    ResultsNote + c +           // Column N
                                    ResultsPlace + c +          // Column O
                                    ResultsNotFinished + c +    // Column P
                                    Created;                    // Column S


                    printWriter.println(record); //write the record in the .csv file
                }

                resultDataSource.close(); // close data soruce to conserve resources
            } catch (Exception exc) {
                //catch any possible exceptions
                Log.i(LOGTAG, "EXCEPTION THROWN!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                return false;
            } finally {
                Log.i(LOGTAG, "Finally Closing writer");
                //close the print writer.
                if (printWriter != null) printWriter.close();
            }

            //If there are no errors, return true.
            return true;
        }
    }


    public static boolean print( ResultDataSource resultDataSource,boolean getAllRaces) {


        if (!getAllRaces) {
            where = DBAdapter.KEY_RACE_ID + " = " + GlobalContent.getRaceRowID()
                    + " AND " + DBAdapter.KEY_RESULTS_VISIBLE + " = 1";
        } else {
            where = DBAdapter.KEY_RESULTS_VISIBLE + " = 1";
        }

            String c = "| "; //short way to make a comma

            try {



                /**This is our database connector class that reads the data from the database.
                 * The code of this class is omitted for brevity.
                 */

                /**Let's read the first table of the database.
                 * getFirstTable() is a method in our DBCOurDatabaseConnector class which retrieves a Cursor
                 * containing all records of the table (all fields).
                 * The code of this class is omitted for brevity.
                 */

                //create the order by clause for the sql statement
                String orderBy = DBAdapter.KEY_RACE_ID + ", " + DBAdapter.KEY_BOAT_CLASS + ", "
                        + DBAdapter.KEY_RESULTS_ADJ_DURATION;

                List<Result> results = resultDataSource.getAllResults(where, orderBy, null);

                //Write the name of the table and the name of the columns (comma separated values) in the .csv file.
                String RESULTS_FIELDS_CSV_HEADER = DBAdapter.KEY_RACE_NAME+"           " + c +
                        DBAdapter.KEY_RACE_DATE+"                " + c +
                        DBAdapter.KEY_RACE_DISTANCE+"            " + c +

                        DBAdapter.KEY_BOAT_NAME+"           " + c +
                        DBAdapter.KEY_BOAT_SAIL_NUM+"            " + c +
                        DBAdapter.KEY_BOAT_CLASS+"           " + c +
                        DBAdapter.KEY_BOAT_PHRF+"                " + c +
                        DBAdapter.KEY_RESULTS_MANUAL_ENTRY+"    " + c +

                        DBAdapter.KEY_RESULTS_CLASS_START+"    " + c +
                        DBAdapter.KEY_RESULTS_FINISH_TIME+"    " + c +
                        DBAdapter.KEY_RESULTS_DURATION+"        " + c +
                        DBAdapter.KEY_RESULTS_ADJ_DURATION+"        " + c +
                        DBAdapter.KEY_RESULTS_PENALTY+"     " + c +
                        DBAdapter.KEY_RESULTS_NOTE+"                " + c +
                        DBAdapter.KEY_RESULTS_PLACE+"               " + c +
                        DBAdapter.KEY_RESULTS_NOT_FINISHED+"         " + c +
                        DBAdapter.KEY_CREATED_AT+"          ";

                Log.i(LOGTAG, RESULTS_FIELDS_CSV_HEADER);


                for (Result r : results) {

                    //results
                    String ClassStartTime =  r.getResultsClassStartTime()+"                    ";
                    String BoatFinishTime =  r.getResultsBoatFinishTime()+"                    ";
                    String ResultsDuration = r.getResultsDuration()+"                    ";
                    String ResultsAdjDuration =  r.getResultsAdjDuration()+"                    ";
                    String ResultsPenalty = ((double)r.getResultsPenalty() / 100)+"                    ";
                    String ResultsNote =  r.getResultsNote()+"                    ";
                    String ResultsPlace =  r.getResultsPlace()+"                    ";
                    String ResultsNotFinished =  r.getResultsNotFinished()+"                    ";
                    String ResultsManualEntry =  r.getResultsManualEntry()+"                    ";
                    //boats
                    String BoatName =  r.getBoatName()+"                    ";
                    String BoatSailNum = r.getBoatSailNum()+"                    ";
                    String BoatClass = r.getBoatClass()+"                    ";
                    String BoatPHRF = r.getBoatPHRF()+"                    ";
                    //races
                    String RaceDistance = r.getRaceDistance()+"                    ";
                    String RaceName = r.getRaceName()+"                    ";
                    String RaceDate = r.getRaceDate()+"                    ";

                    String Created = r.getResultsCreateDate()+"                    ";

                    /**Create the line to write in the .csv file.
                     * We need a String where values are comma separated.
                     * The field date (Long) is formatted in a readable text. The amount field
                     * is converted into String.
                     */
                    String record =

                                    RaceName.substring(0,20) + c +              // Column A
                                    RaceDate.substring(0,20) + c +              // Column B
                                    RaceDistance.substring(0,20) + c +          // Column C

                                    BoatName.substring(0,20) + c +              // Column D
                                    BoatSailNum.substring(0,20) + c +           // Column E
                                    BoatClass.substring(0,20) + c +             // Column F
                                    BoatPHRF.substring(0,20) + c +              // Column G
                                    ResultsManualEntry.substring(0,20) + c +    // Column H

                                    ClassStartTime.substring(0,20) + c +        // Column I
                                    BoatFinishTime.substring(0,20) + c +        // Column J
                                    ResultsDuration.substring(0,20) + c +       // Column K
                                    ResultsAdjDuration.substring(0,20) + c +    // Column L
                                    ResultsPenalty.substring(0,20) + c +        // Column M
                                    ResultsNote.substring(0,20) + c +           // Column N
                                    ResultsPlace.substring(0,20) + c +          // Column O
                                    ResultsNotFinished.substring(0,20) + c +    // Column P
                                    Created.substring(0,20);                    // Column S

                    Log.i(LOGTAG, record);
                }

//                resultDataSource.close(); // close data soruce to conserve resources
            } catch (Exception exc) {
                //catch any possible exceptions
                Log.i(LOGTAG, "EXCEPTION THROWN!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                return false;
            } finally {
                Log.i(LOGTAG, "Finally Closing writer");
            }

            //If there are no errors, return true.
            return true;

    }

}
