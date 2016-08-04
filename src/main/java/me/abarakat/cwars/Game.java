package me.abarakat.cwars;


import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Game {

  public enum Status {PLAYING, LOST, WON}

  private final Random random = new Random();
  private final int x;
  private final int y;
  private final int resurrectionRadius;
  private Location[][] map;
  private Location resurrectionHub;
  private final Cylon cylon;
  private Status status = Status.PLAYING;

  public Game(int x, int y, Cylon cylon) {
    this(x, y, cylon, true);
  }

  protected Game(int x, int y, Cylon cylon, boolean addHumans) {
    this.x = x;
    this.y = y;
    this.cylon = cylon;
    this.resurrectionRadius = Math.max(x / 2, y / 2);
    initMap(addHumans);
  }

  private void initMap(boolean addHumans) {
    map = new Location[x][y];
    for (int i = 0; i < x; i++) {
      for (int j = 0; j < y; j++) {
        map[i][j] = new Location(i, j);
      }
    }
    if (addHumans) {
      Arrays.stream(Human.values()).forEach(h -> map[random.nextInt(x)][random.nextInt(y)].setHuman(h));
    }
    resurrectionHub = map[random.nextInt(x)][random.nextInt(y)].endHumanLife().setupResurrectionHub();
  }

  public Location deployCylon() {
    for (int i = 0; i < x; i++) {
      for (int j = 0; j < i && j < y; j++) {
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
    Location location = map[x][y];
    location.endHumanLife();
    cylon.setLocation(x, y);
    return location;
  }

  public TreeSet<Action> getAvailableActions() {
    TreeSet<Action> actions = new TreeSet<>();
    if (cylon.getX() > 0) {
      actions.add(Action.WEST);
    }
    if (cylon.getX() < x - 1) {
      actions.add(Action.EAST);
    }
    if (cylon.getY() > 0) {
      actions.add(Action.NORTH);
    }
    if (cylon.getY() < y - 1) {
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
        cylonLocation().getHuman().ifPresent(this::fight);
      } else {
        cylon.move(action, x - 1, y - 1);
      }
      return true;
    }
    return false;
  }

  private void fight(Human human) {
    boolean survived = cylon.fight(human);
    if (survived) {
      cylonLocation().endHumanLife();
      long remainingKillableHumans = Arrays.stream(map).flatMap(Arrays::stream)
        .filter(l -> l.getHuman().isPresent() && l.getHuman().get().isKillable())
        .count();
      if (remainingKillableHumans == 0) {
        status = Status.WON;
      }
    } else {
      if (cylonLocation().resurrectionHubNearby(resurrectionHub, resurrectionRadius)) {
        cylon.setLocation(resurrectionHub.getX(), resurrectionHub.getY());
      } else {
        status = Status.LOST;
      }
    }
  }

  private Location cylonLocation() {
    return map[cylon.getX()][cylon.getY()];
  }

  protected Location changeResurrectionHubLocation(int x, int y) {
    resurrectionHub.destroyResurrectionHub();
    resurrectionHub = map[x][y].endHumanLife().setupResurrectionHub();
    return resurrectionHub;
  }

  public Location[][] getMap() {
    return map;
  }

  public Status getStatus() {
    return status;
  }

  public String save() {
    return toString();
  }

  public static Game load(String saved) {
    String[] lines = saved.split("\n");
    String[] xy = lines[0].split(",");
    Game game = new Game(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]), Cylon.load(lines[lines.length - 1]), false);
    for (int i = 1; i < lines.length - 1; i++) {
      game.map[(i - 1) / game.y][(i - 1) % game.y] = Location.load(lines[i]);
    }
    return game;
  }

  @Override
  public String toString() {
    return Arrays.stream(map).flatMap(Arrays::stream).map(Location::save)
      .collect(Collectors.joining("\n", x + "," + y + "\n", "\n" + cylon.save()));
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
    for (int i = 0; i < x; i++) {
      mapEquals &= Arrays.equals(map[i], game.map[i]);
    }
    return x == game.x && y == game.y && mapEquals && Objects.equals(cylon, game.cylon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, map, cylon);
  }

  public static void main(String... args) {
  }

}
