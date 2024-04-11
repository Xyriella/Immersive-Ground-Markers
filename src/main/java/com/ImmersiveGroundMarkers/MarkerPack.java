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
        new MarkerOption("Ritual Star", 1131),
        new MarkerOption("Grave", 40589, 1024)
    ),
    MAGIC("Magical effects",
        new MarkerOption("Ancient Essence", 47096),
        new MarkerOption("Stardust", 41598),
        new MarkerOption("Lightning", 28081),
        new MarkerOption("Dark Circle", 6399),
        new MarkerOption("Golden Glow", 3567),
        new MarkerOption("Arcane Circle", 3094)
    ),
    CRITTERS("Critters",
        new MarkerOption("Butterfly", 3023, 1024, 362),
        new MarkerOption("Puffin", 22792, 1024, 5873),
        new MarkerOption("Seagull", 26874, 1024, 6815),
        new MarkerOption("Squirrel", 11723, 1024, 3211),
        new MarkerOption("Racoon", 11724, 1024, 3213),
        new MarkerOption("Ferret", 19373, 1024, 5188),
        //new MarkerOption("Crab", 0),//TODO: Multimodel
        new MarkerOption("Rat", 9610, 1024, 2704),
        new MarkerOption("Duckling", 26872, 1024, 6818),
        new MarkerOption("Spiderling", 47399, 1024, 6247),
        new MarkerOption("Black Chinchompa", 19371, 1024, 5182, new short[]{5169,7343,7335,7339,7343,165}, new short[]{20,33,12,37,45,49}),
        new MarkerOption("Grey Chinchompa", 19371, 1024, 5182),
        new MarkerOption("Scorpion", 24612, 1024, 6258),
        new MarkerOption("Pet Rock", 4271),
        new MarkerOption("Crow", 26851, 1024, 6785),
        new MarkerOption("Monkey", 7744, 1024, 222),
        //new MarkerOption("Kebbit", 0),//TODO: Multimodel
        new MarkerOption("Orange Salamander", 19391, 1024, 5263, new short[]{61}, new short[]{6059}),
        new MarkerOption("Piglet", 7364, 1024, 2166),
        new MarkerOption("Balloon Animal", 10736, 1024, 3049)
    ),
    MISCELLANEOUS("Miscellaneous",
        new MarkerOption("Rope", 21517),
        new MarkerOption("Cannonballs", 16517),
        new MarkerOption("Scroll", 10347, 1024),
        new MarkerOption("Potion", 1736),
        new MarkerOption("Spotlight", 3698, 1536)
    ),
    /*SHADOWS("Shades", //TODO: Model Colours
        new MarkerOption("", 0),
        new MarkerOption("", 0),
        new MarkerOption("", 0),
        new MarkerOption("", 0),
        new MarkerOption("", 0)
    )*/
    HANANNIE("Hanannie",
        new MarkerOption("Entling", 49908, 1024, 10537),
        new MarkerOption("Cat on stool", 42214, 768, 8231),
        new MarkerOption("Goblin", 43835, 1024, 6181),
        new MarkerOption("Swarm", 2950, 0, 0),
        new MarkerOption("Duke Pet", 49195, 1024, 10217),
        new MarkerOption("Dog", 7755, 1024),
        new MarkerOption("Floating Fish", 13811, 1024, 3442),
        new MarkerOption("Fake Man", 4065, 1024),
        //new MarkerOption("Rock Crab Rock", 4400, 0, 1315 ), //TODO: Multimodel
        //new MarkerOption("Suspect", 0), //TODO: Multimodel
        //new MarkerOption("Leprechaun", 0), //TODO: Multimodel
        new MarkerOption("Shark Fin", 19778, 1024, 10),
        new MarkerOption("Chompy Toad", 3447, 1024, 1020),
        new MarkerOption("Fly Trap", 14207, 1024),
        new MarkerOption("Fox Trap", 49899),
        new MarkerOption("Toy Soldier", 13227, 1024),
        new MarkerOption("Skavid", 20388, 1024, 257)
    )
    ;

    final MarkerOption[] markers;

    String displayName;
    private MarkerPack(String displayName, MarkerOption... options){
        this.displayName = displayName;
        this.markers = options;
    }
}
