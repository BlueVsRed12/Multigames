package xyz.rainbowpunk.multigames.stages.playerselect;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import xyz.rainbowpunk.multigames.Multigames;

//          int[] seeds = {
//                  507, 1323,
//                  513, 1316,
//                  520, 1315,
//                  527, 1315,
//                  533, 1319,
//                  538, 1323,
//                  542, 1330,
//                  542, 1337,
//                  538, 1341,
//                  531, 1345,
//                  526, 1348,
//                  521, 1348,
//                  516, 1349,
//                  510, 1342,
//                  509, 1335,
//                  509, 1331
//          };

public class PlayerSelectStage {
     private final Multigames plugin;

     private ColorPlatform blackColorPlatform;

     public PlayerSelectStage(Multigames plugin) {
          this.plugin = plugin;

          blackColorPlatform = new ColorPlatform(plugin.getMainWorld(), 530, 1322);
     }

     public void start() {
          blackColorPlatform.turnOn();
     }

     public void end() {
          blackColorPlatform.turnOff();
     }
}
