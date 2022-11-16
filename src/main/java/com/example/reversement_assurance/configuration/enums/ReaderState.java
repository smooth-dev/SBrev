package com.example.reversement_assurance.configuration.enums;

public enum ReaderState {
    NEW("NEW"),
    READING("READING"),
    COMPLETE("COMPLETE");

    private String state;

    ReaderState(String state) {
        this.state = state;
    }



}
