package com.ImmersiveGroundMarkers;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("ImmersiveGroundMarkers")
public interface ImmersiveGroundMarkersConfig extends Config
{

	@ConfigItem(
		keyName = "ModelName1",
		name = "1st Model Name",
		description = "Readable name for 1st model"
	)
	default String ModelName1()
	{
		return "Skull";
	}

	@ConfigItem(
		keyName = "ModelID1",
		name = "1st Model ID",
		description = "Model ID for 1st model"
	)
	default int ModelID1()
	{
		return 1234;
	}

	@ConfigItem(
		keyName = "ModelName2",
		name = "2nd Model Name",
		description = "Readable name for 2nd model"
	)
	default String ModelName2()
	{
		return "Rocks";
	}

	@ConfigItem(
		keyName = "ModelID2",
		name = "2nd Model ID",
		description = "Model ID for 2nd model"
	)
	default int ModelID2()
	{
		return 4321;
	}

	@ConfigItem(
		keyName = "ModelName3",
		name = "3rd Model Name",
		description = "Readable name for 3rd model"
	)
	default String ModelName3()
	{
		return "Arrow";
	}

	@ConfigItem(
		keyName = "ModelID3",
		name = "3rd Model ID",
		description = "Model ID for 3rd model"
	)
	default int ModelID3()
	{
		return 1000;
	}

	@ConfigItem(
		keyName = "ModelName4",
		name = "4th Model Name",
		description = "Readable name for 4th model"
	)
	default String ModelName4()
	{
		return "Star";
	}

	@ConfigItem(
		keyName = "ModelID4",
		name = "4th Model ID",
		description = "Model ID for 4th model"
	)
	default int ModelID4()
	{
		return 4242;
	}
}
