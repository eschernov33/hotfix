package com.travels.searchtravels.tests;

public class FPicture {
    private String imgPath;
    private String typePhoto;

    public FPicture() {
    }

    public FPicture(String imgPath, String typePhoto) {
        this.imgPath = imgPath;
        this.typePhoto = typePhoto;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getTypePhoto() {
        return typePhoto;
    }

    public void setTypePhoto(String typePhoto) {
        this.typePhoto = typePhoto;
    }
}
