package com.demodeller.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demodeller.common.Result;
import com.demodeller.common.R;
import com.demodeller.entity.Domain;
import com.demodeller.service.IDomainService;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/")
public class DomainController {
    @Autowired
    private IDomainService domainService;

    @ResponseBody
    @RequestMapping(value = "/getAllDomain", method = {RequestMethod.GET})
    @ApiOperation("获取领域列表")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage, @RequestParam(defaultValue = "5") Integer pageSize,
                       @RequestParam(defaultValue = "") String name) {
        QueryWrapper<Domain> queryWrapper = new QueryWrapper<Domain>().orderByAsc("id");
        //分页
        //开启分页功能
        if (currentPage > 0 && pageSize > 0){
            PageHelper.startPage(currentPage, pageSize);
        }
//        Page<Domain> page = new Page<>(currentPage,pageSize);
        queryWrapper.like("name", name);
        List<Domain> domainList = domainService.list(queryWrapper);
        //使用PageInfo包装查询后的结果
//        PageInfo pageInfo = new PageInfo<>(page);
        PageInfo pageInfo = new PageInfo(domainList);
//        IPage<Domain> pageInfo = domainService.list(queryWrapper);
//        pageInfo.setList(pageInfo);
//        Page<Domain> page = domainService.page(new Page(currentPage, pageSize), queryWrapper);
        System.out.println(pageInfo);
        return Result.succ(pageInfo);
    }

    //添加
    @ResponseBody
    @RequestMapping(value = "/createDomain",method = {RequestMethod.POST})
    @ApiOperation("创建领域")
    public R<String> createDomain(@RequestBody Domain domain) {
        R<String> result = new R<String>();
        try {
            System.out.println(domain);
            domainService.createDomain(domain);
            result.code = 200;
            result.setMsg("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteDomain",method = {RequestMethod.POST})
    @ApiOperation("刪除领域")
    public R<String> deleteDomain(Integer id) {
        R<String> result = new R<String>();
        try {
            System.out.println(id);
            domainService.deleteDomain(id);
            result.code = 200;
            result.setMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }


}
