package me.abarakat.cwars;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

public class CylonTest {

  @Test
  public void cylonShouldSaveItSelfToStringRepresentation() {
    assertEquals(new Cylon("Kara").save(), "Kara<0,0,0>");
  }

  @Test
  public void cylonShouldBeLoadedFromValidRepresentation() {
    Cylon kara = Cylon.load("Kara<3,4,300>");
    assertEquals(kara.getName(), "Kara");
    assertEquals(kara.getX(), 3);
    assertEquals(kara.getY(), 4);
    assertEquals(kara.getExperience(), 300);
  }

  @Test
  public void cylonShouldMoveInAvalidDirection() {
    Cylon kara = new Cylon("kara");
    assertEquals(kara.getX(), 0);
    assertEquals(kara.getY(), 0);
    assertEquals(kara.move(Action.EAST, 5, 5), true);
    assertEquals(kara.getX(), 1);
    assertEquals(kara.move(Action.WEST, 5, 5), true);
    assertEquals(kara.getX(), 0);
    assertEquals(kara.move(Action.SOUTH, 5, 5), true);
    assertEquals(kara.getY(), 1);
    assertEquals(kara.move(Action.NORTH, 5, 5), true);
    assertEquals(kara.getY(), 0);
  }

  @Test
  public void cylonShouldNotMoveOutsideMapBoundries() {
    Cylon kara = new Cylon("kara");
    assertEquals(kara.getX(), 0);
    assertEquals(kara.getY(), 0);
    assertEquals(kara.move(Action.EAST, 0, 1), false);
    assertEquals(kara.getX(), 0);
    assertEquals(kara.move(Action.WEST, 0, 1), false);
    assertEquals(kara.getX(), 0);
    assertEquals(kara.move(Action.SOUTH, 1, 0), false);
    assertEquals(kara.getY(), 0);
    assertEquals(kara.move(Action.NORTH, 1, 0), false);
    assertEquals(kara.getY(), 0);
  }

  @Test
  public void cylonShoudBeDefetedByWilliamAdama() {
    Cylon number6 = new Cylon("Number 6");
    assertEquals(number6.getExperience(), 0);
    assertFalse(number6.fight(Human.WADAMA));
    assertEquals(number6.getExperience(), 0);
  }

  @Test
  public void cylonShoudGainExperineceAfterFightingWithHuman() {
    Cylon number6 = new Cylon("Number 6");
    assertEquals(number6.getExperience(), 0);
    boolean alive = number6.fight(Human.BALTAR);
    if (alive) {
      assertEquals(number6.getExperience(), Human.BALTAR.getExperince());
    } else {
      assertEquals(number6.getExperience(), Human.BALTAR.getExperince() / 3);
    }
  }
}
