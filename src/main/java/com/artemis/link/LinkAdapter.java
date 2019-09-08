package com.artemis.link;

/**
 * Stub implementation of {@link LinkListener}.
 *
 * @see EntityLinkManager#register(Class, LinkListener)
 * @see EntityLinkManager#register(Class, String, LinkListener)
 */
public class LinkAdapter
  implements LinkListener
{
  @Override
  public void onLinkEstablished( final int sourceId, final int targetId )
  {
  }

  @Override
  public void onLinkKilled( final int sourceId, final int targetId )
  {
  }

  @Override
  public void onTargetDead( final int sourceId, final int deadTargetId )
  {
  }

  @Override
  public void onTargetChanged( final int sourceId, final int targetId, final int oldTargetId )
  {
  }
}
