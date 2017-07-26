package com.jinxin.jxsmarthome.entity;

import java.util.List;

public class ProductConnectionVO extends Base {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3994559395283825141L;
	
	private boolean isSelected = false;
	private boolean isOpen = false;
	private ProductFun productFun = null;
	private ProductConnection productConnection = null;
	private List<ProductConnection> connections=null;
	public ProductConnectionVO() {
		this(false,false,null,null);
	}
	
	public ProductConnectionVO(ProductFun productFun) {
		this(false,false,productFun,null);
	}

	public ProductConnectionVO(boolean isSelected, boolean isOpen,
			ProductFun productFun, ProductConnection productConnection) {
		super();
		this.isSelected = isSelected;
		this.isOpen = isOpen;
		this.productFun = productFun;
		this.productConnection = productConnection;
	}

	public ProductConnectionVO(boolean isSelected, boolean isOpen,
			ProductFun productFun, ProductConnection productConnection,List<ProductConnection> connections) {
		super();
		this.isSelected = isSelected;
		this.isOpen = isOpen;
		this.productFun = productFun;
		this.productConnection = productConnection;
		this.connections=connections;
	}
	
	public List<ProductConnection> getConnections() {
		return connections;
	}

	public void setConnections(List<ProductConnection> connections) {
		this.connections = connections;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isOpen() {
		return isOpen;
	}

	@Override
	public String toString() {
		return "ProductConnectionVO [isSelected=" + isSelected + ", isOpen=" + isOpen + ", productFun=" + productFun
				+ ", productConnection=" + productConnection + ", connections=" + connections + "]";
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public ProductFun getProductFun() {
		return productFun;
	}

	public void setProductFun(ProductFun productFun) {
		this.productFun = productFun;
	}

	public ProductConnection getProductConnection() {
		return productConnection;
	}

	public void setProductConnection(ProductConnection productConnection) {
		this.productConnection = productConnection;
	}
	
}
