package unife.icedroid.core;


public class Subscription {

    /** Application-level Dissemination Channel of membership */
    private String ADChannel;

    /** App IDentifier */
    private String appID;

    public Subscription() {}

    public Subscription(String ADChannel, String appID) {
        this.ADChannel = ADChannel;
        this.appID = appID;
    }

    public String getADChannel() {
        return ADChannel;
    }

    public String getAppID() {
        return appID;
    }

    public void setADChannel(String ADChannel) {
        this.ADChannel = ADChannel;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public void copy(Subscription subscription) {
        ADChannel = subscription.ADChannel;
        appID = subscription.appID;
    }

    public boolean equals(Subscription subscription) {
        return (subscription.ADChannel.equals(this.getADChannel())) &&
                (subscription.appID.equals(this.getAppID()));
    }

}
