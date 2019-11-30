package org.realityforge.saber.components;

import galdr.annotations.Component;
import javax.annotation.Nullable;
import org.realityforge.saber.CommandType;

@Component
public final class CommandTarget
{
  @Nullable
  public CommandType command;
}
