package com.arrend_system.controller;

import com.arrend_system.common.Result;
import com.arrend_system.config.baseconfig.MinioConfig;
import com.arrend_system.pojo.entity.Goods;
import com.arrend_system.pojo.entity.Orders;
import com.arrend_system.pojo.entity.Shop;
import com.arrend_system.service.GoodsService;
import com.arrend_system.service.ShopService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shop/{user_id}")
public class ShopController {

    @Autowired
    ShopService shopService;

    @Autowired
    GoodsService goodsService;


    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioClient minioClient;

    // 获取商店信息
    @GetMapping("/getShopInfo")
    public Result<?> getShopInfo(@PathVariable Integer user_id) {
        return shopService.getShopInfo(user_id);
    }

    // 修改商店信息
    @PutMapping("/updateShop")
    public Result<?> updateShop(@RequestBody Shop shop) {
        return shopService.updateShop(shop);
    }

    // 查询全部订单
    @GetMapping("/getAllOrders/{shop_id}")
    public Result<?> getAllOrders(@PathVariable Integer shop_id) {
        return shopService.getAllOrders(shop_id);
    }


    // 查询已取消订单
    @GetMapping("/getUndoOrders/{shop_id}")
    public Result<?> getUndoOrders(@PathVariable Integer shop_id) {
        return shopService.getUndoOrders(shop_id);
    }


    // 查询待接单订单
    @GetMapping("/getWaitingOrders/{shop_id}")
    public Result<?> getWaitingOrders(@PathVariable Integer shop_id) {
        return shopService.getWaitingOrders(shop_id);
    }

    // 查询进行中订单
    @GetMapping("/getGoingOrders/{shop_id}")
    public Result<?> getGoingOrders(@PathVariable Integer shop_id) {
        return shopService.getGoingOrders(shop_id);
    }

    // 查询已完成订单
    @GetMapping("/getFinishedOrders/{shop_id}")
    public Result<?> getFinishedOrders(@PathVariable Integer shop_id) {
        return shopService.getFinishedOrders(shop_id);
    }

    // 查询全部商品
    @GetMapping("/getAllGoods/{shop_id}")
    public Result<?> getAllGoods(@PathVariable Integer shop_id) {
        return goodsService.getAllGoods(shop_id);
    }

    // 查询库存还有的商品
    @GetMapping("/getSaleGoods/{shop_id}")
    public Result<?> getSaleGoods(@PathVariable Integer shop_id) {
        return goodsService.getSaleGoods(shop_id);
    }

    // 查询库存没有的商品
    @GetMapping("/getSoldOutGoods/{shop_id}")
    public Result<?> getSoldOutGoods(@PathVariable Integer shop_id) {
        return goodsService.getSoldOutGoods(shop_id);
    }


    // 添加商品
    @PostMapping("/addGoods")
    public Result<?> addGoods(@RequestBody Goods good, String imagePath) {
        return goodsService.addGoods(good, imagePath);
    }

    // 下架商品
    @DeleteMapping("/{item_id}")
    public Result<?> deleteGoods(@PathVariable Integer item_id) {
        return goodsService.deleteGood(item_id);
    }

    // 更改商品信息
    @PutMapping("/{item_id}")
    public Result<?> updateGood(@PathVariable Integer item_id, @RequestBody Goods good, String imgPath) {
        return goodsService.updateGood(item_id, good, imgPath);
    }

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<?> uploadFile(@RequestParam("file") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return Result.fail(400, "文件为空", null);
        }

        List<String> urls = new ArrayList<>(); // 用于存储每个文件的 URL
        String bucketName = minioConfig.getBucketName();

        try {
            for (MultipartFile file : files) {
                if (file == null || file.getSize() == 0) {
                    continue; // 跳过空文件
                }

                // 生成唯一文件名
                String objectName = UUID.randomUUID() + "_" + file.getOriginalFilename();

                // 上传文件到 MinIO
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());

                // 生成固定 URL
                String url = minioConfig.getEndpoint() + "/" + bucketName + "/" + objectName;
                urls.add(url); // 添加 URL 到列表中
            }

            if (urls.isEmpty()) {
                return Result.fail(400, "所有文件均为空或上传失败", null);
            }

            return Result.success(urls); // 返回所有文件的 URL
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(500, "上传失败", null);
        }
    }

}
