package com.arrend_system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.arrend_system.common.Result;
import com.arrend_system.pojo.form.add.AddressForm;
import com.arrend_system.pojo.form.update.UpdateAddressForm;
import com.arrend_system.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address")
@Tag(name = "地址模块",description = "对地址信息的控制")
public class AddressController {
    @Autowired
    private AddressService addressService;

    //根据用户id查询默认地址
    @Operation(summary = "根据用户id查询默认地址")
    @PostMapping("/default")
    public Result<?> getDefault(@RequestParam Integer userId) throws JsonProcessingException {
        return addressService.getDefault(userId);
    }

    //添加地址
    @Operation(summary = "添加地址")
    @PostMapping("/add")
    public Result<?> addOne(@RequestBody AddressForm addressForm) throws JsonProcessingException {
        return addressService.addOne(addressForm);
    }

    //根据用户id获取地址列表
    @Operation(summary = "根据用户id获取地址列表",description = "default_flag为1的是该用户的默认地址")
    @PostMapping("/list")
    public Result<?> list(@RequestParam Integer userId){
        return addressService.getByuserId(userId);
    }

    //修改地址信息
    @Operation(summary = "修改地址信息")
    @PutMapping("/update")
    public Result<?> update(@RequestBody UpdateAddressForm updateAddressForm ) throws JsonProcessingException {
        return addressService.updateAddressInfo(updateAddressForm);
    }

    //修改为默认地址
    @Operation(summary = "修改为默认地址")
    @PutMapping("/updateDefault")
    public Result<?> updateDefault(@RequestParam Integer id,@RequestParam Integer userId) throws JsonProcessingException {
        return addressService.updateDefault(id,userId);
    }

    //删除地址
    @Operation(summary = "删除地址")
    @DeleteMapping("/delete")
    public Result<?> delete(@RequestParam Integer id,@RequestParam Integer userId) throws JsonProcessingException {
        return addressService.deleteById(id,userId);
    }

}
