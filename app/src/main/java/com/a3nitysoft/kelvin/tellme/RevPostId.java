package com.a3nitysoft.kelvin.tellme;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class RevPostId {
    @Exclude
    public String RevPostId;

    public <T extends RevPostId> T withId(@NonNull final String id) {
        this.RevPostId = id;
        return (T) this;
    }
}
