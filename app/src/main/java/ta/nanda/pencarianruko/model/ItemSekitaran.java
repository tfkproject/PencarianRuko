package ta.nanda.pencarianruko.model;

/**
 * Created by taufik on 21/05/18.
 */

public class ItemSekitaran {
    String id, nama_tempat, lat, lon;

    public ItemSekitaran(String id, String nama_tempat, String lat, String lon){
        this.id = id;
        this.nama_tempat = nama_tempat;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    public String getNama_tempat() {
        return nama_tempat;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}
