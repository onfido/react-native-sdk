package com.onfido.reactnative.sdk;

/*
    Good guy Java doesn't have either .withIndex() , or the (Kotlin) Pair, so here is a dedicated class
    to be used for mapping, to simulate the Kotlin Pair
 */
public class Pair {
    int index;
    byte[] data;

    public Pair(int index, byte[] data) {
        this.index = index;
        this.data = data;
    }
}
