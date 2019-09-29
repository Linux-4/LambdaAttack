package org.spacehq.mc.protocol.packet.ingame.server;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.spacehq.mc.protocol.data.MagicValues;
import org.spacehq.mc.protocol.data.game.statistic.BreakBlockStatistic;
import org.spacehq.mc.protocol.data.game.statistic.BreakItemStatistic;
import org.spacehq.mc.protocol.data.game.statistic.CraftItemStatistic;
import org.spacehq.mc.protocol.data.game.statistic.CustomStatistic;
import org.spacehq.mc.protocol.data.game.statistic.DropItemStatistic;
import org.spacehq.mc.protocol.data.game.statistic.GenericStatistic;
import org.spacehq.mc.protocol.data.game.statistic.KillEntityStatistic;
import org.spacehq.mc.protocol.data.game.statistic.KilledByEntityStatistic;
import org.spacehq.mc.protocol.data.game.statistic.PickupItemStatistic;
import org.spacehq.mc.protocol.data.game.statistic.Statistic;
import org.spacehq.mc.protocol.data.game.statistic.StatisticCategory;
import org.spacehq.mc.protocol.data.game.statistic.UseItemStatistic;
import org.spacehq.mc.protocol.packet.MinecraftPacket;

public class ServerStatisticsPacket extends MinecraftPacket {
    private Map<Statistic, Integer> statistics = new HashMap<Statistic, Integer>();

    @SuppressWarnings("unused")
    private ServerStatisticsPacket() {
    }

    public ServerStatisticsPacket(Map<Statistic, Integer> statistics) {
        this.statistics = statistics;
    }

    public Map<Statistic, Integer> getStatistics() {
        return this.statistics;
    }

    @Override
    public void read(NetInput in) throws IOException {
        int length = in.readVarInt();
        for(int index = 0; index < length; index++) {
            int categoryId = in.readVarInt();
            int statisticId = in.readVarInt();
            Statistic statistic;
            try {
                switch (MagicValues.key(StatisticCategory.class, categoryId)) {
                    case BREAK_BLOCK:
                        statistic = new BreakBlockStatistic(statisticId);
                        break;
                    case CRAFT_ITEM:
                        statistic = new CraftItemStatistic(statisticId);
                        break;
                    case USE_ITEM:
                        statistic = new UseItemStatistic(statisticId);
                        break;
                    case BREAK_ITEM:
                        statistic = new BreakItemStatistic(statisticId);
                        break;
                    case PICKED_UP_ITEM:
                        statistic = new PickupItemStatistic(statisticId);
                        break;
                    case DROP_ITEM:
                        statistic = new DropItemStatistic(statisticId);
                        break;
                    case KILL_ENTITY:
                        statistic = new KillEntityStatistic(statisticId);
                        break;
                    case KILLED_BY_ENTITY:
                        statistic = new KilledByEntityStatistic(statisticId);
                        break;
                    case GENERIC:
                        statistic = MagicValues.key(GenericStatistic.class, statisticId);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException e) {
                statistic = new CustomStatistic(categoryId, statisticId);
            }
            this.statistics.put(statistic, in.readVarInt());
        }
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(this.statistics.size());
        for(Statistic statistic : this.statistics.keySet()) {
            int categoryId;
            int statisticId;
            if(statistic instanceof CustomStatistic) {
                categoryId = ((CustomStatistic) statistic).getCategory();
                statisticId = ((CustomStatistic) statistic).getId();
            } else {
                StatisticCategory category;
                if(statistic instanceof CraftItemStatistic) {
                    category = StatisticCategory.CRAFT_ITEM;
                    statisticId = ((CraftItemStatistic) statistic).getId();
                } else if(statistic instanceof BreakBlockStatistic) {
                    category = StatisticCategory.BREAK_BLOCK;
                    statisticId = ((BreakBlockStatistic) statistic).getId();
                } else if(statistic instanceof UseItemStatistic) {
                    category = StatisticCategory.USE_ITEM;
                    statisticId = ((UseItemStatistic) statistic).getId();
                } else if(statistic instanceof BreakItemStatistic) {
                    category = StatisticCategory.BREAK_ITEM;
                    statisticId = ((BreakItemStatistic) statistic).getId();
                } else if(statistic instanceof KillEntityStatistic) {
                    category = StatisticCategory.KILL_ENTITY;
                    statisticId = ((KillEntityStatistic) statistic).getId();
                } else if(statistic instanceof KilledByEntityStatistic) {
                    category = StatisticCategory.KILLED_BY_ENTITY;
                    statisticId = ((KilledByEntityStatistic) statistic).getId();
                } else if(statistic instanceof DropItemStatistic) {
                    category = StatisticCategory.DROP_ITEM;
                    statisticId = ((DropItemStatistic) statistic).getId();
                } else if(statistic instanceof PickupItemStatistic) {
                    category = StatisticCategory.PICKED_UP_ITEM;
                    statisticId = ((PickupItemStatistic) statistic).getId();
                } else if(statistic instanceof GenericStatistic) {
                    category = StatisticCategory.GENERIC;
                    statisticId = MagicValues.value(Integer.class, statistic);
                } else {
                    throw new IllegalArgumentException(statistic.getClass().getName());
                }
                categoryId = MagicValues.value(Integer.class, category);
            }
            out.writeVarInt(categoryId);
            out.writeVarInt(statisticId);
            out.writeVarInt(this.statistics.get(statistic));
        }
    }
}
