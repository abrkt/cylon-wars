package me.abarakat.cwars;

import java.util.Objects;
import java.util.Optional;

public class Location {

  private final int x;
  private final int y;
  private Optional<Human> human;
  private boolean resurrectionHub;


  public Location(int x, int y) {
    this.x = x;
    this.y = y;
    this.human = Optional.empty();
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public Optional<Human> getHuman() {
    return human;
  }

  public Location setHuman(Human human) {
    this.human = Optional.of(human);
    return this;
  }

  public boolean isResurrectionHub() {
    return resurrectionHub;
  }

  public Location setupResurrectionHub() {
    this.resurrectionHub = true;
    return this;
  }

  protected Location destroyResurrectionHub() {
    this.resurrectionHub = false;
    return this;
  }

  public boolean humanLifeDetected() {
    return human.isPresent();
  }

  public Location endHumanLife() {
    this.human = Optional.empty();
    return this;
  }

  public boolean resurrectionHubNearby(Location hub, int radius) {
    int minX = hub.getX() - radius, maxX = hub.getX() + radius, minY = hub.getY() - radius, maxY = hub.getY() + radius;
    return x >= minX && x <= maxX && y >= minY && y <= maxY;
  }

  public String save() {
    return toString();
  }


  @Override
  public String toString() {
    return String.format("%d,%d,%b,%s", x, y, resurrectionHub, human.map(Human::name).orElse("NONE"));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Location location = (Location) o;
    return x == location.x &&
           y == location.y &&
           resurrectionHub == location.resurrectionHub &&
           Objects.equals(human, location.human);
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, human, resurrectionHub);
  }

  public static Location load(String saved) {
    String[] values = saved.split(",");
    Location location = new Location(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
    if (Boolean.parseBoolean(values[2])) {
      location.setupResurrectionHub();
    }
    if (!values[3].equals("NONE")) {
      location.setHuman(Human.valueOf(values[3]));
    }
    return location;
  }
}
