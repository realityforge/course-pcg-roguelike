package org.realityforge.saber.screens;

import com.artemis.utils.BitVector;
import javax.annotation.Nonnull;
import org.realityforge.saber.Renderer;

public interface Screen
{
  void handleKeys( @Nonnull BitVector keys );

  void display( @Nonnull Renderer renderer );
}
