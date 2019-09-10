package org.realityforge.saber.components;

import com.artemis.Component;
import javax.annotation.Nullable;
import org.realityforge.saber.CommandType;

public final class CommandTarget
  extends Component
{
  @Nullable
  public CommandType command;
}
