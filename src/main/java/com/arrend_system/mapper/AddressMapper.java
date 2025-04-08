package com.arrend_system.mapper;

import com.arrend_system.pojo.entity.Address;
import com.arrend_system.pojo.form.update.UpdateAddressForm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author 张明阳
* @description 针对表【address】的数据库操作Mapper
* @createDate 2025-04-06 12:14:15
* @Entity genertor.entity.Address
*/
public interface AddressMapper extends BaseMapper<Address> {

    //根据用户id查询地址列表
    @Select("select * from address where user_id=#{userId}")
    List<Address> findAddressByUserId(Integer userId);

    //根据用户id查询默认地址
    @Select("select * from address where user_id=#{userId} and default_flag=1")
    Address findDefaultAddressByUserId(Integer userId);

    void updateAddressInfo(UpdateAddressForm updateAddressForm);

    @Update("update address set default_flag=#{defaultFlag} where id=#{id}")
    void updateDefaultFlag(Integer id,Integer defaultFlag);

}




