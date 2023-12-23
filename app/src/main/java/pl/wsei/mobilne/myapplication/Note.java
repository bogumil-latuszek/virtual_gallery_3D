package pl.wsei.mobilne.myapplication;

import java.util.Date;

public class Note {

    private int id;
    private String title;
    private String description;
    private Date deleted;

    public int getId() {return id;}
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
     public Date getDeleted() {
        return deleted;
    }


    public Note(int id, String description, String title, Date deleted) {
        this.id = id;
        this.description = description;
        this.title = title;
        this.deleted = deleted;
    }

    public Note(int id, String description, String title) {
        this.id = id;
        this.description = description;
        this.title = title;
        this.deleted = null;
    }
}