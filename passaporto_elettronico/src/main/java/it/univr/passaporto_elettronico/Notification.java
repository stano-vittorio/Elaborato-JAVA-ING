package it.univr.passaporto_elettronico;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class Notification implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private LocalDate startDate;
    private LocalDate endDate;
    private String item;

    public Notification() {
        this.startDate = null;
        this.endDate = null;
        this.item = "";
    }

    public void setNotification(LocalDate startDate, LocalDate endDate, String item){
        this.startDate = startDate;
        this.endDate = endDate;
        this.item = item;
    }

    public LocalDate getStartDate(){
        return this.startDate;
    }

    public LocalDate getEndDate(){
        return this.endDate;
    }

    public String getItem(){
        return this.item;
    }

    @Override
    public String toString(){
        return getClass().getSimpleName() + "{" +
                "startDate='" + getStartDate() + '\'' +
                ", endDate='" + getEndDate() + '\'' +
                ", item=" + getItem() +
                '}';
    }
}
