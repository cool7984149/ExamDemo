package com.migu.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.migu.schedule.constants.ReturnCodeKeys;
import com.migu.schedule.info.ServiceInfo;
import com.migu.schedule.info.TaskInfo;
import com.migu.schedule.info.TaskPool;

/*
 *类名和方法不能修改
 */
public class Schedule
{
    
    public int init()
    {
        TaskPool pool = TaskPool.getPool();
        pool.getInfos().clear();
        pool.getTaskpool().clear();
        return ReturnCodeKeys.E001;
    }
    
    public int registerNode(int nodeId)
    {
        if (nodeId < 0)
        {
            return ReturnCodeKeys.E004;
        }
        String nodeId_ = String.valueOf(nodeId);
        if (null != TaskPool.getPool().getInfos().get(nodeId_))
        {
            return ReturnCodeKeys.E005;
        }
        ServiceInfo info = new ServiceInfo();
        info.setUuid(UUID.randomUUID().toString());
        TaskPool.getPool().getInfos().put(nodeId_, 0);
        return ReturnCodeKeys.E003;
    }
    
    public int unregisterNode(int nodeId)
    {
        // 没有则返回
        if (null == TaskPool.getPool().getInfos().get(String.valueOf(nodeId)))
        {
            return ReturnCodeKeys.E007;
        }
        TaskPool.getPool().getInfos().remove(String.valueOf(nodeId));
        TaskInfo run = null;
        // 正在运行的话，任务队列应该有才对
        for (TaskInfo task : TaskPool.getPool().getTaskpool())
        {
            if (task.getNodeId() == nodeId)
            {
                run = task;
                break;
            }
        }
        
        if (null != run)
        {
            // 从正在运行的队列中移除
            TaskPool.getPool().getTaskpool().remove(run);
            // 添加到挂起队列
            TaskPool.getPool().getUnTaskpool().add(run);
        }
        return ReturnCodeKeys.E006;
    }
    
    public int addTask(int taskId, int consumption)
    {
        if (taskId <= 0)
        {
            return ReturnCodeKeys.E009;
        }
        String key = String.valueOf(taskId);
        // 任务统计池
        ConcurrentHashMap<String, Integer> taskTimes = TaskPool.getPool().getConsumptions();
        if (taskTimes.get(key) != null)
        {
            return ReturnCodeKeys.E010;
        }
        TaskInfo info = new TaskInfo();
        info.setTaskId(taskId);
        TaskPool.getPool().getUnTaskpool().add(info);
        TaskPool.getPool().getConsumptions().put(key, consumption);
        return ReturnCodeKeys.E008;
    }
    
    public int deleteTask(int taskId)
    {
        if (taskId < 0)
        {
            return ReturnCodeKeys.E009;
        }
        String key = String.valueOf(taskId);
        ConcurrentHashMap<String, Integer> taskTimes = TaskPool.getPool().getConsumptions();
        if (taskTimes.get(key) == null)
        {
            return ReturnCodeKeys.E012;
        }
        TaskInfo run = null;
        for (TaskInfo task : TaskPool.getPool().getUnTaskpool())
        {
            if (task.getTaskId() == taskId)
            {
                run = task;
                break;
            }
        }
        if (null != run)
        {
            TaskPool.getPool().getUnTaskpool().remove(run);
            taskTimes.remove(key);
        }
        return ReturnCodeKeys.E011;
    }
    
    public int scheduleTask(int threshold)
    {
        if (threshold <= 0)
        {
            return ReturnCodeKeys.E002;
        }
        
        int size = TaskPool.getPool().getUnTaskpool().size();
        if (size > 0)
        {
            try
            {
                while (TaskPool.getPool().getUnTaskpool().isEmpty())
                {
                    for(Entry<String, Integer> node1 : TaskPool.getPool().getInfos().entrySet())
                    {
                        for(Entry<String, Integer> node2 : TaskPool.getPool().getInfos().entrySet())
                        {
                            if(node1.getKey().equals(node2.getKey()) && Math.abs(node2.getValue() - node1.getValue()) > threshold)
                            {
                                return ReturnCodeKeys.E014;
                            }
                        }
                    }
                    
                    List<Map<Integer,String>> tempNode = new LinkedList<Map<Integer,String>>();
                    List<Integer> consumptionList = new LinkedList<Integer>();
                    for(Entry<String, Integer> node : TaskPool.getPool().getInfos().entrySet())
                    {
                        consumptionList.add(node.getValue());
                        Map<Integer,String> consumptionMap = new HashMap<Integer,String>();
                        consumptionMap.put(node.getValue(), node.getKey());
                        tempNode.add(consumptionMap);
                    }
                    Collections.sort(consumptionList);
                    TaskInfo info = TaskPool.getPool().getUnTaskpool().take();
                    int consumption = TaskPool.getPool().getConsumptions().get(info.getTaskId());
                    String nodeid = null;
                    for(Map<Integer,String> consumptionTemp : tempNode)
                    {
                        if(null != consumptionTemp.get(consumptionList.get(consumptionList.size() - 1)))
                        {
                            nodeid = consumptionTemp.get(consumptionList.get(consumptionList.size() - 1));
                        }
                    }
                    int temp = TaskPool.getPool().getInfos().get(nodeid);
                    TaskPool.getPool().getInfos().put(nodeid, temp + consumption);
                    info.setNodeId(Integer.parseInt(nodeid));
                    TaskPool.getPool().getTaskpool().add(info);
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        return ReturnCodeKeys.E013;
    }
    
    public int queryTaskStatus(List<TaskInfo> tasks)
    {
        if(null == tasks)
        {
            return ReturnCodeKeys.E016;
        }
        for(TaskInfo info : TaskPool.getPool().getTaskpool())
        {
            tasks.add(info);
        }
        for(TaskInfo info : TaskPool.getPool().getUnTaskpool())
        {
            info.setTaskId(-1);
            tasks.add(info);
        }
        return ReturnCodeKeys.E015;
    }
    
}
