package assignment2;

public class Guest {
    private String name;
    private Room room;
    private String checkInDate;
    private String checkOutDate;

    public Guest(String name) {
        this.name = name;
    }

    public Guest(String name, Room room) {
        this(name);
        this.room = room;
    }

    public Guest(String name, Room room, String checkInDate, String checkOutDate) {
        this(name, room);
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public String getName() {
        return name;
    }

    public Room getRoom() {
        return room;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String toString() {
        if (room instanceof SingleRoom) {
            return "\tName: " + name + "\n\tRoom Number: " + (room.getRoomNumber() + 1) + "\n\tRoom size: Single"
                    + "\n\tCheck in date: "
                    + checkInDate + "\n\tCheck out date: " + checkOutDate + "\n";
        } else {
            return "\tName: " + name + "\n\tRoom Number: " + (room.getRoomNumber() + 1) + "\n\tRoom size: Double"
                    + "\n\tCheck in date: "
                    + checkInDate + "\n\tCheck out date: " + checkOutDate + "\n";
        }
    }
}
