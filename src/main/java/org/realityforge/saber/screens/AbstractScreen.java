package org.realityforge.saber.screens;

import org.realityforge.saber.ecs.PassiveSystem;
import org.realityforge.saber.systems.ScreenSystem;

public abstract class AbstractScreen
  extends PassiveSystem
  implements Screen
{
  ScreenSystem screen;
}
