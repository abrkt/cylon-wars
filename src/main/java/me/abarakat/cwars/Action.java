package me.abarakat.cwars;

import java.util.Optional;

public enum Action {
  EAST, WEST, NORTH, SOUTH, FIGHT;

  public static Optional<Action> fromChar(char abbreviation) {
    switch (abbreviation) {
      case 'E':
      case 'e':
        return Optional.of(EAST);
      case 'W':
      case 'w':
        return Optional.of(WEST);
      case 'N':
      case 'n':
        return Optional.of(NORTH);
      case 'S':
      case 's':
        return Optional.of(SOUTH);
      case 'F':
      case 'f':
        return Optional.of(FIGHT);
      default:
        return Optional.empty();
    }
  }
}
