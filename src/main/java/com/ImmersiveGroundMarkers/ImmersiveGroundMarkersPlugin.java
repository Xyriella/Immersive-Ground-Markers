/*
 * Copyright (c) 2021, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ImmersiveGroundMarkers;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import com.ImmersiveGroundMarkers.ImmersiveGroundMarkersConfig.OrientationMethod;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.Runnables;

import javax.inject.Inject;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Animation;
import net.runelite.api.ChatMessageType;
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
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
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
	private ChatMessageManager chatMessageManager;

	@Inject ChatboxPanelManager chatboxPanelManager;

	@Inject
	private Gson gson;

	private int lastPlane = -1;

	private MarkerOption markerToPlace = null;

	private boolean isPlacingTile = false;
	//private int currentModelOrientationOffset = 0;
	private RuneLiteObject placingObject;

	Random rnd = new Random();

	private static final String CONFIG_GROUP = "immersiveGroundMarkers";
	private static final String REGION_PREFIX = "imregion_";
	private static final String WALK_HERE = "Walk here";
	private static final String ORIENTATION_CONFIG = "markerOrientation";

	private final List<MarkerPoint> markers = new ArrayList<>();
	private final Map<MarkerPoint, List<RuneLiteObject>> objects = new LinkedHashMap<>();

	@Setter
	private boolean shiftPressed = false;

	@Setter
	private boolean escapePressed = false;

	private boolean clientHasFocus = false;

	private PropSelectPanel panel;
	private NavigationButton navButton;

	public void clearMarkers(){
		int[] regions = client.getMapRegions();
		if(regions == null){
			return;
		}
		for(int region : regions){
			configManager.unsetConfiguration(CONFIG_GROUP, REGION_PREFIX + region);
		}
		removeObjects();
		loadMarkers();
	}

	//Copy of Runelite ground markers export
	public void exportMarkers(){
		int[] regions = client.getMapRegions();
		if(regions == null){
			return;
		}

		List<MarkerPoint> activePoints = Arrays.stream(regions)
			.mapToObj(region -> getPoints(region).stream())
			.flatMap(Function.identity())
			.collect(Collectors.toList());

		if(activePoints.isEmpty()){
			sendChatMessage("You have no ground markers to export.");
			return;
		}

		final String exportDump = gson.toJson(activePoints);

		Toolkit.getDefaultToolkit()
			.getSystemClipboard()
			.setContents(new StringSelection(exportDump), null);
		
		sendChatMessage(activePoints.size() + " ground markers were copied to your clipboard.");
	}

	protected void promptForImport()
	{
		final String clipboardText;
		try
		{
			clipboardText = Toolkit.getDefaultToolkit()
				.getSystemClipboard()
				.getData(DataFlavor.stringFlavor)
				.toString();
		}
		catch (IOException | UnsupportedFlavorException ex)
		{
			sendChatMessage("Unable to read system clipboard.");
			log.warn("error reading clipboard", ex);
			return;
		}

		log.debug("Clipboard contents: {}", clipboardText);
		if (Strings.isNullOrEmpty(clipboardText))
		{
			sendChatMessage("You do not have any ground markers copied in your clipboard.");
			return;
		}

		List<MarkerPoint> importPoints;
		try
		{
			// CHECKSTYLE:OFF
			importPoints = gson.fromJson(clipboardText, new TypeToken<List<MarkerPoint>>(){}.getType());
			// CHECKSTYLE:ON
		}
		catch (JsonSyntaxException e)
		{
			log.debug("Malformed JSON for clipboard import", e);
			sendChatMessage("You do not have any ground markers copied in your clipboard.");
			return;
		}

		if (importPoints.isEmpty())
		{
			sendChatMessage("You do not have any ground markers copied in your clipboard.");
			return;
		}

		chatboxPanelManager.openTextMenuInput("Are you sure you want to import " + importPoints.size() + " ground markers?")
			.option("Yes", () -> importGroundMarkers(importPoints))
			.option("No", Runnables.doNothing())
			.build();
	}

	public void importGroundMarkers(List<MarkerPoint> points){
		// regions being imported may not be loaded on client,
		// so need to import each bunch directly into the config
		// first, collate the list of unique region ids in the import
		Map<Integer, List<MarkerPoint>> regionGroupedPoints = points.stream()
			.collect(Collectors.groupingBy(MarkerPoint::getRegionID));

		// now import each region into the config
		regionGroupedPoints.forEach((regionId, groupedPoints) ->
		{
			// combine imported points with existing region points
			log.debug("Importing {} points to region {}", groupedPoints.size(), regionId);
			Collection<MarkerPoint> regionPoints = getPoints(regionId);

			List<MarkerPoint> mergedList = new ArrayList<>(regionPoints.size() + groupedPoints.size());
			// add existing points
			mergedList.addAll(regionPoints);

			// add new points
			for (MarkerPoint point : groupedPoints)
			{
				// filter out duplicates
				if (!mergedList.contains(point))
				{
					mergedList.add(point);
				}
			}

			saveMarkers(regionId, mergedList);
		});

		// reload points from config
		log.debug("Reloading points after import");
		loadMarkers();
		sendChatMessage(points.size() + " ground markers were imported from the clipboard.");
	}

	public void sendChatMessage(final String message){
		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.CONSOLE)
			.runeLiteFormattedMessage(message)
			.build()
			);
	}

	public OrientationMethod getOrientationMethod(){
		return config.markerOrientation();
	}

	public void setOrientationMethod(OrientationMethod newMethod){
		configManager.setConfiguration(CONFIG_GROUP, ORIENTATION_CONFIG, newMethod);
	}
	
	@Override
	protected void startUp() throws Exception
	{
		panel = new PropSelectPanel(this, chatboxPanelManager);
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");
		
		navButton = NavigationButton.builder()
		.tooltip("Immersive Ground Markers")
		.icon(icon)
		.panel(panel)
		.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		removeObjects();
		clientToolbar.removeNavigation(navButton);
	}

	@Subscribe
	public void onProfileChanged(ProfileChanged profileChanged)
	{
		loadMarkers();
	}

	private void stopPlacing(){
		isPlacingTile = false;
		markerToPlace = null;
		placingObject.setActive(false);
		placingObject = null;
	}

	@Subscribe
	public void onClientTick(final ClientTick event){
		if(isPlacingTile && placingObject != null){
			final Tile hoveredTile = client.getSelectedSceneTile();
			placingObject.setLocation(hoveredTile.getLocalLocation(), hoveredTile.getPlane());
			if( config.markerOrientation() != OrientationMethod.RANDOM) placingObject.setOrientation(getOrientation(markerToPlace.orientationOffset, hoveredTile.getLocalLocation()));
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

	@Subscribe
	public void onConfigChanged(ConfigChanged event){
		if(event.getGroup() == CONFIG_GROUP){
			panel.reselectOrientationButton();
		}
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
			int animationId = marker.getAnimation();
			short[] findColors = marker.getColorsToFind();
			short[] replaceColors = marker.getColorsToReplace();

			if( !client.isClientThread() ){
				log.debug("Not on client thread");
				return;
			}

			Model model = client.loadModel(modelId, findColors, replaceColors);
			if(model == null) continue;
			Animation modelAnim = client.loadAnimation(animationId);
				

			for( WorldPoint localWP : lWorldPoints){
				RuneLiteObject rlObj = client.createRuneLiteObject();
				LocalPoint modelLocation = LocalPoint.fromWorld(client, localWP);
				if(model == null){
					final Instant loadTimeOutInstant = Instant.now().plus(Duration.ofSeconds((5)));
					clientThread.invoke(() -> {
						if(Instant.now().isAfter(loadTimeOutInstant)){
							return true;
						}
						Model reloadedModel = client.loadModel(modelId, findColors, replaceColors);
						Animation reloadedAnimation = null;
						reloadedAnimation = client.loadAnimation(animationId);
						if(reloadedAnimation == null){
							return false;
						}
						if(reloadedModel == null){
							return false;
						}
						rlObj.setModel(reloadedModel);
						rlObj.setAnimation(reloadedAnimation);
						rlObj.setShouldLoop(true);
						return true;
					});
				}else{
					rlObj.setModel(model);
					rlObj.setAnimation(modelAnim);
					rlObj.setShouldLoop(true);
				}
	
				if( modelLocation == null ){ 
					log.debug("Failed to get local location");
					continue;
				}
	
				rlObj.setLocation(modelLocation, localWP.getPlane());
				rlObj.setActive(true);
				rlObj.setOrientation(marker.getOrientation());
				
				if(!objects.containsKey(marker)){
					List<RuneLiteObject> newList = new ArrayList<>();
					objects.put(marker, newList);
				}
				objects.get(marker).add(rlObj);
			}
		}
	}

	void loadMarkers(){
		removeObjects();

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
		Set<MarkerPoint> keys = objects.keySet();
		for(MarkerPoint key : keys){
			for(RuneLiteObject obj : objects.get(key)){
				obj.setActive(false);
			}
		}
		objects.clear();
		markers.clear();
	}

	void saveMarkers(int regionId, Collection<MarkerPoint> markers){
		if(markers == null || markers.isEmpty()){
			configManager.unsetConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId);
			return;
		}
		String json = gson.toJson(markers);
		configManager.setConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId, json);
	}

	private void remodelTile(MarkerPoint existing, MarkerOption newMarker, LocalPoint localPoint){
		Collection<MarkerPoint> tempPoints = new ArrayList<>(getPoints(existing.getRegionID()));
		var newPoint = new MarkerPoint(
			existing.getRegionID(), 
			existing.getRegionX(), 
			existing.getRegionY(), 
			existing.getZ(), 
			newMarker.modelID, 
			getOrientation(newMarker.orientationOffset, localPoint), 
			newMarker.animation, 
			newMarker.colorsToReplace, 
			newMarker.colorsToFind);
		tempPoints.remove(newPoint);
		tempPoints.add(newPoint);
		saveMarkers(existing.getRegionID(), tempPoints);
		loadMarkers();
	}

	private void markTile(LocalPoint localPoint, MarkerOption markerInfo)
	{	
		if (localPoint == null)
		{
			return;
		}
		
		WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, localPoint);

		int regionId = worldPoint.getRegionID();

		List<MarkerPoint> tempPoints = new ArrayList<>(getPoints(regionId));

		if(markerInfo == null){
			MarkerPoint point = new MarkerPoint(
			regionId, 
			worldPoint.getRegionX(), 
			worldPoint.getRegionY(),
			worldPoint.getPlane(), 
			0, 
			0, 
			0, 
			null, 
			null);
			tempPoints.remove(point);
			List<RuneLiteObject> affectedObjects = objects.get(point);
			for (RuneLiteObject obj : affectedObjects) {
				obj.setActive(false);
			}
			objects.remove(point);

			saveMarkers(regionId, tempPoints);
	
			loadMarkers();
			return;
		}


		int orientation = getOrientation(markerInfo.orientationOffset, localPoint);

		MarkerPoint point = new MarkerPoint(
			regionId, 
			worldPoint.getRegionX(), 
			worldPoint.getRegionY(),
			worldPoint.getPlane(), 
			markerInfo.modelID, 
			orientation, 
			markerInfo.animation, 
			markerInfo.colorsToReplace, 
			markerInfo.colorsToFind);
		log.debug("Updating point: {} - {}", point, worldPoint);

		tempPoints.add(point);

		saveMarkers(regionId, tempPoints);

		loadMarkers();
	}

	void startPlacingTile(MarkerOption newMarker){

		if (client.getGameState() != GameState.LOGGED_IN){
			return;
		}

		final Instant loadTimeOutInstant = Instant.now().plus(Duration.ofSeconds((5)));
		clientThread.invoke(() -> {
			if(Instant.now().isAfter(loadTimeOutInstant)){
				return true;
			}
			Model model; ;
			if(newMarker.colorsToFind == null || newMarker.colorsToFind.length == 0 || newMarker.colorsToReplace == null || newMarker.colorsToReplace.length == 0 || newMarker.colorsToFind.length != newMarker.colorsToReplace.length){
				model = client.loadModel(newMarker.modelID);
			}else{
				model = client.loadModel(newMarker.modelID, newMarker.colorsToFind, newMarker.colorsToReplace);
			}
			placingObject = client.createRuneLiteObject();
			LocalPoint modelLocation = client.getLocalPlayer().getLocalLocation();

			if(model == null){
				return false;
			}
			placingObject.setModel(model);
			placingObject.setLocation(modelLocation, client.getLocalPlayer().getWorldLocation().getPlane());
			placingObject.setActive(true);
			placingObject.setOrientation(getOrientation(newMarker.orientationOffset, modelLocation));
			isPlacingTile = true;
			markerToPlace = newMarker;
			return true;
		});
	}

	public int getOrientation(int orientationOffset, LocalPoint point){
		int xDiff;
		int yDiff;
		double angle;
		switch(config.markerOrientation()){
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
				return (client.getLocalPlayer().getOrientation() + orientationOffset + 1024) % 2048;
			case NORTH:
				return orientationOffset;
			case OPPOSE_PLAYER:
				return (client.getLocalPlayer().getOrientation() + orientationOffset) % 2048;
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

		if(isPlacingTile && markerToPlace != null){

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
					remodelTile(existing, markerToPlace, target.getLocalLocation());
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
						markTile(target.getLocalLocation(), markerToPlace);
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
						markTile(target.getLocalLocation(), null);
					}
				});
		
			}
		}
	}
}