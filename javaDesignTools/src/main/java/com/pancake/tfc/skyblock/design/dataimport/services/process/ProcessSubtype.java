package com.pancake.tfc.skyblock.design.dataimport.services.process;

public enum ProcessSubtype {
    CRAFTING_2x2,
    CRAFTING_3x3,
    HEATING_60(60),
    HEATING_200(200),
    HEATING_230(230),
    HEATING_270(270),
    HEATING_400(400),
    HEATING_420(420),
    HEATING_500(500),
    HEATING_700(700),
    HEATING_840(840),
    HEATING_930(930),
    HEATING_950(950),
    HEATING_960(960),
    HEATING_961(961),
    HEATING_985(985),
    HEATING_1060(1060),
    HEATING_1070(1070),
    HEATING_1080(1080),
    HEATING_1399(1399),
    HEATING_1453(1453),
    HEATING_1485(1485),
    HEATING_1500(1500),
    HEATING_1535(1535),
    HEATING_1540(1540),
    POT_SOUP_300(300),
    POT_300(300),
    POT_600(600),
    KNAPPING_TFC_CLAY,
    KNAPPING_TFC_LEATHER,
    KNAPPING_TFC_ROCK,
    KNAPPING_TFC_FIRE_CLAY,
    ANVIL_1(1),
    ANVIL_2(2),
    ANVIL_3(3),
    ANVIL_4(4),
    ANVIL_5(6),
    ANVIL_6(6),
    WELDING_MINUS1(-1),
    WELDING_1(1),
    WELDING_2(2),
    WELDING_3(3),
    WELDING_4(4),
    WELDING_5(5);

    private final Integer tier;

    ProcessSubtype(){
        this.tier = null;
    }

    ProcessSubtype(int tier){
        this.tier = tier;
    }

    public Integer getTier() {
        return tier;
    }
}
