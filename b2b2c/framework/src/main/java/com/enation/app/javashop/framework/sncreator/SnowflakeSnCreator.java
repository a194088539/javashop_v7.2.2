package com.enation.app.javashop.framework.sncreator;

import com.enation.app.javashop.framework.context.instance.AppInstance;
import org.springframework.stereotype.Service;

/**
 * Snowflake 实现的发号器
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-11-22
 */

@Service
public class SnowflakeSnCreator implements SnCreator {


    /**
     *最大递增序列
     */
    private static final long MAX_SEQ = 4000L;

    /**
     * 起始时间戳
     */
    private static final long START_TIME = 1587092627355L;

    private static long COUNT =0;

    /**
     * 获取递增序列
     * @return
     */
    private  synchronized long getSequence() {
        COUNT++;

        if (COUNT > MAX_SEQ) {
            COUNT =0;
        }

        return COUNT;
    }


    @Override
    public Long create(int subCode) {

        long workerId = AppInstance.getInstance().getWorkId();

        /**
         *当前数据：
         *十进制：1587092627355
         *二进制：
         * |      时间戳      |
         * |______41位_______|
         */
        long v = System.currentTimeMillis() - START_TIME;
        Long sequence = getSequence();

        /**
         *当前数据：
         *十进制：时间戳 + subid (subid 不能大于二进制的10个1，也就是1023）
         *二进制：
         * |      时间戳      ||      子业务id    |
         * |______41位_______| |______5位_______|
         */
        v = v << 5;
        v = v + subCode;

        /**
         *当前数据：
         *十进制：时间戳 + subid+  机器id (机器id 不能大于二进制的10个1，也就是1023）
         *二进制：
         * |      时间戳      ||      子业务id   |      机器id    |
         * |______41位_______| |______5位_______|______5位_______|
         */
        v = v << 5;
        v = v + workerId;


        /**
         *当前数据：
         *十进制：时间戳 + subid+  workid + sequence (sequence 不能大于二进制的12个1，也就是4095）
         *二进制：
         * |      时间戳      |      子业务id  |     work id    |     sequence   |
         * |______41位_______|______5位_______|______5位_______|______10 _______|
         */
        v = v << 12;

        v = v + sequence;

        return  v;
    }



    public static void main(String[] args) {
        SnowflakeSnCreator snowflakeSnCreator = new SnowflakeSnCreator();

        for (int i = 0; i < 100; i++) {
            Long sn = snowflakeSnCreator.create(2);
            System.out.println(sn);
        }

    }


}
