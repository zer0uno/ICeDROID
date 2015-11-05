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

    public Subscription(Subscription subscription) {
        ADChannel = subscription.ADChannel;
        appID = subscription.appID;
    }

    public String getADChannel() {
        return ADChannel;
    }

    public void setADChannel(String ADChannel) {
        this.ADChannel = ADChannel;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public boolean equals(Subscription subscription) {
        return (subscription.ADChannel.equals(ADChannel)) &&
                (subscription.appID.equals(appID));
    }

}
