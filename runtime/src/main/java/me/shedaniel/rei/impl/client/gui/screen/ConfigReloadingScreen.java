/*
 * This file is licensed under the MIT License, part of Roughly Enough Items.
 * Copyright (c) 2018, 2019, 2020, 2021, 2022, 2023 shedaniel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.shedaniel.rei.impl.client.gui.screen;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@ApiStatus.Internal
public class ConfigReloadingScreen extends Screen {
    private final Component title;
    private final BooleanSupplier predicate;
    private Supplier<@Nullable Component> subtitle = () -> null;
    private final Runnable parent;
    private final Runnable cancel;
    
    public ConfigReloadingScreen(Component title, BooleanSupplier predicate, Runnable parent, Runnable cancel) {
        super(Component.empty());
        this.title = title;
        this.predicate = predicate;
        this.parent = parent;
        this.cancel = cancel;
    }
    
    public void setSubtitle(Supplier<@Nullable Component> subtitle) {
        this.subtitle = subtitle;
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public void init() {
        super.init();
        if (cancel == null) return;
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> {
            cancel.run();
        }).bounds(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20).build());
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (!predicate.getAsBoolean()) {
            parent.run();
            return;
        }
        super.render(graphics, mouseX, mouseY, delta);
        String text = switch ((int) (Util.getMillis() / 300L % 4L)) {
            case 1, 3 -> "o O o";
            case 2 -> "o o O";
            default -> "O o o";
        };
        Component subtitle = this.subtitle.get();
        int width = Math.max(font.width(text), font.width(title)), height = subtitle == null ? 18 : 27;
        if (subtitle != null) width = Math.max(width, font.width(subtitle));
        int x = this.width / 2 - width / 2;
        int k = x - 12;
        int l = this.height / 2 - height / 2 - 12;
        int m = width + 12 * 2;
        int n = height + 12 * 2;
        int o = this.isFocused() ? -1 : -6250336;
        graphics.fill(k + 1, l, k + m, l + n, -16777216);
        graphics.renderOutline(k, l, m, n, o);
        graphics.drawCenteredString(this.font, title, this.width / 2, l + 12, 0xffffff);
        graphics.drawCenteredString(this.font, text, this.width / 2, l + 12 + 9, 0x808080);
        if (subtitle != null) {
            graphics.drawCenteredString(this.font, subtitle, this.width / 2, l + 12 + 9 + 9, 0x808080);
        }
    }
}
