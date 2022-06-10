package com.pam.finalproject;

public class SOS {
    private String name, alamat, noTelp;

    public SOS() {
    }

    public SOS(String name, String alamat, String noTelp) {
        this.name = name;
        this.alamat = alamat;
        this.noTelp = noTelp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }
}
