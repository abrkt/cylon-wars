package me.abarakat.cwars.model;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import java.util.Optional;

public class CylonTest {

  @Test
  public void cylonShouldSaveItSelfToStringRepresentation() {
    assertEquals(new Cylon("Kara").save(), "Kara,0,0,100");
  }

  @Test
  public void cylonShouldBeLoadedFromValidRepresentation() {
    Optional<Cylon> kara = Cylon.load("Kara,3,4,100");
    assertTrue(kara.isPresent());
    Cylon expected = new Cylon("Kara");
    expected.setLocation(3, 4);
    kara.ifPresent(cylon -> assertEquals(cylon, expected));
  }

  @Test
  public void cylonShouldNotBeLoadedFromInvalidRepresentation() {
    Optional<Cylon> kara = Cylon.load("invalid");
    assertFalse(kara.isPresent());
  }

  @Test
  public void cylonShouldMoveInAvalidDirection() {
    Cylon kara = new Cylon("kara");
    assertEquals(kara.getX(), 0);
    assertEquals(kara.getY(), 0);
    assertTrue(kara.move(Action.EAST, 5, 5));
    assertEquals(kara.getX(), 1);
    assertTrue(kara.move(Action.WEST, 5, 5));
    assertEquals(kara.getX(), 0);
    assertTrue(kara.move(Action.SOUTH, 5, 5));
    assertEquals(kara.getY(), 1);
    assertTrue(kara.move(Action.NORTH, 5, 5));
    assertEquals(kara.getY(), 0);
  }

  @Test
  public void cylonShouldNotMoveOutsideMapBoundries() {
    Cylon kara = new Cylon("kara");
    assertFalse(kara.move(Action.EAST, 0, 1));
    assertFalse(kara.move(Action.WEST, 0, 1));
    assertFalse(kara.move(Action.SOUTH, 1, 0));
    assertFalse(kara.move(Action.NORTH, 1, 0));
  }

  @Test
  public void cylonShoudBeDefetedByWilliamAdama() {
    Cylon number6 = new Cylon("Number 6");
    int experience = number6.getExperience();
    assertFalse(number6.fight(Human.WADAMA));
    assertEquals(number6.getExperience(), experience);
  }

  @Test
  public void cylonShoudGainExperineceAfterFightingWithHuman() {
    Cylon number6 = new Cylon("Number 6");
    int experience = number6.getExperience();
    boolean alive = number6.fight(Human.BALTAR);
    if (alive) {
      assertEquals(number6.getExperience(), experience + Human.BALTAR.getExperience());
    } else {
      assertEquals(number6.getExperience(), experience + Human.BALTAR.getExperience() / 3);
    }
  }
}
