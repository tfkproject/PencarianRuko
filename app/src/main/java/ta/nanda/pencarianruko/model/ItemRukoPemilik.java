package ta.nanda.pencarianruko.model;

/**
 * Created by taufik on 21/05/18.
 */

public class ItemRukoPemilik {
    String id,
            id_kec,
            kec,
            url_gambar,
            judul,
            ukuran,
            sertifikat,
            harga,
            l_bangunan,
            l_tanah,
            jum_kamar,
            kamar_mandi,
            daya_listrik,
            no_hp,
            latitude,
            longitude,
            status
    ;

    public ItemRukoPemilik(
            String id,
            String id_kec,
            String kec,
            String url_gambar,
            String judul,
            String ukuran,
            String sertifikat,
            String harga,
            String l_bangunan,
            String l_tanah,
            String jum_kamar,
            String kamar_mandi,
            String daya_listrik,
            String no_hp,
            String latitude,
            String longitude,
            String status
    ){
        this.id = id;
        this.id_kec = id_kec;
        this.kec = kec;
        this.url_gambar = url_gambar;
        this.judul = judul;
        this.ukuran = ukuran;
        this.sertifikat = sertifikat;
        this.harga = harga;
        this.l_bangunan = l_bangunan;
        this.l_tanah = l_tanah;
        this.jum_kamar = jum_kamar;
        this.kamar_mandi = kamar_mandi;
        this.daya_listrik = daya_listrik;
        this.no_hp = no_hp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getId_kec() {
                return  id_kec;
    }

    public String getKec() {
        return kec;
    }

    public String getUrl_gambar() {
        return url_gambar;
    }

    public String getJudul() {
        return judul;
    }

    public String getUkuran() {
        return ukuran;
    }

    public String getSertifikat() {
        return sertifikat;
    }

    public String getHarga() {
        return harga;
    }

    public String getL_bangunan() {
        return l_bangunan;
    }

    public String getL_tanah() {
        return l_tanah;
    }

    public String getJum_kamar() {
        return jum_kamar;
    }

    public String getKamar_mandi() {
        return kamar_mandi;
    }

    public String getDaya_listrik() {
        return daya_listrik;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getStatus() {
        return status;
    }
}
