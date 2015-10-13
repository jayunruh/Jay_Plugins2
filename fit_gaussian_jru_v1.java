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
import jalgs.*;
import jalgs.jfit.*;
import jguis.*;
import ij.text.*;

public class fit_gaussian_jru_v1 implements PlugIn, NLLSfitinterface_v2{
	float[] tempx;
	String[] paramnames={"baseline","xc1","stdev1","amp1"};
	gausfunc gf;

	public void run(String arg) {
		//for now use fit multi gaussian jru v4 with a single position to get errors
		/*GenericDialog gd=new GenericDialog("Options");
		gd.addCheckbox("Get_Errors",false);
		gd.showDialog(); if(gd.wasCanceled()) return;
		boolean errs=gd.getNextBoolean();*/
		ImageWindow iw=WindowManager.getCurrentWindow();
		PlotWindow4 pw=jutils.getPW4SelCopy(iw);
		String title=pw.getTitle();
		float[][] yvals=pw.getYValues();
		float[][] xvals=pw.getXValues();
		int length=yvals[0].length;
		int[] colors=pw.getColors();
		colors[0]=0;

		//parameters are baseline,xc1,stdev1,amp1
		double[] params=guessParams(xvals[0],yvals[0],length);
		int[] fixes={0,0,0,0};
		double c2=0.0f;
		int iterations=0;

		double[] stats=new double[2];
		double[][] constraints=getConstraints(xvals[0],params);

		pw.addPoints(xvals[0],new float[length],false);
		int series=pw.getNpts().length-1;
		float[] fit=runFit(xvals[0],yvals[0],params,stats,constraints,fixes);
		pw.updateSeries(fit,series,false);
		c2=(float)stats[1];
		iterations=(int)stats[0];
		/*if(errs){
			monte_carlo_errors_v2 mcerrs=new monte_carlow_errors_v2(this,0.0001,50,false,0.1);
			double[][] temp=mcerrs.geterrors(params,fixes,constraints,yvals[0],null,1000);
		}*/
		
		TextWindow outtable=jutils.selectTable("Gauss Fits");
		if(outtable==null){
			outtable=FitDialog.make_outtable("Gauss Fits",paramnames);
		}
		StringBuffer sb=new StringBuffer();
		sb.append(pw.getTitle());
		IJ.log("Chi Squared = "+(float)stats[1]); sb.append("\t"+(float)stats[1]);
		IJ.log("Iterations = "+(int)stats[0]); sb.append("\t"+(int)stats[0]);
		for(int i=0;i<params.length;i++){
			IJ.log(paramnames[i]+" = "+params[i]);  sb.append("\t"+(float)params[i]);
		}
		outtable.append(sb.toString());
	}

	public float[] runFit(float[] xvals,float[] yvals,double[] params,double[] stats,double[][] constraints,int[] fixes){
		tempx=new float[xvals.length];
		System.arraycopy(xvals,0,tempx,0,xvals.length);
		gf=new gausfunc();
		NLLSfit_v2 fitclass=new NLLSfit_v2(this,0.0001,50,0.1);
		float[] fit=fitclass.fitdata(params,fixes,constraints,yvals,null,stats,false);
		return fit;
	}

	public double[][] getConstraints(float[] xvals,double[] params){
		double[][] constraints=new double[2][4];
		constraints[0][0]=params[0]-0.5*params[3]; constraints[1][0]=params[3]+params[0];
		constraints[0][1]=(double)xvals[0]; constraints[1][1]=(double)xvals[xvals.length-1];
		constraints[0][2]=0.1*(double)(xvals[1]-xvals[0]); constraints[1][2]=(double)(xvals[xvals.length-1]-xvals[0]);
		constraints[0][3]=0.0; constraints[1][3]=10.0*params[3];
		return constraints;
	}

	public double[] guessParams(float[] xvals,float[] yvals,int length){
		float min=yvals[0];
		int maxloc=0;
		float max=yvals[0];
		for(int i=1;i<length;i++){
			if(yvals[i]<min){min=yvals[i];}
			if(yvals[i]>max){max=yvals[i]; maxloc=i;}
		}
		float halfmax=(max+min)/2.0f;
		float fwhm=0.0f;
		for(int i=(maxloc+1);i<length;i++){
			if(yvals[i]<halfmax){
				fwhm+=0.5f*(xvals[i]-xvals[maxloc]);
				break;
			}
			if(i==(length-1)){
				fwhm+=0.5f*(xvals[i]-xvals[maxloc]);
			}
		}
		for(int i=(maxloc-1);i>=0;i--){
			if(yvals[i]<halfmax){
				fwhm+=0.5f*(xvals[maxloc]-xvals[i]);
				break;
			}
			if(i==0){
				fwhm+=0.5f*(xvals[maxloc]-xvals[i]);
			}
		}

		//parameters are baseline,xc1,stdev1,amp1
		double[] params={(double)min,(double)xvals[maxloc],(double)fwhm/2.35,(double)(max-min)};
		return params;
	}

	/*public double fitfunc(double[] params,int indvar){
		//parameters are baseline,xc1,stdev1,amp1
		double retval=params[0];
		for(int i=0;i<1;i++){
			if(params[3+i*3]>0.0){
				double xshift2=(params[1+i*3]-tempx[indvar])*(params[1+i*3]-tempx[indvar]);
				retval+=params[3+i*3]*Math.exp(-xshift2/(2.0*params[2+i*3]*params[2+i*3]));
			}
		}
		return retval;
	}*/

	public double[] fitfunc(double[] params){
		//parameters are baseline,xc1,stdev1,amp1
		double[] func=new double[tempx.length];
		for(int i=0;i<tempx.length;i++){
			func[i]=params[0]+params[3]*gf.getinterpgaus(Math.abs(tempx[i]-params[1]),params[2]);
		}
		return func;
	}

	public void showresults(String results){
		IJ.log(results);
	}
}
