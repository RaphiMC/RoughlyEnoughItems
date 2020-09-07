/*
 * This file is licensed under the MIT License, part of Roughly Enough Items.
 * Copyright (c) 2018, 2019, 2020 shedaniel
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

package me.shedaniel.rei.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.shedaniel.clothconfig2.forge.gui.widget.DynamicNewSmoothScrollingEntryListWidget;
import me.shedaniel.rei.RoughlyEnoughItemsState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.ApiStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Consumer;

@ApiStatus.Internal
public class WarningAndErrorScreen extends Screen {
    private Widget buttonExit;
    private StringEntryListWidget listWidget;
    private String action;
    private Screen parent;
    private List<Tuple<String, String>> warnings;
    private List<Tuple<String, String>> errors;
    private Consumer<Screen> onContinue;
    
    public WarningAndErrorScreen(String action, List<Tuple<String, String>> warnings, List<Tuple<String, String>> errors, Consumer<Screen> onContinue) {
        super(NarratorChatListener.NO_TITLE);
        this.action = action;
        this.warnings = warnings;
        this.errors = errors;
        this.onContinue = onContinue;
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    public void setParent(Screen parent) {
        this.parent = parent;
    }
    
    private void addText(ITextComponent string) {
        for (IReorderingProcessor s : font.split(string, width - 80)) {
            listWidget.creditsAddEntry(new TextItem(s));
        }
    }
    
    private void addLink(ITextComponent string, String link) {
        for (IReorderingProcessor s : font.split(string, width - 80)) {
            listWidget.creditsAddEntry(new LinkItem(s, link));
        }
    }
    
    @Override
    public void init() {
        children.add(listWidget = new StringEntryListWidget(minecraft, width, height, 32, height - 32));
        listWidget.max = 80;
        listWidget.creditsClearEntries();
        listWidget.creditsAddEntry(new EmptyItem());
        if (!warnings.isEmpty())
            listWidget.creditsAddEntry(new TextItem(new StringTextComponent("Warnings:").withStyle(TextFormatting.GOLD).getVisualOrderText()));
        for (Tuple<String, String> pair : warnings) {
            addText(new StringTextComponent(pair.getA()));
            if (pair.getB() != null)
                addLink(new StringTextComponent(pair.getB()), pair.getB());
            for (int i = 0; i < 2; i++) {
                listWidget.creditsAddEntry(new EmptyItem());
            }
        }
        if (!warnings.isEmpty() && !errors.isEmpty()) {
            listWidget.creditsAddEntry(new EmptyItem());
        }
        if (!errors.isEmpty())
            listWidget.creditsAddEntry(new TextItem(new StringTextComponent("Errors:").withStyle(TextFormatting.RED).getVisualOrderText()));
        for (Tuple<String, String> pair : errors) {
            addText(new StringTextComponent(pair.getA()));
            if (pair.getB() != null)
                addLink(new StringTextComponent(pair.getB()), pair.getB());
            for (int i = 0; i < 2; i++) {
                listWidget.creditsAddEntry(new EmptyItem());
            }
        }
        for (StringItem child : listWidget.children()) {
            listWidget.max = Math.max(listWidget.max, child.getWidth());
        }
        children.add(buttonExit = new Button(width / 2 - 100, height - 26, 200, 20,
                new StringTextComponent(errors.isEmpty() ? "Continue" : "Exit"),
                button -> onContinue.accept(parent)));
    }
    
    @Override
    public boolean mouseScrolled(double double_1, double double_2, double double_3) {
        return listWidget.mouseScrolled(double_1, double_2, double_3) || super.mouseScrolled(double_1, double_2, double_3);
    }
    
    @Override
    public void render(MatrixStack matrices, int int_1, int int_2, float float_1) {
        this.renderDirtBackground(0);
        this.listWidget.render(matrices, int_1, int_2, float_1);
        if (RoughlyEnoughItemsState.getErrors().isEmpty()) {
            drawCenteredString(matrices, this.font, "Warnings during Roughly Enough Items' " + action, this.width / 2, 16, 16777215);
        } else {
            drawCenteredString(matrices, this.font, "Errors during Roughly Enough Items' " + action, this.width / 2, 16, 16777215);
        }
        super.render(matrices, int_1, int_2, float_1);
        this.buttonExit.render(matrices, int_1, int_2, float_1);
    }
    
    private static class StringEntryListWidget extends DynamicNewSmoothScrollingEntryListWidget<StringItem> {
        private boolean inFocus;
        private int max = 80;
        
        public StringEntryListWidget(Minecraft client, int width, int height, int startY, int endY) {
            super(client, width, height, startY, endY, AbstractGui.BACKGROUND_LOCATION);
        }
        
        @Override
        public boolean changeFocus(boolean boolean_1) {
            if (!this.inFocus && this.getItemCount() == 0) {
                return false;
            } else {
                this.inFocus = !this.inFocus;
                if (this.inFocus && this.getFocused() == null && this.getItemCount() > 0) {
                    this.moveSelection(1);
                } else if (this.inFocus && this.getFocused() != null) {
                    this.moveSelection(0);
                }
                
                return this.inFocus;
            }
        }
        
        public void creditsClearEntries() {
            clearItems();
        }
        
        private StringItem rei_getEntry(int int_1) {
            return this.children().get(int_1);
        }
        
        public void creditsAddEntry(StringItem entry) {
            addItem(entry);
        }
        
        @Override
        public int getItemWidth() {
            return max;
        }
        
        @Override
        protected int getScrollbarPosition() {
            return width - 40;
        }
    }
    
    private abstract static class StringItem extends DynamicNewSmoothScrollingEntryListWidget.Entry<StringItem> {
        public abstract int getWidth();
    }
    
    private static class EmptyItem extends StringItem {
        @Override
        public void render(MatrixStack matrixStack, int i, int i1, int i2, int i3, int i4, int i5, int i6, boolean b, float v) {
            
        }
        
        @Override
        public int getItemHeight() {
            return 5;
        }
        
        @Override
        public int getWidth() {
            return 0;
        }
    }
    
    private static class TextItem extends StringItem {
        private IReorderingProcessor text;
        
        public TextItem(IReorderingProcessor text) {
            this.text = text;
        }
        
        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            Minecraft.getInstance().font.drawShadow(matrices, text, x + 5, y, -1);
        }
        
        @Override
        public int getItemHeight() {
            return 12;
        }
        
        @Override
        public boolean changeFocus(boolean boolean_1) {
            return false;
        }
        
        @Override
        public int getWidth() {
            return Minecraft.getInstance().font.width(text) + 10;
        }
    }
    
    private class LinkItem extends StringItem {
        private IReorderingProcessor text;
        private String link;
        private boolean contains;
        
        public LinkItem(IReorderingProcessor text, String link) {
            this.text = text;
            this.link = link;
        }
        
        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            contains = mouseX >= x && mouseX <= x + entryWidth && mouseY >= y && mouseY <= y + entryHeight;
            if (contains) {
                WarningAndErrorScreen.this.renderTooltip(matrices, new StringTextComponent("Click to open link."), mouseX, mouseY);
                Minecraft.getInstance().font.drawShadow(matrices, characterVisitor -> {
                    return text.accept((charIndex, style, codePoint) -> characterVisitor.accept(charIndex, style.applyFormat(TextFormatting.UNDERLINE), codePoint));
                }, x + 5, y, 0xff1fc3ff);
            } else {
                Minecraft.getInstance().font.drawShadow(matrices, text, x + 5, y, 0xff1fc3ff);
            }
        }
        
        @Override
        public int getItemHeight() {
            return 12;
        }
        
        @Override
        public boolean changeFocus(boolean boolean_1) {
            return false;
        }
        
        @Override
        public int getWidth() {
            return Minecraft.getInstance().font.width(text) + 10;
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (contains && button == 0) {
                Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                try {
                    Util.getPlatform().openUri(new URI(link));
                    return true;
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }
}
