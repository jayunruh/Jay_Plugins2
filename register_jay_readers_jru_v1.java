/*******************************************************************************
 * Copyright (c) 2012 Jay Unruh, Stowers Institute for Medical Research.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import loci.formats.*;

public class register_jay_readers_jru_v1 implements PlugIn {

	public void run(String arg) {
		try{
			Class jayreader=Class.forName("loci_pw_reader_jru_v1");
			ImageReader.getDefaultReaderClasses().addClass(jayreader);
		} catch(ClassNotFoundException e){
			//if the reader isn't intalled, don't bother
			IJ.log("loci pw reader wasn't found");
			return;
		}
	}

}
