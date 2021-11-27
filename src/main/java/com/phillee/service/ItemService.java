package com.phillee.service;

import com.phillee.pojo.Item;

import java.util.List;

/**
 * @Description:
 * @Author: PhilLee
 * @Date: 2021/11/26 22:59
 */
public interface ItemService {

    /**
     * @Description: 保存商品
     * @Param: [com.phillee.pojo.Item]
     * @Return: void
     */
    public void save(Item item);

    /**
     * @Description: 根据条件查询商品
     * @Param: [com.phillee.pojo.Item]
     * @Return: java.util.List<com.phillee.pojo.Item>
     */
    public List<Item> findAll(Item item);

}
