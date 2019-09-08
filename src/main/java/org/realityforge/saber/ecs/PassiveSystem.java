package org.realityforge.saber.ecs;

import com.artemis.BaseSystem;

public abstract class PassiveSystem
  extends BaseSystem
{
  @Override
  protected void processSystem()
  {
  }

  @Override
  protected boolean checkProcessing()
  {
    setEnabled( false );
    return false;
  }
}
