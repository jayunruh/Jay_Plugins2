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
import ij.macro.Functions;
import ij.macro.MacroExtension;
import ij.macro.ExtensionDescriptor;
import java.awt.Color;
import java.awt.Frame;
import ij.plugin.*;
import jalgs.*;
import jguis.*;
import ij.text.*;


public class PlotWindow_Extensions_jru_v1 implements PlugIn, MacroExtension {
	private ExtensionDescriptor[] extensions;

	public void run(String arg) {
		if (!IJ.macroRunning()) {
			IJ.error("Cannot install extensions from outside a macro!");
			return;
		}

		extensions=new ExtensionDescriptor[]{
			ExtensionDescriptor.newDescriptor("getNSeries",this,MacroExtension.ARG_OUTPUT+MacroExtension.ARG_NUMBER),
			ExtensionDescriptor.newDescriptor("selectSeries",this,MacroExtension.ARG_NUMBER),
			ExtensionDescriptor.newDescriptor("scalePlotCoords",this,ARG_NUMBER,ARG_NUMBER,ARG_OUTPUT+ARG_NUMBER,ARG_OUTPUT+ARG_NUMBER),
			ExtensionDescriptor.newDescriptor("setLimits",this,ARG_NUMBER,ARG_NUMBER,ARG_NUMBER,ARG_NUMBER),
			ExtensionDescriptor.newDescriptor("getLimits",this,ARG_OUTPUT+ARG_NUMBER,ARG_OUTPUT+ARG_NUMBER,ARG_OUTPUT+ARG_NUMBER,ARG_OUTPUT+ARG_NUMBER),
			ExtensionDescriptor.newDescriptor("setLogAxes",this,ARG_NUMBER,ARG_NUMBER),
			ExtensionDescriptor.newDescriptor("setXLabel",this,ARG_STRING),
			ExtensionDescriptor.newDescriptor("setYLabel",this,ARG_STRING),
			ExtensionDescriptor.newDescriptor("setGridWhiteness",this,ARG_NUMBER),
			ExtensionDescriptor.newDescriptor("deleteSelected",this),
			ExtensionDescriptor.newDescriptor("autoscaleX",this),
			ExtensionDescriptor.newDescriptor("autoscaleY",this),
			ExtensionDescriptor.newDescriptor("scaleROI",this),
			ExtensionDescriptor.newDescriptor("setMagnification",this,ARG_NUMBER),
			ExtensionDescriptor.newDescriptor("setMagRatio",this,ARG_NUMBER),
			ExtensionDescriptor.newDescriptor("getSelNpts",this,ARG_OUTPUT+ARG_NUMBER),
			ExtensionDescriptor.newDescriptor("getSelIndexXYVals",this,ARG_NUMBER,ARG_OUTPUT+ARG_NUMBER,ARG_OUTPUT+ARG_NUMBER),
			ExtensionDescriptor.newDescriptor("getSelStat",this,ARG_STRING,ARG_OUTPUT+ARG_NUMBER),
			ExtensionDescriptor.newDescriptor("addXYSeries",this,ARG_ARRAY,ARG_ARRAY),
			ExtensionDescriptor.newDescriptor("updateSelSeries",this,ARG_ARRAY,ARG_ARRAY),
			ExtensionDescriptor.newDescriptor("createPlot",this,ARG_STRING,ARG_STRING,ARG_ARRAY,ARG_ARRAY),
			ExtensionDescriptor.newDescriptor("convertToPW4",this),
			ExtensionDescriptor.newDescriptor("convertToPW",this),
			ExtensionDescriptor.newDescriptor("setBinSize",this,MacroExtension.ARG_NUMBER),
			ExtensionDescriptor.newDescriptor("getSelected",this,MacroExtension.ARG_OUTPUT+MacroExtension.ARG_NUMBER),
			ExtensionDescriptor.newDescriptor("setCommand",this,ARG_STRING,ARG_STRING),
			ExtensionDescriptor.newDescriptor("getCommand",this,ARG_STRING,ARG_OUTPUT+ARG_STRING),
			ExtensionDescriptor.newDescriptor("getXLabel",this,ARG_OUTPUT+ARG_STRING),
			ExtensionDescriptor.newDescriptor("getYLabel",this,ARG_OUTPUT+ARG_STRING),
			ExtensionDescriptor.newDescriptor("selectTable",this,ARG_OUTPUT+ARG_STRING)
		};

		Functions.registerExtensions(this);
	}

	public ExtensionDescriptor[] getExtensionFunctions(){
		return extensions;
	}

