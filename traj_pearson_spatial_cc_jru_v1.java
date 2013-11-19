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
import ij.plugin.frame.*;
import jguis.*;
import jalgs.*;
import jalgs.jfft.*;

public class traj_pearson_spatial_cc_jru_v1 implements PlugIn {

	public void run(String arg) {
		ImageWindow iw=WindowManager.getCurrentWindow();
		float[][] yvals=(float[][])jutils.runPW4VoidMethod(iw,"getYValues");
		float[][] xvals2=(float[][])jutils.runPW4VoidMethod(iw,"getXValues");
		float psize=xvals2[0][1]-xvals2[0][0];
		int length=yvals[0].length;
		crosscorr cc=new crosscorr(length,false);
		float[][] temp=cc.docrosscorrnofft(yvals[0],yvals[1],false);
		float stdev1=jstatistics.getstatistic("StDev",yvals[0],null);
		float stdev2=jstatistics.getstatistic("StDev",yvals[1],null);
		float[] ics=new float[length];
		float[] xvals=new float[length];
		for(int i=0;i<length;i++){
			int position=i+length/2;
			if(position>=length) position-=length;
			ics[i]=temp[0][position]*temp[1][0]*temp[1][1]/(stdev1*stdev2);
			xvals[i]=psize*(float)(i-length/2);
		}
		new PlotWindow4("Spatial Correlation","shift","Pearson",xvals,ics).draw();
	}

}
