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
		keyName = "ModelName1",
		name = "1st Model Name",
		description = "Readable name for 1st model"
	)
	default String ModelName1()
	{
		return "Arrow";
	}

	@ConfigItem(
		keyName = "ModelID1",
		name = "1st Model ID",
		description = "Model ID for 1st model"
	)
	default int ModelID1()
	{
		return 5125;
	}

	@ConfigItem(
		keyName = "ModelName2",
		name = "2nd Model Name",
		description = "Readable name for 2nd model"
	)
	default String ModelName2()
	{
		return "Cross";
	}

	@ConfigItem(
		keyName = "ModelID2",
		name = "2nd Model ID",
		description = "Model ID for 2nd model"
	)
	default int ModelID2()
	{
		return 5139;
	}

	@ConfigItem(
		keyName = "ModelName3",
		name = "3rd Model Name",
		description = "Readable name for 3rd model"
	)
	default String ModelName3()
	{
		return "Skull";
	}

	@ConfigItem(
		keyName = "ModelID3",
		name = "3rd Model ID",
		description = "Model ID for 3rd model"
	)
	default int ModelID3()
	{
		return 2388;
	}

	@ConfigItem(
		keyName = "ModelName4",
		name = "4th Model Name",
		description = "Readable name for 4th model"
	)
	default String ModelName4()
	{
		return "Shadow";
	}

	@ConfigItem(
		keyName = "ModelID4",
		name = "4th Model ID",
		description = "Model ID for 4th model"
	)
	default int ModelID4()
	{
		return 5077;
	}

	@ConfigItem(
		keyName = "ModelName5",
		name = "5th Model Name",
		description = "Readable name for 5th model"
	)
	default String ModelName5()
	{
		return "Circle";
	}

	@ConfigItem(
		keyName = "ModelID5",
		name = "5th Model ID",
		description = "Model ID for 5th model"
	)
	default int ModelID5()
	{
		return 4240;
	}

}
