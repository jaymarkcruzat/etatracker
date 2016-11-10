package sp.ics.uplb.gtrack.controllers;

public class Contact {

    int _id;
    String _name;
    String _firebaseid;

    public Contact(){
    }

    public Contact(int id, String name, String firebaseid){
        this._id = id;
        this._name = name;
        this._firebaseid = firebaseid;
    }

    public Contact(String name, String firebaseid){
        this._name = name;
        this._firebaseid = firebaseid;
    }

    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public String getName(){
        return this._name;
    }

    public void setName(String name){
        this._name = name;
    }

    public String getFirebaseId(){
        return this._firebaseid;
    }

    public void setFirebaseId(String _firebaseid){
        this._firebaseid = _firebaseid;
    }
}
