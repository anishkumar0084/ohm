package com.ohmshantiapps.menu.wallet;

public class CoinUtils {

    public static String formatCoins(int coins) {
        if (coins >= 1_000_000) {
            return String.format("%.1fM", coins / 1_000_000.0);
        } else if (coins >= 1_000) {
            return String.format("%.1fK", coins / 1_000.0);
        } else {
            return String.valueOf(coins);
        }
    }
}
