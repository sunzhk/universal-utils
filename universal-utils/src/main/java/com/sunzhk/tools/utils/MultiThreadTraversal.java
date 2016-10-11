package com.sunzhk.tools.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * 多线程简单遍历，也许用Rxjava重写一次比较好
 * @author sunzhk
 *
 */
public class MultiThreadTraversal {
	/**
	 * 线程池大小
	 */
	private static final int POOL_SIZE = 6;

	private ThreadPoolExecutor pool = null;
	/**
	 * 任务队列
	 */
	private LinkedBlockingQueue<Runnable> queue;
	/**
	 * 运行状态
	 */
	private boolean runningFlag;

	public MultiThreadTraversal() {
		queue = new LinkedBlockingQueue<Runnable>();

		pool = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE, 0L, TimeUnit.MILLISECONDS, queue) {
			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				if (pool.getActiveCount() == 0){
					runningFlag = false;
				}
			};
		};

	}
	/**
	 * 启动一个任务。当已有任务在运行时会抛出异常，参数均不能为空
	 * @param rootNode	起始数据
	 * @param filter	过滤器
	 */
	public <T> void exce(T rootNode, AnalyzerFilter<T> filter){
		if(rootNode == null ||filter == null){
			throw new NullPointerException("rootNode and filter cannot be null.");
		}
		if(runningFlag == true){
			throw new RuntimeException("The task is running.Check the state by use isRunning()");
		}
		pool.execute(new Analyzer<T>(rootNode, filter));
		runningFlag = true;
	}
	/**
	 * 获取当前运行状态
	 * @return
	 */
	public boolean isRunning(){
		return runningFlag;
	}
	/**
	 * 分析器
	 * @author sunzhk
	 *
	 * @param <T>
	 */
	class Analyzer<T> implements Runnable {

		private T rootNode;
		private AnalyzerFilter<T> filter;
		
		public Analyzer(T rootNode, AnalyzerFilter<T> filter) {
			this.rootNode = rootNode;
			this.filter = filter;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			T[] childs = filter.getChild(rootNode);
			
			if (childs == null) {
				return;
			}
			
			for (T child : childs) {
				if (child == null) {
					continue;
				}
				
				filter.process(child);
				
				if (filter.hasChild(child)) {
					pool.execute(new Analyzer<T>(child, filter));
				}
			}
			if (pool.getActiveCount() == 1){
				filter.onFinished();
			}
		}
	}
	/**
	 * 过滤器
	 * @author sunzhk
	 *
	 * @param <T>
	 */
	public interface AnalyzerFilter<T>{
		/**
		 * 需要返回这个节点的子节点
		 * @param rootNode
		 * @return
		 */
		T[] getChild(T rootNode);
		/**
		 * 需要返回这个节点是否有子节点
		 * @param rootNode
		 * @return
		 */
		boolean hasChild(T rootNode);
		/**
		 * 对每个节点进行处理
		 * @param node
		 */
		void process(T node);
		/**
		 * 所有节点遍历完成后执行
		 */
		void onFinished();
	}
	
	
}
