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
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.*;
import ome.xml.model.primitives.*;
import java.io.*;
import jguis.*;

public class loci_pw_reader_jru_v1 extends FormatReader implements ImageListener {
	public String path;
	public Object plot;
	public int plotindex;

	public loci_pw_reader_jru_v1(){
		super("Plot Window",new String[]{"pw","pw2"});
		suffixSufficient=true;
		suffixNecessary=true;
		domains=new String[]{FormatTools.UNKNOWN_DOMAIN};
	}

	public boolean isThisType(RandomAccessInputStream stream) throws IOException{
		return false;
	}

	public byte[] openBytes(int no,byte[] buf,int x,int y,int w,int h){
		//for(int i=0;i<buf.length;i++) buf[i]=(byte)255;
		return buf;
	}

	public void close(boolean fileOnly) throws IOException{
		super.close(fileOnly);
		plot=null;
		path=null;
	}

	protected void initFile(String id) throws FormatException, IOException{
		super.initFile(id);
		path=Location.getMappedId(id);
		//IJ.log(path);
		//CoreMetadata cm=core.get(0);
		//CoreMetadata cm=super.getCoreMetadataList().get(0);
		CoreMetadata cm=getCoreMetadata()[0];
		cm.sizeX=Plot4.WIDTH+Plot4.LEFT_MARGIN+Plot4.RIGHT_MARGIN;
		cm.sizeY=Plot4.HEIGHT+Plot4.BOTTOM_MARGIN+Plot4.TOP_MARGIN;
		cm.sizeZ=1;
		cm.sizeC=1;
		cm.sizeT=1;
		cm.imageCount=1;
		cm.dimensionOrder="XYCZT";
		cm.rgb=true;
		cm.interleaved=true;
		cm.littleEndian=true;
		cm.indexed=false;
		cm.falseColor=false;
		cm.metadataComplete=true;
		cm.pixelType=FormatTools.UINT32;
		cm.thumbnail=false;
		cm.bitsPerPixel=32;
		MetadataStore store = makeFilterMetadata();
		MetadataTools.populatePixels(store, this);
		store.setImageName(path,0);
		store.setPixelsPhysicalSizeX(new PositiveFloat(1.0),0);
		store.setPixelsPhysicalSizeY(new PositiveFloat(1.0),0);
		store.setPixelsPhysicalSizeZ(new PositiveFloat(1.0),0);
		store.setPixelsTimeIncrement(1.0,0);
		if(path.endsWith(".pw") || Plot4.is_this(path)){
			plot=new Plot4(path); plotindex=0;
		} else if(Plot3D.is_this(path)){
			plot=new Plot3D(path); plotindex=1;
		} else if(Traj3D.is_this(path)){
			plot=new Traj3D(path); plotindex=2;
		} else if(PlotHist.is_this(path)){
			plot=new PlotHist(path); plotindex=3;
		} else if(Plot2DHist.is_this(path)){
			plot=new Plot2DHist(path); plotindex=4;
		} else {
			plot=null;
			throw new FormatException("unsupported or corrupted pw2 file");
		}
		if(plot!=null) ImagePlus.addImageListener(this);
		//IJ.runPlugIn("import_plot_jru_v1",path);
	}

	public void imageOpened(ImagePlus imp){
		String dir=imp.getOriginalFileInfo().directory;
		String tpath=dir+imp.getTitle();
		//IJ.log(tpath);
		if(tpath.equalsIgnoreCase(path)){
			if(plot instanceof Plot4){
				new PlotWindow4(imp,(Plot4)plot).draw();
			} else if(plot instanceof Plot3D){
				new PlotWindow3D(imp,(Plot3D)plot).draw();
			} else if(plot instanceof PlotHist){
				new PlotWindowHist(imp,(PlotHist)plot).draw();
			} else {
				new PlotWindow2DHist(imp,(Plot2DHist)plot).draw();
			}
			ImagePlus.removeImageListener(this);
		}
	}

	public void imageClosed(ImagePlus imp){}

	public void imageUpdated(ImagePlus imp){}

}
