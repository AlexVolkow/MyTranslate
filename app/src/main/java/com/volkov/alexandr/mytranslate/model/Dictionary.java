package com.volkov.alexandr.mytranslate.model;

import java.util.*;

/**
 * Created by AlexandrVolkov on 14.07.2017.
 */
public class Dictionary {
    public static final Dictionary EMPTY = new Dictionary("");

    private String text;
    private String trans;
    private Map<String, List<Translate>> trs = new TreeMap<>(Collections.<String>reverseOrder());

    public Dictionary(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setTranscription(String trans) {
        this.trans = trans;
    }

    public void addTranslate(String pos, Translate translate) {
        if (!trs.containsKey(pos)) {
            trs.put(pos, new ArrayList<Translate>());
        }
        trs.get(pos).add(translate);
    }

    public Map<String, List<Translate>> getTranslates() {
        return trs;
    }

    public String getTranscription() {
        return trans;
    }

    @Override
    public String toString() {
        return text + (trans != null ? "[" + trans + "]" : "") + "(number of translations " + trs.size() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dictionary)) return false;

        Dictionary that = (Dictionary) o;

        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (trans != null ? !trans.equals(that.trans) : that.trans != null) return false;
        return trs != null ? trs.equals(that.trs) : that.trs == null;
    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + (trans != null ? trans.hashCode() : 0);
        result = 31 * result + (trs != null ? trs.hashCode() : 0);
        return result;
    }

    public static class Translate {
        private String text;
        private String gen;
        private List<String> means = new ArrayList<>();

        public Translate(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setGender(String gen) {
            this.gen = gen;
        }

        public void addMean(String mean) {
            means.add(mean);
        }

        public String getGender() {
            return gen;
        }

        public List<String> getMeans() {
            return means;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Translate)) return false;

            Translate translate = (Translate) o;

            if (text != null ? !text.equals(translate.text) : translate.text != null) return false;
            return gen != null ? gen.equals(translate.gen) : translate.gen == null;
        }

        @Override
        public int hashCode() {
            int result = text != null ? text.hashCode() : 0;
            result = 31 * result + (gen != null ? gen.hashCode() : 0);
            return result;
        }
    }
}
