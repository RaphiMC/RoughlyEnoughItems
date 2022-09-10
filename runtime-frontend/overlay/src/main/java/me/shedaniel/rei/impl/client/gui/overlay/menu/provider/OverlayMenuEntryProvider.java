package me.shedaniel.rei.impl.client.gui.overlay.menu.provider;

import me.shedaniel.rei.api.client.favorites.FavoriteMenuEntry;
import me.shedaniel.rei.impl.client.ClientInternals;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public interface OverlayMenuEntryProvider {
    List<OverlayMenuEntryProvider> PROVIDERS = ClientInternals.resolveServices(OverlayMenuEntryProvider.class);
    
    List<FavoriteMenuEntry> provide(Type type);
    
    enum Type {
        CRAFTABLE_FILTER,
        CONFIG,
        ;
    }
}
