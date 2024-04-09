package com.ImmersiveGroundMarkers;

import net.runelite.client.ui.PluginPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.ImmersiveGroundMarkers.ImmersiveGroundMarkersConfig.OrientationMethod;

public class PropSelectPanel extends PluginPanel {

    private final ImmersiveGroundMarkersPlugin plugin;

    private final JPanel OrientationButtonPanel;
    private final JButton NorthButton;
    private final JButton EastButton;
    private final JButton SouthButton;
    private final JButton WestButton;
    private final JButton FacePlayerButton;
    private final JButton FaceAwayFromPlayerButton;
    private final JButton FaceSameAsPlayerButton;
    private final JButton FaceOppositePlayerButton;
    private final JButton RandomButton;   
    private final ButtonGroup OrientationSelectionGroup;

    private JButton prevButton;

    private JPanel groupControlPanel;
    private JButton prevGroupButton;
    private JLabel groupTitle;
    private JButton nextGroupButton;

    public PropSelectPanel(ImmersiveGroundMarkersPlugin plugin){
        this.plugin = plugin;


        GroupLayout fullLayout = new GroupLayout(this);

        //final String wrap = "<html><body style = \"text-align: center\" >";
        //final String br = "<br>";
        //final String endWrap = "</body>";

        setLayout(new GridLayout(3, 1));
        setBorder(new EmptyBorder(4,4,4,4));

        OrientationButtonPanel = new JPanel();
        GroupLayout orientationLayout = new GroupLayout(OrientationButtonPanel);
        //layout.setAutoCreateGaps(true);
        //layout.setAutoCreateContainerGaps(true);

        OrientationButtonPanel.setLayout(orientationLayout);
        OrientationButtonPanel.setBorder(new EmptyBorder(2,2,2,2));
        OrientationButtonPanel.setSize(180, 200);

        OrientationSelectionGroup = new ButtonGroup();

        NorthButton = new JButton("North");
        setupButton(NorthButton, OrientationMethod.NORTH);

        EastButton = new JButton("East");
        setupButton(EastButton, OrientationMethod.EAST);

        SouthButton = new JButton("South");
        setupButton(SouthButton, OrientationMethod.SOUTH);

        WestButton = new JButton("West");
        setupButton(WestButton, OrientationMethod.WEST);

        FacePlayerButton = new JButton("Towards");
        setupButton(FacePlayerButton, OrientationMethod.FACE_PLAYER);

        FaceAwayFromPlayerButton = new JButton("Away");
        setupButton(FaceAwayFromPlayerButton, OrientationMethod.FACE_AWAY_PLAYER);

        FaceSameAsPlayerButton = new JButton("Match");
        setupButton(FaceSameAsPlayerButton, OrientationMethod.MATCH_PLAYER);

        FaceOppositePlayerButton = new JButton("Oppose");
        setupButton(FaceOppositePlayerButton, OrientationMethod.OPPOSE_PLAYER);

        RandomButton = new JButton("Random");
        setupButton(RandomButton, OrientationMethod.RANDOM);

        OrientationSelectionGroup.add(NorthButton);
        OrientationSelectionGroup.add(EastButton);
        OrientationSelectionGroup.add(SouthButton);
        OrientationSelectionGroup.add(WestButton);
        OrientationSelectionGroup.add(FacePlayerButton);
        OrientationSelectionGroup.add(FaceAwayFromPlayerButton);
        OrientationSelectionGroup.add(FaceSameAsPlayerButton);
        OrientationSelectionGroup.add(FaceOppositePlayerButton);
        OrientationSelectionGroup.add(RandomButton);

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
        nextGroupButton = new JButton(">");
        groupTitle = new JLabel("Testing", SwingConstants.CENTER); //TODO: Find proper group name;

        groupControlPanel.add(prevGroupButton, prevConstraints);
        groupControlPanel.add(groupTitle, titleConstraints);
        groupControlPanel.add(nextGroupButton, nextConstraints);
        
        fullLayout.setHorizontalGroup(fullLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
        .addComponent(OrientationButtonPanel)
        .addComponent(groupControlPanel));
        
        fullLayout.setVerticalGroup(fullLayout.createSequentialGroup()
        .addComponent(OrientationButtonPanel)
        .addComponent(groupControlPanel, 0, 30, 40));
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
        if(button.isSelected()){
            prevButton = button;
        }
    }
}
