package com.ImmersiveGroundMarkers;
//NULLPACK(new String[]{"", "", "", ""}, new int[]{1,1,1,1})
public enum MarkerPack {
    ROCKS(new String[]{"Rocks", "Mining Rocks", "Smooth Rock", "Purple Crystals", "Sandy Rock", "Little Rocks"}, new int[]{868,1390,1799, 30680, 785, 49742}),
    SMOOTH_SYMBOLS(new String[]{"Arrow", "Cross", "Ban", "Square Highlight"}, new int[]{4852,4856,7669, 40787}),
    //NUMBERS(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}, new int[]{4863, 4864, 4865, 4866, 4867, 4868, 4869, 4870, 4871, 4872}),
    NUMBERS(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}, new int[]{42993, 42992, 42995, 42990, 42991, 42997, 42989, 42988, 42996, 42994}),
    MAP_SYMBOLS(new String[]{"Quest", "Anvil", "Bank", "Range", "Fishing", "Furnace", "Pottery", "Mine", "Transport"}, new int[]{7137, 7130, 7131, 7132, 7133, 7134, 7135, 7136, 35795}),
    WEAPONS(new String[]{"Staff", "Sword", "2H Sword", "Arrow", "Axe", "Pickaxe"}, new int[]{2810,2604,2754,5049, 2544, 2529}),
    ROUGH_SYMBOLS(new String[]{"Arrow", "Cross"}, new int[]{5125,5139}),
    FLOWERS(new String[]{"Pastel Flowers", "Bright Blue Flowers", "Lilies", "Cornflower", "Daisies", "Thistle", "Roses"}, new int[]{237,1569,1581,1610,1699,1724, 42562}),
    PLANTS(new String[]{"Flax", "Bush", "Cactus", "Cabbage", "Grass", "Fern", "Clover"}, new int[]{1668,1565, 44729,1692,4745,1696,48256}),
    BONES(new String[]{"Half-Buried", "Skull Pair", "Skull Stack", "Misc Bones", "Burnt Skeleton"}, new int[]{49004,46258,46172, 40112, 29271}),
    SPOOKY(new String[]{"Cobweb", "Blood Splat 1", "Blood Splat 2", "Blood Splat 3", "Miasma", "Poison"}, new int[]{37309, 42797, 45073, 35395, 29475, 36159}),
    MAGIC(new String[]{"Magic Dust", "Thrall Summon", "Stardust", "Lightning"}, new int[]{47096,41981,41598,28081}),
    MISC_OBJECTS(new String[]{"", "Dynamite", "", ""}, new int[]{1,30183,1,1})
    //CORRUPTED_WEAPONS(new String[]{"Godsword", "Scythe", "Voidwaker", "Twisted Bow", "Tumeken's Shadow", "Dragon Claws"}, new int[]{49502,49501,49500,49499,49498,495496})
    //PRIDE_CROWNS(new String[]{"Lesbian", "Gay", "Transgender", "Aromantic", "Bisexual", "Non-Binary", "Asexual", "Pansexual"}, new int[]{45207, 45206, 45205, 45204, 45203, 45202, 45201, 45200 })
    ;

    String[] names;
    int[] ids;
    private MarkerPack(String[] names, int[] ids){
        this.names = names;
        this.ids = ids;
    }
}
