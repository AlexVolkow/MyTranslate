package com.volkov.alexandr.mytranslate.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by AlexandrVolkov on 07.07.2017.
 */
public class Language implements Parcelable{
    private long id;
    private final String code;
    private final String label;

    public Language(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public Language(long id, String code, String label) {
        this.id = id;
        this.code = code;
        this.label = label;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label + "(" + code + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Language)) return false;

        Language language = (Language) o;

        if (code != null ? !code.equals(language.code) : language.code != null) return false;
        return label != null ? label.equals(language.label) : language.label == null;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(code);
        dest.writeString(label);
    }

    private Language(Parcel parcel) {
        id = parcel.readLong();
        code = parcel.readString();
        label = parcel.readString();
    }

    public static final Parcelable.Creator<Language> CREATOR = new Parcelable.Creator<Language>() {
        public Language createFromParcel(Parcel in) {
            return new Language(in);
        }

        public Language[] newArray(int size) {
            return new Language[size];
        }
    };
}
