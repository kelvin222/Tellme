package com.a3nitysoft.kelvin.tellme;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class FeedId {
    @Exclude
    public String FeedId;

    public <T extends FeedId> T withId(@NonNull final String id) {
        this.FeedId = id;
        return (T) this;
    }
}
