package com.hc.ticket.module.tkt.service.show;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.framework.common.util.object.BeanUtils;
import com.hc.ticket.module.tkt.cache.TktMetaCache;
import com.hc.ticket.module.tkt.controller.admin.show.vo.ShowAddReqVO;
import com.hc.ticket.module.tkt.controller.admin.show.vo.ShowPageReqVO;
import com.hc.ticket.module.tkt.controller.admin.show.vo.ShowPageRespVO;
import com.hc.ticket.module.tkt.controller.admin.show.vo.ShowRespVO;
import com.hc.ticket.module.tkt.controller.admin.show.vo.ShowUpdateReqVO;
import com.hc.ticket.module.tkt.controller.app.show.vo.AppShowPageReqVO;
import com.hc.ticket.module.tkt.controller.app.show.vo.AppShowPageRespVO;
import com.hc.ticket.module.tkt.controller.app.show.vo.AppShowRespVO;
import com.hc.ticket.module.tkt.dal.dataobject.show.ShowDO;
import com.hc.ticket.module.tkt.dal.mysql.show.ShowMapper;
import com.hc.ticket.module.tkt.enums.ShowStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.hc.ticket.framework.common.exception.ServiceExceptionUtil.exception;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.SHOW_NOT_EXISTS;

@Service
@Validated
public class ShowServiceImpl implements ShowService {

    @Resource
    private ShowMapper showMapper;
    @Resource
    private TktMetaCache tktMetaCache;

    @Override
    public Long createShow(ShowAddReqVO reqVO) {
        ShowDO show = BeanUtils.toBean(reqVO, ShowDO.class);
        if (show.getSort() == null) {
            show.setSort(0);
        }
        if (show.getTenantId() == null) {
            show.setTenantId(0L);
        }
        showMapper.insert(show);
        tktMetaCache.refreshShow(show.getId());
        return show.getId();
    }

    @Override
    public void updateShow(ShowUpdateReqVO reqVO) {
        validateShowExists(reqVO.getId());
        ShowDO updateObj = BeanUtils.toBean(reqVO, ShowDO.class);
        showMapper.updateById(updateObj);
        tktMetaCache.refreshShow(reqVO.getId());
    }

    @Override
    public void deleteShow(Long id) {
        validateShowExists(id);
        showMapper.deleteById(id);
        tktMetaCache.evictShow(id);
    }

    @Override
    public ShowRespVO getShow(Long id) {
        return BeanUtils.toBean(validateShowExists(id), ShowRespVO.class);
    }

    @Override
    public PageResult<ShowPageRespVO> getShowPage(ShowPageReqVO reqVO) {
        return BeanUtils.toBean(showMapper.selectPage(reqVO), ShowPageRespVO.class);
    }

    @Override
    public PageResult<AppShowPageRespVO> getAppShowPage(AppShowPageReqVO reqVO) {
        return BeanUtils.toBean(showMapper.selectAppPage(reqVO), AppShowPageRespVO.class);
    }

    @Override
    public AppShowRespVO getAppShow(Long id) {
        ShowDO show = validateShowExists(id);
        if (!ShowStatusEnum.PUBLISHED.getStatus().equals(show.getStatus())) {
            throw exception(SHOW_NOT_EXISTS);
        }
        return BeanUtils.toBean(show, AppShowRespVO.class);
    }

    @Override
    public ShowDO validateShowExists(Long id) {
        ShowDO show = tktMetaCache.getShow(id);
        if (show == null) {
            throw exception(SHOW_NOT_EXISTS);
        }
        return show;
    }
}
