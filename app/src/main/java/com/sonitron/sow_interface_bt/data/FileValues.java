package com.sonitron.sow_interface_bt.data;

class FileValues {
    private final String fileName;
    private final String date;

    public FileValues(String fileName, String date) {
        this.fileName = fileName;
        this.date = date;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDate() {
        return date;
    }
}

