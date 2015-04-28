package aad.finalproject.jhoregatta;

/**
 * Created by Daniel on 4/27/2015.
 */
public class Coordinates {

    private double latitudeDouble;
    private double longitudeDouble;
    public String latitudeString;
    public String longitudeString;

    public double getLatitudeDouble() {
        return convertLatLonToDecimal(getLatitudeString());
    }

    public void setLatitudeDouble(double latitudeDouble) {
        this.latitudeDouble = latitudeDouble;
    }

    public double getLongitudeDouble() {
        return convertLatLonToDecimal(getLongitudeString());
    }

    public void setLongitudeDouble(double longitudeDouble) {
        this.longitudeDouble = longitudeDouble;
    }

    public String[] getLatitudeString() {
        return latitudeString.split(" - ");
    }


    public void setLatitudeString(int deg, int min, double sec, String dir) {

        this.latitudeString = deg + " - " + min + " - " + sec + " - " + dir;
    }

    public String[] getLongitudeString() {
//        for (String s : longitudeString.split(" - ")) {
//            Log.i("Get LOng ", s);
//        }
        return longitudeString.split(" - ");
    }


    public void setLongitudeString(int deg, int min, double  sec, String dir) {
        this.longitudeString = deg + " - " + min + " - " + sec + " - " + dir;
    }

    public static double convertLatLonToDecimal(String[] latOrLonString) {
        double dirInt = 1;
        double d = Double.parseDouble(latOrLonString[0].trim());
        double m = Double.parseDouble(latOrLonString[1].trim());
        double s = Double.parseDouble(latOrLonString[2].trim());
        String dir = latOrLonString[3].trim();

        if (dir.equals("S") || dir.equals("W")) {
            dirInt = -1;
        }
        return (d + (m/60) + (s/3600)) * dirInt;
    }

}
