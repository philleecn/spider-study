package com.phillee.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


/**
 * @Description: 爬虫工具类
 * @Author: PhilLee
 * @Date: 2021/11/26 23:14
 */
@Slf4j
@Component
public class HttpUtils {

    //连接池
    private PoolingHttpClientConnectionManager connectionManager;

    public HttpUtils() {
        this.connectionManager = new PoolingHttpClientConnectionManager();
        //设置最大连接数
        this.connectionManager.setMaxTotal(100);
        //设置每个主机的最大连接数
        this.connectionManager.setDefaultMaxPerRoute(10);
    }

    /**
     * @Description: 根据请求地址获取页面数据
     * @Param: [java.lang.String]
     * @Return: java.lang.String
     */
    public String doGetHTML(String url) {

        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(this.connectionManager).build();

        HttpGet httpGet = new HttpGet(url);

        //设置请求信息
        httpGet.setConfig(getConfig());

        //设置请求头
        setHeaders(httpGet);

        CloseableHttpResponse response = null;

        try {
            //发起请求 获取响应
            response = httpClient.execute(httpGet);

            //解析响应 返回结果
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (response.getEntity() != null) {
                    return EntityUtils.toString(response.getEntity());
                }
                log.info("返回响应结果");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert response != null;
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * @Description: 下载图片
     * @Param: [java.lang.String]
     * @Return: java.lang.String
     */
    public String doGetImage(String url) {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(getConfig());
        setHeaders(httpGet);

        CloseableHttpResponse response = null;

        try {
            response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (response.getEntity() != null) {
                    //下载图片
                    //获取图片的后缀
                    String extName = url.substring(url.lastIndexOf("."));
                    //创建图片名称
                    String picName = UUID.randomUUID() + extName;
                    //保存
                    FileOutputStream outputStream = new FileOutputStream("/Users/phillee/work/code/study/spider/src/main/resources/static.images" + picName);
                    response.getEntity().writeTo(outputStream);
                    log.info("图片已保存为" + picName + extName);
                    return picName;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert response != null;
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private void setHeaders(HttpGet httpGet) {
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
    }

    private RequestConfig getConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(1000)            //创建连接的最长时间
                .setConnectionRequestTimeout(500)   //获取连接的最长时间
                .setSocketTimeout(10000)            //数据传输的最长时间
                .build();
    }
}
