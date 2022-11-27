package en.omega.capture;

import arc.Core;
import arc.Events;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
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
            Core.app.post(() -> {
                if (otherTeam.active()) return;
                Groups.build.each(b -> {
                    if (b.team() != otherTeam) return;
                    b.team(team);
                });
                Groups.unit.each(u -> {
                    if (u.team() != otherTeam) return;
                    u.team(team);
                });
            });
        });
    }
}

