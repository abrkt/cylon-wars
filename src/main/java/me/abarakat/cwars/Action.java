package me.abarakat.cwars;

public enum Action {
  EAST, WEST, NORTH, SOUTH, FIGHT;

  public static Action fromChar(char abbreviation) {
    switch (abbreviation) {
      case 'E':
      case 'e':
        return EAST;
      case 'W':
      case 'w':
        return WEST;
      case 'N':
      case 'n':
        return NORTH;
      case 'S':
      case 's':
        return SOUTH;
      default:
        throw new IllegalArgumentException("abbreviation must be e, w, n or s");
    }
  }
}
