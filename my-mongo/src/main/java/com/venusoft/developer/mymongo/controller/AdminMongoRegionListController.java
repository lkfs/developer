package com.venusoft.developer.mymongo.controller;

import cn.hutool.core.bean.BeanUtil;
import com.venusoft.developer.common.result.CommonResult;
import com.venusoft.developer.common.result.ICommonResult;
import com.venusoft.developer.mongo.BaseMongoQuery;
import com.venusoft.developer.mymongo.dao.entity.DictRegionEntity;
import com.venusoft.developer.mymongo.manager.DictRegionManager;
import com.venusoft.developer.mymongo.qo.DictRegionListQO;
import com.venusoft.developer.mymongo.vo.DictRegionListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO
 *
 * @author : lkfs
 * @date : 2024-03-12 14:02
 */

@RestController
@RequestMapping("/mongo")
@Slf4j
@Tag(name = "mongo-demo")
public class AdminMongoRegionListController {
    @Resource
    private DictRegionManager manager;

    @PostMapping("/admin-mongo-region-list")
    @Operation(summary = "列表")
    public ICommonResult exec(
            @RequestBody @Parameter(required = true) @Validated DictRegionListQO qo,
            @RequestHeader(required = false) String platform,
            @RequestHeader(required = false) String version,
            HttpServletRequest request) {

        Query query = new BaseMongoQuery(qo).toQuery();

        List<DictRegionEntity> rawList = manager.findAll(query);

        List<DictRegionListVO> list = BeanUtil.copyToList(rawList, DictRegionListVO.class);

        return new CommonResult<>(list);


    }
}
