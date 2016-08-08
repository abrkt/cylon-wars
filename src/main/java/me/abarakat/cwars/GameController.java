package me.abarakat.cwars;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

import me.abarakat.cwars.model.Action;
import me.abarakat.cwars.model.Cylon;
import me.abarakat.cwars.model.Game;

public class GameController {

  private final GameView view;
  private boolean savedGameLoaded = false;

  public GameController(GameView view) {
    this.view = view;
  }

  public void loop() {
    do {
      start(7, 7);
    } while (view.prompt("Start new Game? (y, n)"));
    view.message("Bye ...");
  }

  public boolean start(int width, int height) {
    Game game = createOrLoadGame(width, height);
    view.redraw(game);
    boolean quit = false;
    do {
      Action action = view.nextTurn();
      switch (action) {
        case QUIT:
          quit = true;
        case SAVE:
          save(game);
          break;
        default:
          if (game.turn(action)) {
            view.redraw(game);
          }
      }

    } while (game.getStatus() == Game.Status.PLAYING && !quit);
    view.endDialog(game.getStatus());
    return !quit;
  }

  private Game createOrLoadGame(int width, int height) {
    if (!savedGameLoaded) {
      savedGameLoaded = true;
      Optional<Game> savedGame = load();
      if (savedGame.isPresent()) {
        return savedGame.get();
      }
    }
    Game game = new Game(width, height, new Cylon(view.startDialog()));
    game.deployCylon();
    return game;
  }

  public void save(Game game) {
    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("saved-game.txt"))) {
      writer.write(game.save());
      view.message("Game saved");
    } catch (IOException e) {
      view.message("could not save the game");
    }
  }

  public Optional<Game> load() {
    Path path = Paths.get("saved-game.txt");
    if (Files.exists(path)) {
      try {
        return Game.load(Files.lines(path).collect(Collectors.joining("\n")));
      } catch (IOException e) {
        return Optional.empty();
      }
    }
    return Optional.empty();
  }

  public static void main(String... args) {
    new GameController(new CommandLineView(System.in, System.out)).loop();
  }

}
