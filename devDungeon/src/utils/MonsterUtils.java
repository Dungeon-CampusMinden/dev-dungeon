package utils;

import core.Entity;
import core.Game;
import core.components.PositionComponent;
import core.level.Tile;
import core.level.utils.Coordinate;
import core.utils.Point;
import core.utils.components.MissingComponentException;
import entities.MonsterType;
import java.io.IOException;
import java.util.logging.Logger;

public class MonsterUtils {

  private static final Logger LOGGER = Logger.getLogger(MonsterUtils.class.getName());

  /**
   * Spawns a monster of the given type at the given position and adds it to the game. The Position
   * is cast to a Tile and the monster is spawned at the center of the tile.
   *
   * @param monsterType the type of monster to spawn
   * @param position the position to spawn the monster; the tile at the given point must be
   *     accessible else the monster will not be spawned
   * @throws MissingComponentException if the monster does not have a PositionComponent
   * @throws RuntimeException if an error occurs while spawning the monster
   * @see Game#add(Entity)
   * @see MonsterType
   */
  public static void spawnMonster(MonsterType monsterType, Point position) {
    Tile tile = Game.tileAT(position);
    if (tile == null || !tile.isAccessible()) {
      LOGGER.warning(
          "Cannot spawn monster at "
              + position
              + " because the tile is not accessible or does not exist");
      return;
    }
    spawnMonster(monsterType, tile.coordinate());
  }

  /**
   * Spawns a monster of the given type at the given coordinate and adds it to the game.
   *
   * @param monsterType the type of monster to spawn
   * @param coordinate the coordinate to spawn the monster; the tile at the given coordinate must be
   *     accessible else the monster will not be spawned
   * @throws MissingComponentException if the monster does not have a PositionComponent
   * @throws RuntimeException if an error occurs while spawning the monster
   * @see Game#add(Entity)
   * @see MonsterType
   */
  public static void spawnMonster(MonsterType monsterType, Coordinate coordinate) {
    Tile tile = Game.tileAT(coordinate);
    if (tile == null || !tile.isAccessible()) {
      LOGGER.warning(
          "Cannot spawn monster at "
              + coordinate
              + " because the tile is not accessible or does not exist");
      return;
    }
    try {
      Entity newMob = monsterType.buildMonster();
      PositionComponent positionComponent =
          newMob
              .fetch(PositionComponent.class)
              .orElseThrow(() -> MissingComponentException.build(newMob, PositionComponent.class));
      positionComponent.position(tile.position());
      Game.add(newMob);
    } catch (IOException e) {
      throw new RuntimeException("Error spawning monster", e);
    }
  }
}
