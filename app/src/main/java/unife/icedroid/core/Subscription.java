package unife.icedroid.core;


public class Subscription {
    private String ADChannel;
    private String appID;

    public String getADChannel() {
        return ADChannel;
    }

    public void setADChannel(String ADChannel) {
        this.ADChannel = ADChannel;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public boolean equals(Subscription sub) {
        return ((sub.getADChannel() == this.getADChannel()) && (sub.getAppID() == this.getAppID())) ? true : false;
    }

    public void copy(Subscription subscription) {
        ADChannel = subscription.getADChannel();
        appID = subscription.getAppID();
    }
}
