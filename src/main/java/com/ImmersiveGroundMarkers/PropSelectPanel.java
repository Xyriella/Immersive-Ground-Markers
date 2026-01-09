package com.ImmersiveGroundMarkers;

import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints.Key;
import java.awt.image.BufferedImage;
import java.security.AlgorithmConstraints;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;

import com.ImmersiveGroundMarkers.ImmersiveGroundMarkersConfig.OrientationMethod;
import com.google.common.util.concurrent.Runnables;


public class PropSelectPanel extends PluginPanel implements KeyListener{

    private final ImmersiveGroundMarkersPlugin plugin;

    private JPanel titlePanel;

    private JLabel panelTitle;
    private JLabel helpComponent;

    private JPanel customPropPanel;
    private JSpinner idSelectSpinner;

    private final JPanel orientationButtonPanel;
    private final ButtonGroup orientationSelectionGroup;

    private final JButton northButton;
    private final JButton eastButton;
    private final JButton southButton;
    private final JButton westButton;
    private final JButton facePlayerButton;
    private final JButton faceAwayFromPlayerButton;
    private final JButton faceSameAsPlayerButton;
    private final JButton faceOppositePlayerButton;
    private final JButton randomButton;

    //private JButton prevButton;

    private JPanel groupControlPanel;
    private JButton prevGroupButton;
    private JLabel groupTitle;
    private JButton nextGroupButton;

    private JButton clearMarkersButton;
    private JButton exportMarkersButton;
    private JButton importMarkersButton;

    private JPanel propButtonsPanel;

    private MarkerPack currentPack;

