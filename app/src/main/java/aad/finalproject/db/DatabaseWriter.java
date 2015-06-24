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
    private static final String LOGTAG = "DatabaseWriter ";
    // sql elements for selecting boats
    private static String where;

    // make a publicly accessible directory path
    private static File exportDir = Environment.getExternalStoragePublicDirectory(Environment
            .DIRECTORY_DOWNLOADS);

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
                String orderBy = DBAdapter.KEY_RACE_ID + ", " + DBAdapter.KEY_RESULTS_CLASS_START + ", "
                        + DBAdapter.KEY_RESULTS_ADJ_DURATION;

                List<Result> results = resultDataSource.getAllResults(where, orderBy, null);


                //Write the name of the table and the name of the columns (comma separated values) in the .csv file.
                String RESULTS_FIELDS_CSV_HEADER = "Race Name" + c +
                        "Date" + c +
                        "Distance" + c +
                        "Fleet Color" + c +
                        "Boat Name" + c +
                        "Elapsed Time" + c +
                        "Adj Elapsed Time" + c +
                        "Penalty Percent" + c +
                        "Notes" + c +
                        "DNF (1/0)" + c +
                        "PHRF rating" + c +
                        "Sail Number" + c +
                        "Elapsed Time Set Manually(1/0)" + c +
                        "Class Flag Down At:" + c +
                        "Boat Finished At:";



                printWriter.println(RESULTS_FIELDS_CSV_HEADER);

                for (Result r : results) {

                    //results
                    String ClassStartTime =  r.getResultsClassStartTime();
                    String BoatFinishTime =  r.getResultsBoatFinishTime();
                    String ResultsDuration = r.getResultsDuration();
                    String ResultsAdjDuration =  r.getResultsAdjDuration();
                    double ResultsPenalty = ((double)r.getResultsPenalty() / 100);
                    String ResultsNote =  r.getResultsNote();
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

                    /**Create the line to write in the .csv file.
                     * We need a String where values are comma separated.
                     * The field date (Long) is formatted in a readable text. The amount field
                     * is converted into String.
                     */
                    String record =

                            RaceName + c +              // Column A
                            RaceDate + c +              // Column B
                            RaceDistance + c +          // Column C

                            BoatClass + c +             // Column F
                            BoatName + c +              // Column D
                            ResultsDuration + c +       // Column K
                            ResultsAdjDuration + c +    // Column L
                            ResultsPenalty + c +        // Column M
                            ResultsNote + c +           // Column N
                            ResultsNotFinished + c +    // Column P

                            BoatPHRF + c +              // Column G
                            BoatSailNum + c +           // Column E
                            ResultsManualEntry + c +    // Column H

                            ClassStartTime + c +        // Column I
                            BoatFinishTime;

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


}
