package com.hc.ticket.module.tkt.dal.mysql.show;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.framework.mybatis.core.mapper.BaseMapperX;
import com.hc.ticket.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hc.ticket.module.tkt.controller.admin.show.vo.ShowPageReqVO;
import com.hc.ticket.module.tkt.controller.app.show.vo.AppShowPageReqVO;
import com.hc.ticket.module.tkt.dal.dataobject.show.ShowDO;
import com.hc.ticket.module.tkt.enums.ShowStatusEnum;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShowMapper extends BaseMapperX<ShowDO> {

    default PageResult<ShowDO> selectPage(ShowPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ShowDO>()
                .likeIfPresent(ShowDO::getName, reqVO.getName())
                .eqIfPresent(ShowDO::getStatus, reqVO.getStatus())
                .orderByDesc(ShowDO::getSort)
                .orderByDesc(ShowDO::getId));
    }

    /** C 端：仅已发布 */
    default PageResult<ShowDO> selectAppPage(AppShowPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ShowDO>()
                .likeIfPresent(ShowDO::getName, reqVO.getName())
                .eq(ShowDO::getStatus, ShowStatusEnum.PUBLISHED.getStatus())
                .orderByDesc(ShowDO::getSort)
                .orderByDesc(ShowDO::getId));
    }
}
