package sp.ics.uplb.gtrack.controllers;

public class Node {

    private long id;
    private double node_latitude;
    private double node_longitude;
    private double node_dest_latitude;
    private double node_dest_longitude;
    private long node_next;

    public Node() {}

    public Node(double node_latitude,double node_longitude,double node_dest_latitude,double node_dest_longitude,long node_next) {
        setLatitude(node_latitude);
        setLongitude(node_longitude);
        setDestLatitude(node_dest_latitude);
        setDestLongitude(node_dest_longitude);
        setNodeNext(node_next);
    }

    public Node(long id, double node_latitude,double node_longitude,double node_dest_latitude,double node_dest_longitude,long node_next) {
        setId(id);
        setLatitude(node_latitude);
        setLongitude(node_longitude);
        setDestLatitude(node_dest_latitude);
        setDestLongitude(node_dest_longitude);
        setNodeNext(node_next);
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setLatitude(double node_latitude) {
        this.node_latitude = node_latitude;
    }

    public double getLatitude() {
        return node_latitude;
    }

    public void setLongitude(double node_longitude) {
        this.node_longitude = node_longitude;
    }

    public double getLongitude() {
        return node_longitude;
    }

    public void setDestLatitude(double node_dest_latitude) {
        this.node_dest_latitude = node_dest_latitude;
    }

    public double getDestLatitude() {
        return node_dest_latitude;
    }

    public void setDestLongitude(double node_dest_longitude) {
        this.node_dest_longitude = node_dest_longitude;
    }

    public double getDestLongitude() {
        return node_dest_longitude;
    }

    public void setNodeNext(long node_next) {
        this.node_next = node_next;
    }

    public long getNodeNext() {
        return node_next;
    }
}
