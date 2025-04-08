package com.arrend_system.service;

import com.arrend_system.common.Result;
import com.arrend_system.pojo.entity.Address;
import com.arrend_system.pojo.form.add.AddressForm;
import com.arrend_system.pojo.form.update.UpdateAddressForm;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
* @author 张明阳
* @description 针对表【address】的数据库操作Service
* @createDate 2025-04-06 12:14:15
*/
public interface AddressService extends IService<Address> {
    Result<?> addOne(AddressForm addressForm) throws JsonProcessingException;

    Result<?> getByuserId(Integer userId);

    Result<?> updateAddressInfo(UpdateAddressForm updateAddressForm) throws JsonProcessingException;

    Result<?> getDefault(Integer userId) throws JsonProcessingException;

    Result<?> updateDefault(Integer id, Integer userId) throws JsonProcessingException;

    Result<?> deleteById(Integer id,Integer userId) throws JsonProcessingException;

}
