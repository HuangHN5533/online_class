package com.atguigu.guli.service.statistics.feign.fallback;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.statistics.feign.UcenterMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UcenterMemberServiceFallback implements UcenterMemberService {
    @Override
    public R countRegisterNum(String day) {
        log.error("熔断保护");
        return R.ok().data("registerNum", 0);
    }
}
