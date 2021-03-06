package com.xiakee.ecdao.order;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;

import com.xiakee.domain.ecgoods.XiakeeHaul;

public interface XiakeeHaulMapper {
    @Delete({
        "delete from sdb_xiakee_haul",
        "where haul_id = #{haulId,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long haulId);

    @Insert({
        "insert into sdb_xiakee_haul (haul_id, order_id, ",
        "item_id, createtime)",
        "values (#{haulId,jdbcType=BIGINT}, #{orderId,jdbcType=BIGINT}, ",
        "#{itemId,jdbcType=INTEGER}, #{createtime,jdbcType=INTEGER})"
    })
    int insert(XiakeeHaul record);

    @InsertProvider(type=XiakeeHaulSqlProvider.class, method="insertSelective")
    int insertSelective(XiakeeHaul record);

    @Select({
        "select",
        "haul_id, order_id, item_id, createtime",
        "from sdb_xiakee_haul",
        "where haul_id = #{haulId,jdbcType=BIGINT}"
    })
    @Results({
        @Result(column="haul_id", property="haulId", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="order_id", property="orderId", jdbcType=JdbcType.BIGINT),
        @Result(column="item_id", property="itemId", jdbcType=JdbcType.INTEGER),
        @Result(column="createtime", property="createtime", jdbcType=JdbcType.INTEGER)
    })
    XiakeeHaul selectByPrimaryKey(Long haulId);
    
    @Select({
        "select",
        "haul_id, order_id, item_id, createtime",
        "from sdb_xiakee_haul",
        "where order_id = #{orderId,jdbcType=BIGINT}",
        "and item_id = #{itemId,jdbcType=INTEGER}"
    })
    @Results({
        @Result(column="haul_id", property="haulId", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="order_id", property="orderId", jdbcType=JdbcType.BIGINT),
        @Result(column="item_id", property="itemId", jdbcType=JdbcType.INTEGER),
        @Result(column="createtime", property="createtime", jdbcType=JdbcType.INTEGER)
    })
    XiakeeHaul selectByOrderIdAndItemId(Map map);
    
    @Select("select * from sdb_xiakee_haul where order_id = #{orderId}")
    List<XiakeeHaul> selectByOrderId(Long orderId);

    @UpdateProvider(type=XiakeeHaulSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(XiakeeHaul record);

    @Update({
        "update sdb_xiakee_haul",
        "set order_id = #{orderId,jdbcType=BIGINT},",
          "item_id = #{itemId,jdbcType=INTEGER},",
          "createtime = #{createtime,jdbcType=INTEGER}",
        "where haul_id = #{haulId,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(XiakeeHaul record);
}