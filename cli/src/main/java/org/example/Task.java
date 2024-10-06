package org.example;

public class Task {

    private int id;
    private String title;
    private String data;
    private String creationDate;
    private String status;

    public Task() {
    }

    public Task(int id, String title, String data, String creationDate, String status) {
        this.id = id;
        this.title = title;
        this.data = data;
        this.creationDate = creationDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", data='" + data + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
