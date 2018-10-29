package com.symbio.i2qcamera.data;

public class ImgDeleteEvent {

    private int position;

    public ImgDeleteEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
