/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.opds;

import java.net.*;
import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.util.Opds;
//import org.luwrain.popups.Popups;

public class OpdsApp implements Application, Actions
{
    static private final String STRINGS_NAME = "luwrain.opds";

    private final Base base = new Base();
    private Luwrain luwrain;
    private Strings strings;
    private ListArea area;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	Object o = luwrain.i18n().getStrings(STRINGS_NAME);
	if (o == null || !(o instanceof Strings))
	    return false;
	strings = (Strings)o;
	this.luwrain = luwrain;
	if (!base.init(luwrain))
	    return false;
	createArea();
	return true;
    }

    @Override public void onReady()
    {
	if (!base.onReady())
	    return;
	area.refresh();
	area.resetHotPoint(false);
    }

    @Override public boolean onClick(Object obj)
    {
	if (obj == null)
	    return false;
	try {
	    if (obj instanceof Opds.Entry)
		base.start(area, new java.net.URL(base.currentUrl(), ((Opds.Entry)obj).link())); else
	    if (obj instanceof RemoteLibrary)
		base.start(area, new java.net.URL(((RemoteLibrary)obj).url)); else
	    return false;
	    area.refresh();
	    return true;
	}
	catch (java.net.MalformedURLException e)
	{
	    e.printStackTrace();
	    //FIXME:message;
	    return false;
	}
    }

    @Override public boolean onReturnBack()
    {
	if (!base.returnBack(area))
	    return false;
	area.refresh();
	    return true;
    }


    private void createArea()
    {
	final Actions actions = this;
	//	final Strings s = strings;

	final ListParams params = new ListParams();
	params.environment = new DefaultControlEnvironment(luwrain);
	params.model = base.getModel();
	params.appearance = new DefaultListItemAppearance(params.environment);
	params.clickHandler = (area, index, obj)->actions.onClick(obj);
	params.name = "FIXME";

	area = new ListArea(params){
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isCommand() && !event.isModified())
			switch(event.getCommand())
		    {
		    case KeyboardEvent.BACKSPACE:
			return actions.onReturnBack();
		    }
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    switch(event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.closeApp();
			return true;
		    case EnvironmentEvent.THREAD_SYNC:
			actions.onReady();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
		@Override protected String noContentStr()
		{
		    if (base.isFetchingInProgress())
			return "Идёт загрузка. Пожалуйста, подождите.";
		    return super.noContentStr();
		}
	    };


    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    @Override public AreaLayout getAreasToShow()
    {
	return new AreaLayout(area);
    }

    @Override public boolean closeApp()
    {
	/*
	if (thread != null && !thread.done())
	    return false;
	*/
	luwrain.closeApp();
	return true;
    }
}
