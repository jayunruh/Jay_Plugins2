/*******************************************************************************
 * Copyright (c) 2015 Jay Unruh, Stowers Institute for Medical Research.
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

public class set_imaris_spot_traj_jru_v1 implements PlugIn {

	public void run(String arg) {
		ImageWindow[] iw=jutils.selectPlotFamily(false,1,new String[]{"Trajectory Plot"});
		if(iw==null || iw.length<1) return;
		GenericDialog gd=new GenericDialog("Options");
		gd.addCheckbox("Pixel Units",false);
		gd.addNumericField("Spot Radius",2.0,5,15,null);
		gd.showDialog(); if(gd.wasCanceled()) return;
		boolean pixunits=gd.getNextBoolean();
		float rad=(float)gd.getNextNumber();
		int sel=(Integer)jutils.runPW4VoidMethod(iw[0],"getSelected");
		int[] npts=(int[])jutils.runPW4VoidMethod(iw[0],"getNpts");
		float[] x=((float[][])jutils.runPW4VoidMethod(iw[0],"getXValues"))[sel];
		float[] y=((float[][])jutils.runPW4VoidMethod(iw[0],"getYValues"))[sel];
		if(x.length>npts[sel]){
			x=algutils.get_subarray(x,0,npts[sel]);
			y=algutils.get_subarray(y,0,npts[sel]);
		}
		float[] z=new float[npts[sel]];
		if(jutils.isPlot(iw[0])){
			z=((float[][][])jutils.runPW4VoidMethod(iw[0],"getZValues"))[0][sel];
			if(x.length>npts[sel]) z=algutils.get_subarray(z,0,npts[sel]);
		}
		String[] annot=(String[])jutils.runPW4VoidMethod(iw[0],"getAnnotations");
		int[] timeindices=new int[npts[sel]];
		if(annot!=null){
			int start=(int)Float.parseFloat(annot[sel]);
			for(int i=0;i<npts[sel];i++){
				timeindices[i]=start+i;
			}
		}
		boolean success=ImarisXT_utils.setSpotsTraj(pixunits,new float[][]{x,y,z},timeindices,rad);
		if(!success) IJ.log("Imaris Transfer Failed");
	}

}
