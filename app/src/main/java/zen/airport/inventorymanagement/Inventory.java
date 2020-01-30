package zen.airport.inventorymanagement;

public class Inventory {
    private String SerialNumber,Name,Date,Location;
    private long time;

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Inventory() {
    }

    public Inventory(String serialNumber, String name, String date, String location, long time) {
        SerialNumber = serialNumber;
        Name = name;
        Date = date;
        Location = location;
        this.time = time;
    }
}