    public PropSelectPanel(ImmersiveGroundMarkersPlugin plugin, ChatboxPanelManager chatboxPanelManager){
        //Variable init
        this.plugin = plugin;
        currentPack = MarkerPack.ROCKS;

        GroupLayout fullLayout = new GroupLayout(this);
        fullLayout.setAutoCreateGaps(true);
        setLayout(fullLayout);
        
        //Import/export/clear buttons
        clearMarkersButton = new JButton("Clear");
        clearMarkersButton.setToolTipText("Clears all markers in loaded regions.\nWill check for confirmation.");
        clearMarkersButton.addActionListener(l -> {
            chatboxPanelManager.openTextMenuInput("Are you sure you want to clear all loaded ground markers?")
			.option("Yes", () -> plugin.clearMarkers())
			.option("No", Runnables.doNothing())
			.build();
        });
        exportMarkersButton = new JButton("Export");
        exportMarkersButton.setToolTipText("Export all markers in loaded regions to the clipboard.");
        exportMarkersButton.addActionListener(l -> plugin.exportMarkers());
        importMarkersButton = new JButton("Import");
        importMarkersButton.setToolTipText("Import markers from clipboard.\nWill check for confirmation.");
        importMarkersButton.addActionListener(l -> plugin.promptForImport());

        titlePanel = new JPanel();
        titlePanel.setLayout(new GridBagLayout());
        GridBagConstraints helpConstraints = new GridBagConstraints();
        GridBagConstraints titleAConstraints = new GridBagConstraints();

        try {
            BufferedImage helpIcon = ImageUtil.loadImageResource(getClass(), "help.png");
            helpComponent = new JLabel(new ImageIcon(helpIcon));
        } catch (Exception e) {
            helpComponent = new JLabel("Help");
        }
        helpComponent.setSize(10, 10);
        helpComponent.setToolTipText("- Select a prop from below, then left click in the world to place it.\n- Hold Left Shift while placing to continue placing.\n- Press Escape to cancel placement.\n- Shift + Right Click on a tile with a prop to remove it.");
        helpConstraints.anchor = GridBagConstraints.LINE_END;
        helpConstraints.gridx = 2;
        helpConstraints.gridy = 0;
        helpConstraints.gridwidth = 3;
        helpConstraints.gridheight = 1;
        helpConstraints.weightx = 1.0;
        helpConstraints.weighty = 1.0;
        helpConstraints.ipadx = 8;
        helpConstraints.ipady = 4;

        //Orientation Selection
        panelTitle = new JLabel("Orientation");
        titleAConstraints.anchor = GridBagConstraints.CENTER;
        titleAConstraints.gridx = 1;
        titleAConstraints.gridy = 0;
        titleAConstraints.gridwidth = 3;
        titleAConstraints.gridheight = 1;
        titleAConstraints.weightx = 1.0;
        titleAConstraints.weighty = 1.0;

        titlePanel.add(panelTitle, titleAConstraints);
        titlePanel.add(helpComponent, helpConstraints);
        
        //Custom Prop Panel
        /*customPropPanel = new JPanel();
        JButton placeCustomButton = new JButton("Place");
        placeCustomButton.addKeyListener(this);
        placeCustomButton.addActionListener(l -> {
            placeCustomProp();
        });

        SpinnerModel idSelectModel = new SpinnerNumberModel(0, 0, 100000, 1);
        idSelectSpinner = new JSpinner(idSelectModel);
        
        customPropPanel.add(idSelectSpinner);
        customPropPanel.add(placeCustomButton);*/

        orientationButtonPanel = new JPanel();
        GroupLayout orientationLayout = new GroupLayout(orientationButtonPanel);

        orientationButtonPanel.setLayout(orientationLayout);

        orientationSelectionGroup = new ButtonGroup();

        northButton = new JButton("North");
        setupOrientationButton(northButton, OrientationMethod.NORTH, "Point North");

        eastButton = new JButton("East");
        setupOrientationButton(eastButton, OrientationMethod.EAST, "Point East");
        
        southButton = new JButton("South");
        setupOrientationButton(southButton, OrientationMethod.SOUTH, "Point South");

        westButton = new JButton("West");
        setupOrientationButton(westButton, OrientationMethod.WEST, "Point West");

        facePlayerButton = new JButton("Towards");
        setupOrientationButton(facePlayerButton, OrientationMethod.FACE_PLAYER, "Point towards the Player");

        faceAwayFromPlayerButton = new JButton("Away");
        setupOrientationButton(faceAwayFromPlayerButton, OrientationMethod.FACE_AWAY_PLAYER, "Point away from the Player");

        faceSameAsPlayerButton = new JButton("Match");
        setupOrientationButton(faceSameAsPlayerButton, OrientationMethod.MATCH_PLAYER, "Point the same way the Player is facing");

        faceOppositePlayerButton = new JButton("Oppose");
        setupOrientationButton(faceOppositePlayerButton, OrientationMethod.OPPOSE_PLAYER, "Point the opposite way the Player is facing");

        randomButton = new JButton("Random");
        setupOrientationButton(randomButton, OrientationMethod.RANDOM, "Point in a random direction");

        orientationSelectionGroup.add(northButton);
        orientationSelectionGroup.add(eastButton);
        orientationSelectionGroup.add(southButton);
        orientationSelectionGroup.add(westButton);
        orientationSelectionGroup.add(facePlayerButton);
        orientationSelectionGroup.add(faceAwayFromPlayerButton);
        orientationSelectionGroup.add(faceSameAsPlayerButton);
        orientationSelectionGroup.add(faceOppositePlayerButton);
        orientationSelectionGroup.add(randomButton);
        reselectOrientationButton(plugin.getOrientationMethod());

        final int hMin = 0, hPref = 40, hMax = 95;
        final int vMin = 0, vPref = 40, vMax = 60;

        orientationLayout.setVerticalGroup(
            orientationLayout.createSequentialGroup()
            .addGroup(orientationLayout.createParallelGroup()
            .addComponent(faceSameAsPlayerButton, vMin, vPref, vMax)
            .addComponent(northButton, vMin, vPref, vMax)
            .addComponent(facePlayerButton, vMin, vPref, vMax))

            .addGroup(orientationLayout.createParallelGroup()
            .addComponent(westButton, vMin, vPref, vMax)
            .addComponent(randomButton, vMin, vPref, vMax)
            .addComponent(eastButton, vMin, vPref, vMax))

            .addGroup(orientationLayout.createParallelGroup()
            .addComponent(faceOppositePlayerButton, vMin, vPref, vMax)
            .addComponent(southButton, vMin, vPref, vMax)
            .addComponent(faceAwayFromPlayerButton, vMin, vPref, vMax))
        );

        orientationLayout.setHorizontalGroup(
            orientationLayout.createSequentialGroup()
            .addGroup(orientationLayout.createParallelGroup()
            .addComponent(faceSameAsPlayerButton, hMin, hPref, hMax)
            .addComponent(westButton, hMin, hPref, hMax)
            .addComponent(faceOppositePlayerButton, hMin, hPref, hMax))

            .addGroup(orientationLayout.createParallelGroup()
            .addComponent(northButton, hMin, hPref, hMax)
            .addComponent(randomButton, hMin, hPref, hMax)
            .addComponent(southButton, hMin, hPref, hMax))

            .addGroup(orientationLayout.createParallelGroup()
            .addComponent(facePlayerButton, hMin, hPref, hMax)
            .addComponent(eastButton, hMin, hPref, hMax)
            .addComponent(faceAwayFromPlayerButton, hMin, hPref, hMax))
        );

        //Pack selection
        groupControlPanel = new JPanel();
        groupControlPanel.setLayout(new GridBagLayout());
        groupControlPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        final double weightXPackSelectionButtons = 0.2;
        GridBagConstraints prevConstraints = new GridBagConstraints();
        prevConstraints.weightx = weightXPackSelectionButtons;
        prevConstraints.gridx = 0;
        prevConstraints.gridy = 0;
        prevConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        GridBagConstraints titleConstraints = new GridBagConstraints();
        titleConstraints.fill = GridBagConstraints.HORIZONTAL;
        titleConstraints.gridx = 1;
        titleConstraints.gridy = 0;
        titleConstraints.anchor = GridBagConstraints.PAGE_START;
        GridBagConstraints nextConstraints = new GridBagConstraints();
        nextConstraints.weightx = weightXPackSelectionButtons;
        nextConstraints.gridx = 2;
        nextConstraints.gridy = 0;
        nextConstraints.anchor = GridBagConstraints.FIRST_LINE_END;

        prevGroupButton = new JButton("<");
        prevGroupButton.addActionListener(l -> {
            int nextPackOrdinal = (MarkerPack.values().length + currentPack.ordinal() - 1)%MarkerPack.values().length;
            currentPack = MarkerPack.values()[nextPackOrdinal];
            groupTitle.setText(currentPack.displayName);
            generatePropButtons();
        });
        prevGroupButton.setToolTipText("Previous");

        nextGroupButton = new JButton(">");
        nextGroupButton.addActionListener(l -> {
            int nextPackOrdinal = (currentPack.ordinal() + 1)%MarkerPack.values().length;
            currentPack = MarkerPack.values()[nextPackOrdinal];
            groupTitle.setText(currentPack.displayName);
            generatePropButtons();
        });
        nextGroupButton.setToolTipText("Next");

        groupTitle = new JLabel(currentPack.displayName, SwingConstants.CENTER);
        groupTitle.setToolTipText("Click and drop props into the world.\nShift-click to keep placing.\nEscape to cancel placement.");

        groupControlPanel.add(prevGroupButton, prevConstraints);
        groupControlPanel.add(groupTitle, titleConstraints);
        groupControlPanel.add(nextGroupButton, nextConstraints);
        
        //Prop selection buttons
        propButtonsPanel = new JPanel();
        propButtonsPanel.setLayout(new GridLayout(0, 4, 2,2));
        generatePropButtons();

        //Combine into panel
        fullLayout.setHorizontalGroup(fullLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
        .addGroup(fullLayout.createSequentialGroup()
            .addComponent(exportMarkersButton)
            .addComponent(importMarkersButton)
            .addComponent(clearMarkersButton)
        )
        .addComponent(titlePanel)
        .addComponent(orientationButtonPanel)
        //.addComponent(customPropPanel)
        .addComponent(groupControlPanel)
        .addComponent(propButtonsPanel)
        );
        
        fullLayout.setVerticalGroup(fullLayout.createSequentialGroup()
        .addGroup(fullLayout.createParallelGroup()
            .addComponent(exportMarkersButton)
            .addComponent(importMarkersButton)
            .addComponent(clearMarkersButton)
        )
        .addComponent(titlePanel)
        .addComponent(orientationButtonPanel)
        //.addComponent(customPropPanel)
        .addComponent(groupControlPanel)
        .addComponent(propButtonsPanel)
        );

        setFocusable(true);
        
    } 

