package com.a3nitysoft.kelvin.tellme;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class NotifId {

    @Exclude
    public String NotifId;

    public <T extends NotifId> T withId(@NonNull final String id) {
        this.NotifId = id;
        return (T) this;
    }
}
