package com.demodeller.mapper;

import com.demodeller.entity.Domain;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 */

@Repository
@Mapper
public interface DomainMapper extends BaseMapper<Domain> {


    void createDomain(Domain domain);
    void deleteDomain(Integer id);

}
