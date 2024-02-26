package cn.seiua.skymatrix.utils;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class PlayerListUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final Ordering<PlayerListEntry> playerOrdering = Ordering.from(new PlayerComparator());

    public static final Ordering<PlayerListEntry> playerInfoOrdering2 = new Ordering<PlayerListEntry>() {
        @Override
        public int compare(PlayerListEntry yuanshen, PlayerListEntry ple) {
            Team scoreplayerteam = yuanshen.getScoreboardTeam();
            Team scoreplayerteam1 = ple.getScoreboardTeam();
            return ComparisonChain.start()
                    .compareTrueFirst(yuanshen.getGameMode() != GameMode.SPECTATOR,
                            ple.getGameMode() != GameMode.SPECTATOR)
                    .compare(scoreplayerteam != null ? scoreplayerteam.getName() : "",
                            scoreplayerteam1 != null ? scoreplayerteam1.getName() : "")
                    .compare(yuanshen.getProfile().getName(), ple.getProfile().getName()).result();
        }
    };


    public static PlayerListHud getTabList() {
        return mc.inGameHud.getPlayerListHud();
    }

    public static List<PlayerListEntry> getTabEntries() {
        if (mc.player == null || mc.getNetworkHandler() == null)
            return Collections.emptyList();
        return playerInfoOrdering2.sortedCopy(mc.getNetworkHandler().getPlayerList());
    }

    public static List<String> getTabListListStr() {
        List<String> list = new ArrayList<>();
        for (PlayerListEntry entry : getTabEntries())
            list.add(mc.inGameHud.getPlayerListHud().getPlayerName(entry).getString());
        return list;
    }


    public static String copyContainsLine(String str) {

        if (mc.getNetworkHandler() == null)
            return null;

        List<PlayerListEntry> players = playerOrdering.sortedCopy(mc.getNetworkHandler().getPlayerList());
        for (PlayerListEntry info : players) {
            String name = mc.inGameHud.getPlayerListHud().getPlayerName(info).getString();
            if (name.contains(str))
                return name;
        }
        return null;
    }

    static class PlayerComparator implements Comparator<PlayerListEntry> {
        public int compare(PlayerListEntry ple1, PlayerListEntry ple2) {
            Team team1 = ple1.getScoreboardTeam();
            Team team2 = ple2.getScoreboardTeam();
            return ComparisonChain.start()
                    .compareTrueFirst(ple1.getGameMode() != GameMode.SPECTATOR,
                            ple2.getGameMode() != GameMode.SPECTATOR)
                    .compare(team1 != null ? team1.getName() : "", team2 != null ? team2.getName() : "")
                    .compare(ple1.getProfile().getName(), ple2.getProfile().getName()).result();
        }
    }
}
