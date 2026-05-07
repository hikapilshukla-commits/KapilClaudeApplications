package com.claude.tool;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class DateTimeTool {

    // Define an Enum for the allowed formats

    @JsonPropertyDescription("The Allowed format to return the date in yyyy-MM-dd,dd-MM-yyyy,MM-dd-yyyy HH:mm:ss")
    @JsonProperty("format")
    public String format;
    public String   getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getCurrentDateTime() {
        if(!(format.equals("yyyy-MM-dd")||format.equals("dd-MM-yyyy")||format.equals("MM-dd-yyyy HH:mm:ss"))){
            try {
                throw new Exception("Send the right datetime format");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // Now 'format' is guaranteed to be one of your 3 options
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.now().format(formatter);
    }
}

