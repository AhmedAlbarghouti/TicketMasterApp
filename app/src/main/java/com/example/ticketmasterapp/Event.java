package com.example.ticketmasterapp;
import android.graphics.Bitmap;

public class Event {
    private String name;
    private String url;
    private String start;
    private String minPrice;



    private String imageUrl;


    private String maxPrice;
    private long id;
    private Bitmap eventPic;

    public Event(String name, long id, String url, String start, String minPrice,String maxPrice, Bitmap eventPic,String imageUrl) {
        this.name = name;
        this.id = id;
        this.url = url;
        this.start = start;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.eventPic = eventPic;
        this.imageUrl = imageUrl;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }


    public Bitmap getEventPic() {
        return eventPic;
    }

    public void setEventPic(Bitmap eventPic) {
        this.eventPic = eventPic;
    }
    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
