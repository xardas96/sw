package ar.models;

import javax.media.j3d.BranchGroup;

public class ArModel {
	private BranchGroup model;
	private int markerCode;
	private boolean rendered;
	
	public ArModel(BranchGroup model) {
		this.model = model;
	}
	
	public void setMarkerCode(int markerCode) {
		this.markerCode = markerCode;
	}
	
	public int getMarkerCode() {
		return markerCode;
	}
	
	public BranchGroup getModel() {
		return model;
	}
	
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}
	
	public boolean isRendered() {
		return rendered;
	}
}