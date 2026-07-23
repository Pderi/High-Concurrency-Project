package com.hc.ticket.module.tkt.service.order;

import com.hc.ticket.module.tkt.config.TktProperties;
import com.hc.ticket.module.tkt.redis.TktRedisKeys;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;

import static com.hc.ticket.framework.common.exception.ServiceExceptionUtil.exception;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.GRAB_RESULT_NOT_EXISTS;

/**
 * 受理结果协议：status|orderNo|errorCode|errorMsg
 */
@Service
public class GrabResultServiceImpl implements GrabResultService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private TktProperties tktProperties;

    @Override
    public void savePending(String acceptToken) {
        write(acceptToken, GrabResultBO.pending());
    }

    @Override
    public void saveSuccess(String acceptToken, String orderNo) {
        write(acceptToken, GrabResultBO.success(orderNo));
    }

    @Override
    public void saveFail(String acceptToken, Integer errorCode, String errorMsg) {
        write(acceptToken, GrabResultBO.fail(errorCode, errorMsg));
    }

    @Override
    public GrabResultBO get(String acceptToken) {
        if (!StringUtils.hasText(acceptToken)) {
            throw exception(GRAB_RESULT_NOT_EXISTS);
        }
        String raw = stringRedisTemplate.opsForValue().get(TktRedisKeys.grabResult(acceptToken));
        if (!StringUtils.hasText(raw)) {
            throw exception(GRAB_RESULT_NOT_EXISTS);
        }
        return parse(raw);
    }

    private void write(String acceptToken, GrabResultBO bo) {
        int minutes = tktProperties.getGrab().getResultTtlMinutes() == null
                ? 30 : tktProperties.getGrab().getResultTtlMinutes();
        stringRedisTemplate.opsForValue().set(
                TktRedisKeys.grabResult(acceptToken),
                format(bo),
                Duration.ofMinutes(Math.max(minutes, 1)));
    }

    private static String format(GrabResultBO bo) {
        return bo.getStatus()
                + "|" + nullToEmpty(bo.getOrderNo())
                + "|" + (bo.getErrorCode() == null ? "" : bo.getErrorCode())
                + "|" + nullToEmpty(bo.getErrorMsg());
    }

    private static GrabResultBO parse(String raw) {
        String[] parts = raw.split("\\|", -1);
        if (parts.length < 4) {
            throw exception(GRAB_RESULT_NOT_EXISTS);
        }
        if (!StringUtils.hasText(parts[0])) {
            throw exception(GRAB_RESULT_NOT_EXISTS);
        }
        GrabResultBO bo = new GrabResultBO();
        bo.setStatus(Integer.valueOf(parts[0]));
        if (StringUtils.hasText(parts[1])) {
            bo.setOrderNo(parts[1]);
        }
        if (StringUtils.hasText(parts[2])) {
            bo.setErrorCode(Integer.valueOf(parts[2]));
        }
        if (StringUtils.hasText(parts[3])) {
            bo.setErrorMsg(parts[3]);
        }
        return bo;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
