package com.artemis.injection;

import javax.annotation.Nonnull;

public class SharedInjectionCache
{
  @Nonnull
  protected InjectionCache initialValue()
  {
    return new InjectionCache();
  }

  @Nonnull
  public InjectionCache get()
  {
    return new InjectionCache();
  }
}
