package com.ImmersiveGroundMarkers;

public class MarkerOption{
    public MarkerOption(String name, int modelID){
        this.name = name;
        this.modelID = modelID;
        this.orientationOffset = 0;
    }
    public MarkerOption(String name, int modelID, int orientationOffset){
        this.name = name;
        this.modelID = modelID;
        this.orientationOffset = orientationOffset;
    }
    String name;
    int modelID;
    int orientationOffset;
}
