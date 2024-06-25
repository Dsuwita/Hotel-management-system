package assignment2;

public class DoubleRoom extends Room {
    public DoubleRoom(int roomNumber) {
        this.setRoomNumber(roomNumber);
        this.setPrice(90.00);
    }

    public int getRoomSize() {
        return 2;
    }
}
