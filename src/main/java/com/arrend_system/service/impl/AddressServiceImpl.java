package com.arrend_system.service.impl;

import com.arrend_system.common.Result;
import com.arrend_system.exception.AddressException.AddressBelongException;
import com.arrend_system.exception.AddressException.AddressNoExistedException;
import com.arrend_system.pojo.entity.Address;
import com.arrend_system.mapper.AddressMapper;
import com.arrend_system.pojo.form.add.AddressForm;
import com.arrend_system.pojo.form.update.UpdateAddressForm;
import com.arrend_system.service.AddressService;
import com.arrend_system.utils.JsonUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.arrend_system.exception.ServerException;
import java.util.List;
import java.util.Objects;

/**
* @author 张明阳
* @description 针对表【address】的数据库操作Service实现
* @createDate 2025-04-06 12:14:15
*/
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address>
    implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Transactional
    @Override
    public Result<?> addOne(AddressForm addressForm) throws JsonProcessingException {
        List<Address> addresses = addressMapper.findAddressByUserId(addressForm.getUserId());

        // 创建新的地址对象
        Address newAddress = new Address(
                null,
                addressForm.getUserId(),
                addressForm.getConsignee(),
                addressForm.getDetailAddress(),
                addressForm.getArea(),
                addressForm.getPhone(),
                addressForm.getLabel(),
                addresses.isEmpty() ? 1 : 0, // 如果是第一个地址，则设置为默认地址
                addressForm.getLongitude(),
                addressForm.getLatitude()
        );

        // 插入新地址到数据库
        addressMapper.insert(newAddress);

        // 更新 Redis 中的默认地址信息
        if (newAddress.getDefaultFlag() == 1) {
            String defaultKey = "defaultAddress:" + addressForm.getUserId();
            String newAddressJson= JsonUtil.serialize(newAddress);
            redisTemplate.opsForValue().set(defaultKey, newAddressJson);
        }

        return Result.success("添加地址成功");
    }

    @Override
    public Result<?> getDefault(Integer userId) throws JsonProcessingException {
        //先从redis查询
        String defaultKey = "defaultAddress:" + userId;
        String addressString = redisTemplate.opsForValue().get(defaultKey);
        Address address=new Address();
        //如果redis中存在，则直接返回
        if (addressString != null){
            address=JsonUtil.deserialize(addressString,Address.class);
            return Result.success(address);
        }else{
            //如果redis中不存在，则从数据库中查询,并将默认地址存储到redis中
            address=addressMapper.findDefaultAddressByUserId(userId);
            String addressJson = JsonUtil.serialize(address);
            redisTemplate.opsForValue().set(defaultKey,addressJson);
            return Result.success(address);
        }

    }

    @Transactional //开启事务
    @Override
    public Result<?> updateDefault(Integer id, Integer userId) throws JsonProcessingException {
        // 从数据库中获取地址信息
        Address address = addressMapper.selectById(id);
        if (address == null) {
            throw new AddressNoExistedException();
        }
        // 检查地址是否属于该用户
        if (!Objects.equals(address.getUserId(), userId)) {
            throw new AddressBelongException();
        }

        //如果是默认地址，返回该地址已为默认地址
        if (address.getDefaultFlag() == 1) {
            return Result.success("该地址已经是默认地址");
        }
        try {
            System.out.println("1111"+addressMapper.findDefaultAddressByUserId(userId));
            //如果不是默认地址，则将之前的默认地址设为非默认地址
            Address oldDefaultAddress = addressMapper.findDefaultAddressByUserId(userId);
            if (oldDefaultAddress != null) {
                addressMapper.updateDefaultFlag(oldDefaultAddress.getAddressId(), 0);
            }
            //把该地址设置为默认地址
            addressMapper.updateDefaultFlag(id,1);
            //更新redis中的默认地址信息(先删除再新增)
            String defaultKey = "defaultAddress:" + userId;
            redisTemplate.delete(defaultKey);
            Address newDefaultAddress = addressMapper.findDefaultAddressByUserId(userId);
            String newDefaultAddressJson = JsonUtil.serialize(newDefaultAddress);
            redisTemplate.opsForValue().set(defaultKey, newDefaultAddressJson);
            return Result.success("设置默认地址成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException();
        }
    }

    @Transactional
    @Override
    public Result<?> deleteById(Integer id,Integer userId) throws JsonProcessingException {
        Address address = addressMapper.selectById(id);
        if (address == null) {
            throw new AddressNoExistedException();
        }

        if(address.getDefaultFlag()==0){
            //不是默认地址
            addressMapper.deleteById(id);
            return Result.success("删除地址成功");
        } else if (address.getDefaultFlag()==1) {
            //是默认地址，先删除redis中的默认地址信息
            String defaultKey = "defaultAddress:" + userId;
            redisTemplate.delete(defaultKey);
            //再删除数据库中的地址
            addressMapper.deleteById(id);
            //再重新选一个作为默认地址（按用户注册地址的顺序选择第一个作为默认地址）
            Address newDefaultAddress = addressMapper.findAddressByUserId(userId).get(0);
            addressMapper.updateDefaultFlag(newDefaultAddress.getAddressId(), 1);
            //更新redis中的默认地址信息
            newDefaultAddress.setDefaultFlag(1);
            String newDefaultAddressJson = JsonUtil.serialize(newDefaultAddress);
            redisTemplate.opsForValue().set(defaultKey, newDefaultAddressJson);
            return Result.success("删除地址成功");
        }else {
            throw new ServerException();
        }
    }

    @Override
    public Result<?> getByuserId(Integer userId) {
        List<Address> addressList = addressMapper.findAddressByUserId(userId);
        return Result.success(addressList);
    }

    @Transactional
    @Override
    public Result<?> updateAddressInfo(UpdateAddressForm updateAddressForm) throws JsonProcessingException {
        //判断地址是否存在
        Address address = addressMapper.selectById(updateAddressForm.getId());
        if (address == null) {
            throw new AddressNoExistedException();
        }
        // 更新地址信息
        addressMapper.updateAddressInfo(updateAddressForm);

        // 如果更新的是默认地址，同步更新 Redis
        if (address.getDefaultFlag() == 1) {
            String defaultKey = "defaultAddress:" + address.getUserId();
            Address updatedAddress = addressMapper.findDefaultAddressByUserId(address.getUserId());
            String updatedAddressJson = JsonUtil.serialize(updatedAddress);
            redisTemplate.opsForValue().set(defaultKey, updatedAddressJson);
        }

        return Result.success("地址信息更新成功");
    }


}




