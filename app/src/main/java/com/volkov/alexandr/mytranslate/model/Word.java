package com.volkov.alexandr.mytranslate.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by AlexandrVolkov on 10.07.2017.
 */
public class Word implements Parcelable {
    private long id;
    private final String text;
    private final Language lan;

    public Word(String text, Language lan) {
        this.text = text;
        this.lan = lan;
    }

    public Word(long id, String text, Language lan) {
        this(text, lan);
        this.id = id;
    }

    public Word(Language lan) {
        this("", lan);
    }

    public String getText() {
        return text;
    }

    public Language getLanguage() {
        return lan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Word)) return false;

        Word word = (Word) o;

        if (text != null ? !text.equals(word.text) : word.text != null) return false;
        return lan != null ? lan.equals(word.lan) : word.lan == null;
    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + (lan != null ? lan.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return text + "(" + lan.getCode() + ")";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(lan, flags);
        dest.writeString(text);
    }

    private Word(Parcel parcel) {
        id = parcel.readLong();
        lan = parcel.readParcelable(Language.class.getClassLoader());
        text = parcel.readString();
    }

    public static final Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>() {
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        public Word[] newArray(int size) {
            return new Word[size];
        }
    };
}
