/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.reader.filters;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;

import org.luwrain.app.reader.doctree.*;

class Html implements Filter
{
    private String fileName = "";
    private String src;

    public Html(String fileName)
    {
	this.fileName = fileName;
	if (fileName == null)
	    throw new NullPointerException("fileName may not be null");
    }

	@Override public Document constructDocument()
    {
	read(StandardCharsets.UTF_8);
	if (src == null)
	    return null;
	HtmlParse parse = new HtmlParse(src);
	parse.strip();
	Node root = parse.constructRoot();
	if (root == null)
	    return null;
	root.setParentOfSubnodes();
	Document doc = new Document(root);
	doc.buildView(60);
	return doc;
    }

    private void read(Charset encoding)
    {
	try {
	    readImpl(encoding);
	}
	catch (IOException e)
	{
		e.printStackTrace();
		src = null;
		return;
	}
    }

    private void readImpl(Charset encoding) throws IOException
    {
	StringBuilder b = new StringBuilder();
	Path path = Paths.get(fileName);
	try (Scanner scanner =  new Scanner(path, encoding.name())) {
		while (scanner.hasNextLine())
		{
		    b.append(scanner.nextLine());
		    b.append(" ");
		}
	    }
	src = b.toString();
    }
}
