package org.realityforge.saber.screens;

import com.artemis.managers.PlayerManager;
import org.realityforge.saber.ecs.PassiveSystem;
import org.realityforge.saber.systems.ScreenSystem;

public abstract class AbstractScreen
  extends PassiveSystem
  implements Screen
{
  ScreenSystem screen;
  PlayerManager pManager;
}
