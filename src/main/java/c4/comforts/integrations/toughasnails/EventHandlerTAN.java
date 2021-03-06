/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.integrations.toughasnails;

import c4.comforts.common.blocks.BlockSleepingBag;
import c4.comforts.common.capability.CapabilitySleepTime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.temperature.TemperatureHelper;

public class EventHandlerTAN {

    @SubscribeEvent
    public void onPlayerWakeUp(PlayerWakeUpEvent e) {
        EntityPlayer player = e.getEntityPlayer();
        World world = player.world;
        BlockPos pos = player.bedLocation;

        if (!world.isRemote && pos != null && world.getBlockState(pos).getBlock() instanceof BlockSleepingBag) {
            CapabilitySleepTime.ISleepTime sleepTime = CapabilitySleepTime.getSleepTime(player);

            if (sleepTime != null) {
                long timeSlept = world.getWorldTime() - sleepTime.getSleepTime();

                if (timeSlept > 1000L) {
                    warmBody(player, timeSlept);
                }
            }
        }
    }

    @Optional.Method(modid = "toughasnails")
    private void warmBody(EntityPlayer player, long timeSlept) {
        ITemperature playerTemp = TemperatureHelper.getTemperatureData(player);
        int temp = playerTemp.getTemperature().getRawValue();
        if (temp < 10) {
            int warmTemp = (int) (timeSlept / 1000L);
            temp = Math.min(10, temp + Math.min(5, warmTemp));
        }
        playerTemp.setTemperature(new Temperature(temp));
    }
}
