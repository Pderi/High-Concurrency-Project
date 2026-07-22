package com.hc.ticket.framework.common.util.object;

import com.hc.ticket.framework.common.pojo.PageResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 对象转换工具（对齐 CRUD 规范 BeanUtils.toBean）
 */
public final class BeanUtils {

    private BeanUtils() {
    }

    public static <T> T toBean(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            org.springframework.beans.BeanUtils.copyProperties(source, target);
            return target;
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Bean 转换失败: " + targetClass.getName(), ex);
        }
    }

    public static <S, T> List<T> toBean(List<S> source, Class<T> targetClass) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(source.size());
        for (S item : source) {
            result.add(toBean(item, targetClass));
        }
        return result;
    }

    public static <S, T> PageResult<T> toBean(PageResult<S> page, Class<T> targetClass) {
        if (page == null) {
            return PageResult.empty();
        }
        return new PageResult<>(toBean(page.getList(), targetClass), page.getTotal());
    }
}
