package it.univr.passaporto_elettronico;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class Reservation implements Serializable, Comparable<Reservation> {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Date date;
    private final String item;
    private final String site;

    public Reservation(Date date, String item, String site){
        this.date = date;
        this.item = item;
        this.site = site;
    }

    public String getSite() {
        return site;
    }

    public String getItem() {
        return item;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public int compareTo(Reservation other){
        return other != null ?
                date.compareTo(other.getDate()) :
                0;
    }

    @Override
    public String toString(){
        return getClass().getSimpleName() + "{" +
                "date='" + this.date + '\'' +
                "item='" + this.item + '\'' +
                "site='" + this.site + '\'' +
                "}\n";
    }
}
