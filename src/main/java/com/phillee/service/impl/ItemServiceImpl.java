package com.phillee.service.impl;

import com.phillee.dao.ItemDao;
import com.phillee.pojo.Item;
import com.phillee.service.ItemService;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description:
 * @Author: PhilLee
 * @Date: 2021/11/26 23:06
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Resource
    private ItemDao itemDao;

    @Override
    public void save(Item item) {
        this.itemDao.save(item);
    }

    @Override
    public List<Item> findAll(Item item) {

        Example<Item> example = Example.of(item);

        return this.itemDao.findAll(example);
    }
}
