package me.abarakat.cwars;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cylon {

  private final Random random = new Random();

  private String name;
  private int experience;
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

  public boolean fight(Human human) {
    if (human.isKillable()) {
      int result = human.getExperince() + random.nextInt(200) - 100 - experience;
      if (result > 0) {
        experience += human.getExperince();
        return true;
      } else {
        experience += human.getExperince() / 3;
      }
    }
    return false;
  }


  public boolean move(Direction direction, int maxX, int maxY) {
    switch (direction) {
      case WEST:
        if (x > 0) {
          x--;
          return true;
        }
        break;
      case EAST:
        if (x < maxX) {
          x++;
          return true;
        }
        break;
      case NORTH:
        if (y > 0) {
          y--;
          return true;
        }
        break;
      case SOUTH:
        if (y < maxY) {
          y++;
          return true;
        }
        break;
    }
    return false;
  }

  public void speak(String message) {
  }

  @Override
  public String toString() {
    return Stream.of(x, y, experience).map(Object::toString).collect(Collectors.joining(",", name + "<", ">"));
  }

  public static Cylon load(String data) {

    int nameEnd = data.lastIndexOf("<");
    Cylon cylon = new Cylon(data.substring(0, nameEnd));
    String[] values = data.substring(nameEnd + 1, data.length() - 1).split(",");
    cylon.x = Integer.parseInt(values[0]);
    cylon.y = Integer.parseInt(values[1]);
    cylon.experience = Integer.parseInt(values[2]);
    return cylon;
  }
}
