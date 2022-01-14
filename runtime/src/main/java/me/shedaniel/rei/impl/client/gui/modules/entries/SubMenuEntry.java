package me.shedaniel.rei.impl.client.gui.modules.entries;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.api.ScissorsHandler;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.common.util.ImmutableTextComponent;
import me.shedaniel.rei.impl.client.gui.modules.AbstractMenuEntry;
import me.shedaniel.rei.impl.client.gui.modules.Menu;
import me.shedaniel.rei.impl.client.gui.modules.MenuEntry;
import me.shedaniel.rei.impl.client.gui.widget.TabWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class SubMenuEntry extends AbstractMenuEntry {
    public final Component text;
    private int textWidth = -69;
    protected List<MenuEntry> entries;
    protected Menu childMenu;
    
    public SubMenuEntry(Component text) {
        this(text, Collections.emptyList());
    }
    
    public SubMenuEntry(Component text, Supplier<List<MenuEntry>> entries) {
        this(text, entries.get());
    }
    
    public SubMenuEntry(Component text, List<MenuEntry> entries) {
        this.text = MoreObjects.firstNonNull(text, ImmutableTextComponent.EMPTY);
        this.entries = entries;
    }
    
    private int getTextWidth() {
        if (textWidth == -69) {
            this.textWidth = Math.max(0, font.width(text));
        }
        return this.textWidth;
    }
    
    public Menu getChildMenu() {
        if (childMenu == null) {
            this.childMenu = new Menu(new Rectangle(getParent().getBounds().x + 1, getY() - 1, getParent().getBounds().width - 2, getEntryHeight() - 2), entries, false);
        }
        return childMenu;
    }
    
    @Override
    public int getEntryWidth() {
        return 12 + getTextWidth() + 4;
    }
    
    @Override
    public int getEntryHeight() {
        return 12;
    }
    
    @Override
    public void render(PoseStack poses, int mouseX, int mouseY, float delta) {
        renderBackground(poses, getX(), getY(), getX() + getWidth(), getY() + getEntryHeight());
        if (isSelected()) {
            if (!entries.isEmpty()) {
                Menu menu = getChildMenu();
                
                Rectangle menuStart = new Rectangle(getParent().getBounds().x, getY(), getParent().getBounds().width, getEntryHeight());
                
                int fullWidth = Minecraft.getInstance().screen.width;
                int fullHeight = Minecraft.getInstance().screen.height;
                boolean facingRight = getParent().facingRight;
                int menuWidth = menu.getMaxEntryWidth() + 2 + (menu.hasScrollBar() ? 6 : 0);
                if (facingRight && fullWidth - menuStart.getMaxX() < menuWidth + 10) {
                    facingRight = false;
                } else if (!facingRight && menuStart.x < menuWidth + 10) {
                    facingRight = true;
                }
                boolean facingDownwards = fullHeight - menuStart.getMaxY() > menuStart.y;
                
                menu.menuStartPoint.y = facingDownwards ? menuStart.y - 1 : menuStart.getMaxY() - (menu.scrolling.getMaxScrollHeight() + 1);
                menu.menuStartPoint.x = facingRight ? menuStart.getMaxX() : menuStart.x - (menu.getMaxEntryWidth() + 2 + (menu.scrolling.getMaxScrollHeight() > menu.getInnerHeight(menu.menuStartPoint.y) ? 6 : 0));
                
                List<Rectangle> areas = Lists.newArrayList(ScissorsHandler.INSTANCE.getScissorsAreas());
                ScissorsHandler.INSTANCE.clearScissors();
                menu.render(poses, mouseX, mouseY, delta);
                for (Rectangle area : areas) {
                    ScissorsHandler.INSTANCE.scissor(area);
                }
            }
        }
        font.draw(poses, text, getX() + 2, getY() + 2, isSelected() ? 16777215 : 8947848);
        if (!entries.isEmpty()) {
            Minecraft.getInstance().getTextureManager().bind(TabWidget.CHEST_GUI_TEXTURE);
            blit(poses, getX() + getWidth() - 15, getY() - 2, 0, 28, 18, 18);
        }
    }
    
    protected void renderBackground(PoseStack poses, int x, int y, int width, int height) {
        if (isSelected()) {
            fill(poses, x, y, x + width, y + height, -12237499);
        }
    }
    
    @Override
    public boolean containsMouse(double mouseX, double mouseY) {
        if (super.containsMouse(mouseX, mouseY))
            return true;
        if (childMenu != null && !childMenu.children().isEmpty() && isSelected())
            return childMenu.containsMouse(mouseX, mouseY);
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return childMenu != null && !childMenu.children().isEmpty() && isSelected() && childMenu.mouseScrolled(mouseX, mouseY, amount);
    }
    
    @Override
    public List<? extends GuiEventListener> children() {
        if (childMenu != null && !childMenu.children().isEmpty() && isSelected()) {
            return Collections.singletonList(childMenu);
        }
        return Collections.emptyList();
    }
}
