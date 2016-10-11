package com.sunzhk.tools.utils.tree;

import java.util.ArrayList;

/**
 * 树的节点
 * @author sunzhk
 *
 * @param <T>
 */
public class Node<T> {

	private int mDepth;
	
	private Node<?> mParent;
	
	private ArrayList<Node<?>> mChilds = null;
	
	private T data;
	
	public Node(int depth, T data) {
		// TODO Auto-generated constructor stub
		this.mDepth = depth;
		this.data = data;
		
	}
	
	public void addChild(Node<?> child){
		if(mChilds == null){
			mChilds = new ArrayList<Node<?>>();
		}
		mChilds.add(child);
		child.setParent(this);
	}
	/**
	 * 返回子节点数组，若没有则返回null
	 * @return
	 */
	public Node<?>[] getChilds(){
		return mChilds == null ? null : (Node<?>[]) mChilds.toArray();
	}
	/**
	 * 去掉一个子节点
	 * @param child
	 */
	public boolean removeChild(Node<?> child){
		if(mChilds == null){
			return true;
		}
		child.setParent(null);
		return mChilds.remove(child);
	}
	/**
	 * 从父节点上脱离
	 */
	public void clearParent(){
		if(mParent == null){
			return;
		}
		mParent.removeChild(this);
	}
	/**
	 * 获得父节点
	 * @return
	 */
	public Node<?> getParent(){
		return mParent;
	}
	/**
	 * 设置父节点，可以为null。若已有父节点则会抛出异常
	 * @param parent
	 */
	public void setParent(Node<?> parent){
		if(parent == null){
			mParent = null;
			return;
		}
		if(mParent != null){
			throw new RuntimeException("this node is already had a parent!");
		}
		mParent = parent;
	}
	
	
	public boolean isRoot(){
		return mParent != null;
	}
	public boolean hasChild(){
		return mChilds == null ? false : mChilds.isEmpty();
	}
	public int getChildCount(){
		return mChilds == null ? 0 : mChilds.size();
	}
	public int getDepth(){
		return mDepth;
	}
	public void setDepth(int depth){
		this.mDepth = depth;
	}
	public void setDate(T data){
		this.data = data;
	}
	public T getData(){
		return data;
	}
}
