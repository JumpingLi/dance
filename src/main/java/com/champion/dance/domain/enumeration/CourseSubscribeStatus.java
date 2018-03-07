package com.champion.dance.domain.enumeration;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/28 11:02
 * Description：
 */
public enum CourseSubscribeStatus {
    SUBSCRIBED,//已预约
    CANCEL_SUBSCRIBE,//已取消预约
    USED,//过了上课时间未取消则默认上课
    COMMENTED,//已评价
}
