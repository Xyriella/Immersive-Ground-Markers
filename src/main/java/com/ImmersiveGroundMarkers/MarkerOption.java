package com.ImmersiveGroundMarkers;

public class MarkerOption{
    public MarkerOption(String name, int modelID){
        this.name = name;
        this.modelID = modelID;
        this.orientationOffset = 0;
        this.animation = -1;
    }
    public MarkerOption(String name, int modelID, int orientationOffset){
        this.name = name;
        this.modelID = modelID;
        this.orientationOffset = orientationOffset;
        this.animation = -1;
    }
    public MarkerOption(String name, int modelID, int orientationOffset, int animation){
        this.name = name;
        this.modelID = modelID;
        this.orientationOffset = orientationOffset;
        this.animation = animation;
    }
    public MarkerOption(String name, int modelID, int orientationOffset, int animation, short[] colorsToFind, short[] colorsToReplace){
        this.name = name;
        this.modelID = modelID;
        this.orientationOffset = orientationOffset;
        this.animation = animation;
        this.colorsToFind = colorsToFind;
        this.colorsToReplace = colorsToReplace;
    }
    public MarkerOption(String name, int modelID, short[] colorsToFind, short[] colorsToReplace){
        this.name = name;
        this.modelID = modelID;
        this.orientationOffset = 0;
        this.animation = -1;
        this.colorsToFind = colorsToFind;
        this.colorsToReplace = colorsToReplace;
    }
    
    public MarkerOption(String name, int modelID, int orientationOffset, short[] colorsToFind, short[] colorsToReplace){
        this.name = name;
        this.modelID = modelID;
        this.orientationOffset = orientationOffset;
        this.animation = -1;
        this.colorsToFind = colorsToFind;
        this.colorsToReplace = colorsToReplace;
    }
    String name;
    int modelID;
    int orientationOffset;
    int animation;
    short[] colorsToFind;
    short[] colorsToReplace;
}
