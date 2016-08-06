package me.abarakat.cwars;

import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cylon {

  private final Random random = new Random();

  private String name;
  private int experience = 100;
  private int x = 0;
  private int y = 0;


  public Cylon(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public int getExperience() {
    return experience;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public Cylon setLocation(int x, int y) {
    this.x = x;
    this.y = y;
    return this;
  }

  public boolean fight(Human human) {
    if (human.isKillable()) {
      if (experience - human.getExperience() + random.nextInt(200) - 100 > 0) {
        experience += human.getExperience();
        return true;
      } else {
        experience += human.getExperience() / 3;
      }
    }
    return false;
  }


  public boolean move(Action direction, int maxX, int maxY) {
    if (direction == Action.WEST && x > 0) {
      x--;
    } else if (direction == Action.EAST && x < maxX) {
      x++;
    } else if (direction == Action.NORTH && y > 0) {
      y--;
    } else if (direction == Action.SOUTH && y < maxY) {
      y++;
    } else {
      return false;
    }
    return true;
  }

  public String save() {
    return Stream.of(x, y, experience).map(Object::toString).collect(Collectors.joining(",", name + ",", ""));
  }

  @Override
  public String toString() {
    return "Cylon(" + save() + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Cylon cylon = (Cylon) o;
    return experience == cylon.experience && x == cylon.x && y == cylon.y && Objects.equals(name, cylon.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, experience, x, y);
  }

  public static Cylon load(String data) {

    String[] values = data.split(",");
    Cylon cylon = new Cylon(values[0]);
    cylon.x = Integer.parseInt(values[1]);
    cylon.y = Integer.parseInt(values[2]);
    cylon.experience = Integer.parseInt(values[3]);
    return cylon;
  }

}
