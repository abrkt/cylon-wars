package me.abarakat.cwars;

public enum Human {
  WADAMA(0, "William Adama", false),
  DUALLA(50, "Anastasia Dualla"),
  AGATHON(100, "Karl Agathon"),
  LADAMA(300, "Lee Adama"),
  ROSLIN(400, "Laura Roslin"),
  BALTAR(500, "Gaius Baltar");


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

  public int getExperince() {
    return experince;
  }

  public String getName() {
    return name;
  }

  public boolean isKillable() {
    return killable;
  }
}
