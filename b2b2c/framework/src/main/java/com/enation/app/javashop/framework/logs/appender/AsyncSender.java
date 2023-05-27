package com.enation.app.javashop.framework.logs.appender;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;

/**
 * @Description : 异步推送Elasticsearch
 * @Author snow
 * @Date: 2020-01-09 16:03
 * @Version v1.0
 */
public class AsyncSender extends Thread {
	/** 最大队列数 */
	private final static int MAX_CAPACITY = 65536;
	private static BlockingQueue<EsLog> queue = new LinkedBlockingQueue<EsLog>(MAX_CAPACITY);
	private ElasticsearchTemplate esTemplate;
	private Logger logger = LoggerFactory.getLogger(AsyncSender.class);

	/**
	 * 初始化
	 * @param esTemplate
	 */
	public AsyncSender(ElasticsearchTemplate esTemplate) {
		this.esTemplate = esTemplate;
	}

	private static int retryTime = 0;
	
	@Override
	public void run() {
		while (true) {
			try {
				sendLog(queue.take());
				retryTime=0;
			} catch (Exception e) {
				retryTime++;
				e.printStackTrace();

				//重试3次失败，
				if (retryTime > 2) {
					queue.remove();
					break;
				}

			}
		}
	}
	
	public static void put(EsLog log) {
			queue.offer(log);
	}

	/**
	 * 发送日志
	 * @param log
	 */
	private void sendLog(EsLog log) {
		IndexQuery indexQuery = new IndexQueryBuilder().withId(log.getId())
				.withObject(log).build();
		if (!esTemplate.indexExists(EsLog.class)) {
			esTemplate.createIndex(EsLog.class);
			esTemplate.putMapping(EsLog.class);
		}
		esTemplate.index(indexQuery);
	}
}