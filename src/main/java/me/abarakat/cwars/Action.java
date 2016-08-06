package me.abarakat.cwars;

import java.util.Optional;

public enum Action {
  EAST, WEST, NORTH, SOUTH, FIGHT, SAVE, QUIT;

  public static Optional<Action> fromChar(char abbreviation) {
    switch (abbreviation) {
      case 'e':
        return Optional.of(EAST);
      case 'w':
        return Optional.of(WEST);
      case 'n':
        return Optional.of(NORTH);
      case 's':
        return Optional.of(SOUTH);
      case 'f':
        return Optional.of(FIGHT);
      case 'p':
        return Optional.of(SAVE);
      case 'q':
        return Optional.of(QUIT);
      default:
        return Optional.empty();
    }
  }
}
