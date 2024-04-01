package com.ImmersiveGroundMarkers;


import java.applet.Applet;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import com.ImmersiveGroundMarkers.ImmersiveGroundMarkersConfig.OrientationMethod;
import com.google.common.base.Strings;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.KeyCode;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Model;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
//import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Immersive Ground Markers"
)
public class ImmersiveGroundMarkersPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ImmersiveGroundMarkersConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ChatboxPanelManager chatboxPanelManager;

	@Inject
	private Gson gson;

	Random rnd = new Random();

	private static final String CONFIG_GROUP = "immersiveGroundMarkers";
	private static final String REGION_PREFIX = "imregion_";
	private static final String WALK_HERE = "Walk here";

	private final List<ImmersiveMarker> markers = new ArrayList<>();
	private final LinkedHashMap<WorldPoint, RuneLiteObject> objects = new LinkedHashMap<>();

	@Override
	protected void startUp() throws Exception
	{
		loadMarkers();
	}

	@Override
	protected void shutDown() throws Exception
	{
		removeObjects();
	}

	@Subscribe
	public void onProfileChanged(ProfileChanged profileChanged)
	{
		loadMarkers();
	}

	/*@Subscribe //Code for searching models
	public void onGameTick(GameTick event)
	{
		final boolean shiftPressed = client.isKeyPressed(KeyCode.KC_SHIFT);
		final boolean ctrlPressed = client.isKeyPressed(KeyCode.KC_CONTROL);
		if(shiftPressed || ctrlPressed){
			WorldPoint wp = client.getLocalPlayer().getWorldLocation();
			AtomicInteger counter = new AtomicInteger(-1);
			int index = markers.stream()
			.filter(marker -> {
				counter.getAndIncrement();
				return marker.getWorldPoint().equals(wp);
			})
			.mapToInt(user -> counter.get())
			.findFirst()
			.orElse(-1);
			if(index == -1){
				log.info("Tile has no model");
				return;
			}
			ImmersiveMarker mk = markers.get(index);
			clientThread.invoke(() -> {remodelTile(mk, mk.getModelId()+ (shiftPressed ? 1 : -1));});
		}
	}*/

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		// map region has just been updated
		loadMarkers();
	}

	@Provides
	ImmersiveGroundMarkersConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ImmersiveGroundMarkersConfig.class);
	}

	Collection<ImmersiveMarker> getPoints(int regionId){
		String json = configManager.getConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId);

		if(Strings.isNullOrEmpty((json))){
			return Collections.emptyList();
		}

		return gson.fromJson(json, new TypeToken<List<ImmersiveMarker>>(){}.getType());
	}

	void loadMarkers(){
		markers.clear();
		removeObjects();
		objects.clear();

		int[] regions = client.getMapRegions();

		if(regions == null){
			return;
		}

		for(int regionId : regions){
			log.debug("Loading points for region {}", regionId);

			markers.addAll(getPoints(regionId));
		}

		for(ImmersiveMarker marker : markers){
			int modelId = marker.getModelId();
			WorldPoint worldPoint = marker.getWorldPoint();
			RuneLiteObject rlObj = client.createRuneLiteObject();
			Model model = client.loadModel(modelId);
			if(model == null){
				final Instant loadTimeOutInstant = Instant.now().plus(Duration.ofSeconds((5)));
				clientThread.invoke(() -> {
					if(Instant.now().isAfter(loadTimeOutInstant)){
						return true;
					}
					Model reloadedModel = client.loadModel(modelId);

					if(reloadedModel == null){
						return false;
					}
					rlObj.setModel(reloadedModel);
					return true;
				});
			}else{
				rlObj.setModel(model);
			}
			LocalPoint modelLocation = LocalPoint.fromWorld(client, worldPoint);
			if( modelLocation == null ) return;
			rlObj.setLocation(modelLocation, worldPoint.getPlane());
			rlObj.setActive(true);
			rlObj.setOrientation(marker.getOrientation());
			rlObj.setModelHeight(-100);
			objects.put(worldPoint, rlObj);
		}
	}

	void removeObjects(){
		for(ImmersiveMarker marker : markers){
			objects.get(marker.getWorldPoint()).setActive(false);
		}
		Set<WorldPoint> keys = objects.keySet();
		for(WorldPoint key : keys){
			objects.get(key).setActive(false);
		}
	}

	void saveMarkers(int regionId, Collection<ImmersiveMarker> markers){
		if(markers == null || markers.isEmpty()){
			configManager.unsetConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId);
			return;
		}
		String json = gson.toJson(markers);
		configManager.setConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId, json);
	}

	private void remodelTile(ImmersiveMarker tile, int modelId){
		AtomicInteger counter = new AtomicInteger(-1);
		int index = markers.stream()
		.filter(marker -> {
			counter.getAndIncrement();
			return marker.getWorldPoint().equals(tile.getWorldPoint());
		})
		.mapToInt(user -> counter.get())
		.findFirst()
		.orElse(-1);
		if(index == -1){
			log.info("Tile has no model");
			return;
		}
		tile.setModelId(modelId);
		markers.set(index, tile);
		saveMarkers(tile.getWorldPoint().getRegionID(), markers);
		loadMarkers();
	}

	private void markTile(LocalPoint localPoint, int modelId)
	{	
		if (localPoint == null)
		{
			return;
		}

		WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, localPoint);

		int regionId = worldPoint.getRegionID();
		int orientation = 0;
		OrientationMethod method = config.markerOrientation();
		switch(method){
			case EAST:
				orientation = 512;
				break;
			case MATCH_PLAYER:
				orientation = (client.getLocalPlayer().getOrientation() + 1024)%2048;
				break;
			case NORTH:
				orientation = 0;
				break;
			case RANDOM:
				orientation = rnd.nextInt(2048);
				break;
			case SOUTH:
				orientation = 1024;
				break;
			case WEST:
				orientation = 1536;
				break;
			default:
				orientation = 0;
				break;
			
		}

		ImmersiveMarker point = new ImmersiveMarker(modelId, worldPoint, orientation);
		log.debug("Updating point: {} - {}", point, worldPoint);

		List<ImmersiveMarker> groundMarkerPoints = new ArrayList<>(getPoints(regionId));
		AtomicInteger counter = new AtomicInteger(-1);
		int index = markers.stream()
			.filter(marker -> {
				counter.getAndIncrement();
				return marker.getWorldPoint().equals(worldPoint);
			})
			.mapToInt(user -> counter.get())
			.findFirst()
			.orElse(-1);
		if (index != -1)
		{
			WorldPoint key = point.getWorldPoint();
			objects.get(key).setActive(false);
			objects.remove(key);
			groundMarkerPoints.remove(index);
		}
		else
		{
			groundMarkerPoints.add(point);
		}

		saveMarkers(regionId, groundMarkerPoints);

		loadMarkers();
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		final boolean hotKeyPressed = client.isKeyPressed(KeyCode.KC_SHIFT);
		if (hotKeyPressed && event.getOption().equals(WALK_HERE))
		{
			final Tile selectedSceneTile = client.getSelectedSceneTile();

			if (selectedSceneTile == null)
			{
				return;
			}

			final WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, selectedSceneTile.getLocalLocation());
			final int regionId = worldPoint.getRegionID();
			var regionPoints = getPoints(regionId);
			var existingOpt = regionPoints.stream()
				.filter(p -> p.getWorldPoint().equals(worldPoint))
				.findFirst();

			client.createMenuEntry(-1)
				.setOption(existingOpt.isPresent() ? "Unmark" : "Mark")
				.setTarget("Tile")
				.setType(MenuAction.RUNELITE)
				.onClick(e ->
				{
					Tile target = client.getSelectedSceneTile();
					if (target != null)
					{
						markTile(target.getLocalLocation(), config.ModelID1());
					}
				});

			if (existingOpt.isPresent())
			{
				ImmersiveMarker existing = existingOpt.get();

				MenuEntry propSelect = client.createMenuEntry(-2)
					.setOption("Remodel")
					.setTarget("Tile")
					.setType(MenuAction.RUNELITE_SUBMENU);

				client.createMenuEntry(-3)
					.setOption(config.ModelName1())
					.setType(MenuAction.RUNELITE)
					.setParent(propSelect)
					.onClick(e -> remodelTile(existing, config.ModelID1()));
				client.createMenuEntry(-3)
					.setOption(config.ModelName2())
					.setType(MenuAction.RUNELITE)
					.setParent(propSelect)
					.onClick(e -> remodelTile(existing, config.ModelID2()));
				client.createMenuEntry(-3)
					.setOption(config.ModelName3())
					.setType(MenuAction.RUNELITE)
					.setParent(propSelect)
					.onClick(e -> remodelTile(existing, config.ModelID3()));
				client.createMenuEntry(-3)
					.setOption(config.ModelName4())
					.setType(MenuAction.RUNELITE)
					.setParent(propSelect)
					.onClick(e -> remodelTile(existing, config.ModelID4()));
					client.createMenuEntry(-3)
						.setOption(config.ModelName5())
						.setType(MenuAction.RUNELITE)
						.setParent(propSelect)
						.onClick(e -> remodelTile(existing, config.ModelID5()));
				client.createMenuEntry(-2)
				.setOption("Other")
				.setType(MenuAction.RUNELITE)
				.setParent(propSelect)
				.onClick(e ->
				{	
					chatboxPanelManager.openTextInput("Model ID")
					.value(Integer.toString(existing.getModelId()))
					.charValidator(num -> num >= (int)'0' && num <= (int)'9')
					.onDone(input -> {
						int newModelId = Integer.parseInt(input);
						clientThread.invoke(() -> {remodelTile(existing, newModelId);});
					}).build();
				});
			}
		}
	}
}
