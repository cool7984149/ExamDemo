package com.migu.schedule.info;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 任务池
 *
 * @author wangyaxuan
 * @version C10 2018年6月20日
 * @since SDP V300R003C10
 */
public class TaskPool
{
    public static int sum = 0;
        
    /**
     * 
     * 静态内部类，线程安全
     *
     * @author wangyaxuan
     * @version C10 2018年6月20日
     * @since SDP V300R003C10
     */
    public static final class TaskPoolInner
    {
        /**
         * 实例池
         */
        private static volatile TaskPool pool = new TaskPool();
        
        /**
         * 任务队列
         */
        private static volatile BlockingQueue<TaskInfo> taskpool = new LinkedBlockingDeque<TaskInfo>();
        
        /**
         * 任务队列
         */
        private static volatile BlockingQueue<TaskInfo> untaskpool = new LinkedBlockingDeque<TaskInfo>();
        
        /**
         * 服务信息
         */
        private static volatile ConcurrentHashMap<String, Integer> consumptions = new ConcurrentHashMap<String, Integer>();
        
        /**
         * 服务信息
         */
        private static volatile ConcurrentHashMap<String, Integer> infos = new ConcurrentHashMap<String, Integer>();
    }
    
    /**
     * 
     * 构造函数
     */
    private TaskPool()
    {
        
    }
    
    /**
     * 
     * 返回infos
     *
     * @author wangyaxuan
     * @return 返回infos
     */
    public ConcurrentHashMap<String, Integer> getConsumptions()
    {
        return TaskPoolInner.consumptions;
    }
    
    /**
     * 
     * 返回infos
     *
     * @author wangyaxuan
     * @return 返回infos
     */
    public ConcurrentHashMap<String, Integer> getInfos()
    {
        return TaskPoolInner.infos;
    }
    
        
    /**
     * 
     * 
     * 取得taskpool
     * @return 返回taskpool。
     */
    public BlockingQueue<TaskInfo> getTaskpool()
    {
        return TaskPoolInner.taskpool;
    }
    
    /**
     * 
     * 
     * 取得taskpool
     * @return 返回taskpool。
     */
    public BlockingQueue<TaskInfo> getUnTaskpool()
    {
        return TaskPoolInner.untaskpool;
    }

    /**
     * 
     * 获取单例
     *
     * @author wangyaxuan
     * @return
     */
    public static TaskPool getPool()
    {
        return TaskPoolInner.pool;
    }
}
