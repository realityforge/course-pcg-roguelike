package com.artemis;

/**
 * Simple sequential invocation strategy.
 *
 * @see SystemInvocationStrategy
 */
public class InvocationStrategy
  extends SystemInvocationStrategy
{
  /**
   * Processes all systems in order.
   *
   * Should guarantee artemis is in a sane state using calls to #updateEntityStates
   * before each call to a system, and after the last system has been called, or if no
   * systems have been called at all.
   */
  @Override
  protected void process()
  {
    for ( int i = 0, s = systems.size(); s > i; i++ )
    {
      if ( disabled.get( i ) )
      {
        continue;
      }

      updateEntityStates();
      systems.get( i ).process();
    }

    updateEntityStates();
  }
}
