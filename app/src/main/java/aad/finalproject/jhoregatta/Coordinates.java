package aad.finalproject.jhoregatta;

/*
This class contains the latitude and longitude of a particular location
It is uesed with the distance calculator
 */
public class Coordinates {

    //declare the lat and lon as strings
    public String latitudeString;
    public String longitudeString;

    public double getLatitudeDouble() {
        return convertLatLonToDecimal(getLatitudeString());
    }

    public double getLongitudeDouble() {
        return convertLatLonToDecimal(getLongitudeString());
    }

    public String[] getLatitudeString() {
        return latitudeString.split(" - ");
    }

    public void setLatitudeString(int deg, int min, double sec, String dir) {
        this.latitudeString = deg + " - " + min + " - " + sec + " - " + dir;
    }

    public String[] getLongitudeString() {
        return longitudeString.split(" - ");
    }

    public void setLongitudeString(int deg, int min, double  sec, String dir) {
        this.longitudeString = deg + " - " + min + " - " + sec + " - " + dir;
    }

    //using a string that stores teh coordinates, convert the number to a decimal format for
    //calculation
    public static double convertLatLonToDecimal(String[] latOrLonString) {
        double dirInt = 1;
        double d = Double.parseDouble(latOrLonString[0].trim());
        double m = Double.parseDouble(latOrLonString[1].trim());
        double s = Double.parseDouble(latOrLonString[2].trim());
        String dir = latOrLonString[3].trim();

        //set pos or neg depending on cardinal direction
        if (dir.equals("S") || dir.equals("W")) {
            dirInt = -1;
        }
        return (d + (m/60) + (s/3600)) * dirInt;
    }

}
