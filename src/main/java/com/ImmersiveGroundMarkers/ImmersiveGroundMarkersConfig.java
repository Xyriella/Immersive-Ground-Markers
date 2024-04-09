package com.ImmersiveGroundMarkers;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("ImmersiveGroundMarkers")
public interface ImmersiveGroundMarkersConfig extends Config
{

	enum OrientationMethod {
        RANDOM,
        MATCH_PLAYER,
		OPPOSE_PLAYER,
		FACE_PLAYER,
		FACE_AWAY_PLAYER,
        NORTH,
        SOUTH,
        EAST,
        WEST;
	}

	@ConfigItem(
		keyName = "markerOrientation",
		name = "Default Marker Orientation",
		description = "How to set the orientation of the marker when marking a new tile"
	)
	default OrientationMethod markerOrientation(){
		return OrientationMethod.RANDOM;
	}

	@ConfigItem(
		keyName = "markerPack",
		name = "Marker Pack",
		description = "Set of markers to choose between"
	)
	default MarkerPack markerPack(){
		return MarkerPack.ROCKS;
	}

}
