/*
Copyright (C) 2023  YeetHosting

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
            //DO NOT REMOVE UNLESS YOU ARE AUTHORED BY THE AUTHOR.
            Call.infoMessage("Copyright (C) 2023 YeetHosting This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. You should have received a copy of the GNU Affero General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.")
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

