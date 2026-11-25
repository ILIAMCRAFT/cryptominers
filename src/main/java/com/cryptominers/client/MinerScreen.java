package com.cryptominers.client;

import com.cryptominers.coin.CoinType;
import com.cryptominers.miner.MinerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.Locale;

public class MinerScreen extends AbstractContainerScreen<MinerMenu> {
    private static final String[] SHORT = {"Mine", "Red", "Dragon"};
    private final Button[] coinButtons = new Button[CoinType.values().length];

    public MinerScreen(MinerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 192;
        this.inventoryLabelY = 100;
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < coinButtons.length; i++) {
            final int idx = i;
            coinButtons[i] = addRenderableWidget(Button.builder(Component.literal(SHORT[i]),
                            b -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, idx))
                    .bounds(leftPos + 122, topPos + 20 + i * 18, 50, 16).build());
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        int sel = menu.getCoin();
        for (int i = 0; i < coinButtons.length; i++) {
            if (coinButtons[i] == null) continue;
            coinButtons[i].setMessage(Component.literal((i == sel ? "> " : "") + SHORT[i]));
            coinButtons[i].active = i != sel;
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        super.render(g, mouseX, mouseY, partialTick);
        renderTooltip(g, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        g.fill(x - 1, y - 1, x + imageWidth + 1, y + imageHeight + 1, 0xFF000000);
        g.fill(x, y, x + imageWidth, y + imageHeight, 0xFFC6C6C6);
        for (Slot s : menu.slots) {
            g.fill(x + s.x - 1, y + s.y - 1, x + s.x + 17, y + s.y + 17, 0xFF373737);
            g.fill(x + s.x, y + s.y, x + s.x + 16, y + s.y + 16, 0xFF8B8B8B);
        }

        float temp = menu.getTemperature();
        int tempColor = temp < 70f ? 0xFF3DA640 : temp < 90f ? 0xFFD4B400 : temp < 110f ? 0xFFE07A10 : 0xFFD02020;
        CoinType coin = CoinType.values()[Math.max(0, Math.min(CoinType.values().length - 1, menu.getCoin()))];

        bar(g, x + 8, y + 20, menu.getMaxEnergy() == 0 ? 0 : (float) menu.getEnergy() / menu.getMaxEnergy(), 0xFFC02828,
                "FE " + k(menu.getEnergy()) + " / " + k(menu.getMaxEnergy()));
        bar(g, x + 8, y + 34, temp / 130f, tempColor,
                String.format(Locale.ROOT, "Temp %.0f\u00b0C / 130", temp));
        bar(g, x + 8, y + 48, menu.getWater() / 4000f, 0xFF2F6FE0,
                "Water " + menu.getWater() + " / 4000 mB");
        bar(g, x + 8, y + 62, menu.getProgress(), 0xFF8A3CD0,
                coin.displayName + " " + (int) (menu.getProgress() * 100) + "%");

        String status;
        int color = 0x2E7D32;
        if (temp >= 90f) {
            status = "OVERHEATING!";
            color = 0xB71C1C;
        } else if (menu.isRunning()) {
            status = "Mining (" + menu.getPower() + " FE/t)";
        } else if (menu.getEnergy() < menu.getPower()) {
            status = "No power";
            color = 0xB71C1C;
        } else {
            status = "Idle / output full";
            color = 0x555555;
        }
        g.drawString(font, status, x + 8, y + 80, color, false);
        g.drawString(font, "Out:", x + 122, y + 82, 0x404040, false);
    }

    private void bar(GuiGraphics g, int x, int y, float frac, int color, String text) {
        frac = Math.max(0f, Math.min(1f, frac));
        g.fill(x - 1, y - 1, x + 107, y + 12, 0xFF373737);
        g.fill(x, y, x + 106, y + 11, 0xFF1E1E1E);
        g.fill(x, y, x + (int) (106 * frac), y + 11, color);
        g.drawString(font, text, x + 3, y + 2, 0xFFFFFF, true);
    }

    private static String k(int n) {
        return n >= 1000 ? String.format(Locale.ROOT, "%.1fk", n / 1000f) : Integer.toString(n);
    }
}
