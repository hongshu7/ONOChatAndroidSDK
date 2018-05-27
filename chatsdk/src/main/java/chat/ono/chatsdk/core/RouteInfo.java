package chat.ono.chatsdk.core;

/**
 * Created by kevin on 16/4/20.
 */
public class RouteInfo {
    private int routeId;
    private String request;
    private String response;

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
