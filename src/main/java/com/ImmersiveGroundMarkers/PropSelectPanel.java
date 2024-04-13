package com.ImmersiveGroundMarkers;

import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import java.awt.event.KeyEvent;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.ImmersiveGroundMarkers.ImmersiveGroundMarkersConfig.OrientationMethod;
import com.google.common.util.concurrent.Runnables;


public class PropSelectPanel extends PluginPanel implements KeyListener{

    private final ImmersiveGroundMarkersPlugin plugin;

    private JLabel panelTitle;

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

    private JButton prevButton;

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
        this.plugin = plugin;

        currentPack = MarkerPack.ROCKS;

        GroupLayout fullLayout = new GroupLayout(this);
        fullLayout.setAutoCreateGaps(true);
        
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

        panelTitle = new JLabel("Orientation");

        setLayout(fullLayout);

        orientationButtonPanel = new JPanel();
        GroupLayout orientationLayout = new GroupLayout(orientationButtonPanel);

        orientationButtonPanel.setLayout(orientationLayout);

        orientationSelectionGroup = new ButtonGroup();

        northButton = new JButton("North");
        setupButton(northButton, OrientationMethod.NORTH);
        northButton.setToolTipText("Point North");

        eastButton = new JButton("East");
        setupButton(eastButton, OrientationMethod.EAST);
        eastButton.setToolTipText("Point East");
        
        southButton = new JButton("South");
        setupButton(southButton, OrientationMethod.SOUTH);
        southButton.setToolTipText("Point South");

        westButton = new JButton("West");
        setupButton(westButton, OrientationMethod.WEST);
        westButton.setToolTipText("Point West");

        facePlayerButton = new JButton("Towards");
        setupButton(facePlayerButton, OrientationMethod.FACE_PLAYER);
        facePlayerButton.setToolTipText("Point towards the Player");

        faceAwayFromPlayerButton = new JButton("Away");
        setupButton(faceAwayFromPlayerButton, OrientationMethod.FACE_AWAY_PLAYER);
        faceAwayFromPlayerButton.setToolTipText("Point away from the Player");

        faceSameAsPlayerButton = new JButton("Match");
        setupButton(faceSameAsPlayerButton, OrientationMethod.MATCH_PLAYER);
        faceSameAsPlayerButton.setToolTipText("Point the same way the Player is facing");

        faceOppositePlayerButton = new JButton("Oppose");
        setupButton(faceOppositePlayerButton, OrientationMethod.OPPOSE_PLAYER);
        faceOppositePlayerButton.setToolTipText("Point the opposite way the Player is facing");

        randomButton = new JButton("Random");
        setupButton(randomButton, OrientationMethod.RANDOM);
        randomButton.setToolTipText("Point in a random direction");

        orientationSelectionGroup.add(northButton);
        orientationSelectionGroup.add(eastButton);
        orientationSelectionGroup.add(southButton);
        orientationSelectionGroup.add(westButton);
        orientationSelectionGroup.add(facePlayerButton);
        orientationSelectionGroup.add(faceAwayFromPlayerButton);
        orientationSelectionGroup.add(faceSameAsPlayerButton);
        orientationSelectionGroup.add(faceOppositePlayerButton);
        orientationSelectionGroup.add(randomButton);
        reselectOrientationButton();

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

        groupControlPanel = new JPanel();
        groupControlPanel.setLayout(new GridBagLayout());
        groupControlPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        GridBagConstraints prevConstraints = new GridBagConstraints();
        prevConstraints.weightx = 0.2;
        prevConstraints.gridx = 0;
        prevConstraints.gridy = 0;
        prevConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        GridBagConstraints titleConstraints = new GridBagConstraints();
        titleConstraints.fill = GridBagConstraints.HORIZONTAL;
        titleConstraints.gridx = 1;
        titleConstraints.gridy = 0;
        titleConstraints.anchor = GridBagConstraints.PAGE_START;
        GridBagConstraints nextConstraints = new GridBagConstraints();
        nextConstraints.weightx = 0.2;
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
        
        propButtonsPanel = new JPanel();
        generatePropButtons();


        fullLayout.setHorizontalGroup(fullLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
        .addGroup(fullLayout.createSequentialGroup()
            .addComponent(exportMarkersButton)
            .addComponent(importMarkersButton)
            .addComponent(clearMarkersButton)
        )
        .addComponent(panelTitle)
        .addComponent(orientationButtonPanel)
        .addComponent(groupControlPanel)
        .addComponent(propButtonsPanel)
        );
        
        fullLayout.setVerticalGroup(fullLayout.createSequentialGroup()
        .addGroup(fullLayout.createParallelGroup()
            .addComponent(exportMarkersButton)
            .addComponent(importMarkersButton)
            .addComponent(clearMarkersButton)
        )
        .addComponent(panelTitle)
        .addComponent(orientationButtonPanel)
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
    }

    void setupButton(JButton button, OrientationMethod direction){
        button.addActionListener(l -> {
            if(prevButton != null) prevButton.setSelected(false);
            plugin.setOrientationMethod(direction);
            JButton newButton = ((JButton)l.getSource());
            newButton.setSelected(true);
            prevButton = newButton;
        });
        button.setBorder(new EmptyBorder(2,2,2,2));
        button.addKeyListener(this);
        if(button.isSelected()){
            prevButton = button;
        }
    }

    public void reselectOrientationButton(){
        orientationSelectionGroup.clearSelection();
        if(prevButton != null){
            prevButton.setSelected(false);
        }
        switch(plugin.getOrientationMethod()){
            case EAST:
                eastButton.setSelected(true);
                prevButton = eastButton;
                break;
            case FACE_AWAY_PLAYER:
                faceAwayFromPlayerButton.setSelected(true);
                prevButton = faceAwayFromPlayerButton;
                break;
            case FACE_PLAYER:
                facePlayerButton.setSelected(true);
                prevButton = facePlayerButton;
                break;
            case MATCH_PLAYER:
                faceSameAsPlayerButton.setSelected(true);
                prevButton = faceSameAsPlayerButton;
                break;
            case NORTH:
                northButton.setSelected(true);
                prevButton = northButton;
                break;
            case OPPOSE_PLAYER:
                faceOppositePlayerButton.setSelected(true);
                prevButton = faceOppositePlayerButton;
                break;
            case RANDOM:
                randomButton.setSelected(true);
                prevButton = randomButton;
                break;
            case SOUTH:
                southButton.setSelected(true);
                prevButton = southButton;
                break;
            case WEST:
                westButton.setSelected(true);
                prevButton = westButton;
                break;
            default:
                break;

        }
    }

    void generatePropButtons(){
        for (Component c : propButtonsPanel.getComponents()) {
            JButton button = (JButton)c;
            if(button != null){
                button.setVisible(false);
            }
        }
        propButtonsPanel.removeAll();
        propButtonsPanel.validate();
        int propCount = currentPack.markers.length;
        propButtonsPanel.setLayout(new GridLayout(0, 4, 2,2));

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
