package com.volkov.alexandr.mytranslate.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by AlexandrVolkov on 10.07.2017.
 */
public class Translate implements Parcelable{
    private long id;
    private final Word from;
    private final Word to;
    private boolean isFavorite;

    public Translate(Word from, Word to, boolean isFavorite) {
        this.from = from;
        this.to = to;
        this.isFavorite = isFavorite;
    }

    public Translate(long id, Word from, Word to, boolean isFavorite) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.isFavorite = isFavorite;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Word getFrom() {
        return from;
    }

    public Word getTo() {
        return to;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Translate)) return false;

        Translate translate = (Translate) o;

        if (from != null ? !from.equals(translate.from) : translate.from != null) return false;
        return to != null ? to.equals(translate.to) : translate.to == null;
    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return  from + "-" + to;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(from, flags);
        dest.writeParcelable(to, flags);
        dest.writeInt(isFavorite ? 1 : 0);
    }

    private Translate(Parcel parcel) {
        id = parcel.readLong();
        from = parcel.readParcelable(Word.class.getClassLoader());
        to = parcel.readParcelable(Word.class.getClassLoader());
        isFavorite = parcel.readInt() == 1;
    }

    public static final Parcelable.Creator<Translate> CREATOR = new Parcelable.Creator<Translate>() {
        public Translate createFromParcel(Parcel in) {
            return new Translate(in);
        }

        public Translate[] newArray(int size) {
            return new Translate[size];
        }
    };
}
