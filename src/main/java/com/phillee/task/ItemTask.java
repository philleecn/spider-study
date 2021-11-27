package com.phillee.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phillee.pojo.Item;
import com.phillee.service.ItemService;
import com.phillee.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description: 爬取任务
 * @Author: PhilLee
 * @Date: 2021/11/27 14:01
 */
@Slf4j
@Component
public class ItemTask {

    @Resource
    private HttpUtils httpUtils;

    @Resource
    private ItemService itemService;

    private static final ObjectMapper mapper = new ObjectMapper();

    //等下载任务结束后，间隔多长时间进行下次任务
    @Scheduled(fixedDelay = 100 * 1000)
    public void itemTask() throws JsonProcessingException {
        //需要解析的初始地址
        String url = "https://search.jd.com/Search?" +
                "keyword=java&enc=utf-8&wq=java" +
                "&pvid=0a152d0ece6c4a2ab36c2da6c503fdd9?page=";

        //按照页面对搜索结果进行解析
        for (int i = 1; i < 2; i = i + 2) {
            String html = httpUtils.doGetHTML(url + 1);
            //解析页面
            this.parse(html);
        }
        log.info("数据抓取完成");
    }

    private void parse(String html) throws JsonProcessingException {
        //解析html
        Document document = Jsoup.parse(html);
        //获取spu即商品品类信息
        Elements spuElements = document.select("div#J_goodsList > ul > li");

        for (Element spuElement : spuElements) {
            //排除没有data-spu的值的广告
            if (StringUtils.isEmpty(spuElement.attr("data-spu"))) {
                //获取spu
                /*这里第一个spu可能是空的，所以判断一下做跳过处理*/
                if (StringUtils.isEmpty(spuElement.attr("data-spu"))) {
                    continue;
                }
                long spu = Long.parseLong(spuElement.attr("data-spu"));
                //获取sku信息
                Elements skuElements = spuElement.select("li.ps-item");
                for (Element skuElement : skuElements) {
                    //获取sku
                    long sku = Long.parseLong(skuElement.select("[data-sku]").first().attr("data-sku"));
                    //根据sku查询商品信息
                    Item item = new Item();
                    item.setSku(sku);
                    List<Item> itemList = itemService.findAll(item);
                    if (itemList.size() > 0) {
                        continue;
                    }
                    //设置商品的spu
                    item.setSpu(spu);
                    //获取商品详情的url
                    item.setUrl("https://item.jd.com/" + sku + ".html");
                    //获取商品图片
                    String picUrl = "https:" + skuElement.select("img[data-sku]").first().attr("data-lazy-img");
                    picUrl = picUrl.replace("/n7/", "/n0/");
                    String image = httpUtils.doGetImage(picUrl);
                    item.setPic(image);
                    log.info("商品图片" + image);
                    //获取商品价格
                    String priceJson = this.httpUtils.doGetHTML("https://p.3.cn/prices/mgets?skuIds=J_" + sku);
                    double price = mapper.readTree(priceJson).get(0).get("p").asDouble();
                    item.setPrice(price);
                    // 获取商品的标题
                    String itemInfo = this.httpUtils.doGetHTML(item.getUrl());
                    String title = Jsoup.parse(itemInfo).select("div.sku-name").text();
                    item.setTitle(title);
                    item.setCreated(new Date());
                    item.setUpdated(item.getCreated());

                    // 保存商品数据到数据库中
                    this.itemService.save(item);
                }
            }
        }
    }
}
