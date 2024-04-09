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

	default OrientationMethod markerOrientation(){
		return OrientationMethod.RANDOM;
	}

	default MarkerPack markerPack(){
		return MarkerPack.ROCKS;
	}

}
