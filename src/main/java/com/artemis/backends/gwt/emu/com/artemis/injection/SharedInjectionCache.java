package com.artemis.backends.gwt.emu.com.artemis.injection;

import com.artemis.injection.InjectionCache;

public class SharedInjectionCache
{
  protected InjectionCache initialValue()
  {
    return new InjectionCache();
  }

  public InjectionCache get()
  {
    return new InjectionCache();
  }
}
