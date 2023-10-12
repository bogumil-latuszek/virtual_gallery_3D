package pl.wsei.mobilne.myapplication;

public class AminoAcidModel {
    String aminoAcidName;
    int image;


    public AminoAcidModel(String aminoAcidName, int image) {
        this.aminoAcidName = aminoAcidName;
        this.image = image;
    }

    public String getAminoAcidName() {
        return aminoAcidName;
    }

    public int getImage() {
        return image;
    }

    public void setAminoAcidName(String aminoAcidName) {
        this.aminoAcidName = aminoAcidName;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
