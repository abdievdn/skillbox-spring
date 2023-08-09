package com.example.MyBookShopApp.data.google.api.books;

import lombok.Data;

import java.util.ArrayList;

@Data
public class VolumeInfo {
    public String title;
    public ArrayList<String> authors;
    public String publisher;
    public String publishedDate;
    public String description;
    public ArrayList<IndustryIdentifier> industryIdentifiers;
    public ReadingModes readingModes;
    public int pageCount;
    public String printType;
    public ArrayList<String> categories;
    public String maturityRating;
    public boolean allowAnonLogging;
    public String contentVersion;
    public PanelizationSummary panelizationSummary;
    public ImageLinks imageLinks;
    public String language;
    public String previewLink;
    public String infoLink;
    public String canonicalVolumeLink;
}
