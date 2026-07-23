package com.hc.ticket.module.tkt.service.show;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.module.tkt.controller.admin.show.vo.ShowAddReqVO;
import com.hc.ticket.module.tkt.controller.admin.show.vo.ShowPageReqVO;
import com.hc.ticket.module.tkt.controller.admin.show.vo.ShowPageRespVO;
import com.hc.ticket.module.tkt.controller.admin.show.vo.ShowRespVO;
import com.hc.ticket.module.tkt.controller.admin.show.vo.ShowUpdateReqVO;
import com.hc.ticket.module.tkt.controller.app.show.vo.AppShowPageReqVO;
import com.hc.ticket.module.tkt.controller.app.show.vo.AppShowPageRespVO;
import com.hc.ticket.module.tkt.controller.app.show.vo.AppShowRespVO;
import com.hc.ticket.module.tkt.dal.dataobject.show.ShowDO;

/**
 * 演出领域服务
 */
public interface ShowService {

    Long createShow(ShowAddReqVO reqVO);

    void updateShow(ShowUpdateReqVO reqVO);

    void deleteShow(Long id);

    ShowRespVO getShow(Long id);

    PageResult<ShowPageRespVO> getShowPage(ShowPageReqVO reqVO);

    /** C 端：已发布演出分页 */
    PageResult<AppShowPageRespVO> getAppShowPage(AppShowPageReqVO reqVO);

    /** C 端：已发布演出详情 */
    AppShowRespVO getAppShow(Long id);

    /**
     * 校验演出存在，返回 DO
     */
    ShowDO validateShowExists(Long id);
}
