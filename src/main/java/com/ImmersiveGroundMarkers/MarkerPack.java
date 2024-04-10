package com.ImmersiveGroundMarkers;
//NULLPACK("", new String[]{"", "", "", ""}, new int[]{1,1,1,1})

public enum MarkerPack {
    ROCKS("Rocks", 
        new MarkerOption("Rocks", 868),
        new MarkerOption("Mining Rocks", 1390),
        new MarkerOption("Smooth Rock", 1799),
        new MarkerOption("Sandy Rock", 785),
        new MarkerOption("Purple Crystals", 30680),
        new MarkerOption("Mud", 12027),
        new MarkerOption("Small Rocks", 49742)
    ),
    SMOOTH_SYMBOLS("Smooth Symbols",
        new MarkerOption("Arrow", 4852),
        new MarkerOption("Zzz", 4626),
        new MarkerOption("Cross", 4856),
        new MarkerOption("Ban", 7669),
        new MarkerOption("Square Highlight", 40787),
        new MarkerOption("Warning", 15427),
        new MarkerOption("Star", 15272),
        new MarkerOption("Crosshair", 14947)
    ),
    NUMBERS("Numbers",
        new MarkerOption("0", 42993),
        new MarkerOption("1", 42992),
        new MarkerOption("2", 42995),
        new MarkerOption("3", 42990),
        new MarkerOption("4", 42991),
        new MarkerOption("5", 42997),
        new MarkerOption("6", 42989),
        new MarkerOption("7", 42988),
        new MarkerOption("8", 42996),
        new MarkerOption("9", 42994)
    ),
    MAP_SYMBOLS("Map Symbols",
        new MarkerOption("Quest", 7137),
        new MarkerOption("Anvil", 7130),
        new MarkerOption("Bank", 7131),
        new MarkerOption("Range", 7132),
        new MarkerOption("Fishing", 7133),
        new MarkerOption("Furnace", 7134),
        new MarkerOption("Pottery", 7135),
        new MarkerOption("Mine", 7136),
        new MarkerOption("Transport", 35795)
    ),
    WEAPONS_AND_TOOLS("Weapons/Tools",
        new MarkerOption("Staff", 2810, 1280),
        new MarkerOption("Sword", 2604, 1024),
        new MarkerOption("2H Sword", 2754, 1280),
        new MarkerOption("Arrow", 5049),
        new MarkerOption("Axe", 2544, 1024),
        new MarkerOption("Pickaxe", 2529, 950)
    ),
    ROUGH_SYMBOLS("Rough Symbols",
        new MarkerOption("Arrow", 5125),
        new MarkerOption("Cross", 5139),
        new MarkerOption("Defend", 20566),
        new MarkerOption("Attack", 20561, 1792),
        new MarkerOption("Heal", 20569),
        new MarkerOption("No", 8684),
        new MarkerOption("Yes", 8685)
    ),
    FOLIAGE("Plants",
        new MarkerOption("Pastel Flowers", 237),
        new MarkerOption("Bright Blue Flowers", 1569),
        new MarkerOption("Lilies", 1581),
        new MarkerOption("Cornflower", 1610),
        new MarkerOption("Daisies", 1699),
        new MarkerOption("Thistle", 1724),
        new MarkerOption("Roses", 42562),
        new MarkerOption("Flax", 1668),
        new MarkerOption("Bush", 1565),
        new MarkerOption("Cactus", 44729),
        new MarkerOption("Cabbage", 1692),
        new MarkerOption("Grass", 4745),
        new MarkerOption("Fern", 1696),
        new MarkerOption("Clover", 48256),
        new MarkerOption("Mushroom", 1686),
        new MarkerOption("Magic Mushroom", 1579),
        new MarkerOption("Mushroom Cluster", 3917)
    ),
    BONES("Bones",
        new MarkerOption("Bones", 222),
        new MarkerOption("Half-Buried", 49004),
        new MarkerOption("Skull Pair", 46258),
        new MarkerOption("Skull Stack", 46172),
        new MarkerOption("Misc Bones", 40112),
        new MarkerOption("Burnt Skeleton", 29271),
        new MarkerOption("Fish Bones", 18177),
        new MarkerOption("Bone Pile", 10594),
        new MarkerOption("Skull", 2388, 1024),
        new MarkerOption("Skeleton", 2595)
    ),
    SPOOKY("Spooky",
        new MarkerOption("Cobweb", 37309),
        new MarkerOption("Blood Splat 1", 42797),
        new MarkerOption("Blood Splat 2", 45073),
        new MarkerOption("Blood Splat 3", 35395, 1280),
        new MarkerOption("Miasma", 29475),
        new MarkerOption("Poison", 36159, 1024),
        new MarkerOption("Ritual Star", 1131)
    ),
    MAGIC("Magical effects",
        new MarkerOption("Ancient Essence", 47096),
        new MarkerOption("Stardust", 41598),
        new MarkerOption("Lightning", 28081),
        new MarkerOption("Dark Circle", 6399),
        new MarkerOption("Golden Glow", 3567),
        new MarkerOption("Arcane Circle", 3094)
    ),
    MISCELLANEOUS("Miscellaneous",
        new MarkerOption("Rope", 21517),
        new MarkerOption("Cannonballs", 16517),
        new MarkerOption("Scroll", 10347, 1024),
        new MarkerOption("Potion", 1736),
        new MarkerOption("Spotlight", 3698, 1536)
    )
    ;

    MarkerOption[] markers;
    //String[] names;
    //int[] ids;
    String displayName;
    private MarkerPack(String displayName, MarkerOption... options){
        this.displayName = displayName;
        this.markers = options;
        //this.names = names;
        //this.ids = ids;
    }
}
