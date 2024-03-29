package com.lion.demo.provider.temp.controller;

import com.lion.common.base.controller.BaseController;
import com.lion.common.entity.Result;
import com.lion.demo.provider.temp.entity.TempTransactional;
import com.lion.demo.provider.temp.service.TempTransactionalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * TempTransactionalController
 * TODO
 *
 * @author Yanzheng
 * @date 2019/10/23
 * Copyright 2019 Yanzheng. All rights reserved.
 */
@Api("Provider分布式事务示例模块")
@RestController
@RequestMapping("/temp/transactional")
@Slf4j
public class TempTransactionalController extends BaseController {

    @Autowired
    private TempTransactionalService tempTransactionalService;

    @ApiOperation(value = "Provider分布式事物测试方法", notes = "当 num > 3 时触发事务回滚")
    @ApiParam(name = "num", value = "插入数据条数", defaultValue = "3", required = true)
    @RequestMapping(value = "/save/{num}", method = {RequestMethod.GET, RequestMethod.POST})
    @Transactional
    public Result save(@PathVariable int num) {

        if (0 >= num) {
            return Result.failure("[num] 参数不正确，取值范围必须大于 0 的整数（例：/save/3）");
        }

        for (int i = 0; i < num; i++) {

            String randomStr = Math.ceil(Math.random() * 100) + "";

            TempTransactional tempTransactional = new TempTransactional();
            tempTransactional.setName("ProviderName-" + randomStr);
            tempTransactional.setCreatetime(new Date());

            if (i + 1 < 4) {
                //正常插入
                tempTransactional.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            } else {
                //超出范围长度，触发事物回滚
                tempTransactional.setId(UUID.randomUUID().toString());
            }

            // 若使用 Try Catch 需要手动回滚事务：TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            tempTransactionalService.insert(tempTransactional);
        }

        return Result.success("provider -> temp_transactional 数据保存成功，执行条数：" + num);
    }

}
