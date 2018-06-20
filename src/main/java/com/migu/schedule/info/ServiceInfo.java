package com.migu.schedule.info;

import java.io.Serializable;

/**
 * 服务信息
 *
 * @author wangyaxuan
 * @version C10 2018年6月20日
 * @since SDP V300R003C10
 */
public class ServiceInfo implements Serializable
{
    
    /**
     * UID
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 埋点的id
     */
    private String uuid;
        
    /**
     * 取得uuid
     * 
     * @return 返回uuid。
     */
    public String getUuid()
    {
        return uuid;
    }
    
    /**
     * 设置uuid
     * 
     * @param uuid 要设置的uuid。
     */
    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }
    
}
