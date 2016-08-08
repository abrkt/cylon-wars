package me.abarakat.cwars.model;

import org.testng.annotations.Test;

import java.util.Optional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class LocationTest {

  @Test
  public void exploringLocationShouldMakeItVisible() {
    Location location = new Location(0, 0);
    assertFalse(location.isVisible());
    location.explore();
    assertTrue(location.isVisible());
  }

  public void setupResurrectionHubShouldEndHumanLife() {
    Location location = new Location(0, 0);
    location.setHuman(Human.WADAMA);
    assertTrue(location.humanLifeDetected());
    location.setupResurrectionHub();
    assertFalse(location.humanLifeDetected());
  }

  public void resurrectionHubShouldAlwaysBeVisible() {
    Location location = new Location(0, 0);
    assertFalse(location.isVisible());
    location.setupResurrectionHub();
    assertTrue(location.isVisible());
  }

  public void destroyingResurrectionHubShouldHideItsLocation() {
    Location location = new Location(0, 0);
    location.setupResurrectionHub();
    assertTrue(location.isVisible());
    location.destroyResurrectionHub();
    assertFalse(location.isVisible());
  }

  @Test
  public void locationShouldSaveItSelfToStringRepresentation() {
    assertEquals(new Location(0, 0).setupResurrectionHub().save(), "0,0,true,true,NONE");
    assertEquals(new Location(3, 4).setHuman(Human.WADAMA).save(), "3,4,false,false,WADAMA");
  }

  @Test
  public void locationShouldBeLoadedFromValidRepresentation() {
    assertEquals(Location.load("0,0,true,true,NONE").get(), new Location(0, 0).setupResurrectionHub());
    assertEquals(Location.load("3,4,false,false,WADAMA").get(), new Location(3, 4).setHuman(Human.WADAMA));
  }

  @Test
  public void locationShouldNotBeLoadedFromInvalidRepresentation() {
    Optional<Location> kara = Location.load("invalid");
    assertFalse(kara.isPresent());
  }

}
