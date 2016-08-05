package me.abarakat.cwars;


import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Game {

  public enum Status {PLAYING, LOST, WON}

  private final Random random = new Random();
  private final int mapWidth;
  private final int mapHeight;
  private final int resurrectionRadius;
  private final Cylon cylon;
  private Location[][] map;
  private Location resurrectionHub;

  private Status status = Status.PLAYING;

  public Game(int mapWidth, int mapHeight, Cylon cylon) {
    this(mapWidth, mapHeight, cylon, true);
  }

  protected Game(int mapWidth, int mapHeight, Cylon cylon, boolean addHumans) {
    this.mapWidth = mapWidth;
    this.mapHeight = mapHeight;
    this.cylon = cylon;
    this.resurrectionRadius = Math.max(mapWidth / 2, mapHeight / 2);
    initMap(addHumans);
  }

  private void initMap(boolean addHumans) {
    map = new Location[mapWidth][mapHeight];
    for (int i = 0; i < mapWidth; i++) {
      for (int j = 0; j < mapHeight; j++) {
        map[i][j] = new Location(i, j);
      }
    }
    if (addHumans) {
      Arrays.stream(Human.values()).forEach(h -> map[random.nextInt(mapWidth)][random.nextInt(mapHeight)].setHuman(h));
    }
    resurrectionHub = map[random.nextInt(mapWidth)][random.nextInt(mapHeight)].endHumanLife().setupResurrectionHub();
  }

  public Location deployCylon() {
    for (int i = 0; i < mapWidth; i++) {
      for (int j = 0; j < i && j < mapHeight; j++) {
        Location location = map[i][j];
        if (location.humanLifeDetected() || location.isResurrectionHub()) {
          continue;
        }
        return deployCylon(i, j);
      }
    }
    throw new IllegalStateException("Game map is full of humans");
  }

  protected Location deployCylon(int x, int y) {
    cylon.setLocation(x, y);
    return map[x][y].endHumanLife().explore();
  }

  public TreeSet<Action> getAvailableActions() {
    TreeSet<Action> actions = new TreeSet<>();
    if (cylon.getX() > 0) {
      actions.add(Action.WEST);
    }
    if (cylon.getX() < mapWidth - 1) {
      actions.add(Action.EAST);
    }
    if (cylon.getY() > 0) {
      actions.add(Action.NORTH);
    }
    if (cylon.getY() < mapHeight - 1) {
      actions.add(Action.SOUTH);
    }
    if (map[cylon.getX()][cylon.getY()].humanLifeDetected()) {
      actions.add(Action.FIGHT);
    }
    return actions;
  }

  public boolean turn(Action action) {
    if (getAvailableActions().contains(action)) {
      if (action == Action.FIGHT) {
        getCylonLocation().getHuman().ifPresent(this::fight);
      } else {
        cylon.move(action, mapWidth - 1, mapHeight - 1);
        getCylonLocation().explore();
      }
      return true;
    }
    return false;
  }

  private void fight(Human human) {
    boolean survived = cylon.fight(human);
    if (survived) {
      getCylonLocation().endHumanLife();
      long remainingKillableHumans = Arrays.stream(map).flatMap(Arrays::stream)
        .filter(l -> l.getHuman().isPresent() && l.getHuman().get().isKillable())
        .count();
      if (remainingKillableHumans == 0) {
        status = Status.WON;
      }
    } else {
      if (getCylonLocation().resurrectionHubNearby(resurrectionHub, resurrectionRadius)) {
        cylon.setLocation(resurrectionHub.getX(), resurrectionHub.getY());
      } else {
        status = Status.LOST;
      }
    }
  }

  protected Location changeResurrectionHubLocation(int x, int y) {
    resurrectionHub.destroyResurrectionHub();
    resurrectionHub = map[x][y].endHumanLife().setupResurrectionHub();
    return resurrectionHub;
  }

  public Location[][] getMap() {
    return map;
  }

  public Location getCylonLocation() {
    return map[cylon.getX()][cylon.getY()];
  }

  public boolean canResurrect() {
    return getCylonLocation().resurrectionHubNearby(resurrectionHub, resurrectionRadius);
  }

  public Cylon getCylon() {
    return cylon;
  }

  public int getMapWidth() {
    return mapWidth;
  }

  public int getMapHeight() {
    return mapHeight;
  }

  public Status getStatus() {
    return status;
  }

  public String save() {
    return Arrays.stream(map).flatMap(Arrays::stream).map(Location::save)
      .collect(Collectors.joining("\n", mapWidth + "," + mapHeight + "\n", "\n" + cylon.save()));
  }

  public static Game load(String saved) {
    String[] lines = saved.split("\n");
    String[] xy = lines[0].split(",");
    Game game = new Game(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]), Cylon.load(lines[lines.length - 1]), false);
    for (int i = 1; i < lines.length - 1; i++) {
      Location location = Location.load(lines[i]);
      game.map[(i - 1) / game.mapHeight][(i - 1) % game.mapHeight] = location;
      if (location.isResurrectionHub()) {
        game.resurrectionHub = location;
      }
    }
    return game;
  }

  @Override
  public String toString() {

    return "Game(\n" + Arrays.stream(map).flatMap(Arrays::stream).map(Location::save)
      .collect(
        Collectors.joining(",\n\t\t", "\t(" + mapWidth + "," + mapHeight + ")[\n\t\t", "\n\t]\n\t," + cylon.save()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Game game = (Game) o;
    boolean mapEquals = true;
    for (int i = 0; i < mapWidth; i++) {
      mapEquals &= Arrays.equals(map[i], game.map[i]);
    }
    return mapWidth == game.mapWidth && mapHeight == game.mapHeight && mapEquals && Objects.equals(cylon, game.cylon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mapWidth, mapHeight, map, cylon);
  }

}
