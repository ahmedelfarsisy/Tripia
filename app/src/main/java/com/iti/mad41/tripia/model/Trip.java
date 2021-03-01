package com.iti.mad41.tripia.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Trip implements Parcelable {
    private int id;
    private String tripTitle;
    private String startAddress;
    private String destinationAddress;

    private Long dateTime;
    private String imageUrl;


    private Double startLongitude;
    private Double startLatitude;

    private Double destinationLongitude;
    private Double destinationLatitude;
    private List<Note> noteList;


    public Trip() {
    }

    public Trip(String tripTitle, String startAddress, Double startLongitude, Double startLatitude, String destinationAddress, Double destinationLongitude, Double destinationLatitude, Long dateTime, String imageUrl) {
        this.tripTitle = tripTitle;
        this.startAddress = startAddress;
        this.startLongitude = startLongitude;
        this.startLatitude = startLatitude;
        this.destinationAddress = destinationAddress;
        this.destinationLongitude = destinationLongitude;
        this.destinationLatitude = destinationLatitude;
        this.dateTime = dateTime;
        this.imageUrl = imageUrl;
    }

    public Trip(int id, String tripTitle, String startAddress, String destinationAddress, Long dateTime, String imageUrl) {
        this.id = id;
        this.tripTitle = tripTitle;
        this.startAddress = startAddress;
        this.destinationAddress = destinationAddress;
        this.dateTime = dateTime;
        this.imageUrl = imageUrl;
    }


    protected Trip(Parcel in) {
        id = in.readInt();
        tripTitle = in.readString();
        startAddress = in.readString();
        destinationAddress = in.readString();
        if (in.readByte() == 0) {
            dateTime = null;
        } else {
            dateTime = in.readLong();
        }
        imageUrl = in.readString();
        startLongitude = in.readDouble();
        startLatitude = in.readDouble();
        destinationLongitude = in.readDouble();
        destinationLatitude = in.readDouble();
    }


    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTripTitle() {
        return tripTitle;
    }

    public void setTripTitle(String tripTitle) {
        this.tripTitle = tripTitle;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public Double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }


    public List<Note> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(tripTitle);
        dest.writeString(startAddress);
        dest.writeString(destinationAddress);
        if (dateTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(dateTime);
        }
        dest.writeString(imageUrl);
        if (startLongitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(startLongitude);
        }
        if (startLatitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(startLatitude);
        }
        if (destinationLongitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(destinationLongitude);
        }
        if (destinationLatitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(destinationLatitude);
        }
    }
}
