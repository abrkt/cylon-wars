package me.abarakat.cwars;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GameController {

  private static final Scanner in = new Scanner(System.in);
  private Game game;
  private GameView view;

  public GameController(GameView view) {
    this.view = view;
  }

  public boolean start(int width, int height) {
    if (getSavedGame().isPresent()) {
      game = getSavedGame().get();
    } else {
      game = new Game(width, height, new Cylon(view.startDialog()));
      game.deployCylon();
    }
    view.redraw(game);
    do {
      String input = in.nextLine();
      if (input.length() > 0) {
        Optional<Action> action = Action.fromChar(input.charAt(0));
        if (action.isPresent()) {
          game.turn(action.get());
          view.redraw(game);
        } else {
          if (input.startsWith("p") || input.startsWith("P")) {
            if (save()) {
              System.out.println("Game saved");
            } else {
              System.out.println("could not save the game");
            }
          } else if (input.startsWith("q") || input.startsWith("Q")) {
            return false;
          } else {
            System.out.println("Command is not available");
          }
        }
      }
    } while (game.getStatus() == Game.Status.PLAYING);
    return true;
  }

  private boolean save() {

    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("saved-game.txt"))) {
      writer.write(game.save());
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  private Optional<Game> getSavedGame() {
    Path path = Paths.get("saved-game.txt");
    if (Files.exists(path)) {
      try {
        return Optional.of(Game.load(Files.lines(path).collect(Collectors.joining("\n"))));
      } catch (IOException e) {
        return Optional.empty();
      }
    }
    return Optional.empty();
  }

  public static void main(String... args) {
    GameController controller = new GameController(new CommandLineView());
    while (controller.start(7, 7)) {
      System.out.println("Start new Game? (y, n)");
      String input = in.nextLine();
      if (!input.startsWith("y") && !input.startsWith("Y")) {
        break;
      }
    }
    System.out.println("Bye ...");
  }
}
