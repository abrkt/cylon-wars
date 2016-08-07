package me.abarakat.cwars.model;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.testng.Assert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertTrue;


public class GameTest {

  @Test
  public void gameMapShouldContainASingleResurectionHub() {
    Location[][] map = new Game(10, 10, new Cylon("Kara")).getMap();
    long hubCount = Arrays.stream(map).flatMap(Arrays::stream).filter(Location::isResurrectionHub).count();
    assertEquals(hubCount, 1);
  }

  @Test
  public void gameMapShouldContainHumans() {
    Location[][] map = new Game(10, 10, new Cylon("Kara")).getMap();
    assertTrue(Arrays.stream(map).flatMap(Arrays::stream).filter(Location::humanLifeDetected).findAny().isPresent());
  }

  @Test
  public void gameShouldDeployCylonInAnEmptyLocation() {
    Cylon sharonMocked = mock(Cylon.class);
    Location location = new Game(10, 10, sharonMocked).deployCylon();
    assertFalse(location.humanLifeDetected());
    assertFalse(location.isResurrectionHub());
    verify(sharonMocked).setLocation(location.getX(), location.getY());
  }

  @Test
  public void gameShouldNotDeployCylonInAMapFullOfHumans() {
    Game game = new Game(3, 3, new Cylon("Sharon"));
    Arrays.stream(game.getMap()).flatMap(Arrays::stream)
      .filter(l -> !l.humanLifeDetected())
      .forEach(l -> l.setHuman(Human.WADAMA));
    assertThrows(IllegalStateException.class, game::deployCylon);
  }

  @Test
  public void resurrectionHubShouldAlwaysBeVisible() {
    Game game = new Game(5, 5, new Cylon("Sharon"));
    assertTrue(game.getResurrectionHub().isVisible());
  }

  @Test
  public void changingResurrectionHubLocationShouldHideThePreviousLocation() {
    Game game = new Game(5, 5, new Cylon("Sharon"));
    Location previous = game.getResurrectionHub();
    Location current = game.changeResurrectionHubLocation((previous.getX() + 1) % 5, (previous.getY() + 1) % 5);
    assertTrue(current.isResurrectionHub());
    assertTrue(current.isVisible());
    assertFalse(previous.isVisible());
  }

  @Test
  public void allLocationVisitedByTheCylonShouldBeVisible() {
    Game game = new Game(5, 5, new Cylon("Sharon"));
    game.deployCylon(0, 0);
    assertTrue(game.getLocation(0, 0).isVisible());
    game.turn(Action.SOUTH);
    assertTrue(game.getLocation(0, 1).isVisible());
    game.turn(Action.EAST);
    assertTrue(game.getLocation(1, 1).isVisible());
    game.turn(Action.NORTH);
    assertTrue(game.getLocation(1, 0).isVisible());
  }

  @Test
  public void gameShouldPerformOnlyAvailableActions() {
    Cylon cylon = mock(Cylon.class);
    Game game = new Game(3, 3, cylon);
    game.getLocation(0, 0).endHumanLife();
    game.deployCylon(0, 0);
    assertTrue(game.turn(Action.EAST));
    verify(cylon).move(Action.EAST, 2, 2);
    assertFalse(game.turn(Action.NORTH));
    game.getLocation(0, 0).setHuman(Human.LADAMA);
    assertTrue(game.turn(Action.FIGHT));
    verify(cylon).fight(Human.LADAMA);
  }

  @Test
  public void humanLifeShouldBeEndedAfterCylonDefeatsHuman() {
    Cylon cylon = mock(Cylon.class);
    Game game = new Game(3, 3, cylon);
    when(cylon.fight(any())).thenReturn(true);
    Location location = game.deployCylon(0, 0);
    location.setHuman(Human.LADAMA);
    assertTrue(game.turn(Action.FIGHT));
    assertFalse(location.humanLifeDetected());
  }

