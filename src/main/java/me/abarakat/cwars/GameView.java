package me.abarakat.cwars;

import me.abarakat.cwars.model.Action;
import me.abarakat.cwars.model.Game;

public interface GameView {

  void redraw(Game game);

  String startDialog();

  void message(String message);

  void endDialog(Game.Status status);

  boolean prompt(String question);

  Action nextTurn();
}
