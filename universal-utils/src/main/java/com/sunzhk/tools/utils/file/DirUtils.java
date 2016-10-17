package com.sunzhk.tools.utils.file;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 文件夹遍历处理
 * @deprecated
 */
public class DirUtils {

    private final static int POOL_SIZE = 6;

    private static ThreadPoolExecutor pool = null;

    private static LinkedBlockingQueue<Runnable> queue;

    private OnTraverseDirListener listener;

    public DirUtils(){
        if(queue == null){
            queue = new LinkedBlockingQueue<>();
        }
        if(pool == null){
            pool = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE , 0L, TimeUnit.MILLISECONDS, queue);
        }
    }
    /**
     * 开始扫描
     * @param path 要扫描的文件夹路径
     * @param listener
     */
    public void start(String path, OnTraverseDirListener listener){
        start(new File(path), listener);
    }

    /**
     * 开始扫描
     * @param dir 要扫描的文件夹
     * @param listener
     */
    public void start(File dir, OnTraverseDirListener listener){
        if(dir == null || !dir.exists() || !dir.isDirectory()){
            throw new RuntimeException("The dir cannot be null or its a file!");
        }
        if(listener == null){
            throw new RuntimeException("The listener cannot be null!");
        }
        this.listener = listener;

        listener.onTraversing(dir);
        pool.execute(new TraverseDirRunnable(dir));

    }

    public static void calculateDirSize(File dir, final OnTraverseDirListener listener){
        new DirUtils().start(dir, new OnTraverseDirListener() {
            @Override
            public void onTraversing(File file) {

            }

            @Override
            public <E> void afterTraversing(E... result) {
                listener.afterTraversing(result);
            }
        });
    }

    final class TraverseDirRunnable implements  Runnable {

        File root ;

        public TraverseDirRunnable(File file){
            this.root = file;
        }

        public final void run() {

            File[] files = root.listFiles();

            if(files == null || files.length == 0){
                return;
            }

            for(File file : files){
                listener.onTraversing(file);
                if (file.isDirectory() ) {
                    String lists[] = file.list();
                    if(lists != null && lists.length > 0)
                        pool.execute(new TraverseDirRunnable(file));
                }
            }
        }
    }

    public interface OnTraverseDirListener{

        void onTraversing(File file);

        <E> void afterTraversing(E... result);
    }

}
