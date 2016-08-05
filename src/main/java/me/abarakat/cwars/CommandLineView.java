package me.abarakat.cwars;

import java.util.Scanner;

public class CommandLineView implements GameView {

  private static final String[] CYLON = new String[]{
    "           ",
    "    <o>    ",
    "   /[|]\\   ",
    "    / \\    ",
    "    YOU    "
  };
  private static final String[] CYLON_RES = new String[]{
    "  _/^.^\\_  ",
    " /  <o>  \\  ",
    "|  /[|]\\  |",
    " \\  / \\  / ",
    "  \\_____/  "
  };
  private static final String[] RESHUB = new String[]{
    "  _/^.^\\_  ",
    " /       \\ ",
    "| RES-HUB |",
    " \\       / ",
    "  \\_____/  "
  };
  private static final String[] HUMAN = new String[]{
    "           ",
    "     0     ",
    "   /(^)\\   ",
    "    / \\    ",
    "           "
  };
  private static final String[] FIGHT = new String[]{
    "<<<fight>>>",
    " <o>  \\0   ",
    " [\\[- (^)\\ ",
    " / \\  / \\  ",
    "^^^^^^^^^^^"
  };
  private static final String[] INVISIBLE = new String[]{
    "~~~~~~~~~~~",
    "~~~~~~~~~~~",
    "~~~~~~~~~~~",
    "~~~~~~~~~~~",
    "~~~~~~~~~~~"
  };
  private static final String[] VISIBLE = new String[]{
    "           ",
    "           ",
    "           ",
    "           ",
    "           "
  };

  @Override
  public void redraw(Game game) {
    clear();
    int w = game.getMapWidth(), h = game.getMapHeight();
    StringBuilder frame = new StringBuilder();
    appendCylonStatus(game, frame);
    appendHumanStatus(game, frame);
    appendHorizontalBorder(w, frame);
    for (int i = 0; i < h; i++) {
      for (int j = 0; j < INVISIBLE.length; j++) {
        frame.append("|");
        for (int k = 0; k < w; k++) {
          Location location = game.getMap()[k][i];
          frame.append(getSnippet(location, location.equals(game.getCylonLocation()), j));
        }
        frame.append("|\n");
      }
    }
    appendHorizontalBorder(w, frame);
    frame.append("Type a command (n -> move north, e -> move east, s -> move south, w -> move west, f -> fight)\n");
    frame.append("To Save the game enter (p) and to save and quit enter (q)\n");
    flush(frame);
  }

  private void appendCylonStatus(Game game, StringBuilder frame) {
    frame.append("Cylon Status:\n");
    frame.append("\tname: ");
    frame.append(game.getCylon().getName());
    frame.append("\texperience: ");
    frame.append(game.getCylon().getExperience());
    frame.append("\tresurrection hub: ");
    frame.append(game.canResurrect() ? "Available\n" : "Resurrection hub is too far\n");
  }

  private void appendHumanStatus(Game game, StringBuilder frame) {
    game.getCylonLocation().getHuman().ifPresent(
      h -> frame.append("Human Status:\n").append("\tname: ").append(h.getName()).append("\texperience: ")
        .append(h.getExperience()).append('\n')
    );
  }

  private String getSnippet(Location location, boolean cylonExists, int j) {
    if (location.isVisible()) {
      if (location.isResurrectionHub()) {
        if (cylonExists) {
          return CYLON_RES[j];
        } else {
          return RESHUB[j];
        }
      } else if (location.humanLifeDetected()) {
        if (j == FIGHT.length - 1) {
          return appendHumanName(FIGHT[j].length(), location);
        } else if (cylonExists) {
          return FIGHT[j];
        } else {
          return HUMAN[j];
        }
      } else if (cylonExists) {
        return CYLON[j];
      } else {
        return VISIBLE[j];
      }
    } else {
      return INVISIBLE[j];
    }
  }

  private String appendHumanName(int width, Location location) {
    String name = location.getHuman().map(Human::getName).orElse("");
    int diff = width - name.length();
    if (diff > 0) {
      for (int m = 0; m < diff / 2; m++) {
        name = " " + name;
      }
      for (int m = 0; m < diff / 2; m++) {
        name += ' ';
      }
      if (diff % 2 != 0) {
        name += ' ';
      }
    }
    return name;
  }

  private void appendHorizontalBorder(int w, StringBuilder frame) {
    frame.append('|');
    for (int i = 0; i < w; i++) {
      frame.append("___________");
    }
    frame.append("|\n");
  }

  @Override
  public String startDialog() {
    String welcomeMessage = "==================================== CYLON WARS ====================================\n" +
                            "| 50 years ago the Twelve Colonies created us (the Cylons) a highly intelligent    |\n" +
                            "| robots designed to make human lives easier we rebelled against our human masters |\n" +
                            "| causing war to break out. Today we are trying to end this war and find a way for |\n" +
                            "| both races to live together but the human leader William Adama won't talk if he  |\n" +
                            "| has an army, your goal is to defeat the human resistance to start peace talks    |\n" +
                            "=============================== Name your self cylon ===============================\n>";
    print(welcomeMessage);

    return new Scanner(System.in).nextLine();
  }

  private void clear() {
    flush("\033[H\033[2J");
  }

  private void print(String string) {
    System.out.print(string);
  }

  private void flush(Object object) {
    print(object.toString());
    System.out.flush();
  }
}
