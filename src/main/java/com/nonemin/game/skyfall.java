/*
 * Copyright 2021 AntonioLiang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nonemin.game;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Random;

import static org.bukkit.Particle.ASH;
import static org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_IMPACT;

public class skyfall extends JavaPlugin {
    public boolean isstop = false;
    @Override
    public void onEnable() {
        getLogger().info("skyfall流星插件已启用");
    }
    @Override
    public void onDisable() {
        getLogger().info("skyfall流星插件已卸载");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("skyfall")) {
            if (!(args.length == 4 || args.length == 0 || args.length == 1)) {
                sender.sendMessage("参数太多，正确用法为：/skyfall 流星类型 x y z");
                return false;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("这个指令只能让玩家使用。");
                return false;
            }
            Player player = (Player) sender;
            Location loc = player.getLocation();
            int ret = 0;
            if (args.length == 4 ) {
                loc.setX(Double.parseDouble(args[1]));
                loc.setY(Double.parseDouble(args[2]));
                loc.setZ(Double.parseDouble(args[3]));
                ret = skyfall(Integer.valueOf(args[0]),loc);
            }
            if (args.length == 1) {
                ret = skyfall(Integer.valueOf(args[0]),loc);
            }
            if (args.length == 0) {
                ret = skyfall(1,loc);
            }
            if(ret == -1){
                sender.sendMessage("找不到流星类型：" + args[0]);
            }

            return false;
        } else if (cmd.getName().equalsIgnoreCase("qskyfall")) {

            return false;
        }else if (cmd.getName().equalsIgnoreCase("stopskyfall")) {

            return false;
        }
        return false;
    }

    //立即在loc地点生成类型为type的流星事件
    public int skyfall(Integer type,Location loc){
        switch (type){
            case 1:
                firestar(loc);
                return 1;
            case 2:
                meteor(loc);
                return 1;
            case 3:
                bigstar(loc);
                return 1;
            case 4:
                comet(loc);
                return 1;
            case 5:
                spaceship(loc);
                return 1;
            default:
                return -1;

        }
    }

    //生成火流星
    public void firestar(final Location loc) {
        Bukkit.broadcastMessage("天空中闪过一条亮带，然后爆开了，应该是一颗火流星");
        getLogger().info("正在生成火流星，地点：" + loc.toString());
        isstop = false;
        //生成粒子
        loc.add(-500, 0, -500);
        loc.setY(250);
        getLogger().info("正在生成流星轨迹" + loc.toString());
        //World world = loc.getWorld();
        new BukkitRunnable() {
            int t = 500;

            public void run() {
                if (t < 0) {
                    cancel();
                    firestar2(loc);
                    return;
                }
                t--;
                loc.add(1, 0, 1);
                loc.getWorld().spawnParticle(ASH, loc, 1);
            }
        }.runTaskTimer(this, 0L, 1L);

    }
    public void firestar2(final Location loc){
        int sa = 3;//散布距离，越小越密集
        int ms = 4;//最长边，越大，资源堆体积越大
        int sc = 100;//陨石数量
        int sv = 100;//爆炸声音大小
        World world = loc.getWorld();
        assert world != null;
        world.playSound(loc,ENTITY_LIGHTNING_BOLT_IMPACT,100,0);

        getLogger().info("正在生成资源堆：" + loc.toString());
        new BukkitRunnable() {
            //生成次数
            int v = 0;
            //生成x
            //生成y
            int dx = 0;
            int dy = 0;

            public void run() {
                Location NewLocation;
                World world = loc.getWorld();
                v += 3;
                if (v > 500) {
                    cancel();
                    return;
                }
                dx = new Random().nextInt(v);
                dy = new Random().nextInt(v);
                if(dx %2 == 1){dx *= -1;}
                if(dy %2 == 0){dy *= -1;}
                NewLocation = loc.clone();
                NewLocation.add(dx,0,dy);
                if(new Random().nextInt(100) > 70) {
                    world.createExplosion(NewLocation, 4F);
                }
                do{
                    NewLocation.add(0,-1,0);
                }
                while(!NewLocation.getBlock().getType().isSolid() && NewLocation.getY()>3);
                //world.playSound(NewLocation,ENTITY_LIGHTNING_BOLT_IMPACT,100,0);
                int ox = NewLocation.getBlockX();
                int oy = NewLocation.getBlockY();
                int oz = NewLocation.getBlockZ();
                int xsize = new Random().nextInt(6);
                int ysize = new Random().nextInt(6);
                int zsize = new Random().nextInt(6);
                for(int x = 0;x < xsize;x++){
                    for(int z = 0;z < zsize;z++){
                        for(int y = 0;y < ysize;y++){
                            NewLocation.setX(ox + x);
                            NewLocation.setY(oy + y);
                            NewLocation.setZ(oz + z);
                            Block B = NewLocation.getBlock();
                            if(new Random().nextInt(100) > 50){
                                B.setType(Material.AIR);
                            }else {
                                int i = new Random().nextInt(100);
                                if (i < 70) {
                                    B.setType(Material.STONE);
                                }
                                if (i >= 70 && i < 85) {
                                    B.setType(Material.IRON_ORE);
                                }
                                if (i >= 85 && i < 95) {
                                    B.setType(Material.OBSIDIAN);
                                }
                                if (i >= 95 && i < 99) {
                                    B.setType(Material.REDSTONE_BLOCK);
                                }
                            }
                        }
                    }
                }

            }
        }.runTaskTimer(this, 0L, 5L);
        getLogger().info("生成资源堆完毕：");
    }

    //生成陨星
    public void bigstar(final Location loc) {
        Bukkit.broadcastMessage("大地一阵颤动，一颗陨星深深地砸到地上");
        World world = loc.getWorld();
        //List<Entity> elist = new ArrayList<Entity>();
        Collection<Entity> cole;
        do {
            loc.add(0, -1, 0);
        }
        while (!loc.getBlock().getType().isSolid() && loc.getY() > 3);
        assert world != null;
        world.playSound(loc, ENTITY_LIGHTNING_BOLT_IMPACT, 100, 0);
        new BukkitRunnable() {
            final int ox = loc.getBlockX();
            final int oy = loc.getBlockY();
            final int oz = loc.getBlockZ();
            int y = oy - 50;

            public void run() {

                if (y > oy + 25) {
                    cancel();
                    bigstar2(loc);
                    return;
                }
                getLogger().info("移除第" + y + "层");
                for (int x = ox - 100; x < ox + 100; x++) {
                    for (int z = oz - 100; z < oz + 100; z++) {
                        int dx = ox - x;
                        int dy = oy - y;
                        int dz = oz - z;
                        int d = dx * dx + dy * dy + dz * dz;
                        if (d < 10000) {
                            Location BL = loc.clone();
                            BL.setX(x);
                            BL.setY(y);
                            BL.setZ(z);
                            Block B = BL.getBlock();
                            B.setType(Material.AIR);
                        }
                    }
                }
                y++;
            }
        }.runTaskTimer(this, 20L, 30L);
    }
    //生成陨星2
    public void bigstar2(final Location loc){
        do {
            loc.add(0, -1, 0);
        }
        while (!loc.getBlock().getType().isSolid() && loc.getY() > 3);
        new BukkitRunnable() {
            final int ox = loc.getBlockX();
            final int oy = loc.getBlockY();
            final int oz = loc.getBlockZ();
            int y = oy;
            public void run() {
                y ++;
                if (y > oy + 30) {
                    cancel();
                    return;
                }
                getLogger().info("生成第" + y + "层");
                for(int x = ox - 30;x < ox + 30;x ++){
                    for(int z = oz - 30;z < oz + 30;z ++){
                        int dx = ox - x;
                        int dy = oy - y;
                        int dz = oz - z;
                        int d = dx*dx+dy*dy+dz*dz;
                        if(d < 900){
                            Location BL = loc;
                            BL.setX(x);
                            BL.setY(y);
                            BL.setZ(z);
                            Block B = BL.getBlock();
                            if(new Random().nextInt(100) > 90){
                                B.setType(Material.AIR);
                            }else {
                                int i = new Random().nextInt(100);
                                if (i < 70) {
                                    B.setType(Material.STONE);
                                }
                                if (i >= 70 && i < 95) {
                                    B.setType(Material.IRON_ORE);
                                }
                                if (i >= 95 && i < 98) {
                                    B.setType(Material.GOLD_ORE);
                                }
                                if (i == 98) {
                                    B.setType(Material.DIAMOND_ORE);
                                }
                                if (i >= 99) {
                                    B.setType(Material.EMERALD_ORE);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 20L, 10L);


    }

    //生成彗星
    public void comet(final Location loc) {
        Bukkit.broadcastMessage("一颗彗星拖着长长的尾巴掉了下来");
        World world = loc.getWorld();
        do {
            loc.add(0, -1, 0);
        }
        while (!loc.getBlock().getType().isSolid() && loc.getY() > 3);
        assert world != null;
        world.playSound(loc, ENTITY_LIGHTNING_BOLT_IMPACT, 100, 0);
        new BukkitRunnable() {
            final int ox = loc.getBlockX();
            final int oy = loc.getBlockY();
            final int oz = loc.getBlockZ();
            int y = oy - 30;
            public void run() {
                if (y > oy + 14) {
                    cancel();
                    comet2(loc);
                    return;
                }
                getLogger().info("移除第" + y + "层");
                for (int x = ox - 40; x < ox + 40; x++) {
                    for (int z = oz - 40; z < oz + 40; z++) {
                        int dx = ox - x;
                        int dy = oy - y;
                        int dz = oz - z;
                        int d = dx * dx + dy * dy + dz * dz;
                        if (d < 1600) {
                            Location BL = loc.clone();
                            BL.setX(x);
                            BL.setY(y);
                            BL.setZ(z);
                            Block B = BL.getBlock();
                            B.setType(Material.AIR);
                        }
                    }
                }
                y++;
            }
        }.runTaskTimer(this, 20L, 20L);
    }

    //生成彗星2
    public void comet2(final Location loc){
        do {
            loc.add(0, -1, 0);
        }
        while (!loc.getBlock().getType().isSolid() && loc.getY() > 3);
        new BukkitRunnable() {
            final int ox = loc.getBlockX();
            final int oy = loc.getBlockY();
            final int oz = loc.getBlockZ();
            int y = oy;
            public void run() {
                y ++;
                if (y > oy + 15) {
                    cancel();
                    return;
                }
                getLogger().info("生成第" + y + "层");
                for(int x = ox - 15;x < ox + 15;x ++){
                    for(int z = oz - 15;z < oz + 15;z ++){
                        int dx = ox - x;
                        int dy = oy - y;
                        int dz = oz - z;
                        int d = dx*dx+dy*dy+dz*dz;
                        if(d < 225){
                            Location BL = loc;
                            BL.setX(x);
                            BL.setY(y);
                            BL.setZ(z);
                            Block B = BL.getBlock();
                            if(new Random().nextInt(100) > 90){
                                B.setType(Material.AIR);
                            }else {
                                int i = new Random().nextInt(100);
                                if (i < 30) {
                                    B.setType(Material.STONE);
                                }
                                if (i >= 30 && i < 60) {
                                    B.setType(Material.IRON_ORE);
                                }
                                if (i >= 60 && i < 80) {
                                    B.setType(Material.ICE);
                                }
                                if (i >= 80 && i < 90) {
                                    B.setType(Material.BLUE_ICE);
                                }
                                if (i >= 90) {
                                    B.setType(Material.DIAMOND_BLOCK);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 20L, 10L);


    }

    //生成外星飞船
    public void spaceship(final Location loc) {
        Bukkit.broadcastMessage("天边出现了一条轨迹，摇摆着坠了下来，没有人知道那是什么");
        World world = loc.getWorld();
        do {
            loc.add(0, -1, 0);
        }
        while (!loc.getBlock().getType().isSolid() && loc.getY() > 3);
        assert world != null;
        world.playSound(loc, ENTITY_LIGHTNING_BOLT_IMPACT, 100, 0);
        new BukkitRunnable() {
            final int ox = loc.getBlockX();
            final int oy = loc.getBlockY();
            final int oz = loc.getBlockZ();
            int y = oy - 40;
            public void run() {
                if (y > oy + 20) {
                    cancel();
                    spaceship2(loc);
                    return;
                }
                getLogger().info("移除第" + y + "层");
                for (int x = ox - 60; x < ox + 60; x++) {
                    for (int z = oz - 60; z < oz + 60; z++) {
                        int dx = ox - x;
                        int dy = oy - y;
                        int dz = oz - z;
                        int d = dx * dx + dy * dy + dz * dz;
                        if (d < 3600) {
                            Location BL = loc.clone();
                            BL.setX(x);
                            BL.setY(y);
                            BL.setZ(z);
                            Block B = BL.getBlock();
                            B.setType(Material.AIR);
                        }
                    }
                }
                y++;
            }
        }.runTaskTimer(this, 20L, 25L);
    }

    //生成外星飞船2
    public void spaceship2(final Location loc){
        do {
            loc.add(0, -1, 0);
        }
        while (!loc.getBlock().getType().isSolid() && loc.getY() > 3);
        new BukkitRunnable() {
            //生成次数
            int v = 0;
            //生成x
            //生成y
            int dx = 0;
            int dy = 0;

            public void run() {
                Location NewLocation;
                World world = loc.getWorld();
                v += 1;
                if (v > 200) {
                    cancel();
                    return;
                }
                dx = new Random().nextInt(v);
                dy = new Random().nextInt(v);
                if(dx %2 == 1){dx *= -1;}
                if(dy %2 == 0){dy *= -1;}
                NewLocation = loc.clone();
                NewLocation.add(dx,0,dy);
                if(new Random().nextInt(100) > 70) {
                    world.createExplosion(NewLocation, 4F);
                }
                do{
                    NewLocation.add(0,-1,0);
                }
                while(!NewLocation.getBlock().getType().isSolid() && NewLocation.getY()>3);
                assert world != null;
                //world.playSound(NewLocation,ENTITY_LIGHTNING_BOLT_IMPACT,100,0);
                int ox = NewLocation.getBlockX();
                int oy = NewLocation.getBlockY();
                int oz = NewLocation.getBlockZ();
                int xsize = new Random().nextInt(9);
                int ysize = new Random().nextInt(9);
                int zsize = new Random().nextInt(9);
                for(int x = 0;x < xsize;x++){
                    for(int z = 0;z < zsize;z++){
                        for(int y = 0;y < ysize;y++){
                            NewLocation.setX(ox + x);
                            NewLocation.setY(oy + y);
                            NewLocation.setZ(oz + z);
                            Block B = NewLocation.getBlock();
                            if(new Random().nextInt(100) > 40){
                                B.setType(Material.AIR);
                            }else {
                                int i = new Random().nextInt(100);
                                if (i < 50) {
                                    B.setType(Material.IRON_BLOCK);
                                }
                                if (i >= 50 && i < 60) {
                                    B.setType(Material.REDSTONE_BLOCK);
                                }
                                if (i >= 60 && i < 70) {
                                    B.setType(Material.END_STONE);
                                }
                                if (i >= 70 && i < 80) {
                                    B.setType(Material.IRON_BARS);
                                }
                                if (i >= 80 && i < 90) {
                                    B.setType(Material.GOLD_BLOCK);
                                }
                                if (i >= 90 && i < 95) {
                                    B.setType(Material.GLOWSTONE);
                                }
                                if (i >= 95 && i < 98) {
                                    B.setType(Material.DIAMOND_BLOCK);
                                }
                                if (i >= 98) {
                                    B.setType(Material.EMERALD_BLOCK);
                                }
                            }
                        }
                    }
                }

            }
        }.runTaskTimer(this, 0L, 5L);
        getLogger().info("生成飞船完毕：");


    }

    //生成流星雨
    public void meteor(final Location loc){
        Bukkit.broadcastMessage("一场流星雨悄然而至，将他的美好散布人间");
        new BukkitRunnable() {
            //生成次数
            int i = 100;
            int maxsize = 3;

            public void run() {
                if (i < 0) {
                    cancel();
                    return;
                }
                int rx = new Random().nextInt(1000000);
                int rz = new Random().nextInt(1000000);
                rx = rx % 2 == 1 ? rx : rx * -1;
                rz = rz % 2 == 0 ? rz : rz * -1;
                getLogger().info("流星雨" + rx + ":" + rz);
                loc.setX(rx);
                loc.setZ(rz);
                Location NewLocation = loc.clone();
                do{
                    NewLocation.add(0,-1,0);
                }
                while(!NewLocation.getBlock().getType().isSolid() && NewLocation.getY()>3);
                int sx = new Random().nextInt(maxsize) + 1;
                int sz = new Random().nextInt(maxsize) + 1;
                int sy = new Random().nextInt(maxsize) + 1;
                int ox = NewLocation.getBlockX();
                int oz = NewLocation.getBlockZ();
                int oy = NewLocation.getBlockY();
                for(int x = 0;x < sx;x++){
                    for(int z = 0;z < sz;z++){
                        for(int y = 0;y < sy;y++){
                            NewLocation.setX(ox + x);
                            NewLocation.setY(oy + y);
                            NewLocation.setZ(oz + z);
                            Block B = NewLocation.getBlock();
                            int i = new Random().nextInt(100);
                            if (i < 65) {
                                B.setType(Material.STONE);
                            }
                            if (i >= 65 && i < 95) {
                                B.setType(Material.IRON_BLOCK);
                            }
                            if (i >= 95 && i < 99) {
                                B.setType(Material.GOLD_BLOCK);
                            }
                            if (i >= 99) {
                                B.setType(Material.DIAMOND_BLOCK);
                            }
                        }
                    }
                }
                i --;
            }
        }.runTaskTimer(this, 0L, 20L);

    }

}
