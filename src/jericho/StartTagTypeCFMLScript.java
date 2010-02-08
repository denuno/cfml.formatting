// Jericho HTML Parser - Java based library for analysing and manipulating HTML
// Version 3.1
// Copyright (C) 2004-2009 Martin Jericho
// http://jericho.htmlparser.net/
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of either one of the following licences:
//
// 1. The Eclipse Public License (EPL) version 1.0,
// included in this distribution in the file licence-epl-1.0.html
// or available at http://www.eclipse.org/legal/epl-v10.html
//
// 2. The GNU Lesser General Public License (LGPL) version 2.1 or later,
// included in this distribution in the file licence-lgpl-2.1.txt
// or available at http://www.gnu.org/licenses/lgpl.txt
//
// This library is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied.
// See the individual licence texts for more details.

package jericho;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagTypeGenericImplementation;
import net.htmlparser.jericho.Tag;

final class StartTagTypeCFMLScript extends StartTagTypeGenericImplementation {
	protected static final StartTagTypeCFMLScript INSTANCE=new StartTagTypeCFMLScript();

	private StartTagTypeCFMLScript() {
		super("CFML script","<cfscript",">",EndTagType.NORMAL,true,false,false);
	}

	protected Tag constructTagAt(final Source source, final int pos) {
		final StartTag startTag=(StartTag)super.constructTagAt(source,pos);
		if (startTag==null) return null;
		// A CFML script element requires the attribute language="php".
		//if (!"php".equalsIgnoreCase(startTag.getAttributes().getValue("language"))) return null;
		return startTag;
	}
}

