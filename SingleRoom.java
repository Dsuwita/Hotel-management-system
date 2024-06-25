package assignment2;

public class SingleRoom extends Room {

    public SingleRoom(int roomNumber) {
        this.setRoomNumber(roomNumber);
        this.setPrice(60.00);
    }

    public int getRoomSize() {
        return 1;
    }
}
