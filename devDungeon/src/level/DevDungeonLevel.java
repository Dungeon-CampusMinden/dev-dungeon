package level;

import core.level.Tile;
import core.level.TileLevel;
import core.level.utils.Coordinate;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.Point;
import core.utils.components.path.IPath;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import level.utils.DungeonLoader;
import level.utils.MissingLevelException;

/**
 * Represents a level in the DevDungeon game. This class extends the {@link TileLevel} class and
 * adds functionality for handling custom points. These points are used to add spawn points, door
 * logic or any other custom logic to the level.
 */
public class DevDungeonLevel extends TileLevel {

  private final List<Coordinate> customPoints = new ArrayList<>();

  /**
   * Constructs a new DevDungeonLevel with the given layout, design label, and custom points.
   *
   * @param layout The layout of the level, represented as a 2D array of LevelElements.
   * @param designLabel The design label of the level.
   * @param customPoints A list of custom points to be added to the level.
   */
  public DevDungeonLevel(
      LevelElement[][] layout, DesignLabel designLabel, List<Coordinate> customPoints) {
    super(layout, designLabel);
    this.customPoints.addAll(customPoints);
  }

  /**
   * Load a DevDungeonLevel from a file path
   *
   * @param path The path to the file to load
   * @return The loaded DevDungeonLevel
   */
  public static DevDungeonLevel loadFromPath(IPath path) throws ReflectiveOperationException {
    // Load file from the path
    File file = new File(path.pathString());
    if (!file.exists()) {
      throw new MissingLevelException(path.toString());
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      // Parse DesignLabel
      String designLabelLine = readLine(reader);
      DesignLabel designLabel = parseDesignLabel(designLabelLine);

      // Parse Hero Position
      String heroPosLine = readLine(reader);
      Point heroPos = parseHeroPosition(heroPosLine);

      // Custom Points
      String customPointsLine = readLine(reader);
      List<Coordinate> customPoints = parseCustomPoints(customPointsLine);

      // Parse LAYOUT
      List<String> layoutLines = new ArrayList<>();
      String line;
      while (!(line = readLine(reader)).isEmpty()) {
        layoutLines.add(line);
      }
      LevelElement[][] layout = loadLevelLayoutFromString(layoutLines);

      DevDungeonLevel newLevel =
          getDevLevel(DungeonLoader.CURRENT_LEVEL, layout, designLabel, customPoints);

      // Set Hero Position
      Tile heroTile = newLevel.tileAt(heroPos);
      if (heroTile == null) {
        throw new RuntimeException("Invalid Hero Position: " + heroPos);
      }
      newLevel.startTile(heroTile);

      return newLevel;
    } catch (IOException e) {
      throw new RuntimeException("Error reading level file", e);
    }
  }

  /**
   * Read a line from the reader, ignoring comments. It skips lines that start with a '#' (comments)
   * and returns the next non-empty line.
   *
   * @param reader The reader to read from
   * @return The next non-empty, non-comment line without any comments
   * @throws IOException If an error occurs while reading from the reader
   */
  private static String readLine(BufferedReader reader) throws IOException {
    String line = reader.readLine();
    if (line == null) return "";
    while (line.trim().startsWith("#")) {
      line = reader.readLine();
    }
    line = line.trim().split("#")[0].trim();

    return line;
  }

  private static Point parseHeroPosition(String heroPositionLine) {
    if (heroPositionLine.isEmpty()) throw new RuntimeException("Missing Hero Position");
    String[] parts = heroPositionLine.split(",");
    if (parts.length != 2) throw new RuntimeException("Invalid Hero Position: " + heroPositionLine);
    try {
      float x = Float.parseFloat(parts[0]);
      float y = Float.parseFloat(parts[1]);
      return new Point(x, y);
    } catch (NumberFormatException e) {
      throw new RuntimeException("Invalid Hero Position: " + heroPositionLine);
    }
  }

  private static List<Coordinate> parseCustomPoints(String customPointsLine) {
    List<Coordinate> customPoints = new ArrayList<>();
    if (customPointsLine.isEmpty()) return customPoints;
    String[] points = customPointsLine.split(";");
    for (String point : points) {
      if (point.isEmpty()) continue;
      String[] parts = point.split(",");
      if (parts.length != 2) throw new RuntimeException("Invalid Custom Point: " + point);
      try {
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        customPoints.add(new Coordinate(x, y));
      } catch (NumberFormatException e) {
        throw new RuntimeException("Invalid Custom Point: " + point);
      }
    }
    return customPoints;
  }

  private static DesignLabel parseDesignLabel(String line) {
    if (line.isEmpty()) return DesignLabel.randomDesign();
    try {
      return DesignLabel.valueOf(line);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid DesignLabel: " + line);
    }
  }

  private static LevelElement[][] loadLevelLayoutFromString(List<String> lines) {
    LevelElement[][] layout = new LevelElement[lines.size()][lines.getFirst().length()];

    for (int y = 0; y < lines.size(); y++) {
      for (int x = 0; x < lines.getFirst().length(); x++) {
        char c = lines.get(y).charAt(x);
        switch (c) {
          case 'F':
            layout[y][x] = LevelElement.FLOOR;
            break;
          case 'W':
            layout[y][x] = LevelElement.WALL;
            break;
          case 'E':
            layout[y][x] = LevelElement.EXIT;
            break;
          case 'S':
            layout[y][x] = LevelElement.SKIP;
            break;
          case 'P':
            layout[y][x] = LevelElement.PIT;
            break;
          case 'H':
            layout[y][x] = LevelElement.HOLE;
            break;
          case 'D':
            layout[y][x] = LevelElement.DOOR;
            break;
          default:
            throw new IllegalArgumentException("Invalid character in level layout: " + c);
        }
      }
    }

    return layout;
  }

  private static DevDungeonLevel getDevLevel(
      int levelNumber,
      LevelElement[][] layout,
      DesignLabel designLabel,
      List<Coordinate> customPoints) {
    return switch (levelNumber) {
      case 0 -> new DevLevel00(layout, designLabel, customPoints);
      case 1 -> new DevLevel01(layout, designLabel, customPoints);
      default -> throw new IllegalArgumentException("Invalid level number: " + levelNumber);
    };
  }

  /**
   * Returns the list of custom points.
   *
   * @return A list of custom points.
   */
  public List<Coordinate> customPoints() {
    return customPoints;
  }

  /**
   * Adds a new custom point to the list.
   *
   * @param point The custom point to be added.
   */
  public void addCustomPoint(Coordinate point) {
    customPoints.add(point);
  }

  /**
   * Removes a custom point from the list.
   *
   * @param point The custom point to be removed.
   */
  public void removeCustomPoint(Coordinate point) {
    customPoints.remove(point);
  }

  /**
   * Checks if a custom point is in the list.
   *
   * @param point The custom point to be checked.
   * @return True if the custom point is in the list, false otherwise.
   */
  public boolean hasCustomPoint(Coordinate point) {
    return customPoints.contains(point);
  }
}
