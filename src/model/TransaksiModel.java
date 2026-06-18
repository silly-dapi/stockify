package model;

public class TransaksiModel {
    private int id;
    private String namaBarang;
    private String jenis;
    private int jumlah;
    private String tanggal;

    public TransaksiModel() {
    }

    public TransaksiModel(int id, String namaBarang, String jenis, int jumlah, String tanggal) {
        this.id = id;
        this.namaBarang = namaBarang;
        this.jenis = jenis;
        this.jumlah = jumlah;
        this.tanggal = tanggal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}