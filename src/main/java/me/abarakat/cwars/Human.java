package me.abarakat.cwars;

public enum Human {
  WADAMA(0, "W. Adama", false),
  FOSTER(50, "T. Foster"),
  TYROL(50, "G. Tyrol"),
  ANDERS(50, "S. Anders"),
  DUALLA(50, "A. Dualla"),
  AGATHON(100, "K. Agathon"),
  LADAMA(300, "L. Adama"),
  ROSLIN(400, "L. Roslin"),
  BALTAR(500, "G. Baltar");

  private final int experince;
  private final String name;
  private final boolean killable;

  Human(int level, String name) {
    this(level, name, true);
  }

  Human(int level, String name, boolean killable) {
    this.experince = level;
    this.name = name;
    this.killable = killable;
  }

  public int getExperience() {
    return experince;
  }

  public String getName() {
    return name;
  }

  public boolean isKillable() {
    return killable;
  }
}
