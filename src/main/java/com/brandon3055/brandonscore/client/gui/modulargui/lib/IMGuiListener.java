package com.brandon3055.brandonscore.client.gui.modulargui.lib;

import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;

/**
 * Created by brandon3055 on 4/09/2016.
 * TODO make this more flexible somehow... Not sure how yet
 */
public interface IMGuiListener {

    void onMGuiEvent(String eventString, MGuiElementBase eventElement);
}
