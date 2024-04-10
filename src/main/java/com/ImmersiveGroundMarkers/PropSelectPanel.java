package com.ImmersiveGroundMarkers;

import net.runelite.api.KeyCode;
import net.runelite.client.input.KeyListener;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

//import java.awt.event.KeyListener;
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


public class PropSelectPanel extends PluginPanel implements KeyListener{

    private final ImmersiveGroundMarkersPlugin plugin;

    private JLabel panelTitle;

    private final JPanel orientationButtonPanel;
    private final ButtonGroup orientationSelectionGroup;

    private final JButton NorthButton;
    private final JButton EastButton;
    private final JButton SouthButton;
    private final JButton WestButton;
    private final JButton FacePlayerButton;
    private final JButton FaceAwayFromPlayerButton;
    private final JButton FaceSameAsPlayerButton;
    private final JButton FaceOppositePlayerButton;
    private final JButton RandomButton;

    private JButton prevButton;

    private JPanel groupControlPanel;
    private JButton prevGroupButton;
    private JLabel groupTitle;
    private JButton nextGroupButton;

    private JPanel propButtonsPanel;

    private MarkerPack currentPack;

    public PropSelectPanel(ImmersiveGroundMarkersPlugin plugin){
        this.plugin = plugin;

        currentPack = MarkerPack.ROCKS;

        GroupLayout fullLayout = new GroupLayout(this);
        
        panelTitle = new JLabel("Orientation");
        panelTitle.setBorder(new EmptyBorder(4,0,4,0));

        setLayout(fullLayout);
        setBorder(new EmptyBorder(4,4,4,4));

        orientationButtonPanel = new JPanel();
        GroupLayout orientationLayout = new GroupLayout(orientationButtonPanel);

        orientationButtonPanel.setLayout(orientationLayout);
        orientationButtonPanel.setBorder(new EmptyBorder(2,2,2,2));
        orientationButtonPanel.setSize(180, 200);

        orientationSelectionGroup = new ButtonGroup();

        NorthButton = new JButton("North");
        setupButton(NorthButton, OrientationMethod.NORTH);
        NorthButton.setToolTipText("Point North");

        EastButton = new JButton("East");
        setupButton(EastButton, OrientationMethod.EAST);
        EastButton.setToolTipText("Point East");
        
        SouthButton = new JButton("South");
        setupButton(SouthButton, OrientationMethod.SOUTH);
        SouthButton.setToolTipText("Point South");

        WestButton = new JButton("West");
        setupButton(WestButton, OrientationMethod.WEST);
        WestButton.setToolTipText("Point West");

        FacePlayerButton = new JButton("Towards");
        setupButton(FacePlayerButton, OrientationMethod.FACE_PLAYER);
        FacePlayerButton.setToolTipText("Point towards the Player");

        FaceAwayFromPlayerButton = new JButton("Away");
        setupButton(FaceAwayFromPlayerButton, OrientationMethod.FACE_AWAY_PLAYER);
        FaceAwayFromPlayerButton.setToolTipText("Point away from the Player");

        FaceSameAsPlayerButton = new JButton("Match");
        setupButton(FaceSameAsPlayerButton, OrientationMethod.MATCH_PLAYER);
        FaceSameAsPlayerButton.setToolTipText("Point the same way the Player is facing");

        FaceOppositePlayerButton = new JButton("Oppose");
        setupButton(FaceOppositePlayerButton, OrientationMethod.OPPOSE_PLAYER);
        FaceOppositePlayerButton.setToolTipText("Point the opposite way the Player is facing");

        RandomButton = new JButton("Random");
        setupButton(RandomButton, OrientationMethod.RANDOM);
        RandomButton.setToolTipText("Point in a random direction");

        orientationSelectionGroup.add(NorthButton);
        orientationSelectionGroup.add(EastButton);
        orientationSelectionGroup.add(SouthButton);
        orientationSelectionGroup.add(WestButton);
        orientationSelectionGroup.add(FacePlayerButton);
        orientationSelectionGroup.add(FaceAwayFromPlayerButton);
        orientationSelectionGroup.add(FaceSameAsPlayerButton);
        orientationSelectionGroup.add(FaceOppositePlayerButton);
        orientationSelectionGroup.add(RandomButton);

        final int hMin = 0, hPref = 40, hMax = 95;
        final int vMin = 0, vPref = 40, vMax = 60;

        orientationLayout.setVerticalGroup(
            orientationLayout.createSequentialGroup()
            .addGroup(orientationLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(FaceSameAsPlayerButton, vMin, vPref, vMax)
            .addComponent(NorthButton, vMin, vPref, vMax)
            .addComponent(FacePlayerButton, vMin, vPref, vMax))

            .addGroup(orientationLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(WestButton, vMin, vPref, vMax)
            .addComponent(RandomButton, vMin, vPref, vMax)
            .addComponent(EastButton, vMin, vPref, vMax))

            .addGroup(orientationLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(FaceOppositePlayerButton, vMin, vPref, vMax)
            .addComponent(SouthButton, vMin, vPref, vMax)
            .addComponent(FaceAwayFromPlayerButton, vMin, vPref, vMax))
        );

        orientationLayout.setHorizontalGroup(
            orientationLayout.createSequentialGroup()
            .addGroup(orientationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(FaceSameAsPlayerButton, hMin, hPref, hMax)
            .addComponent(WestButton, hMin, hPref, hMax)
            .addComponent(FaceOppositePlayerButton, hMin, hPref, hMax))

            .addGroup(orientationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(NorthButton, hMin, hPref, hMax)
            .addComponent(RandomButton, hMin, hPref, hMax)
            .addComponent(SouthButton, hMin, hPref, hMax))

            .addGroup(orientationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(FacePlayerButton, hMin, hPref, hMax)
            .addComponent(EastButton, hMin, hPref, hMax)
            .addComponent(FaceAwayFromPlayerButton, hMin, hPref, hMax))
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
        .addComponent(panelTitle)
        .addComponent(orientationButtonPanel)
        .addComponent(groupControlPanel)
        .addComponent(propButtonsPanel)
        );
        
        fullLayout.setVerticalGroup(fullLayout.createSequentialGroup()
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
        button.setSelected(plugin.getOrientationMethod() == direction);
        button.addKeyListener(this);
        if(button.isSelected()){
            prevButton = button;
        }
    }

    void generatePropButtons(){
        propButtonsPanel.removeAll();
        int propCount = currentPack.markers.length;
        propButtonsPanel.setLayout(new GridLayout(propCount/4 + (propCount%4 != 0 ? 1 : 0), 4));

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
            
            final int modelID = currentPack.markers[i].modelID;
            newButton.setToolTipText(mkOpt.name);
            newButton.addKeyListener(this);
            newButton.addActionListener(l -> {
                plugin.startPlacingTile(modelID, mkOpt.orientationOffset);
            });
            propButtonsPanel.add(newButton);
        }
        propButtonsPanel.validate();
    }
}
