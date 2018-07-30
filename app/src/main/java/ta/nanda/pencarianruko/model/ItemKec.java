package ta.nanda.pencarianruko.model;

/**
 * Created by taufik on 21/05/18.
 */

public class ItemKec {

    String id_kec, nama_kec;

    public ItemKec(String id_kec, String nama_kec){
        this.id_kec = id_kec;
        this.nama_kec = nama_kec;
    }

    public String getId_kec() {
        return id_kec;
    }

    public String getNama_kec() {
        return nama_kec;
    }
}
