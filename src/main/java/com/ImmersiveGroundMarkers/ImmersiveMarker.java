package com.ImmersiveGroundMarkers;

import net.runelite.api.coords.WorldPoint;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(exclude = {"id"})
public class ImmersiveMarker{

    public ImmersiveMarker(int modelId, WorldPoint worldPoint, int orientation){
        this.id = modelId;
        this.worldPoint = worldPoint;
        this.orientation = orientation;
    }

    public WorldPoint getWorldPoint(){
        return worldPoint;
    }

    public int getModelId(){
        return id;
    }

    public int getOrientation(){
        return orientation;
    }

    public void setModelId(int modelId){
        this.id = modelId;
    }

    private WorldPoint worldPoint;
    private int id;
    private int orientation;
}