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
    private static String where = DBAdapter.KEY_RACE_ID + " = " + GlobalContent.getRaceRowID()
            + " AND " + DBAdapter.KEY_RESULTS_VISIBLE + " = 1";
    // make a publicly accessible directory path
    public static File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);


    public static boolean exportDatabase(String fileName, ResultDataSource resultDataSource) {

        Log.i(LOGTAG, "Opening DB writer method");
        Log.i(LOGTAG, "Export directory: " + exportDir);



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
                String orderBy = DBAdapter.KEY_BOAT_CLASS + ", " + DBAdapter.KEY_BOAT_NAME;

                List<Result> results = resultDataSource.getAllResults(where, orderBy, null);

                //Write the name of the table and the name of the columns (comma separated values) in the .csv file.
                String RESULTS_FIELDS_CSV_HEADER = DBAdapter.KEY_RACE_NAME + "," +
                        DBAdapter.KEY_RACE_DATE + "," +
                        DBAdapter.KEY_RACE_DISTANCE + "," +

                        DBAdapter.KEY_BOAT_NAME + "," +
                        DBAdapter.KEY_BOAT_SAIL_NUM + "," +
                        "fleet_color," +
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


                Log.i(LOGTAG, "HEADERS: " + RESULTS_FIELDS_CSV_HEADER);

                for (Result r : results) {

                    //results
                    String ClassStartTime =  r.getResultsClassStartTime();
                    String BoatFinishTime =  r.getResultsBoatFinishTime();
                    String ResultsDuration =  r.getResultsDuration();
                    String ResultsAdjDuration =  r.getResultsAdjDuration();
                    Double ResultsPenalty =  r.getResultsPenalty();
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

                                    RaceName + c +
                                    RaceDate + c +
                                    RaceDistance + c +

                                    BoatName + c +
                                    BoatSailNum + c +
                                    BoatClass + c +
                                    BoatPHRF + c +
                                    ResultsManualEntry + c +

                                    ClassStartTime + c +
                                    BoatFinishTime + c +
                                    ResultsDuration + c +
                                    ResultsAdjDuration + c +
                                    ResultsPenalty + c +
                                    ResultsNote + c +
                                    ResultsPlace + c +
                                    ResultsNotFinished + c +
                                    Created;


                    printWriter.println(record); //write the record in the .csv file
                }

                resultDataSource.close(); // close data soruce to conserve resources
            } catch (Exception exc) {
                //catch any possible exceptions
                Log.i(LOGTAG, "EXCEPTION THROWN!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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

}
