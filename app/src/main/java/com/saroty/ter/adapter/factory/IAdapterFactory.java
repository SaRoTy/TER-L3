package com.saroty.ter.adapter.factory;

import com.saroty.ter.adapter.Adapter;
import com.saroty.ter.adapter.exception.NoAdapterFoundException;

import java.net.URL;

/**
 * Created by Arthur on 09/03/2015.
 */
public interface IAdapterFactory
{
    Adapter makeAdapter(URL url) throws NoAdapterFoundException;
}