	public String handleExtension(String name,Object[] args){
		if(name.equals("createPlot")){
			String xlabel=(String)args[0];
			String ylabel=(String)args[1];
			Object[] xvals=(Object[])args[2];
			Object[] yvals=(Object[])args[3];
			float[] yvals2=new float[yvals.length];
			for(int i=0;i<yvals.length;i++) yvals2[i]=((Double)yvals[i]).floatValue();
			if(xvals.length==yvals.length){
				float[] xvals2=new float[xvals.length];
				for(int i=0;i<xvals.length;i++) xvals2[i]=((Double)xvals[i]).floatValue();
				new PlotWindow4("Macro PlotWindow4",xlabel,ylabel,xvals2,yvals2).draw();
			} else {
				new PlotWindow4("Macro PlotWindow4",xlabel,ylabel,yvals2).draw();
			}
			return null;
		}
		if(name.equals("selectTable")){
			TextWindow[] tw=jutils.selectTables(false,1);
			if(tw!=null && tw.length>0){
				((String[])args[0])[0]=tw[0].getTitle();
			}
			return null;
		}
		ImageWindow iw=WindowManager.getCurrentWindow();
		if(!jutils.isPlotFamily(iw)){
			IJ.error("Current Image is not Compatible");
			return null;
		}
		if(name.equals("convertToPW4")){
			jutils.pw2pw4((PlotWindow)iw).draw();
			return null;
		}
		if(name.equals("convertToPW")){
			jutils.pw42pw(iw);
			return null;
		}
		if(name.equals("getNSeries")){
			int nseries=((Integer)jutils.runPW4VoidMethod(iw,"getNSeries")).intValue();
			((Double[])args[0])[0]=new Double((double)nseries);
		}
		if(name.equals("selectSeries")){
			int series=((Double)args[0]).intValue();
			jutils.runReflectionMethod(iw,"selectSeries",new Object[]{series});
		}
		if(name.equals("setLimits")){
			float xmin=((Double)args[0]).floatValue();
			float xmax=((Double)args[1]).floatValue();
			float ymin=((Double)args[2]).floatValue();
			float ymax=((Double)args[3]).floatValue();
			float[] limits=(float[])jutils.runPW4VoidMethod(iw,"getLimits");
			limits[0]=xmin; limits[1]=xmax; limits[2]=ymin; limits[3]=ymax;
			jutils.runReflectionMethod(iw,"setLimits",new Object[]{limits});
		}
		if(name.equals("getLimits")){
			float[] limits=(float[])jutils.runPW4VoidMethod(iw,"getLimits");
			((Double[])args[0])[0]=new Double(limits[0]);
			((Double[])args[1])[0]=new Double(limits[1]);
			((Double[])args[2])[0]=new Double(limits[2]);
			((Double[])args[3])[0]=new Double(limits[3]);
		}
		if(name.equals("scalePlotCoords")){
			//need to make this equivalent to the "toScaled" macro function but with log plot support
			int x=((Double)args[0]).intValue();
			int y=((Double)args[1]).intValue();
			Object plot=jutils.runPW4VoidMethod(iw,"getPlot");
			float[] coords=(float[])jutils.runReflectionMethod(plot,"getPlotCoords",new Object[]{x,y});
			((Double[])args[2])[0]=new Double(coords[0]);
			((Double[])args[3])[0]=new Double(coords[1]);
		}
		if(name.equals("setLogAxes")){
			//0 means linear axis, 1 means log axis
			boolean logx=(((Double)args[0]).intValue()==1)?true:false;
			boolean logy=(((Double)args[1]).intValue()==1)?true:false;
			jutils.runReflectionMethod(iw,"setLogAxes",new Object[]{logx,logy});
		}
		if(name.equals("setXLabel")){
			String label=(String)args[0];
			Object plot=jutils.runPW4VoidMethod(iw,"getPlot");
			jutils.runReflectionMethod(plot,"setxLabel",new Object[]{label});
		}
		if(name.equals("setYLabel")){
			String label=(String)args[0];
			Object plot=jutils.runPW4VoidMethod(iw,"getPlot");
			jutils.runReflectionMethod(plot,"setyLabel",new Object[]{label});
		}
		if(name.equals("setGridWhiteness")){
			int whiteness=((Double)args[0]).intValue();
			Object plot=jutils.runPW4VoidMethod(iw,"getPlot");
			jutils.runReflectionMethod(plot,"setGridWhiteness",new Object[]{whiteness});
		}
		if(name.equals("deleteSelected")){
			Object plot=jutils.runPW4VoidMethod(iw,"getPlot");
			int selindex=(Integer)jutils.runPW4VoidMethod(iw,"getSelected");
			jutils.runReflectionMethod(plot,"deleteSeries",new Object[]{selindex,false});
		}
		if(name.equals("autoscaleX")){
			Object plot=jutils.runPW4VoidMethod(iw,"getPlot");
			jutils.runReflectionMethod(plot,"xautoscale",null);
		}
		if(name.equals("autoscaleY")){
			Object plot=jutils.runPW4VoidMethod(iw,"getPlot");
			jutils.runReflectionMethod(plot,"yautoscale",null);
		}
		if(name.equals("scaleROI")){
			jutils.runPW4VoidMethod(iw,"scaleroi");
		}
		if(name.equals("setMagnification")){
			float mag=((Double)args[0]).floatValue();
			Object plot=jutils.runPW4VoidMethod(iw,"getPlot");
			jutils.runReflectionMethod(plot,"setmagnification",new Object[]{mag});
		}
		if(name.equals("setMagRatio")){
			float magratio=((Double)args[0]).floatValue();
			Object plot=jutils.runPW4VoidMethod(iw,"getPlot");
			jutils.runReflectionMethod(plot,"setmagratio",new Object[]{magratio});
		}
		if(name.equals("getSelNpts")){
			int[] npts=(int[])jutils.runPW4VoidMethod(iw,"getNpts");
			int sel=(Integer)jutils.runPW4VoidMethod(iw,"getSelected");
			if(sel<0) sel=0;
			int selnpts=npts[sel];
			((Double[])args[0])[0]=new Double(selnpts);
		}
		if(name.equals("getSelIndexXYVals")){
			float[][] yvals=(float[][])jutils.runPW4VoidMethod(iw,"getYValues");
			float[][] xvals=(float[][])jutils.runPW4VoidMethod(iw,"getXValues");
			int sel=(Integer)jutils.runPW4VoidMethod(iw,"getSelected");
			if(sel<0) sel=0;
			int index=((Double)args[0]).intValue();
			int[] npts=(int[])jutils.runPW4VoidMethod(iw,"getNpts");
			if(index<0 || index>=npts[sel]){
				((Double[])args[1])[0]=new Double(0.0);
				((Double[])args[2])[0]=new Double(0.0);
			} else {
				((Double[])args[1])[0]=new Double(xvals[sel][index]);
				((Double[])args[2])[0]=new Double(yvals[sel][index]);
			}
		}
		if(name.equals("getSelStat")){
			float[][] yvals=(float[][])jutils.runPW4VoidMethod(iw,"getYValues");
			int sel=(Integer)jutils.runPW4VoidMethod(iw,"getSelected");
			if(sel<0) sel=0;
			String stat=(String)args[0];
			int[] npts=(int[])jutils.runPW4VoidMethod(iw,"getNpts");
			float[] temp=new float[npts[sel]];
			System.arraycopy(yvals[sel],0,temp,0,npts[sel]);
			float stat2=jstatistics.getstatistic(stat,temp,null);
			((Double[])args[1])[0]=new Double(stat2);
		}
		if(name.equals("addXYSeries")){
			Object[] xvals=(Object[])args[0];
			Object[] yvals=(Object[])args[1];
			float[] yvals2=new float[yvals.length];
			for(int i=0;i<yvals.length;i++) yvals2[i]=((Double)yvals[i]).floatValue();
			if(xvals.length==yvals.length){
				float[] xvals2=new float[xvals.length];
				for(int i=0;i<xvals.length;i++) xvals2[i]=((Double)xvals[i]).floatValue();
				jutils.runReflectionMethod(iw,"addPoints",new Object[]{xvals2,yvals2,true});
			} else {
				jutils.runReflectionMethod(iw,"addPoints",new Object[]{yvals2,true});
			}
		}
		if(name.equals("updateSelSeries")){
			Object plot=jutils.runPW4VoidMethod(iw,"getPlot");
			int sel=(Integer)jutils.runPW4VoidMethod(iw,"getSelected");
			if(sel<0) sel=0;
			Object[] xvals=(Object[])args[0];
			Object[] yvals=(Object[])args[1];
			float[] yvals2=new float[yvals.length];
			for(int i=0;i<yvals.length;i++) yvals2[i]=((Double)yvals[i]).floatValue();
			if(xvals.length==yvals.length){
				float[] xvals2=new float[xvals.length];
				for(int i=0;i<xvals.length;i++) xvals2[i]=((Double)xvals[i]).floatValue();
				jutils.runReflectionMethod(plot,"updateSeries",new Object[]{xvals2,yvals2,sel,true});
			} else {
				jutils.runReflectionMethod(plot,"updateSeries",new Object[]{yvals2,sel,true});
			}
		}
		if(name.equals("setBinSize")){
			float binsize=((Double)args[0]).floatValue();
			Object plot=jutils.runReflectionMethod(iw,"getPlot",null);
			if(iw.getClass().getName().equals("jguis.PlotWindow2DHist")) jutils.runReflectionMethod(plot,"setBinSize",new Object[]{binsize});
			else jutils.runReflectionMethod(plot,"setBinSizeUnits",new Object[]{binsize});
		}
		if(name.equals("getSelected")){
			int sel=((Integer)jutils.runPW4VoidMethod(iw,"getSelected")).intValue();
			((Double[])args[0])[0]=new Double((double)sel);
		}
		if(name.equals("getXLabel")){
			String xlab=(String)jutils.runPW4VoidMethod(iw,"getxLabel");
			((String[])args[0])[0]=xlab;
		}
		if(name.equals("getYLabel")){
			String ylab=(String)jutils.runPW4VoidMethod(iw,"getyLabel");
			((String[])args[0])[0]=ylab;
		}
		jutils.runPW4VoidMethod(iw,"updatePlot");
		return null;
	}

}
