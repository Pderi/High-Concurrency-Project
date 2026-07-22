package com.hc.ticket.framework.mybatis.core.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hc.ticket.framework.common.pojo.PageParam;
import com.hc.ticket.framework.common.pojo.PageResult;

/**
 * 扩展 BaseMapper，提供分页便捷方法
 */
public interface BaseMapperX<T> extends BaseMapper<T> {

    default PageResult<T> selectPage(PageParam pageParam, Wrapper<T> queryWrapper) {
        IPage<T> page = selectPage(new Page<>(pageParam.getPageNo(), pageParam.getPageSize()), queryWrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }
}
