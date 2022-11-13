import arc.Events;
import arc.math.geom.Position;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Unit;
import mindustry.mod.Plugin;
import mindustry.world.Block;
import mindustry.world.Tile;

public class Capture extends Plugin {
  private int messageQueue = 0;
  
  private int messageLimit = 4;
  
  public Capture() {
    Events.on(EventType.BlockDestroyEvent.class, e -> {
          if (!(e.tile.build instanceof mindustry.world.blocks.storage.CoreBlock.CoreBuild) || Vars.state.gameOver)
            return; 
          Tile tile = e.tile;
          Block block = tile.block();
          Building build = tile.build;
          Team oldTeam = build.team();
          Unit capturer = Units.closestEnemy(oldTeam, tile.worldx(), tile.worldy(), Float.MAX_VALUE, ());
          if (oldTeam == Team.derelict)
            for (Teams.TeamData team : Vars.state.teams.active) {
              if (team.team == Team.derelict)
                continue; 
              Unit enemy = Units.closest(team.team, tile.worldx(), tile.worldy(), ());
              if (enemy != null && (capturer == null || tile.dst((Position)enemy) < tile.dst((Position)capturer)))
                capturer = enemy; 
            }  
          Team newTeam = (capturer != null) ? capturer.team : Team.derelict;
          Call.effectReliable(Fx.upgradeCore, tile.worldx(), tile.worldy(), block.size, newTeam.color);
          Call.infoPopup("Team [#" + newTeam.color.toString() + "]" + newTeam.name + " []captured team [#" + oldTeam.color.toString() + "]" + oldTeam.name + "[] core at " + tile.x + ", " + tile.y, 5.0F, 1, 0, 0, 50 * this.messageQueue - 50 * this.messageLimit, 0);
          this.messageQueue = (this.messageQueue + 1) % this.messageLimit;
          Timer.schedule((), 0.5F);
          Timer.schedule((), 5.0F);
          boolean teams = true;
          for (Teams.TeamData team : Vars.state.teams.active) {
            if (team.team != Team.malis && team.team != newTeam && (team.team != oldTeam || (oldTeam.data()).cores.size != 1) && team.hasCore()) {
              teams = false;
              break;
            } 
          } 
          if (teams)
            Events.fire(new EventType.GameOverEvent(newTeam)); 
        });
  }
}
