package me.abarakat.cwars;

import java.util.Objects;
import java.util.Optional;

public class Location {

  private final int x;
  private final int y;
  private boolean visible;
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

  public boolean isVisible() {
    return visible;
  }

  public Location explore() {
    this.visible = true;
    return this;
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
    return String.format("%d,%d,%b,%b,%s", x, y, visible, resurrectionHub, human.map(Human::name).orElse("NONE"));
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

  public static Location load(String saved) {
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
    return location;
  }

}
