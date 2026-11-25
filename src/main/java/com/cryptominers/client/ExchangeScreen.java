package com.cryptominers.client;

import com.cryptominers.coin.CoinType;
import com.cryptominers.market.Exchange;
import com.cryptominers.market.ExchangeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;

import java.util.Locale;

public class ExchangeScreen extends AbstractContainerScreen<ExchangeMenu> {
    private static final String[] SELL = {"1", "8", "64", "All"};
    private static final String[] BUY = {"1", "8", "64", "Max"};
    private static final int[] COLORS = {0xFF2E9E3E, 0xFFC02020, 0xFF8A3CD0};
    private static final int ROW_Y = 30;
    private static final int ROW_H = 34;

    public ExchangeScreen(ExchangeMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 256;
        this.imageHeight = 144;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < CoinType.values().length; i++) {
            int rowY = topPos + ROW_Y + i * ROW_H;
            for (int j = 0; j < 4; j++) {
                final int sellId = (i * 2) * 4 + j;
                final int buyId = (i * 2 + 1) * 4 + j;
                addRenderableWidget(Button.builder(Component.literal(SELL[j]),
                                b -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, sellId))
                        .bounds(leftPos + 30 + j * 24, rowY + 12, 22, 14).build());
                addRenderableWidget(Button.builder(Component.literal(BUY[j]),
                                b -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, buyId))
                        .bounds(leftPos + 158 + j * 24, rowY + 12, 22, 14).build());
            }
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

        CoinType[] coins = CoinType.values();
        for (int i = 0; i < coins.length; i++) {
            CoinType c = coins[i];
            int rowY = y + ROW_Y + i * ROW_H;
            g.fill(x + 4, rowY - 3, x + imageWidth - 4, rowY + 28, 0xFFB0B0B0);
            g.fill(x + 4, rowY - 3, x + 7, rowY + 28, COLORS[i]);

            g.drawString(font, c.displayName, x + 12, rowY, COLORS[i] & 0xFFFFFF, false);
            g.drawString(font, String.format(Locale.ROOT, "Price: %.2f E", menu.getPrice(c)), x + 96, rowY, 0x202020, false);
            g.drawString(font, "Have: " + Exchange.count(minecraft.player, c.item()), x + 190, rowY, 0x202020, false);
            g.drawString(font, "Sell", x + 10, rowY + 16, 0x404040, false);
            g.drawString(font, "Buy", x + 136, rowY + 16, 0x404040, false);
        }

        String footer = "Emeralds: " + Exchange.count(minecraft.player, Items.EMERALD)
                + (menu.isOnline() ? String.format(Locale.ROOT, "    Online fee: %.0f%%", menu.getFee() * 100) : "    No fee");
        g.drawString(font, footer, x + 8, y + imageHeight - 14, 0x404040, false);
    }
}