  @Test
  public void cylonShouldBeResurrectedIfItDiedNearResurrectionHub() {
    Cylon cylon = mock(Cylon.class);
    Game game = new Game(5, 5, cylon);
    when(cylon.fight(any())).thenReturn(false);
    Location hubLocation = game.changeResurrectionHubLocation(1, 2);
    Location location = game.deployCylon(0, 0);
    location.setHuman(Human.LADAMA);
    assertTrue(game.turn(Action.FIGHT));
    assertTrue(location.humanLifeDetected());
    verify(cylon).setLocation(hubLocation.getX(), hubLocation.getY());
  }

  @Test
  public void gameShouldBeLostIfCylonDiedAwayFromResurrectionHub() {
    Cylon cylon = mock(Cylon.class);
    Game game = new Game(5, 5, cylon);
    when(cylon.fight(any())).thenReturn(false);
    game.changeResurrectionHubLocation(4, 4);
    Location location = game.deployCylon(0, 0);
    location.setHuman(Human.LADAMA);
    assertTrue(game.turn(Action.FIGHT));
    assertTrue(location.humanLifeDetected());
    assertEquals(game.getStatus(), Game.Status.LOST);
  }

  @Test
  public void gameShouldBeWonIfAllKillableHumansAreDead() {
    Cylon cylon = mock(Cylon.class);
    Game game = new Game(2, 2, cylon, false);
    game.getLocation(1, 1).setHuman(Human.WADAMA);
    when(cylon.fight(any())).thenReturn(true);
    Location location = game.deployCylon(0, 0);
    location.setHuman(Human.LADAMA);
    assertTrue(game.turn(Action.FIGHT));
    assertFalse(location.humanLifeDetected());
    assertEquals(game.getStatus(), Game.Status.WON);
  }

  @Test
  public void gameShouldSaveItSelfToStringRepresentation() {

    Game game = new Game(3, 3, new Cylon("Kara"), false);
    game.changeResurrectionHubLocation(1, 1);
    game.getLocation(0, 2).setHuman(Human.WADAMA);
    game.getLocation(1, 2).setHuman(Human.ROSLIN);
    game.getLocation(2, 2).setHuman(Human.BALTAR);
    game.getLocation(1, 0).setHuman(Human.AGATHON);
    game.deployCylon(0, 0);
    assertEquals(
      game.save(),
      "3,3\n" +
      "0,0,true,false,NONE\n" +
      "0,1,false,false,NONE\n" +
      "0,2,false,false,WADAMA\n" +
      "1,0,false,false,AGATHON\n" +
      "1,1,true,true,NONE\n" +
      "1,2,false,false,ROSLIN\n" +
      "2,0,false,false,NONE\n" +
      "2,1,false,false,NONE\n" +
      "2,2,false,false,BALTAR" +
      "\nKara,0,0,100"
    );
  }

  @Test
  public void gameShouldBeLoadedFromValidRepresentation() {

    Game expected = new Game(3, 3, new Cylon("Kara"), false);
    expected.changeResurrectionHubLocation(1, 1);
    expected.getLocation(0, 2).setHuman(Human.WADAMA);
    expected.getLocation(1, 2).setHuman(Human.ROSLIN);
    expected.getLocation(2, 2).setHuman(Human.BALTAR);
    expected.getLocation(1, 0).setHuman(Human.AGATHON);
    expected.deployCylon(0, 0);
    Optional<Game> loaded = Game.load(
      "3,3\n" +
      "0,0,true,false,NONE\n" +
      "0,1,false,false,NONE\n" +
      "0,2,false,false,WADAMA\n" +
      "1,0,false,false,AGATHON\n" +
      "1,1,true,true,NONE\n" +
      "1,2,false,false,ROSLIN\n" +
      "2,0,false,false,NONE\n" +
      "2,1,false,false,NONE\n" +
      "2,2,false,false,BALTAR" +
      "\nKara,0,0,100"
    );
    assertTrue(loaded.isPresent());
    loaded.ifPresent(g -> assertEquals(g, expected));
  }

  @Test
  public void gameShouldNotBeLoadedFromInalidRepresentation() {
    assertFalse(Game.load("invalid").isPresent());
  }
}
