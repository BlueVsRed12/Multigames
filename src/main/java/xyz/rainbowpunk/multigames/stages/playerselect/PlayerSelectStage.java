package xyz.rainbowpunk.multigames.stages.playerselect;

import xyz.rainbowpunk.multigames.Multigames;
import xyz.rainbowpunk.multigames.utilities.MultiColor;

import java.util.HashMap;
import java.util.Map;


public class PlayerSelectStage {
     private final Multigames plugin;
     private PlayerSelectListener listener;
     private Map<MultiColor, ColorPlatform> platformMap;

     public PlayerSelectStage(Multigames plugin) {
          this.plugin = plugin;
     }

     public void start() {
          initializePlatforms();
          listener = new PlayerSelectListener(this);
          plugin.registerListener(listener);
     }

     private void initializePlatforms() {
          platformMap = new HashMap<>();
          int[] seeds = {
                  507, 1323,
                  513, 1316,
                  520, 1315,
                  527, 1315,
                  533, 1319,
                  538, 1323,
                  542, 1330,
                  542, 1337,
                  538, 1341,
                  531, 1345,
                  526, 1348,
                  521, 1348,
                  516, 1349,
                  510, 1342,
                  509, 1335,
                  509, 1331
          };
          for (int i = 0; i+1 < seeds.length; i += 2) {
               ColorPlatform platform = new ColorPlatform(plugin.getMainWorld(), seeds[i], seeds[i+1]);
               platformMap.put(platform.getColor(), platform);
          }
     }

     public void stepOnto(MultiColor color) {
          platformMap.get(color).turnOn();
     }

     public void stepOff(MultiColor color) {
          platformMap.get(color).turnOff();
     }

     public void end() {
          for (ColorPlatform platform : platformMap.values()) platform.turnOff();
     }

     public void cleanUp() {
          plugin.unregisterListener(listener);
     }
}
