package com.example.MyBookShopApp.data.google.api.books;

import lombok.Data;


// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
@Data
public class AccessInfo{
    public String country;
    public String viewability;
    public boolean embeddable;
    public boolean publicDomain;
    public String textToSpeechPermission;
    public Epub epub;
    public Pdf pdf;
    public String webReaderLink;
    public String accessViewStatus;
    public boolean quoteSharingAllowed;
}

