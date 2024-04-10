package com.ImmersiveGroundMarkers;

import java.applet.Applet;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import com.ImmersiveGroundMarkers.ImmersiveGroundMarkersConfig.OrientationMethod;
import com.google.common.base.Strings;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
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
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.PostMenuSort;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ProfileChanged;
//import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

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
	private ClientToolbar clientToolbar;

	@Inject
	private Gson gson;

	private int lastPlane = -1;

	private int latestModel = -1;

	private boolean isPlacingTile = false;
	private int currentModelOrientationOffset = 0;
	private RuneLiteObject placingObject;

	Random rnd = new Random();

	private static final String CONFIG_GROUP = "immersiveGroundMarkers";
	private static final String REGION_PREFIX = "imregion_";
	private static final String WALK_HERE = "Walk here";
	private static final String ORIENTATION_CONFIG = "markerOrientation";

	private final List<MarkerPoint> markers = new ArrayList<>();
	private final LinkedHashMap<MarkerPoint, RuneLiteObject> objects = new LinkedHashMap<>();

	@Setter
	private boolean shiftPressed = false;

	@Setter
	private boolean escapePressed = false;

	private boolean clientHasFocus = false;

	private PropSelectPanel panel;
	private NavigationButton navButton;

	@Getter
	private OrientationMethod orientationMethod;

	public void setOrientationMethod(OrientationMethod newMethod){
		orientationMethod = newMethod;
		configManager.setConfiguration(CONFIG_GROUP, ORIENTATION_CONFIG, newMethod);
	}

	@Override
	protected void startUp() throws Exception
	{	

		orientationMethod = OrientationMethod.valueOf(configManager.getConfiguration(CONFIG_GROUP, ORIENTATION_CONFIG));
		if(orientationMethod == null){
			orientationMethod = config.markerOrientation();
			if(orientationMethod == null){
				orientationMethod = OrientationMethod.RANDOM;
			}
		}
		panel = new PropSelectPanel(this);
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");
		
		navButton = NavigationButton.builder()
		.tooltip("Immersive Ground Markers")
		.icon(icon)
		.panel(panel)
		.build();

		clientToolbar.addNavigation(navButton);


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

	private void stopPlacing(){
		isPlacingTile = false;
		latestModel = -1;
		placingObject.setActive(false);
		placingObject = null;
	}

	@Subscribe
	public void onClientTick(final ClientTick event){
		if(isPlacingTile && placingObject != null){
			final Tile hoveredTile = client.getSelectedSceneTile();
			placingObject.setLocation(hoveredTile.getLocalLocation(), hoveredTile.getPlane());
			if(orientationMethod != OrientationMethod.RANDOM) placingObject.setOrientation(getOrientation(currentModelOrientationOffset, hoveredTile.getLocalLocation()));
		}
		

		if (client.getCanvas().isFocusOwner()){
			if(!clientHasFocus){
				clientHasFocus = true;
			}else{
				escapePressed = client.isKeyPressed(KeyCode.KC_ESCAPE);
				shiftPressed = client.isKeyPressed(KeyCode.KC_SHIFT);
			}
		}else{
			clientHasFocus = false;
		}

		if(escapePressed && placingObject != null){
			stopPlacing();
		}
	}

	@Subscribe 
	public void onGameTick(GameTick event)
	{

		//TODO: Find a way to do as it's own subscribe. Similar to GameStateChanged?
		int newPlane = client.getPlane();
		if(newPlane != lastPlane){
			loadMarkers();
			lastPlane = newPlane;
		}
	}

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

	Collection<MarkerPoint> getPoints(int regionId){
		String json = configManager.getConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId);

		if(Strings.isNullOrEmpty((json))){
			return Collections.emptyList();
		}

		return gson.fromJson(json, new TypeToken<List<MarkerPoint>>(){}.getType());
	}
	
	void loadObjects(Collection<MarkerPoint> points){
		for(MarkerPoint marker : points){
			WorldPoint wp = WorldPoint.fromRegion(marker.getRegionID(), marker.getRegionX(), marker.getRegionY(), marker.getZ());
			Collection<WorldPoint> lWorldPoints = WorldPoint.toLocalInstance(client, wp);
			int modelId = marker.getModelId();
			
			Model model = client.loadModel(modelId);

			for( WorldPoint localWP : lWorldPoints){
				RuneLiteObject rlObj = client.createRuneLiteObject();
				LocalPoint modelLocation = LocalPoint.fromWorld(client, localWP);
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
	
				if( modelLocation == null ){ 
					log.debug("Failed to get local location");
					continue;
				}
	
				rlObj.setLocation(modelLocation, localWP.getPlane());
				rlObj.setActive(true);
				rlObj.setOrientation(marker.getOrientation());
	
				objects.put(marker, rlObj);
			}
		}
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
			Collection<MarkerPoint> regionPoints = getPoints(regionId);
			loadObjects(regionPoints);
			markers.addAll(regionPoints);
		}
		
	}

	void removeObjects(){
		for(MarkerPoint marker : markers){
			objects.get(marker).setActive(false);
		}
		Set<MarkerPoint> keys = objects.keySet();
		for(MarkerPoint key : keys){
			objects.get(key).setActive(false);
		}
	}

	void saveMarkers(int regionId, Collection<MarkerPoint> markers){
		if(markers == null || markers.isEmpty()){
			configManager.unsetConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId);
			return;
		}
		String json = gson.toJson(markers);
		configManager.setConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId, json);
	}

	private void remodelTile(MarkerPoint existing, int newModelId, LocalPoint localPoint){
		Collection<MarkerPoint> tempPoints = new ArrayList<>(getPoints(existing.getRegionID()));
		var newPoint = new MarkerPoint(existing.getRegionID(), existing.getRegionX(), existing.getRegionY(), existing.getZ(), newModelId, getOrientation(currentModelOrientationOffset, localPoint));
		tempPoints.remove(newPoint);
		tempPoints.add(newPoint);
		saveMarkers(existing.getRegionID(), tempPoints);
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

		int orientation = getOrientation(currentModelOrientationOffset, localPoint);

		MarkerPoint point = new MarkerPoint(regionId, worldPoint.getRegionX(), worldPoint.getRegionY(), worldPoint.getPlane(), modelId, orientation);
		log.debug("Updating point: {} - {}", point, worldPoint);

		List<MarkerPoint> tempPoints = new ArrayList<>(getPoints(regionId));
		if(tempPoints.contains(point)){
			tempPoints.remove(point);
			objects.get(point).setActive(false);
			objects.remove(point);
		}else{
			tempPoints.add(point);
		}

		saveMarkers(regionId, tempPoints);

		loadMarkers();
	}

	void startPlacingTile(int modelID, int orientationOffset){

		if (client.getGameState() != GameState.LOGGED_IN){
			return;
		}

		final Instant loadTimeOutInstant = Instant.now().plus(Duration.ofSeconds((5)));
		clientThread.invoke(() -> {
			if(Instant.now().isAfter(loadTimeOutInstant)){
				return true;
			}
			Model model = client.loadModel(modelID);
			placingObject = client.createRuneLiteObject();
			LocalPoint modelLocation = client.getLocalPlayer().getLocalLocation();

			if(model == null){
				return false;
			}
			placingObject.setModel(model);
			placingObject.setLocation(modelLocation, client.getLocalPlayer().getWorldLocation().getPlane());
			placingObject.setActive(true);
			placingObject.setOrientation(getOrientation(orientationOffset, modelLocation));
			isPlacingTile = true;
			currentModelOrientationOffset = orientationOffset;
			latestModel = modelID;
			return true;
		});
	}

	public int getOrientation(int orientationOffset, LocalPoint point){
		int xDiff;
		int yDiff;
		double angle;
		switch(orientationMethod){
			case EAST:
				return (512 + orientationOffset) % 2048;
			case FACE_AWAY_PLAYER:
				xDiff = client.getLocalPlayer().getLocalLocation().getX() - point.getX();
				yDiff = client.getLocalPlayer().getLocalLocation().getY() - point.getY();
				if(xDiff == 0 && yDiff == 0){
					return (client.getLocalPlayer().getOrientation() + orientationOffset) % 2048;
				}
				angle = 1.0 + Math.atan2(xDiff,yDiff)/(2.0*3.1415926536);
				return (orientationOffset + 1024 + (int)(angle * 2048)) % 2048;
			case FACE_PLAYER:
				xDiff = client.getLocalPlayer().getLocalLocation().getX() - point.getX();
				yDiff = client.getLocalPlayer().getLocalLocation().getY() - point.getY();
				if(xDiff == 0&& yDiff == 0){
					return (client.getLocalPlayer().getOrientation() + orientationOffset + 1024) % 2048;
				}
				angle = 1.0 + Math.atan2(xDiff,yDiff)/(2.0*3.1415926536);
				return (orientationOffset + (int)(angle * 2048)) % 2048;
			case MATCH_PLAYER:
				return (client.getLocalPlayer().getOrientation() + orientationOffset) % 2048;
			case NORTH:
				return orientationOffset;
			case OPPOSE_PLAYER:
				return (client.getLocalPlayer().getOrientation() + orientationOffset + 1024) % 2048;
			case RANDOM:
				return rnd.nextInt(2048);
			case SOUTH:
				return (1024 + orientationOffset) % 2048;
			case WEST:
				return (1536 + orientationOffset) % 2048;
			default:
				return orientationOffset;

		}
	}

	@Subscribe
	public void onPostMenuSort(PostMenuSort postMenuSort){
		if (client.isMenuOpen())
		{
			return;
		}

		final Tile selectedSceneTile = client.getSelectedSceneTile();
		if(selectedSceneTile == null){
			return;
		}

		MenuEntry[] menuEntries = client.getMenuEntries();
		boolean isWalkable = false;
		for (MenuEntry menuEntry : menuEntries) {
			if(menuEntry.getOption().equals(WALK_HERE)){
				isWalkable = true;
				break;
			}
		}
		if(!isWalkable) return;

		if(isPlacingTile && latestModel != -1){

			final WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, selectedSceneTile.getLocalLocation());
			final int regionID = worldPoint.getRegionID();
			var regionPoints = getPoints(regionID);
			var existingOpt = regionPoints.stream()
				.filter(p -> p.getRegionX() == worldPoint.getRegionX() && p.getRegionY() == worldPoint.getRegionY() && p.getZ() == worldPoint.getPlane())
				.findFirst();

			if(existingOpt.isPresent()){
				MarkerPoint existing = existingOpt.get();
				client.createMenuEntry(-1)
				.setOption("Redecorate")
				.setTarget("Tile")
				.setType(MenuAction.RUNELITE_HIGH_PRIORITY)
				.onClick(e -> {
					Tile target = client.getSelectedSceneTile();
					remodelTile(existing, latestModel, target.getLocalLocation());
					if(!shiftPressed){
						stopPlacing();
					}
				});
			}else{
				client.createMenuEntry(-1)
				.setOption("Decorate")
				.setTarget("Tile")
				.setType(MenuAction.RUNELITE_HIGH_PRIORITY)
				.onClick(e -> {
					Tile target = client.getSelectedSceneTile();
					if (target != null)
					{	
						markTile(target.getLocalLocation(), latestModel);
					}
					if(!shiftPressed){
						stopPlacing();
					}
				});
			}

		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		final boolean hotKeyPressed = client.isKeyPressed(KeyCode.KC_SHIFT);
		if(event.getOption().equals(WALK_HERE) && (isPlacingTile || hotKeyPressed)){
			final Tile selectedSceneTile = client.getSelectedSceneTile();
			if(selectedSceneTile == null){
				return;
			}

			final WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, selectedSceneTile.getLocalLocation());
			final int regionID = worldPoint.getRegionID();
			var regionPoints = getPoints(regionID);
			var existingOpt = regionPoints.stream()
				.filter(p -> p.getRegionX() == worldPoint.getRegionX() && p.getRegionY() == worldPoint.getRegionY() && p.getZ() == worldPoint.getPlane())
				.findFirst();
				
			if (hotKeyPressed && existingOpt.isPresent()){

				client.createMenuEntry(-1)
				.setOption("Remove Prop From")
				.setTarget("Tile")
				.setType(MenuAction.RUNELITE)
				.onClick(e ->
				{
					Tile target = client.getSelectedSceneTile();
					if (target != null)
					{	
						markTile(target.getLocalLocation(), 1);
					}
				});
		
			}
		}
	}
}