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
import jguis.*;

public class man_track_jru_v1 implements PlugIn {

	public void run(String arg) {
		ImagePlus imp =WindowManager.getCurrentImage();
		int width=imp.getWidth(); int height=imp.getHeight();
		ImageStack stack=imp.getStack();
		int slices=stack.getSize();
		Roi roi=imp.getRoi();
		Rectangle r=roi.getBounds();
		float[] xvals=new float[slices];
		float[] yvals=new float[slices];
		for(int i=0;i<slices;i++){xvals[i]=r.x; yvals[i]=r.y;}
		for(int x=0;x<slices;x++){
			imp.setRoi(new PointRoi((int)xvals[x],(int)yvals[x]));
			imp.setSlice(x+1);
			(new WaitForUserDialog("Update point if necessary")).show();
			Rectangle tempr=imp.getRoi().getBounds();
			xvals[x]=tempr.x;
			yvals[x]=tempr.y;
			if(x<(slices-1)){
				xvals[x+1]=xvals[x];
				yvals[x+1]=yvals[x];
			}
		}
		new PlotWindow4("Track","x","y",xvals,yvals).draw();
	}

}
