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

