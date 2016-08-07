package me.abarakat.cwars.model;

import java.util.Objects;
import java.util.Optional;

public class Location {

  private final int x;
  private final int y;
  private boolean visible;
  private Human human;
  private boolean resurrectionHub;

  public Location(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public boolean isVisible() {
    return visible;
  }

  public Location explore() {
    this.visible = true;
    return this;
  }

  public Optional<Human> getHuman() {
    return Optional.ofNullable(human);
  }

  public Location setHuman(Human human) {
    this.human = human;
    return this;
  }

  public boolean isResurrectionHub() {
    return resurrectionHub;
  }

  public Location setupResurrectionHub() {
    this.resurrectionHub = true;
    endHumanLife();
    explore();
    return this;
  }

  protected Location destroyResurrectionHub() {
    this.resurrectionHub = false;
    this.visible = false;
    return this;
  }

  public boolean humanLifeDetected() {
    return human != null;
  }

  public Location endHumanLife() {
    this.human = null;
    return this;
  }

  public boolean isNearby(Location hub, int radius) {
    int minX = hub.getX() - radius, maxX = hub.getX() + radius, minY = hub.getY() - radius, maxY = hub.getY() + radius;
    return x >= minX && x <= maxX && y >= minY && y <= maxY;
  }

  public String save() {
    return String.format("%d,%d,%b,%b,%s", x, y, visible, resurrectionHub, getHuman().map(Human::name).orElse("NONE"));
  }

  @Override
  public String toString() {
    return "Location(" + save() + ")";
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
    return x == location.x && y == location.y && visible == location.visible &&
           resurrectionHub == location.resurrectionHub && Objects.equals(human, location.human);
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, visible, human, resurrectionHub);
  }

  public static Optional<Location> load(String saved) {
    try {
      String[] values = saved.split(",");
      Location location = new Location(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
      if (Boolean.parseBoolean(values[2])) {
        location.explore();
      }
      if (Boolean.parseBoolean(values[3])) {
        location.setupResurrectionHub();
      }
      if (!values[4].equals("NONE")) {
        location.setHuman(Human.valueOf(values[4]));
      }
      return Optional.of(location);
    } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignore) {
      return Optional.empty();
    }
  }

}