    @Override
    public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            plugin.setEscapePressed(true);
        }
        if(e.getKeyCode() == KeyEvent.VK_SHIFT){
            plugin.setShiftPressed(true);
        }
    }
    @Override
    public void keyTyped(KeyEvent e){

    }

    @Override
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            plugin.setEscapePressed(false);
        }
        if(e.getKeyCode() == KeyEvent.VK_SHIFT){
            plugin.setShiftPressed(false);
        }
        /*if(e.getKeyCode() == KeyEvent.VK_ADD){
            idSelectSpinner.setValue(idSelectSpinner.getNextValue());
            placeCustomProp();
        }
        if(e.getKeyCode() == KeyEvent.VK_SUBTRACT){
            idSelectSpinner.setValue(idSelectSpinner.getPreviousValue());
            placeCustomProp();
        }*/
    }

    void setupOrientationButton(JButton button, OrientationMethod direction, String tooltip){
        button.addActionListener(l -> {
            plugin.setOrientationMethod(direction);
        });
        button.setBorder(new EmptyBorder(2,2,2,2));
        button.setToolTipText(tooltip);
        button.addKeyListener(this);
    }

    public void reselectOrientationButton(OrientationMethod newOrientationMethod){
        switch(newOrientationMethod){
            case EAST:
                orientationSelectionGroup.setSelected(eastButton.getModel(), true);
                break;
            case FACE_AWAY_PLAYER:
                orientationSelectionGroup.setSelected(faceAwayFromPlayerButton.getModel(), true);
                break;
            case FACE_PLAYER:
                orientationSelectionGroup.setSelected(facePlayerButton.getModel(), true);
                break;
            case MATCH_PLAYER:
                orientationSelectionGroup.setSelected(faceSameAsPlayerButton.getModel(), true);
                break;
            case NORTH:
                orientationSelectionGroup.setSelected(northButton.getModel(), true);
                break;
            case OPPOSE_PLAYER:
                orientationSelectionGroup.setSelected(faceOppositePlayerButton.getModel(), true);
                break;
            case RANDOM:
                orientationSelectionGroup.setSelected(randomButton.getModel(), true);
                break;
            case SOUTH:
                orientationSelectionGroup.setSelected(southButton.getModel(), true);
                break;
            case WEST:
                orientationSelectionGroup.setSelected(westButton.getModel(), true);
                break;
            default:
                break;

        }
    }

    void placeCustomProp(){
        plugin.startPlacingTile(new MarkerOption("", (int)idSelectSpinner.getValue()));
    }

    void generatePropButtons(){
        //Hide images, remove + validate still sometimes left ghosts of old images behind
        for (Component c : propButtonsPanel.getComponents()) {
            JButton button = (JButton)c;
            if(button != null){
                button.setVisible(false);
            }
        }
        propButtonsPanel.removeAll();
        propButtonsPanel.validate();

        //Loop values
        int propCount = currentPack.markers.length;
        BufferedImage defaultIcon = ImageUtil.loadImageResource(getClass(), "icon.png");

        for (int i = 0; i < propCount; i++) {
            JButton newButton;
            final MarkerOption mkOpt = currentPack.markers[i];
            try {
                BufferedImage propIcon = ImageUtil.loadImageResource(getClass(), "props/"+currentPack.name()+"/"+mkOpt.name+".png");
                newButton = new JButton(new ImageIcon(propIcon));
            } catch (Exception e) {
                newButton = new JButton(new ImageIcon(defaultIcon));
            }
            
            newButton.setToolTipText(mkOpt.name);
            newButton.addKeyListener(this);
            newButton.addActionListener(l -> {
                plugin.startPlacingTile(mkOpt);
            });
            propButtonsPanel.add(newButton);
        }
    }
}
