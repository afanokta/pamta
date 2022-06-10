package com.pam.finalproject;

public class Pengaduan {
    private String nama, noHP, alamat, namaSaksi, noHPSaksi, alamatSaksi, kategoriKejahatan,
            kronologiKejadian, urlBukti, tanggal, jam;

    public Pengaduan() {
    }

    public Pengaduan(String nama, String noHP, String alamat, String namaSaksi, String noHPSaksi, String alamatSaksi, String kategoriKejahatan, String kronologiKejadian, String urlBukti, String tanggal, String jam) {
        this.nama = nama;
        this.noHP = noHP;
        this.alamat = alamat;
        this.namaSaksi = namaSaksi;
        this.noHPSaksi = noHPSaksi;
        this.alamatSaksi = alamatSaksi;
        this.kategoriKejahatan = kategoriKejahatan;
        this.kronologiKejadian = kronologiKejadian;
        this.urlBukti = urlBukti;
        this.tanggal = tanggal;
        this.jam = jam;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNoHP() {
        return noHP;
    }

    public void setNoHP(String noHP) {
        this.noHP = noHP;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNamaSaksi() {
        return namaSaksi;
    }

    public void setNamaSaksi(String namaSaksi) {
        this.namaSaksi = namaSaksi;
    }

    public String getNoHPSaksi() {
        return noHPSaksi;
    }

    public void setNoHPSaksi(String noHPSaksi) {
        this.noHPSaksi = noHPSaksi;
    }

    public String getAlamatSaksi() {
        return alamatSaksi;
    }

    public void setAlamatSaksi(String alamatSaksi) {
        this.alamatSaksi = alamatSaksi;
    }

    public String getKategoriKejahatan() {
        return kategoriKejahatan;
    }

    public void setKategoriKejahatan(String kategoriKejahatan) {
        this.kategoriKejahatan = kategoriKejahatan;
    }

    public String getKronologiKejadian() {
        return kronologiKejadian;
    }

    public void setKronologiKejadian(String kronologiKejadian) {
        this.kronologiKejadian = kronologiKejadian;
    }

    public String getUrlBukti() {
        return urlBukti;
    }

    public void setUrlBukti(String urlBukti) {
        this.urlBukti = urlBukti;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }
}
