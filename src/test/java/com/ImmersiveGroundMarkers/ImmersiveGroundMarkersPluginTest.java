package com.ImmersiveGroundMarkers;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ImmersiveGroundMarkersPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ImmersiveGroundMarkersPlugin.class);
		RuneLite.main(args);
	}
}