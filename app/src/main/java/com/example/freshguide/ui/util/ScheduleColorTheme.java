package com.example.freshguide.ui.util;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.freshguide.R;

/**
 * Schedule color palette and theming utility.
 * Provides consistent color schemes for schedule entries across light/dark modes.
 */
public class ScheduleColorTheme {

    private final Context context;
    private final boolean isDarkMode;

    public ScheduleColorTheme(@NonNull Context context) {
        this.context = context;
        this.isDarkMode = (context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * Returns gradient color pairs for all palette slots.
     * Each slot has [startColor, endColor] for gradient backgrounds.
     */
    public String[][] getScheduleGradients() {
        if (isDarkMode) {
            return new String[][]{
                    {"#2C2C2C", "#3E3E3E"},   // Neutral slate
                    {"#4D1A2E", "#6B2D45"},   // Deep rose
                    {"#1A2E4D", "#2D4A6B"},   // Midnight blue
                    {"#2E1A4D", "#4A2D6B"},   // Violet dusk
                    {"#1A3D1C", "#2E5E30"},   // Forest green
                    {"#3D3A14", "#5E5820"}    // Golden earth
            };
        } else {
            return new String[][]{
                    {"#E8E8E8", "#F5F5F5"},   // Soft silver
                    {"#F8C5D8", "#FFE4EE"},   // Blush pink
                    {"#C5D8F8", "#E0EEFF"},   // Sky blue
                    {"#E5C5F8", "#F3E0FF"},   // Lavender
                    {"#C5F0C7", "#E0FFE2"},   // Mint green
                    {"#F0F0A0", "#FFFFF0"}    // Lemon cream
            };
        }
    }

    /**
     * Returns flat colors for each palette slot (backward compatibility).
     */
    public String[] getScheduleColors() {
        String[][] gradients = getScheduleGradients();
        String[] flat = new String[gradients.length];
        for (int i = 0; i < gradients.length; i++) {
            flat[i] = gradients[i][0];
        }
        return flat;
    }

    /**
     * Resolves which palette slot (0-5) a color hex belongs to.
     * Returns 0 (neutral) if not found.
     */
    public int resolveSchedulePaletteSlot(@Nullable String colorHex) {
        if (colorHex == null || colorHex.trim().isEmpty()) {
            return 0;
        }

        int targetColor;
        try {
            targetColor = Color.parseColor(colorHex);
        } catch (Exception e) {
            return 0;
        }

        String[][] light = getScheduleGradientsForTheme(false);
        String[][] dark = getScheduleGradientsForTheme(true);

        for (int i = 0; i < light.length; i++) {
            try {
                int lightStart = Color.parseColor(light[i][0]);
                int lightEnd = Color.parseColor(light[i][1]);
                int darkStart = Color.parseColor(dark[i][0]);
                int darkEnd = Color.parseColor(dark[i][1]);

                if (targetColor == lightStart || targetColor == lightEnd
                        || targetColor == darkStart || targetColor == darkEnd) {
                    return i;
                }
            } catch (Exception ignored) {
                // Invalid color in palette, skip
            }
        }

        return 0; // Default to neutral
    }

    /**
     * Gets primary text color for a palette slot.
     */
    public int getPalettePrimaryTextColor(int slotIndex) {
        if (isDarkMode) {
            switch (slotIndex) {
                case 0: return Color.parseColor("#F2F2F2"); // neutral
                case 1: return Color.parseColor("#FFD6E5"); // rose
                case 2: return Color.parseColor("#D6E8FF"); // blue
                case 3: return Color.parseColor("#E7D6FF"); // violet
                case 4: return Color.parseColor("#D8F5DB"); // green
                case 5: return Color.parseColor("#F5EFC2"); // gold
                default: return ContextCompat.getColor(context, R.color.text_primary);
            }
        } else {
            switch (slotIndex) {
                case 0: return Color.parseColor("#333333"); // neutral
                case 1: return Color.parseColor("#8E2C52"); // dark rose
                case 2: return Color.parseColor("#2F5D9A"); // deep blue
                case 3: return Color.parseColor("#6E3FA3"); // plum
                case 4: return Color.parseColor("#2F7A39"); // forest
                case 5: return Color.parseColor("#7A6A16"); // olive-gold
                default: return ContextCompat.getColor(context, R.color.text_primary);
            }
        }
    }

    /**
     * Gets secondary text color for a palette slot.
     */
    public int getPaletteSecondaryTextColor(int slotIndex) {
        if (isDarkMode) {
            switch (slotIndex) {
                case 0: return adjustColorAlpha(Color.parseColor("#EAEAEA"), 0.72f);
                case 1: return adjustColorAlpha(Color.parseColor("#FFD6E5"), 0.72f);
                case 2: return adjustColorAlpha(Color.parseColor("#D6E8FF"), 0.72f);
                case 3: return adjustColorAlpha(Color.parseColor("#E7D6FF"), 0.72f);
                case 4: return adjustColorAlpha(Color.parseColor("#D8F5DB"), 0.72f);
                case 5: return adjustColorAlpha(Color.parseColor("#F5EFC2"), 0.72f);
                default: return ContextCompat.getColor(context, R.color.text_secondary);
            }
        } else {
            switch (slotIndex) {
                case 0: return Color.parseColor("#666666");
                case 1: return Color.parseColor("#A24B6E");
                case 2: return Color.parseColor("#4F78B1");
                case 3: return Color.parseColor("#855DB5");
                case 4: return Color.parseColor("#4D8D56");
                case 5: return Color.parseColor("#958633");
                default: return ContextCompat.getColor(context, R.color.text_secondary);
            }
        }
    }

    /**
     * Builds a diagonal gradient drawable for a palette slot.
     */
    public GradientDrawable buildGradientDrawable(int slotIndex, float cornerRadiusPx) {
        String[][] gradients = getScheduleGradients();
        if (slotIndex < 0 || slotIndex >= gradients.length) {
            slotIndex = 0;
        }

        String[] pair = gradients[slotIndex];
        int startColor, endColor;
        try {
            startColor = Color.parseColor(pair[0]);
            endColor = Color.parseColor(pair[1]);
        } catch (Exception e) {
            startColor = Color.parseColor("#E8E8E8");
            endColor = Color.parseColor("#F5F5F5");
        }

        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{startColor, endColor}
        );
        drawable.setCornerRadius(cornerRadiusPx);
        return drawable;
    }

    /**
     * Lightens a color by multiplying RGB channels by factor.
     */
    public static int lightenColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.min(255, Math.round(Color.red(color) * factor));
        int g = Math.min(255, Math.round(Color.green(color) * factor));
        int b = Math.min(255, Math.round(Color.blue(color) * factor));
        return Color.argb(a, r, g, b);
    }

    /**
     * Adjusts color alpha channel by multiplying by factor (0-1).
     */
    public static int adjustColorAlpha(int color, float factor) {
        int alpha = Math.min(255, Math.round(Color.alpha(color) * factor));
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * Gets gradients for specific theme mode (for external use).
     */
    private String[][] getScheduleGradientsForTheme(boolean dark) {
        if (dark) {
            return new String[][]{
                    {"#2C2C2C", "#3E3E3E"},
                    {"#4D1A2E", "#6B2D45"},
                    {"#1A2E4D", "#2D4A6B"},
                    {"#2E1A4D", "#4A2D6B"},
                    {"#1A3D1C", "#2E5E30"},
                    {"#3D3A14", "#5E5820"}
            };
        } else {
            return new String[][]{
                    {"#E8E8E8", "#F5F5F5"},
                    {"#F8C5D8", "#FFE4EE"},
                    {"#C5D8F8", "#E0EEFF"},
                    {"#E5C5F8", "#F3E0FF"},
                    {"#C5F0C7", "#E0FFE2"},
                    {"#F0F0A0", "#FFFFF0"}
            };
        }
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public Context getContext() {
        return context;
    }
}
