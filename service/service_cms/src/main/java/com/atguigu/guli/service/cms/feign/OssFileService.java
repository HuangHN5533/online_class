package com.atguigu.guli.service.cms.feign;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.cms.feign.fallback.OssFileServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

@Service
@FeignClient(value = "oss-service", fallback = OssFileServiceFallBack.class)
public interface OssFileService {

    @DeleteMapping("/admin/oss/file/remove")
    R remove(String url);
}
