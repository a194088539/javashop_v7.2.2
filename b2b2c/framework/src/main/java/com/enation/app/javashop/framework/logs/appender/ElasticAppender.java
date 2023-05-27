package com.enation.app.javashop.framework.logs.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.enation.app.javashop.framework.context.instance.AppInstance;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.net.InetAddress;
import java.util.Date;

import static org.apache.commons.lang.StringUtils.*;

/**
 * @Description : Elasticsearch appender for logback
 * @Author snow
 * @Date: 2020-01-09 16:03
 * @Version v1.0
 */
public class ElasticAppender extends AppenderBase<ILoggingEvent> {
	private static Object locker = new Object();

	/** ES cluster-name */
	private String clusterName;
	/** ES cluster-nodes */
	private String clusterNodes;
	/** 服务名 */
	private String appName = "default";
	/** xpack 用户名密码 */
	private String xpackUser;


	@Override
	public void start() {
		synchronized(locker) {
			if (isStarted()) {
				return;
			}
			super.start();

			try {
                Settings.Builder settings = Settings.builder();
                //设置cluster.name
				settings.put("cluster.name", clusterName);
				//如果x-pack权限不为空
				if(xpackUser != null){
                    settings.put("xpack.security.user", xpackUser);
                }
                //settings.put("client.transport.sniff", true);
                //settings.put("client.transport.ignore_cluster_name", Boolean.FALSE);
                //settings.put("client.transport.ping_timeout", "5s");
                //settings.put("client.transport.nodes_sampler_interval", "5s");

				PreBuiltTransportClient client = new PreBuiltTransportClient(settings.build());
				for (String clusterNode : split(clusterNodes, ",")) {
					String hostName = substringBeforeLast(clusterNode, ":");
					String port = substringAfterLast(clusterNode, ":");
					client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostName), Integer.valueOf(port)));
				}
				client.connectedNodes();
				ElasticsearchTemplate esTemplate = new ElasticsearchTemplate(client);

				if (!esTemplate.indexExists(EsLog.class)) {
					esTemplate.createIndex(EsLog.class);
					esTemplate.putMapping(EsLog.class);
				}

				AsyncSender asyncSender = new AsyncSender(esTemplate);
				asyncSender.start();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	protected void append(ILoggingEvent eo) {
		EsLog log = new EsLog();
		log.setAppName(appName);
		log.setId(System.currentTimeMillis() + "-" + System.nanoTime());
		log.setLevel(eo.getLevel().toString());
		log.setLoggerName(eo.getLoggerName());
		log.setMessage(eo.getFormattedMessage());
		log.setThreadName(eo.getThreadName());
		log.setLogTime(new Date(eo.getTimeStamp()));
		//单例
		AppInstance instance = AppInstance.getInstance();
		log.setInstance(instance.getUuid());

		AsyncSender.put(log);
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getClusterNodes() {
		return clusterNodes;
	}

	public void setClusterNodes(String clusterNodes) {
		this.clusterNodes = clusterNodes;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

    public String getXpackUser() {
        return xpackUser;
    }

    public void setXpackUser(String xpackUser) {
        this.xpackUser = xpackUser;
    }
}
