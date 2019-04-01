package com.cruz.fyp.virtualassistant.Azure.SpeechSynthesis;

public class Voice {

    final String lang;
    final String voiceName;
    final Gender gender;

    public enum Gender {
        MALE, FEMALE
    }

    Voice(String lang) {
        this.lang = lang;
        this.voiceName = "";
        this.gender = Gender.FEMALE;
    }

    public Voice(String lang, String voiceName, Gender gender) {
        this.lang = lang;
        this.voiceName = voiceName;
        this.gender = gender;
    }
}
