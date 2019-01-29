package org.lemonframework.disruptor.proxy;

/**
 * Default implementation of MessagePublicationListener
 */
public enum NoMessagePublicationListener implements MessagePublicationListener
{
    INSTANCE;

    @Override
    public void onPrePublish()
    {

    }

    @Override
    public void onPostPublish()
    {

    }
}
