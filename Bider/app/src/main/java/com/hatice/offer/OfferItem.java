package com.hatice.offer;

public class OfferItem {
    private String offerValue;
    private String from;

    public OfferItem(String offerValue, String from) {
        this.offerValue = offerValue;
        this.from = from;
    }

    public String getOfferValue() {
        return offerValue;
    }

    public void setOfferValue(String offerValue) {
        this.offerValue = offerValue;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

}
