package io.github.chakyl.splendidslimes.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.chakyl.splendidslimes.SplendidSlimes;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

public final class Keybindings {
    public static final Keybindings INSTANCE = new Keybindings();

    private Keybindings() {
    }

    public static final String CATEGORY = "key.categories." + SplendidSlimes.MODID;
    public final KeyMapping slimeVacModeKey = new KeyMapping(
            "key." + SplendidSlimes.MODID + ".slime_vac_mode",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_V, -1),
            CATEGORY);
}
