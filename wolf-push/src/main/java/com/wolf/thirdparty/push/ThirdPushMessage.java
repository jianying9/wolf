package com.wolf.thirdparty.push;

import java.util.Map;

/**
 *
 * @author jianying9
 */
public interface ThirdPushMessage {

    public boolean isValid();
    
    public boolean isSound();
    
    public int getNotifyId();
    
    public String getTitle();

    public String getContent();
    
    public Long getCount();

    public Map<String, Object> getExtendMap();
    
    

}
