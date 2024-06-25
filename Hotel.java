package assignment2;

import java.util.ArrayList;
import java.io.*;

public class Hotel {
    private String name;
    private String address;
    private int numberOfRooms;
    private ArrayList<Guest> guests;

    public Hotel(String name, String address, int numberOfRooms) {
        this.name = name;
        this.address = address;
        this.numberOfRooms = numberOfRooms;
        this.guests = new ArrayList<Guest>();
        readGuestsfromFile();
    }

    public int lowestAvailableRoomNumber() {
        int lowestAvailableRoomNumber = 1;
        for (Guest guest : guests) {
            if (guest.getRoom().getRoomNumber() == lowestAvailableRoomNumber) {
                lowestAvailableRoomNumber++;
            }
        }
        return lowestAvailableRoomNumber;
    }

    public int addGuest(String name, int singleDouble) {
        if (guests.size() < numberOfRooms) {
            int roomNumber = lowestAvailableRoomNumber();
            if (singleDouble == 1) {
                Room room = new SingleRoom(roomNumber);
                guests.add(roomNumber, new Guest(name, room));
                return (roomNumber + 1);
            } else if (singleDouble == 2) {
                Room room = new DoubleRoom(roomNumber);
                guests.add(roomNumber, new Guest(name, room));
                return (roomNumber + 1);
            } else {
                System.out.println("Invalid room size");
            }
        } else {
            System.out.println("Hotel is full");
        }

        return (-1);
    }

    public int addGuest(String name, int singleDouble, String checkInDate, String checkOutDate) {
        if (guests.size() < numberOfRooms) {
            int roomNumber = lowestAvailableRoomNumber();
            if (singleDouble == 1) {
                Room room = new SingleRoom(roomNumber + 1);
                guests.add(roomNumber, new Guest(name, room, checkInDate, checkOutDate));
                return (roomNumber);
            } else if (singleDouble == 2) {
                Room room = new DoubleRoom(roomNumber + 1);
                guests.add(roomNumber, new Guest(name, room, checkInDate, checkOutDate));
                return (roomNumber);
            } else {
                System.out.println("Invalid room size");
            }
        } else {
            System.out.println("Hotel is full");
        }

        return (-1);
    }

    public void removeGuest(int roomNumber) {
        for (Guest guest : guests) {
            if (guest.getRoom().getRoomNumber() == roomNumber) {
                guests.remove(guest);
                break;
            }
        }
    }

    public void readGuestsfromFile() {
        try {
            File file = new File(
                    "C:\\Users\\dylan\\Documents\\Code\\1181\\assignment2\\assignment2\\src\\main\\java\\assignment2\\guestList.csv");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            String[] guestInfo;

            bufferedReader.readLine();

            while ((line = bufferedReader.readLine()) != null) {
                guestInfo = line.split(",");

                if (guestInfo[2].equals("1")) {
                    Guest guest = new Guest(guestInfo[0], new SingleRoom(Integer.parseInt(guestInfo[1])),
                            guestInfo[3], guestInfo[4]);
                    guests.add(guest);
                } else {
                    Guest guest = new Guest(guestInfo[0], new DoubleRoom(Integer.parseInt(guestInfo[1])),
                            guestInfo[3], guestInfo[4]);
                    guests.add(guest);
                }
            }

            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile() {
        try {
            File file = new File(
                    "C:\\Users\\dylan\\Documents\\Code\\1181\\assignment2\\assignment2\\src\\main\\java\\assignment2\\guestList.csv");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write("Name,Room Number,Check In Date,Check Out Date\n");
            for (Guest guest : guests) {
                if (guest.getRoom() instanceof SingleRoom) {
                    bufferedWriter.write(guest.getName() + "," + guest.getRoom().getRoomNumber() + ",1,"
                            + guest.getCheckInDate() + "," + guest.getCheckOutDate() + "\n");
                } else {
                    bufferedWriter.write(guest.getName() + "," + guest.getRoom().getRoomNumber() + ",2,"
                            + guest.getCheckInDate() + "," + guest.getCheckOutDate() + "\n");
                }
            }

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public ArrayList<Guest> getGuests() {
        return guests;
    }

    public String toString() {
        String guestList = "";
        for (Guest guest : guests) {
            guestList += "\n" + guest;
        }

        return "Hotel name: " + name + "\nHotel address: " + address + "\nNumber of rooms: " + numberOfRooms
                + "\nGuests: " + guestList;
    }
}