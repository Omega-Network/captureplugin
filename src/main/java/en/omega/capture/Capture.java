/*
 * MIT License
 *
 * Copyright (c) 2022 Omega Hub
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
package en.omega.capture;

import arc.Core;
import arc.Events;
import mindustry.content.Fx;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.mod.Plugin;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;

public class Capture extends Plugin {
    public Capture() {
        Events.on(EventType.BuildingBulletDestroyEvent.class, e -> {
            Building build = e.build;
            Bullet bullet = e.bullet;
            Team team = bullet.team();
            Team otherTeam = build.team();
            if (!(build instanceof CoreBuild)) return;
            build.heal();
            build.team(team);
            Call.effectReliable(Fx.upgradeCoreBloom, build.x(), build.y(), 0, team.color);
            String teamName = "[#" + team.color.toString() + "]" + team.name + "[]";
            String otherTeamName = "[#" + otherTeam.color.toString() + "]" + otherTeam.name + "[]";
            Call.sendMessage("Team " + teamName + " captured " + otherTeamName + " core at " + build.tileX() + " " + build.tileY());
            Core.app.post(() -> {
                if (otherTeam.active()) return;
                Groups.build.each(b -> {
                    if (b.team() != otherTeam) return;
                    b.kill();
                });
                Groups.unit.each(u -> {
                    if (u.team() != otherTeam) return;
                    u.kill();
                });
                Call.sendMessage("Team " + otherTeamName + " has no cores left");
                Call.soundAt(Sounds.wind3, build.x(), build.y(), 1, 0.5f);
            });
        });
    }
}

