package com.hc.ticket.module.tkt.service.ticket;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.framework.common.util.object.BeanUtils;
import com.hc.ticket.module.tkt.controller.app.ticket.vo.AppTicketPageReqVO;
import com.hc.ticket.module.tkt.controller.app.ticket.vo.AppTicketPageRespVO;
import com.hc.ticket.module.tkt.dal.dataobject.order.OrderDO;
import com.hc.ticket.module.tkt.dal.dataobject.ticket.TicketDO;
import com.hc.ticket.module.tkt.dal.mysql.ticket.TicketMapper;
import com.hc.ticket.module.tkt.enums.TicketStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.hc.ticket.framework.common.exception.ServiceExceptionUtil.exception;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.USER_ID_REQUIRED;

@Service
@Validated
public class TicketServiceImpl implements TicketService {

    private static final DateTimeFormatter TICKET_NO_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Resource
    private TicketMapper ticketMapper;

    @Override
    public void createTicketsForOrder(OrderDO order) {
        List<TicketDO> exists = ticketMapper.selectListByOrderId(order.getId());
        if (!exists.isEmpty()) {
            return;
        }
        int quantity = order.getQuantity() == null ? 0 : order.getQuantity();
        for (int i = 0; i < quantity; i++) {
            TicketDO ticket = new TicketDO();
            ticket.setTicketNo(generateTicketNo());
            ticket.setOrderId(order.getId());
            ticket.setUserId(order.getUserId());
            ticket.setSessionId(order.getSessionId());
            ticket.setTierId(order.getTierId());
            ticket.setTicketStatus(TicketStatusEnum.VALID.getStatus());
            ticket.setTenantId(0L);
            ticketMapper.insert(ticket);
        }
    }

    @Override
    public PageResult<AppTicketPageRespVO> getMyTicketPage(Long userId, AppTicketPageReqVO reqVO) {
        if (userId == null) {
            throw exception(USER_ID_REQUIRED);
        }
        PageResult<TicketDO> page = ticketMapper.selectAppPage(userId, reqVO.getTicketStatus(), reqVO);
        return BeanUtils.toBean(page, AppTicketPageRespVO.class);
    }

    private static String generateTicketNo() {
        int rand = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "TK" + LocalDateTime.now().format(TICKET_NO_TIME) + rand;
    }
}
